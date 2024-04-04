package de.derfrzocker.sprinkler.linker.filter;

import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.data.PullRequestInfo;
import de.derfrzocker.sprinkler.data.Repository;
import de.derfrzocker.sprinkler.data.Rev;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RevFilterTest {

    @Test
    public void testIntersectingRevs() {
        RevFilter revFilter = new RevFilter();
        PullRequest current = create(
                createRevWithNumber(10),
                createRevWithNumber(12),
                createRevWithNumber(42)
        );
        PullRequest found = create(
                createRevWithNumber(11),
                createRevWithNumber(13),
                createRevWithNumber(42)
        );

        boolean result = revFilter.isApplicable(current, found);

        assertTrue(result, "Revs should have an intersecting number, but it does not");
    }

    @Test
    public void testNoIntersectingRevs() {
        RevFilter revFilter = new RevFilter();
        PullRequest current = create(
                createRevWithNumber(10),
                createRevWithNumber(12),
                createRevWithNumber(42)
        );
        PullRequest found = create(
                createRevWithNumber(11),
                createRevWithNumber(13),
                createRevWithNumber(43)
        );

        boolean result = revFilter.isApplicable(current, found);

        assertFalse(result, "Revs should have an no intersecting number, but it does");
    }

    private PullRequest create(Rev... revs) {
        PullRequest pullRequest = new PullRequest(new PullRequestInfo(Repository.BUKKIT, -1), Instant.now(), "Dummy");

        pullRequest.setRev(new HashSet<>(List.of(revs)));

        return pullRequest;
    }

    private Rev createRevWithNumber(int number) {
        return new Rev(number, Collections.emptyMap());
    }
}
