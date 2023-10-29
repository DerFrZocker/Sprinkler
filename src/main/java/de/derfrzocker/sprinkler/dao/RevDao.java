package de.derfrzocker.sprinkler.dao;

import de.derfrzocker.sprinkler.data.Repository;
import de.derfrzocker.sprinkler.data.Rev;
import java.util.Set;

public interface RevDao {

    Set<Rev> get(Repository repository, String commitHash);
}
