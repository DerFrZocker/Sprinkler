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
import java.util.logging.Logger;
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
        Set<PullRequestInfo> links = searchForLink(requester, pullRequest, message);

        links.add(pullRequest.getInfo()); // Add self to link

        Set<PullRequestLink> existingLinks = getExistingLinksFor(links);

        PullRequestLink link = new PullRequestLink(shouldHardLink(message, requester), links);

        if (linkIsAlreadyPresent(existingLinks, link)) {
            return;
        }

        if (!shouldHardLink(message, requester) && linksContainsHardLink(existingLinks)) {
            return;
        }

        unlinkExistingLinks(existingLinks);

        linkerDao.create(link);
    }

    private Set<PullRequestInfo> searchForLink(String requester, PullRequest pullRequest, String message) {
        if (!shouldHardLink(message, requester) && isSameAuthor(pullRequest, requester)) {
            return Collections.emptySet();
        }

        Set<PullRequestInfo> links = searchForOtherPullRequestsMentionedIn(message, pullRequest);

        if (!shouldHardLink(message, requester)) {
            links = applyFiltersTo(links, pullRequest);
        }

        if (moreThanOneRepositoryPerTypeIsPresent(links)) {
            Logger.getLogger(LinkService.class.getName()).info(String.format("The searchers and filters where not able to only find one link per repository for message '%s', manuel linking is required.", message));
            return Collections.emptySet();
        }

        return links;
    }

    public boolean shouldHardLink(String message, String requester) {
        return message.startsWith(SPECIAL_PREFIX) && specialLinker.contains(requester);
    }

    public Set<PullRequestLink> getExistingLinksFor(Collection<PullRequestInfo> pullRequestInfos) {
        return pullRequestInfos
                .stream()
                .map(linkerDao::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    public boolean linkIsAlreadyPresent(Collection<PullRequestLink> links, PullRequestLink link) {
        if (links.contains(link)) {
            if (links.size() == 1) {
                return true;
            }

            throw new RuntimeException(String.format("The link %s is already saved, but the found links (%s) are more than 1, which should not happen and means there is an inconsistency in the save state of the dao (%s) which provides the links.", link, links, linkerDao));
        }

        return false;
    }

    public void unlinkExistingLinks(Collection<PullRequestLink> links) {
        links.forEach(linkerDao::removeByValue);
    }

    public boolean linksContainsHardLink(Set<PullRequestLink> existingLinks) {
        return existingLinks.stream().anyMatch(PullRequestLink::hardLink);
    }

    public boolean isSameAuthor(PullRequest pullRequest, String requester) {
        return pullRequest.getAuthorId().equals(requester);
    }

    public Set<PullRequestInfo> searchForOtherPullRequestsMentionedIn(String message, PullRequest pullRequest) {
        return searchers.stream().map(searcher -> searcher.searchForLink(message))
                .flatMap(Collection::stream)
                .filter(found -> found.repository() != pullRequest.getInfo().repository())
                .collect(Collectors.toSet());
    }

    public Set<PullRequestInfo> applyFiltersTo(Set<PullRequestInfo> infos, PullRequest pullRequest) {
        return infos
                .stream()
                .map(pullRequestDao::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(found -> filters.stream().filter(filter -> !filter.isApplicable(pullRequest, found))
                        .findAny().isEmpty())
                .map(PullRequest::getInfo)
                .collect(Collectors.toSet());
    }

    public boolean moreThanOneRepositoryPerTypeIsPresent(Set<PullRequestInfo> infos) {
        class Holder {
            int amount = 0;

            Holder() {
            }

            Holder(int amount) {
                this.amount = amount;
            }
        }

        Map<Repository, Integer> result = infos
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
                return false;
            }
        }

        return true;
    }
}
