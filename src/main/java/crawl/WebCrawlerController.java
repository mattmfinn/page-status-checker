package crawl;

import com.goikosoft.crawler4j.crawler.CrawlConfig;
import com.goikosoft.crawler4j.crawler.CrawlController;
import com.goikosoft.crawler4j.fetcher.PageFetcher;
import com.goikosoft.crawler4j.robotstxt.RobotstxtConfig;
import com.goikosoft.crawler4j.robotstxt.RobotstxtServer;
import google.GoogleSheetsClient;

public class WebCrawlerController
{
    public static void main(String args[]) throws Exception
    {
        CrawlConfig config = new CrawlConfig();
        config.setHaltOnError(true);
        config.setMaxPagesToFetch(1);
        config.setMaxDepthOfCrawling(1);
        config.setCrawlStorageFolder("/home/matt/IdeaProjects/page-status-checker/src/main/java/data");

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        controller.addSeed("http://srilanka.travel-culture.com/sri-lanka-gov-links.shtml");
        controller.start(Crawler.class, 1);

        GoogleSheetsClient googleSheetsClient = new GoogleSheetsClient();
        googleSheetsClient.populateNewSpreadsheet(Crawler.pageStatusResultList);
    }
}
