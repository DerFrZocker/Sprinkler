package de.derfrzocker.sprinkler.linker;

import de.derfrzocker.sprinkler.data.PullRequestInfo;
import java.util.Set;

public interface Linker {

    Set<PullRequestInfo> searchForLink(String message);
}
