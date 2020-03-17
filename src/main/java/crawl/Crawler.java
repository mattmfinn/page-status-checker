package crawl;

import com.goikosoft.crawler4j.crawler.Page;
import com.goikosoft.crawler4j.crawler.WebCrawler;
import com.goikosoft.crawler4j.url.WebURL;
import data.PageStatusResult;
import google.GoogleSheetsClient;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.regex.Pattern;

public class Crawler extends WebCrawler
{
    // We want web pages, so we will exclude certain file types
    private final static Pattern EXCLUSIONS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp4|zip|gz))$");
    public static GoogleSheetsClient googleSheetsClient = new GoogleSheetsClient();
    public static String sectionName;
    // Let's log and count how many pages we crawl
    public static int numberOfCrawledPages = 0;

    // This is a filter that determines if we should visit a page. As per the EXCLUSIONS variable,
    // we skip certain file types
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url)
    {
        String href = url.getURL().toLowerCase();
        return !EXCLUSIONS.matcher(href).matches() && href.startsWith(url.toString());
    }

    @Override
    protected void handlePageStatusCode(WebURL webURL, int statusCode, String statusDescription)
    {
        // Let's store our data in our data structure
        PageStatusResult pageStatusResult = new PageStatusResult();

        pageStatusResult.sectionName = sectionName;
        pageStatusResult.webURL = webURL.getURL();
        pageStatusResult.referringURL = webURL.getParentUrl();
        pageStatusResult.statusCode = statusCode;
        pageStatusResult.statusDescription = statusDescription;
        numberOfCrawledPages++;

        try
        {
            googleSheetsClient.populateSpreadsheet(pageStatusResult);
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
        } catch (GeneralSecurityException e)
        {
            System.out.println(e.getMessage());
        }
    }
}
