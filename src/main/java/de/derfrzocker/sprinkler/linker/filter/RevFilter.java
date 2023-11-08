package de.derfrzocker.sprinkler.linker.filter;

import de.derfrzocker.sprinkler.data.PullRequest;

import java.util.Collections;

public class RevFilter implements Filter {

    @Override
    public boolean isApplicable(PullRequest current, PullRequest found) {
        return !Collections.disjoint(current.getRev(), found.getRev());
    }
}
