package crawl;

import com.goikosoft.crawler4j.crawler.Page;
import com.goikosoft.crawler4j.crawler.WebCrawler;
import com.goikosoft.crawler4j.url.WebURL;
import google.GoogleFormPoster;
import model.PageStatusResult;

import java.util.Arrays;
import java.util.regex.Pattern;

public class ImageCrawler extends WebCrawler
{
    // We want web pages, so we will exclude certain file types
    private final Pattern EXCLUSIONS = Pattern.compile(".*(\\.(css|js|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf" +
            "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
    private final Pattern IMAGES = Pattern.compile(".*(\\\\.(bmp|gif|jpe?g|png|tiff?))$");

    public static GoogleFormPoster googleFormPoster = new GoogleFormPoster(CrawlerController.formUrl);
    public static String sectionName;
    // Let's log and count how many pages we crawl
    public static float numberOfCrawledImages = 0;

    // This is a filter that determines if we should visit a page. As per the EXCLUSIONS variable,
    // we skip certain file types but accept image file types, if they are in the domain we desire
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url)
    {
        String href = url.getURL().toLowerCase();
        if(EXCLUSIONS.matcher(href).matches())
        {
            return false;
        }
        else if(IMAGES.matcher(href).matches() && href.contains(sectionName))
        {
            return true;
        }
        return false;
    }

    @Override
    protected void handlePageStatusCode(WebURL webURL, int statusCode, String statusDescription)
    {
        PageStatusResult pageStatusResult = new PageStatusResult();

        // Let's store our data in our data structure, if we have a negative result
        if (statusCode > 399 && !Arrays.asList(401, 403, 405).contains(statusCode))
        {
            pageStatusResult.crawlType = "Image";
            pageStatusResult.sectionName = sectionName;
            pageStatusResult.webURL = webURL.getURL();
            pageStatusResult.referringURL = webURL.getParentUrl();
            pageStatusResult.statusCode = statusCode;
            pageStatusResult.statusCodeDescription = statusDescription;

            googleFormPoster.appendStatusResult(pageStatusResult);
        }
        numberOfCrawledImages++;
    }
}
