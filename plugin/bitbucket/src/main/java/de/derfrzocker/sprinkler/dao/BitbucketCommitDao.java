package de.derfrzocker.sprinkler.dao;

import com.google.gson.Gson;
import de.derfrzocker.sprinkler.data.Commit;
import de.derfrzocker.sprinkler.data.PullRequestInfo;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

public class BitbucketCommitDao implements CommitDao {

    private final static String PULL_REQUEST_COMMITS = "https://hub.spigotmc.org/stash/rest/api/latest/projects/SPIGOT/repos/%s/pull-requests/%s/commits?start=%s";

    private final String authorization;
    private final HttpClient httpClient;
    private final Gson gson;

    public BitbucketCommitDao(String username, String password, Gson gson) {
        this.authorization = new String(Base64.getEncoder().encode((username + ":" + password).getBytes(StandardCharsets.UTF_8)));
        this.httpClient = HttpClient.newHttpClient();
        this.gson = gson;
    }

    @Override
    public Stream<Commit> getCommits(PullRequestInfo pullRequestInfo) {
        String repository = pullRequestInfo.repository().toString().toLowerCase();

        try {
            BatchResponse firstBatch = getBatch(repository, pullRequestInfo.id(), 0);

            if (firstBatch.values().isEmpty()) {
                return Stream.empty();
            }

            // 50/50 chance that this works
            int[] index = new int[]{1};
            BatchResponse[] responses = new BatchResponse[]{firstBatch};
            return Stream.iterate(firstBatch.values().get(0).parents(),
                            pre -> responses[0].values().size() >= index[0] || !responses[0].isLastPage(),
                            pre -> {
                                if (responses[0].values().size() < index[0]) {
                                    try {
                                        responses[0] = getBatch(repository, pullRequestInfo.id(), responses[0].nextPageStart);
                                        index[0] = 0;
                                    } catch (IOException | InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }

                                    CommitValue value = responses[0].values().get(index[0]);

                                    index[0]++;

                                    return value.parents();
                                }
                                return null;
                            })
                    .map(parent -> parent.stream().map(CommitParent::id).toList())
                    .map(Commit::new);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private BatchResponse getBatch(String repository, int prId, int start) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(HttpRequest
                .newBuilder(URI.create(String.format(PULL_REQUEST_COMMITS, repository, prId, start)))
                .GET()
                .header("Authorization", "Basic " + authorization)
                .build(), HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException(response.toString());
        }

        return gson.fromJson(response.body(), BatchResponse.class);
    }

    public record CommitParent(String id) {
    }

    public record CommitValue(List<CommitParent> parents) {
    }

    public record BatchResponse(boolean isLastPage, int nextPageStart, List<CommitValue> values) {
    }
}
