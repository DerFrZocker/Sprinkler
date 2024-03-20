package de.derfrzocker.sprinkler.linker.filter;

import de.derfrzocker.sprinkler.data.PullRequest;

public class AuthorFilter implements Filter {

    @Override
    public boolean isApplicable(PullRequest current, PullRequest found) {
        return current.getAuthorId().equals(found.getAuthorId());
    }
}
