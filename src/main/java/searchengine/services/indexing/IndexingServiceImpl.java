package searchengine.services.indexing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.ConfigSite;
import searchengine.config.ConfigSitesList;
import searchengine.model.Site;
import searchengine.model.repo.PageRepository;
import searchengine.model.repo.SiteRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class IndexingServiceImpl implements IndexingService{

    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private ConfigSitesList configSitesList;

    private static Set<IndexingThread> indexingThreads = new HashSet<>();

    @Override
    public void deleteIndexes(ConfigSite configSite) {
        Optional<Site> site = siteRepository.findByUrl(configSite.getUrl());
        if (site.isEmpty()) {
            return;
        }
        pageRepository.deleteAllBySiteId(site.get().getId());
        siteRepository.delete(site.get());
    }

    @Override
    public void startIndexing() {
        if (configSitesList == null || configSitesList.getConfigSites() == null) {
            return;
            // TODO: вернуть ошибку пользователю
        }

        for (ConfigSite configSite : configSitesList.getConfigSites()) {
            deleteIndexes(configSite);
            IndexingThread thread = new IndexingThread(configSite, siteRepository, pageRepository);
            thread.start();
            indexingThreads.add(thread);
        }
    }

    @Override
    public void stopIndexing() {
        if (indexingThreads.isEmpty()) {
            // сказать что нет в индексации сайтов
            return;
        }

        for (IndexingThread thread : indexingThreads) {
            thread.terminate();
        }
    }
}
