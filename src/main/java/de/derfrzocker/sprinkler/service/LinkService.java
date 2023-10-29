package de.derfrzocker.sprinkler.service;

import de.derfrzocker.sprinkler.dao.LinkerDao;
import de.derfrzocker.sprinkler.dao.PullRequestDao;
import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.data.PullRequestInfo;
import de.derfrzocker.sprinkler.data.PullRequestLink;
import de.derfrzocker.sprinkler.data.Repository;
import de.derfrzocker.sprinkler.linker.Linker;
import java.time.Duration;
import java.util.Collections;
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
    private static final Duration CREATE_WINDOW = Duration.ofHours(4);

    private final Set<String> specialLinker;
    private final Linker linker;
    private final PullRequestDao pullRequestDao;
    private final LinkerDao linkerDao;

    public LinkService(Set<String> specialLinker, Linker linker, PullRequestDao pullRequestDao, LinkerDao linkerDao) {
        this.specialLinker = specialLinker;
        this.linker = linker;
        this.pullRequestDao = pullRequestDao;
        this.linkerDao = linkerDao;
    }

    public void searchAndCreateLink(String requester, PullRequest pullRequest, String message) {
        Set<PullRequestInfo> links = searchForLink(requester, pullRequest, message);

        links.add(pullRequest.getInfo()); // Add self to link

        Set<PullRequestLink> existingLinks = links
                .stream()
                .map(linkerDao::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        PullRequestLink link = new PullRequestLink(links);

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

        existingLinks.forEach(linkerDao::remove);

        linkerDao.create(link);
    }

    public void removeLinks(PullRequestInfo pullRequestInfo) {
        linkerDao.get(pullRequestInfo).ifPresent(linkerDao::remove);
    }

    private Set<PullRequestInfo> searchForLink(String requester, PullRequest pullRequest, String message) {
        // Allow special linker to link every pull request together
        boolean specialLink = message.startsWith(SPECIAL_PREFIX) && specialLinker.contains(requester);

        if (!specialLink && !pullRequest.getAuthorId().equals(requester)) {
            return Collections.emptySet();
        }

        Set<PullRequestInfo> links = linker.searchForLink(message)
                .stream()
                // It does not make any sense to link two pull requests together, which are from the same repository
                .filter(found -> found.repository() != pullRequest.getInfo().repository())
                .collect(Collectors.toSet());

        if (!specialLink) {
            // Make more checks if it is not a special link
            links = links
                    .stream()
                    .map(pullRequestDao::get)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(found -> filterAuthor(pullRequest, found))
                    .filter(found -> filterDate(pullRequest, found))
                    .filter(found -> filterRev(pullRequest, found))
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

    private boolean filterDate(PullRequest newPullRequest, PullRequest foundPullRequest) {
        return CREATE_WINDOW.compareTo(Duration.between(newPullRequest.getCreateDate(), foundPullRequest.getCreateDate()).abs()) >= 0;
    }

    private boolean filterRev(PullRequest newPullRequest, PullRequest foundPullRequest) {
        return !Collections.disjoint(newPullRequest.getRev(), foundPullRequest.getRev());
    }

    private boolean filterAuthor(PullRequest newPullRequest, PullRequest foundPullRequest) {
        return newPullRequest.getAuthorId().equals(foundPullRequest.getAuthorId());
    }
}
