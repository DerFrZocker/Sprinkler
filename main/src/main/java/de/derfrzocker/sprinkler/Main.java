package de.derfrzocker.sprinkler;

import com.sun.net.httpserver.HttpServer;
import de.derfrzocker.sprinkler.dao.BitbucketCommitDao;
import de.derfrzocker.sprinkler.dao.CommitDao;
import de.derfrzocker.sprinkler.dao.HttpRevDao;
import de.derfrzocker.sprinkler.dao.LinkerDao;
import de.derfrzocker.sprinkler.dao.MemoryLinkerDao;
import de.derfrzocker.sprinkler.dao.MemoryPullRequestDao;
import de.derfrzocker.sprinkler.dao.PullRequestDao;
import de.derfrzocker.sprinkler.dao.RevDao;
import de.derfrzocker.sprinkler.event.PullRequestEventManager;
import de.derfrzocker.sprinkler.event.handler.PullRequestCommentAddedEventHandler;
import de.derfrzocker.sprinkler.event.handler.PullRequestCreateEventHandler;
import de.derfrzocker.sprinkler.event.handler.PullRequestDeclinedEventHandler;
import de.derfrzocker.sprinkler.event.handler.PullRequestDeletedEventHandler;
import de.derfrzocker.sprinkler.event.handler.PullRequestDescriptionUpdatedEventHandler;
import de.derfrzocker.sprinkler.event.handler.PullRequestMergedEventHandler;
import de.derfrzocker.sprinkler.event.handler.PullRequestSourceBranchChangedEventHandler;
import de.derfrzocker.sprinkler.event.handler.PullRequestSourceBranchUptatedEventHandler;
import de.derfrzocker.sprinkler.event.handler.PullRequestTitleUpdatedEventHandler;
import de.derfrzocker.sprinkler.service.LinkService;
import de.derfrzocker.sprinkler.service.RevService;
import de.derfrzocker.sprinkler.webhook.PullRequestWebhookHandler;
import de.derfrzocker.sprinkler.webhook.request.handler.PullRequestCommentAddedRequestHandler;
import de.derfrzocker.sprinkler.webhook.request.handler.PullRequestDeclinedRequestHandler;
import de.derfrzocker.sprinkler.webhook.request.handler.PullRequestDeletedRequestHandler;
import de.derfrzocker.sprinkler.webhook.request.handler.PullRequestMergedRequestHandler;
import de.derfrzocker.sprinkler.webhook.request.handler.PullRequestModifiedRequestHandler;
import de.derfrzocker.sprinkler.webhook.request.handler.PullRequestOpenedRequestHandler;
import de.derfrzocker.sprinkler.webhook.request.handler.PullRequestSourceBranchUpdatedRequestHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws IOException {
        // Daos
        PullRequestDao pullRequestDao = new MemoryPullRequestDao();
        LinkerDao linkerDao = new MemoryLinkerDao();
        RevDao revDao = new HttpRevDao();
        CommitDao commitDao = new BitbucketCommitDao("dummy", "dummy"); // TODO 2024-02-14: Make configurateable

        // Services
        LinkService linkService = new LinkService(Collections.emptySet(), pullRequestDao, linkerDao);
        RevService revService = new RevService(revDao, commitDao);

        // Events
        PullRequestEventManager manager = new PullRequestEventManager();
        manager.registerEventHandler(new PullRequestTitleUpdatedEventHandler(pullRequestDao));
        manager.registerEventHandler(new PullRequestSourceBranchUptatedEventHandler(pullRequestDao, revService));
        manager.registerEventHandler(new PullRequestSourceBranchChangedEventHandler(pullRequestDao, revService));
        manager.registerEventHandler(new PullRequestMergedEventHandler(pullRequestDao));
        manager.registerEventHandler(new PullRequestDescriptionUpdatedEventHandler(pullRequestDao, linkService));
        manager.registerEventHandler(new PullRequestDeletedEventHandler(pullRequestDao, linkService));
        manager.registerEventHandler(new PullRequestDeclinedEventHandler(pullRequestDao));
        manager.registerEventHandler(new PullRequestCreateEventHandler(pullRequestDao, revService, linkService));
        manager.registerEventHandler(new PullRequestCommentAddedEventHandler(pullRequestDao, linkService));

        // Request handler
        PullRequestWebhookHandler webhookHandler = new PullRequestWebhookHandler();
        webhookHandler.registerRequestHandler(new PullRequestSourceBranchUpdatedRequestHandler(manager));
        webhookHandler.registerRequestHandler(new PullRequestOpenedRequestHandler(manager));
        webhookHandler.registerRequestHandler(new PullRequestModifiedRequestHandler(manager));
        webhookHandler.registerRequestHandler(new PullRequestMergedRequestHandler(manager));
        webhookHandler.registerRequestHandler(new PullRequestDeletedRequestHandler(manager));
        webhookHandler.registerRequestHandler(new PullRequestDeclinedRequestHandler(manager));
        webhookHandler.registerRequestHandler(new PullRequestCommentAddedRequestHandler(manager));

        // Create Server
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0); // TODO 2024-02-15: Make configuratable
        server.createContext("/api/v1/bitbucket-webhook/", webhookHandler);
        // Application is designed for single Thread only, but should be enough since at
        // best two or three PR will be modified in the same minute, with long pauses
        // between.
        server.setExecutor(Executors.newFixedThreadPool(1));
        server.start();
    }
}
