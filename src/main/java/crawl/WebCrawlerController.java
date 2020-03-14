package crawl;

import com.goikosoft.crawler4j.crawler.CrawlConfig;
import com.goikosoft.crawler4j.crawler.CrawlController;
import com.goikosoft.crawler4j.fetcher.PageFetcher;
import com.goikosoft.crawler4j.robotstxt.RobotstxtConfig;
import com.goikosoft.crawler4j.robotstxt.RobotstxtServer;

public class WebCrawlerController
{
    private static final String seed = "";
    public static final String seedPartial = "";

    public static void main(String args[]) throws Exception
    {
        CrawlConfig config = new CrawlConfig();
        config.setHaltOnError(false);
        config.setMaxDepthOfCrawling(2);
        config.setCrawlStorageFolder("/home/matt/IdeaProjects/page-status-checker/src/main/java/data");

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        controller.addSeed(seed);
        controller.start(Crawler.class, 100);
    }
}
