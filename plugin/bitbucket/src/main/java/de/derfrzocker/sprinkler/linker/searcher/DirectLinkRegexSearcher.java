package de.derfrzocker.sprinkler.linker.searcher;

import de.derfrzocker.sprinkler.data.PullRequestInfo;
import de.derfrzocker.sprinkler.data.Repository;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirectLinkRegexSearcher implements Searcher {

    private static final Pattern PATTERN = Pattern.compile(
            "https?://hub.spigotmc.org/stash/projects/SPIGOT/repos/(craftbukkit|spigot|bukkit)/pull-requests/\\d*");

    @Override
    public Set<PullRequestInfo> searchForLink(String message) {
        Matcher matcher = PATTERN.matcher(message);
        Set<PullRequestInfo> set = new HashSet<>();

        while (matcher.find()) {
            String result = message.substring(matcher.start() + 53, matcher.end());
            int first = result.indexOf('/');
            String repo = result.substring(0, first);
            String prId = result.substring(first + 14);

            Repository repository;
            int id;
            try {
                repository = Repository.valueOf(repo.toUpperCase(Locale.ROOT));
                id = Integer.parseInt(prId);
            } catch (IllegalArgumentException e) {
                continue;
            }

            PullRequestInfo info = new PullRequestInfo(repository, id);
            set.add(info);
        }

        return set;
    }
}
