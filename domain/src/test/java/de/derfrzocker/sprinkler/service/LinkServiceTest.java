package de.derfrzocker.sprinkler.service;

import de.derfrzocker.sprinkler.dao.LinkerDao;
import de.derfrzocker.sprinkler.data.PullRequestInfo;
import de.derfrzocker.sprinkler.data.PullRequestLink;
import de.derfrzocker.sprinkler.data.Repository;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class LinkServiceTest {

    @Test
    public void testShouldHardLinkMessagePrefixWrong() {
        LinkService linkService = new LinkService(new HashSet<>(List.of("Dummy")), null, null);
        String message = "Hello this is a message!";

        boolean hardLink = linkService.shouldHardLink(message, "Dummy");

        assertFalse(hardLink, "Should not be able to hard link, since the message has the wrong prefix");
    }

    @Test
    public void testShouldHardLinkMessageRequesterWrong() {
        LinkService linkService = new LinkService(new HashSet<>(List.of("Dummy")), null, null);
        String message = "!Link: Hello this is a message!";

        boolean hardLink = linkService.shouldHardLink(message, "Other-Dummy");

        assertFalse(hardLink, "Should not be able to hard link, since requester is not in special linker list");
    }

    @Test
    public void testShouldHardLinkShouldBeAble() {
        LinkService linkService = new LinkService(new HashSet<>(List.of("Dummy")), null, null);
        String message = "!Link: Hello this is a message!";

        boolean hardLink = linkService.shouldHardLink(message, "Dummy");

        assertTrue(hardLink, "Should be able to hard link, since message and requester are correct");
    }

    @Test
    public void testGetExistingLinksFor() {
        LinkerDao dao = mock();
        LinkService linkService = new LinkService(Collections.emptySet(), null, dao);

        PullRequestInfo infoOne = new PullRequestInfo(Repository.BUKKIT, 1);
        PullRequestInfo infoTwo = new PullRequestInfo(Repository.CRAFTBUKKIT, 2);
        PullRequestInfo infoThree = new PullRequestInfo(Repository.SPIGOT, 3);
        PullRequestInfo infoFour = new PullRequestInfo(Repository.BUKKIT, 4);

        PullRequestLink linkOne = new PullRequestLink(false, new HashSet<>(List.of(infoOne, infoTwo)));
        PullRequestLink linkTwo = new PullRequestLink(false, new HashSet<>(List.of(infoThree)));

        when(dao.get(infoOne)).thenReturn(Optional.of(linkOne));
        when(dao.get(infoTwo)).thenReturn(Optional.of(linkOne));
        when(dao.get(infoThree)).thenReturn(Optional.of(linkTwo));
        when(dao.get(infoFour)).thenReturn(Optional.empty());

        Set<PullRequestLink> result = linkService.getExistingLinksFor(List.of(infoOne, infoTwo, infoThree, infoFour));

        assertEquals(new HashSet<>(List.of(linkOne, linkTwo)), result);

        verify(dao).get(infoOne);
        verify(dao).get(infoTwo);
        verify(dao).get(infoThree);
        verify(dao).get(infoFour);
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void testMoreThanOneRepositoryPerTypeIsPresentIsUnique() {
        LinkService linkService = new LinkService(null, null, null);
        Set<PullRequestInfo> pullRequestInfos = new HashSet<>(List.of(
                new PullRequestInfo(Repository.BUKKIT, 2),
                new PullRequestInfo(Repository.SPIGOT, 2)
        ));

        boolean result = linkService.moreThanOneRepositoryPerTypeIsPresent(pullRequestInfos);

        assertFalse(result);
    }

    @Test
    public void testMoreThanOneRepositoryPerTypeIsPresentIsNotUnique() {
        LinkService linkService = new LinkService(null, null, null);
        Set<PullRequestInfo> pullRequestInfos = new HashSet<>(List.of(
                new PullRequestInfo(Repository.BUKKIT, 2),
                new PullRequestInfo(Repository.SPIGOT, 2),
                new PullRequestInfo(Repository.BUKKIT, 4)
        ));

        boolean result = linkService.moreThanOneRepositoryPerTypeIsPresent(pullRequestInfos);

        assertTrue(result);
    }
}
