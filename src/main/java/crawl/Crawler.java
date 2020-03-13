package crawl;

import com.goikosoft.crawler4j.crawler.Page;
import com.goikosoft.crawler4j.crawler.WebCrawler;
import com.goikosoft.crawler4j.url.WebURL;
import data.PageStatusResult;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Crawler extends WebCrawler
{
    // We need to collect a list of the status code results when that method runs
    public static List<PageStatusResult> pageStatusResultList = new ArrayList<>();
    // We want web pages, so we will exclude certain file types
    private final static Pattern EXCLUSIONS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp4|zip|gz))$");

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url)
    {
        String href = url.getURL().toLowerCase();
        return !EXCLUSIONS.matcher(href).matches() && href.startsWith(url.toString());
    }

    @Override
    protected void handlePageStatusCode(WebURL webURL, int statusCode, String statusDescription)
    {
        PageStatusResult pageStatusResult = new PageStatusResult();
        pageStatusResult.webURL = webURL.getURL();
        pageStatusResult.statusCode = statusCode;
        pageStatusResult.statusDescription = statusDescription;
        pageStatusResultList.add(pageStatusResult);
    }
}
