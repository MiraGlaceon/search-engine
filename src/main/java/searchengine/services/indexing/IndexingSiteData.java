package searchengine.services.indexing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import searchengine.model.Page;
import searchengine.model.Site;

import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
public class IndexingSiteData {
    private Map<String, Page> urlToPageMap;
    private IndexingThread masterThread;
    private Site site;
}
