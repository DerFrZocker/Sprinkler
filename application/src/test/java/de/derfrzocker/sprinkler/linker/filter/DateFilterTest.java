package de.derfrzocker.sprinkler.linker.filter;

import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.data.PullRequestInfo;
import de.derfrzocker.sprinkler.data.Repository;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateFilterTest {

    private static final Instant BASIS_TIME = Instant.parse("2007-12-03T10:15:30.00Z");

    @Test
    public void testIn4HourPositiveRange() {
        DateFilter dateFilter = new DateFilter();
        PullRequest current = createWithDate(BASIS_TIME);
        PullRequest found = createWithDate(BASIS_TIME.plus(Duration.ofHours(4)));

        boolean result = dateFilter.isApplicable(current, found);

        assertTrue(result, "Creating time should be between 4 hour, but it is not");
    }

    @Test
    public void testIn4HourNegativeRange() {
        DateFilter dateFilter = new DateFilter();
        PullRequest current = createWithDate(BASIS_TIME);
        PullRequest found = createWithDate(BASIS_TIME.minus(Duration.ofHours(4)));

        boolean result = dateFilter.isApplicable(current, found);

        assertTrue(result, "Creating time should be between 4 hour, but it is not");
    }

    @Test
    public void testOutside4HourPositiveRange() {
        DateFilter dateFilter = new DateFilter();
        PullRequest current = createWithDate(BASIS_TIME);
        PullRequest found = createWithDate(BASIS_TIME.plus(Duration.ofHours(5)));

        boolean result = dateFilter.isApplicable(current, found);

        assertFalse(result, "Creating time should not between 4 hour, but it is");
    }

    @Test
    public void testOutside4HourNegativeRange() {
        DateFilter dateFilter = new DateFilter();
        PullRequest current = createWithDate(BASIS_TIME);
        PullRequest found = createWithDate(BASIS_TIME.minus(Duration.ofHours(5)));

        boolean result = dateFilter.isApplicable(current, found);

        assertFalse(result, "Creating time should not between 4 hour, but it is");
    }

    private PullRequest createWithDate(Instant time) {
        return new PullRequest(new PullRequestInfo(Repository.BUKKIT, -1), time, "Dummy");
    }
}
