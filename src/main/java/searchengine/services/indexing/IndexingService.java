package searchengine.services.indexing;

import searchengine.config.ConfigSite;
import searchengine.model.Site;
import searchengine.model.Status;

public interface IndexingService {
    void deleteIndexes(ConfigSite site);
    void startIndexing();
    void stopIndexing();
}
