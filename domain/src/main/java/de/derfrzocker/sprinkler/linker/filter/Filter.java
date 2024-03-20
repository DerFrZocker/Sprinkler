package de.derfrzocker.sprinkler.linker.filter;

import de.derfrzocker.sprinkler.data.PullRequest;

public interface Filter {

    boolean isApplicable(PullRequest current, PullRequest found);
}
