package searchengine.services.indexing;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import searchengine.model.Page;
import searchengine.model.Status;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class Indexing extends RecursiveAction {

    private static final String URL_REGEX = "^https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~?&\\/=]*)$";

    private IndexingSiteData siteData;
    private String currentUrl;
    private Set<String> checkedUrls;

    public Indexing(IndexingSiteData siteData, String currentUrl) {
        updateClassVariables(siteData, currentUrl);
        this.checkedUrls = new HashSet<>();
    }

    public Indexing(IndexingSiteData siteData, String currentUrl, Set<String> checkedUrls) {
        updateClassVariables(siteData, currentUrl);
        this.checkedUrls = checkedUrls;
    }

    private void updateClassVariables(IndexingSiteData siteData, String currentUrl) {
        siteData.getSite().setStatus(Status.INDEXING);
        siteData.getSite().setStatusTime(LocalDateTime.now());
        this.siteData = siteData;
        this.currentUrl = currentUrl;
    }

    @Override
    protected void compute() {
        if (siteData.getMasterThread().isTerminated()) {
            setSiteFailedStatus("Indexing is stopped by user");
            return;
        }

        Connection connect = Jsoup.connect(currentUrl);
        Document html = getHtmlPageContent(connect);
        if (html == null) {
            return;
        }

        Elements elements = html.getElementsByTag("a");
        List<String> urlsFromHtml = elements.stream()
                .filter(element -> {
                    String url = element.attr("abs:href");
                    return url.matches(URL_REGEX)
                            && url.contains(siteData.getSite().getUrl())
                            && !siteData.getUrlToPageMap().containsKey(url);
                })
                .map(element -> element.attr("abs:href"))
                .toList();

        if (!urlsFromHtml.isEmpty()) {
            ForkJoinTask.invokeAll(createSubtasks(urlsFromHtml));
        }

        Page page = new Page();
        page.setCode(connect.response().statusCode());
        page.setSite(siteData.getSite());
        page.setContent(html.body().text()); //html.body().html()
        page.setPath(currentUrl);
        siteData.getUrlToPageMap().put(currentUrl, page);
    }

    private List<Indexing> createSubtasks(List<String> urls) {
        List<Indexing> subtasks = new ArrayList<>();
        for (String nextUrl : urls) {
            if (!checkedUrls.contains(nextUrl)) {
                checkedUrls.add(nextUrl);
                subtasks.add(new Indexing(siteData, nextUrl, checkedUrls));
            }
        }
        return subtasks;
    }

    private Document getHtmlPageContent(Connection connection) {
        Document content = null;
        try {
            content = connection.get();
            Thread.sleep(100); // to not been banned on site
        } catch (InterruptedException | IOException ex) {
            // TODO: заполнить логи и throw custom exception, а так же проверить на каких ссылках вылетает, чтобы добавить в regex
            setSiteFailedStatus(ex.getMessage());
        }
        return content;
    }

    private void setSiteFailedStatus(String errorMessage) {
        siteData.getSite().setStatus(Status.FAILED);
        siteData.getSite().setStatusTime(LocalDateTime.now());
        siteData.getSite().setLastError(errorMessage);
    }

}
