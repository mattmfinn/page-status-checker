package crawl;

import com.goikosoft.crawler4j.crawler.Page;
import com.goikosoft.crawler4j.crawler.WebCrawler;
import com.goikosoft.crawler4j.url.WebURL;
import data.PageStatusResult;
import google.GoogleSheetsClient;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Crawler extends WebCrawler
{
    // We want web pages, so we will exclude certain file types
    private final static Pattern EXCLUSIONS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp4|zip|gz))$");
    GoogleSheetsClient googleSheetsClient = new GoogleSheetsClient();

    // This is a filter that determines if we should visit a page. As per the EXCLUSIONS variable,
    // we skip certain file types
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url)
    {
        String href = url.getURL().toLowerCase();
        return !EXCLUSIONS.matcher(href).matches() && href.startsWith(url.toString());
    }

    // For now, let's log and count how many pages we crawl
    private static int numberOfCrawledPages = 0;
    @Override
    protected void handlePageStatusCode(WebURL webURL, int statusCode, String statusDescription)
    {
        // Let's store our data in our data structure
        PageStatusResult pageStatusResult = new PageStatusResult();

        // We want to avoid saving data from URLs that are not relevant to the domain
        // Check both the referring / parent URL as well as the current URL
        if(webURL.getURL().contains(WebCrawlerController.seedPartial)
                && webURL.getParentUrl().contains(WebCrawlerController.seedPartial))
        {
            pageStatusResult.webURL = webURL.getURL();
            pageStatusResult.referringURL = webURL.getParentUrl();
            pageStatusResult.statusCode = statusCode;
            pageStatusResult.statusDescription = statusDescription;
            numberOfCrawledPages++;
        }

        try
        {
            googleSheetsClient.populateNewSpreadsheet(pageStatusResult);
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
        } catch (GeneralSecurityException e)
        {
            System.out.println(e.getMessage());
        }
        System.out.println("***NUMBER OF CRAWLED PAGES: " + Crawler.numberOfCrawledPages + "***");
    }
}
