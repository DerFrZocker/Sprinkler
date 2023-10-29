package de.derfrzocker.sprinkler.dao;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.derfrzocker.sprinkler.data.Repository;
import de.derfrzocker.sprinkler.data.Rev;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HttpRevDao implements RevDao {

    private static final URI VERSIONS = URI.create("https://hub.spigotmc.org/versions/");

    // Cache revs, there are not many of them
    private final Map<Integer, Rev> revs = new ConcurrentHashMap<>();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private NavigableSet<Integer> revsIndexes;
    private long lastUpdated = -1;

    @Override
    public Set<Rev> get(Repository repository, String commitHash) {
        Set<Rev> result = new LinkedHashSet<>();

        try {
            // Always get current refs index, since it will probably change in the time PR are done.
            NavigableSet<Integer> refsIndex = getRevs();

            for (Integer index : refsIndex.descendingSet()) {
                Rev rev = getRev(index);

                if (rev.hashes().get(repository).equals(commitHash)) {
                    result.add(rev);
                } else {
                    // Search as long as we have a match, or we don't have any
                    if (!result.isEmpty()) {
                        return result;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private Rev getRev(int number) {
        return revs.computeIfAbsent(number, this::loadRev);
    }

    private Rev loadRev(int number) {
        Map<Repository, String> revMap = new HashMap<>();

        try {
            HttpResponse<String> response = httpClient.send(HttpRequest
                            .newBuilder(VERSIONS.resolve(number + ".json"))
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.ofString());

            JsonElement element = JsonParser.parseString(response.body());
            JsonObject refs = element.getAsJsonObject().getAsJsonObject("refs");

            for (Repository repository : Repository.values()) {
                String hash = refs.getAsJsonPrimitive(repository.getFancyName()).getAsString();
                revMap.put(repository, hash);
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return new Rev(number, revMap);
    }

    private NavigableSet<Integer> getRevs() throws IOException {
        // We don't care about concurrency, since in the worst case, we load the same data twice
        if (revsIndexes != null && (System.currentTimeMillis() - lastUpdated < 5000)) {
            return revsIndexes;
        }

        Document document = Jsoup.connect(VERSIONS.toString()).get();

        NavigableSet<Integer> refs = new TreeSet<>();
        Elements elements = document.getElementsByAttribute("href");
        for (Element element : elements) {
            String attr = element.attr("href");
            if (!attr.endsWith(".json")) {
                continue;
            }

            String stringNumber = attr.substring(0, attr.length() - 5);
            try {
                int number = Integer.parseInt(stringNumber);
                refs.add(number);
            } catch (IllegalArgumentException ignore) {
                // We ignore this since, it is probably the dynamic version e.g. 1.20.2, latest etc.
                // We also ignore versions with characters suffix e.g. 849-a, since there are not common and if they make problems, we can deal with them on a case by case basis.
            }
        }

        revsIndexes = refs;
        lastUpdated = System.currentTimeMillis();

        return refs;
    }

}
