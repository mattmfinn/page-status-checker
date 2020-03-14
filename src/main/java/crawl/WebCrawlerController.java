package crawl;

import com.goikosoft.crawler4j.crawler.CrawlConfig;
import com.goikosoft.crawler4j.crawler.CrawlController;
import com.goikosoft.crawler4j.fetcher.PageFetcher;
import com.goikosoft.crawler4j.robotstxt.RobotstxtConfig;
import com.goikosoft.crawler4j.robotstxt.RobotstxtServer;
import google.GoogleSheetsClient;

public class WebCrawlerController
{
    private static String seed;
    private static int crawlDepth;
    private static int crawlers;

    public static void main(String args[]) throws Exception
    {
        // Set the args from the command line
        setArgs(args);

        CrawlConfig config = new CrawlConfig();
        config.setHaltOnError(false);
        config.setMaxDepthOfCrawling(crawlDepth);
        config.setCrawlStorageFolder("~/storage");

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        controller.addSeed(seed);
        controller.start(Crawler.class, crawlers);
    }

    // This is unfortunately clunky. We need to pass the parameters via gradle command in this exact order:
    // seed, depth, crawlers, projectName
    private static void setArgs(String args[])
    {
        seed = args[0];
        crawlDepth = Integer.decode(args[1]);
        crawlers = Integer.decode(args[2]);
        GoogleSheetsClient.setSheetNamePrefix(args[3]);
    }
}
