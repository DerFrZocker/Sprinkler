package de.derfrzocker.sprinkler.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.derfrzocker.sprinkler.data.Commit;
import de.derfrzocker.sprinkler.data.PullRequestInfo;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public class FileCommitDao implements CommitDao {

    private final Gson gson;
    private final File directory;

    public FileCommitDao(Gson gson, File directory) {
        this.gson = gson;
        this.directory = directory;
    }

    @Override
    public Stream<Commit> getCommits(PullRequestInfo pullRequestInfo) {
        System.out.println("Getting commits for " + pullRequestInfo);
        try (FileReader fileReader = new FileReader(new File(directory, String.format("%s-%s.json", pullRequestInfo.repository().getFancyName(), pullRequestInfo.id())))) {
            List<Commit> commits = (List<Commit>) gson.fromJson(fileReader, TypeToken.getParameterized(List.class, Commit.class));

            return commits.stream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
