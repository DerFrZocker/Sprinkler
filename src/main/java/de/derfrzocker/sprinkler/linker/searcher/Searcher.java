package de.derfrzocker.sprinkler.linker.searcher;

import de.derfrzocker.sprinkler.data.PullRequestInfo;

import java.util.Set;

public interface Searcher {

    Set<PullRequestInfo> searchForLink(String message);
}
