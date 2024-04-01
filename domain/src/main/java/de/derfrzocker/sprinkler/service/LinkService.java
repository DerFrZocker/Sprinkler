package de.derfrzocker.sprinkler.service;

import de.derfrzocker.sprinkler.dao.LinkerDao;
import de.derfrzocker.sprinkler.dao.PullRequestDao;
import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.data.PullRequestInfo;
import de.derfrzocker.sprinkler.data.PullRequestLink;
import de.derfrzocker.sprinkler.data.Repository;
import de.derfrzocker.sprinkler.linker.filter.Filter;
import de.derfrzocker.sprinkler.linker.searcher.Searcher;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class LinkService {

    private static final String SPECIAL_PREFIX = "!Link: ";

    private final Set<Filter> filters = new HashSet<>();
    private final Set<Searcher> searchers = new HashSet<>();
    private final Set<String> specialLinker;
    private final PullRequestDao pullRequestDao;
    private final LinkerDao linkerDao;

    public LinkService(Set<String> specialLinker, PullRequestDao pullRequestDao, LinkerDao linkerDao) {
        this.specialLinker = specialLinker;
        this.pullRequestDao = pullRequestDao;
        this.linkerDao = linkerDao;
    }

    public void registerFilter(Filter filter) {
        filters.add(filter);
    }

    public void registerSearchers(Searcher searcher) {
        searchers.add(searcher);
    }

    public void searchAndCreateLink(String requester, PullRequest pullRequest, String message) {
        // Allow special linker to link every pull request together
        boolean specialLink = message.startsWith(SPECIAL_PREFIX) && specialLinker.contains(requester);
        Set<PullRequestInfo> links = searchForLink(specialLink, requester, pullRequest, message);

        links.add(pullRequest.getInfo()); // Add self to link

        Set<PullRequestLink> existingLinks = links
                .stream()
                .map(linkerDao::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        PullRequestLink link = new PullRequestLink(specialLink, links);

        if (existingLinks.isEmpty()) {
            linkerDao.create(link);
            return;
        }

        if (existingLinks.contains(link)) {
            if (existingLinks.size() == 1) {
                return;
            }
            // TODO: 10/27/23 Log inconsistency, this should not happen
        }

        if (existingLinks.stream().anyMatch(PullRequestLink::hardLink) && !specialLink) {
            // A normal user cannot override a special link
            return;
        }

        existingLinks.forEach(linkerDao::removeByValue);

        linkerDao.create(link);
    }

    private Set<PullRequestInfo> searchForLink(boolean specialLink, String requester, PullRequest pullRequest,
                                               String message) {
        if (!specialLink && !pullRequest.getAuthorId().equals(requester)) {
            return Collections.emptySet();
        }

        Set<PullRequestInfo> links = searchers.stream().map(searcher -> searcher.searchForLink(message))
                .flatMap(Collection::stream)
                // It does not make any sense to link two pull requests together, which are from
                // the same repository
                // We filter them here extra instead of with a Filter object, since this is a
                // hard requirement.
                // And the other filters are soft and can be overridden with the special link
                // If we have down the line more hard requirements we can use a filter system
                // here as well, but for now it should be fine
                .filter(found -> found.repository() != pullRequest.getInfo().repository())
                .collect(Collectors.toSet());

        if (!specialLink) {
            // Make more checks if it is not a special link
            links = links
                    .stream()
                    .map(pullRequestDao::get)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(found -> filters.stream().filter(filter -> !filter.isApplicable(pullRequest, found))
                            .findAny().isEmpty())
                    .map(PullRequest::getInfo)
                    .collect(Collectors.toSet());
        }

        // At this point, there should only be one pull request info per repository,
        // otherwise we cannot link them together reliabel
        class Holder {
            int amount = 0;

            Holder() {
            }

            Holder(int amount) {
                this.amount = amount;
            }
        }

        Map<Repository, Integer> result = links
                .stream()
                .map(PullRequestInfo::repository)
                .collect(Collectors.groupingBy(Function.identity(), new Collector<Repository, Holder, Integer>() {
                    public Supplier<Holder> supplier() {
                        return Holder::new;
                    }

                    @Override
                    public BiConsumer<Holder, Repository> accumulator() {
                        return (holder, repository) -> holder.amount++;
                    }

                    @Override
                    public BinaryOperator<Holder> combiner() {
                        return (f, s) -> new Holder(f.amount + s.amount);
                    }

                    @Override
                    public Function<Holder, Integer> finisher() {
                        return holder -> holder.amount;
                    }

                    @Override
                    public Set<Characteristics> characteristics() {
                        return Collections.emptySet();
                    }
                }));

        for (Map.Entry<Repository, Integer> entry : result.entrySet()) {
            if (entry.getValue() > 1) {
                // TODO: 10/16/23 Log this
                return Collections.emptySet();
            }
        }

        return links;
    }
}
