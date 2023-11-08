package de.derfrzocker.sprinkler.linker.filter;

import de.derfrzocker.sprinkler.data.PullRequest;

import java.time.Duration;

public class DateFilter implements Filter {

    private static final Duration CREATE_WINDOW = Duration.ofHours(4);

    @Override
    public boolean isApplicable(PullRequest current, PullRequest found) {
        return CREATE_WINDOW.compareTo(Duration.between(current.getCreateDate(), found.getCreateDate()).abs()) >= 0;
    }
}
