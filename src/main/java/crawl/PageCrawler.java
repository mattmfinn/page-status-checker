package crawl;

import com.goikosoft.crawler4j.crawler.Page;
import com.goikosoft.crawler4j.crawler.WebCrawler;
import com.goikosoft.crawler4j.url.WebURL;
import google.GoogleFormPoster;
import model.PageStatusResult;

import java.util.Arrays;
import java.util.regex.Pattern;

public class PageCrawler extends WebCrawler
{
    // We want web pages, so we will exclude certain file types
    private final Pattern EXCLUSIONS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp4|zip|gz))$");
    public static GoogleFormPoster googleFormPoster = new GoogleFormPoster(CrawlerController.formUrl);
    public static String sectionName;
    // Let's log and count how many pages we crawl
    public static float numberOfCrawledPages = 0;

    // This is a filter that determines if we should visit a page. As per the EXCLUSIONS variable,
    // we skip certain file types
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url)
    {
        String href = url.getURL().toLowerCase();
        return !EXCLUSIONS.matcher(href).matches() && href.startsWith(sectionName);
    }

    @Override
    protected void handlePageStatusCode(WebURL webURL, int statusCode, String statusDescription)
    {
        PageStatusResult pageStatusResult = new PageStatusResult();

        // Let's store our data in our data structure, if we have a negative result
        if (statusCode > 399 && !Arrays.asList(401, 403, 405).contains(statusCode))
        {
            pageStatusResult.crawlType = "Page";
            pageStatusResult.sectionName = sectionName;
            pageStatusResult.webURL = webURL.getURL();
            pageStatusResult.referringURL = webURL.getParentUrl();
            pageStatusResult.statusCode = statusCode;
            pageStatusResult.statusCodeDescription = statusDescription;

            googleFormPoster.appendStatusResult(pageStatusResult);
        }
        numberOfCrawledPages++;
    }
}
