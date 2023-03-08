package searchengine.services.indexing;

import searchengine.config.ConfigSite;
import searchengine.model.Site;
import searchengine.model.Status;
import searchengine.model.repo.PageRepository;
import searchengine.model.repo.SiteRepository;

import java.util.HashMap;
import java.util.concurrent.ForkJoinPool;

public class IndexingThread extends Thread {

    private ConfigSite configSite;
    private SiteRepository siteRepository;
    private PageRepository pageRepository;

    private volatile boolean terminated = false; // we may make it static if we want to stop all threads, not only running

    public IndexingThread(ConfigSite configSite, SiteRepository siteRepository, PageRepository pageRepository) {
        this.configSite = configSite;
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
    }

    @Override
    public void run() {
        if (terminated) { // example of condition if we want to make `terminated` static
            return;
        }

        Site site = new Site();
        site.setUrl(configSite.getUrl());
        site.setName(configSite.getName());
        IndexingSiteData siteData = new IndexingSiteData(new HashMap<>(), this, site);
        new ForkJoinPool().invoke(new Indexing(siteData, configSite.getUrl()));
        if (!site.getStatus().equals(Status.FAILED)) {
            site.setStatus(Status.INDEXED);
        }
        siteRepository.save(site);
        pageRepository.saveAll(siteData.getUrlToPageMap().values());
    }

    public void terminate() {
        terminated = true;
    }

    public boolean isTerminated() {
        return terminated;
    }
}
