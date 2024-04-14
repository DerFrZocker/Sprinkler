package de.derfrzocker.sprinkler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import de.derfrzocker.sprinkler.dao.BitbucketCommitDao;
import de.derfrzocker.sprinkler.dao.CommitDao;
import de.derfrzocker.sprinkler.dao.FileCommitDao;
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
import de.derfrzocker.sprinkler.linker.filter.AuthorFilter;
import de.derfrzocker.sprinkler.linker.filter.DateFilter;
import de.derfrzocker.sprinkler.linker.filter.RevFilter;
import de.derfrzocker.sprinkler.linker.searcher.DirectLinkRegexSearcher;
import de.derfrzocker.sprinkler.linker.searcher.IndirectLinkRegexSearcher;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;

public class Main {

    private final static Gson GSON = new Gson();
    private final static String PROPERTIES = "server.properties";
    private final static File PROPERTIES_FILE = new File(PROPERTIES);

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties(System.getProperties());
        if (!PROPERTIES_FILE.exists()) {
            Files.copy(Main.class.getResourceAsStream("/" + PROPERTIES), PROPERTIES_FILE.toPath());
        }
        properties.load(new FileInputStream(PROPERTIES_FILE));

        // Daos
        PullRequestDao pullRequestDao = loadPullRequestDao(properties);
        LinkerDao linkerDao = loadLinkerDao(properties);
        RevDao revDao = loadRevDao(properties);
        CommitDao commitDao = loadCommitDao(properties);

        // Services
        LinkService linkService = new LinkService(loadSpecialLinker(properties), pullRequestDao, linkerDao);
        RevService revService = new RevService(revDao, commitDao);

        // Events
        PullRequestEventManager manager = new PullRequestEventManager();
        manager.registerEventHandler(new PullRequestTitleUpdatedEventHandler(pullRequestDao));
        manager.registerEventHandler(new PullRequestSourceBranchUptatedEventHandler(pullRequestDao, revService));
        manager.registerEventHandler(new PullRequestSourceBranchChangedEventHandler(pullRequestDao, revService));
        manager.registerEventHandler(new PullRequestMergedEventHandler(pullRequestDao));
        manager.registerEventHandler(new PullRequestDescriptionUpdatedEventHandler(pullRequestDao, linkService));
        manager.registerEventHandler(new PullRequestDeletedEventHandler(pullRequestDao, linkerDao));
        manager.registerEventHandler(new PullRequestDeclinedEventHandler(pullRequestDao));
        manager.registerEventHandler(new PullRequestCreateEventHandler(pullRequestDao, revService, linkService));
        manager.registerEventHandler(new PullRequestCommentAddedEventHandler(pullRequestDao, linkService));

        // Request handler
        PullRequestWebhookHandler webhookHandler = new PullRequestWebhookHandler(properties.getProperty("sprinkler.webhook-token"), GSON);
        webhookHandler.registerRequestHandler(new PullRequestSourceBranchUpdatedRequestHandler(manager));
        webhookHandler.registerRequestHandler(new PullRequestOpenedRequestHandler(manager));
        webhookHandler.registerRequestHandler(new PullRequestModifiedRequestHandler(manager));
        webhookHandler.registerRequestHandler(new PullRequestMergedRequestHandler(manager));
        webhookHandler.registerRequestHandler(new PullRequestDeletedRequestHandler(manager));
        webhookHandler.registerRequestHandler(new PullRequestDeclinedRequestHandler(manager));
        webhookHandler.registerRequestHandler(new PullRequestCommentAddedRequestHandler(manager));

        // Create Server
        HttpServer server = HttpServer.create(new InetSocketAddress(Integer.parseInt(properties.getProperty("sprinkler.webhook-port"))), 0);
        server.createContext("/api/v1/bitbucket-webhook/", webhookHandler);
        // Application is designed for single Thread only, but should be enough since at
        // best two or three PR will be modified in the same minute, with long pauses
        // between.
        server.setExecutor(Executors.newFixedThreadPool(1));
        server.start();
    }

    private static PullRequestDao loadPullRequestDao(Properties properties) {
        return new MemoryPullRequestDao();
    }

    private static LinkerDao loadLinkerDao(Properties properties) {
        return new MemoryLinkerDao();
    }

    private static RevDao loadRevDao(Properties properties) {
        return new HttpRevDao();
    }

    private static CommitDao loadCommitDao(Properties properties) {
        String username = properties.getProperty("sprinkler.bitbucket-username");
        String token = properties.getProperty("sprinkler.bitbucket-token");

        if (anyNullOrBlank(username, token)) {
            return new FileCommitDao(GSON, new File("test-data/commits/"));
        }

        return new BitbucketCommitDao(username, token, GSON);
    }

    private static Set<String> loadSpecialLinker(Properties properties) {
        String names = properties.getProperty("sprinkler.special-linker");

        if (anyNullOrBlank(names)) {
            return Collections.emptySet();
        }

        Set<String> result = new HashSet<>();
        for (String name : names.split(",")) {
            result.add(name.trim());
        }

        return result;
    }

    private static boolean anyNullOrBlank(String... values) {
        for (String value : values) {
            if (value == null || value.isBlank()) {
                return true;
            }
        }

        return false;
    }
}
