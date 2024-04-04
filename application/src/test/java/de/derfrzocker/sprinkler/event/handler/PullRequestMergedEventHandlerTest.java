package de.derfrzocker.sprinkler.event.handler;

import de.derfrzocker.sprinkler.dao.PullRequestDao;
import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.data.PullRequestInfo;
import de.derfrzocker.sprinkler.data.Repository;
import de.derfrzocker.sprinkler.data.Status;
import de.derfrzocker.sprinkler.event.PullRequestMergedEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class PullRequestMergedEventHandlerTest {
    private final static Instant TIME = Instant.now();

    @Test
    public void testPullRequestAlreadyMerged() {
        PullRequestDao dao = mock();
        PullRequestMergedEventHandler eventHandler = new PullRequestMergedEventHandler(dao);
        PullRequest expected = createWithStatus(Status.CLOSED);
        PullRequest actual = createWithStatus(Status.CLOSED);

        eventHandler.handle(actual, createMergeEvent());

        assertEquals(expected, actual);
        verifyNoInteractions(dao);
    }

    @Test
    public void testPullRequestMerge() {
        PullRequestDao dao = mock();

        PullRequestMergedEventHandler eventHandler = new PullRequestMergedEventHandler(dao);
        PullRequest expected = createWithStatus(Status.CLOSED);
        PullRequest actual = createWithStatus(Status.OPENED);

        doAnswer(a -> {
                    assertSame(Status.CLOSED, a.getArgument(0, PullRequest.class).getStatus());
                    return null;
                }).when(dao).update(actual);

        eventHandler.handle(actual, createMergeEvent());

        assertEquals(expected, actual);
        verify(dao).update(actual);
        verifyNoMoreInteractions(dao);
    }

    private PullRequestMergedEvent createMergeEvent() {
        return new PullRequestMergedEvent(Repository.BUKKIT, 42, "Dummy");
    }

    private PullRequest createWithStatus(Status status) {
        PullRequest request = new PullRequest(new PullRequestInfo(Repository.BUKKIT, 42), TIME, "Dummy");
        request.setStatus(status);

        return request;
    }
}
