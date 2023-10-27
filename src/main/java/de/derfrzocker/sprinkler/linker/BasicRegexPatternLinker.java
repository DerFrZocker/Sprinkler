package de.derfrzocker.sprinkler.linker;

import de.derfrzocker.sprinkler.data.PullRequestInfo;
import de.derfrzocker.sprinkler.data.Repository;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasicRegexPatternLinker implements Linker {

    private static final Pattern PATTERN = Pattern.compile("(craftbukkit|spigot|bukkit):*d");

    @Override
    public Set<PullRequestInfo> searchForLink(String message) {
        Matcher matcher = PATTERN.matcher(message);
        Set<PullRequestInfo> set = new HashSet<>();

        while (matcher.find()) {
            String result = message.substring(matcher.start(), matcher.end());
            int first = result.indexOf(':');
            String repo = result.substring(0, first);
            String prId = result.substring(first + 1);

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
