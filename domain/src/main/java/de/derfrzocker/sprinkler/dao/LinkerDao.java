package de.derfrzocker.sprinkler.dao;

import de.derfrzocker.sprinkler.data.PullRequestInfo;
import de.derfrzocker.sprinkler.data.PullRequestLink;

public interface LinkerDao extends ReadingDao<PullRequestInfo, PullRequestLink>, WritingDao<PullRequestInfo, PullRequestLink> {

}
