package de.derfrzocker.sprinkler.linker.filter;

import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.data.PullRequestInfo;
import de.derfrzocker.sprinkler.data.Repository;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthorFilterTest {

    @Test
    public void testMatchingAuthor() {
        AuthorFilter authorFilter = new AuthorFilter();
        PullRequest current = createWithAuthor("Dummy");
        PullRequest found = createWithAuthor("Dummy");

        boolean result = authorFilter.isApplicable(current, found);

        assertTrue(result, "Authors should match but they don't");
    }

    @Test
    public void testNotMatchingAuthor() {
        AuthorFilter authorFilter = new AuthorFilter();
        PullRequest current = createWithAuthor("Dummy");
        PullRequest found = createWithAuthor("Not Dummy");

        boolean result = authorFilter.isApplicable(current, found);

        assertFalse(result, "Authors should not match but they do");
    }

    private PullRequest createWithAuthor(String author) {
        return new PullRequest(new PullRequestInfo(Repository.BUKKIT, -1), Instant.now(), author);
    }
}
