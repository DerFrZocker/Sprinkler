package de.derfrzocker.sprinkler.linker;

import de.derfrzocker.sprinkler.data.PullRequestInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupLinker implements Linker {

    private final List<Linker> linkers;

    private GroupLinker(List<Linker> linkers) {
        this.linkers = linkers;
    }

    public static GroupLinker of(Linker linker, Linker... linkers) {
        List<Linker> result = new ArrayList<>();

        result.add(linker);
        result.addAll(Arrays.asList(linkers));

        return new GroupLinker(result);
    }

    @Override
    public Set<PullRequestInfo> searchForLink(String message) {
        Set<PullRequestInfo> infos = new HashSet<>();

        linkers.forEach(linker -> infos.addAll(linker.searchForLink(message)));

        return infos;
    }
}
