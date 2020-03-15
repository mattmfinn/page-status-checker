package crawl;

import com.goikosoft.crawler4j.crawler.CrawlConfig;
import com.goikosoft.crawler4j.crawler.CrawlController;
import com.goikosoft.crawler4j.fetcher.PageFetcher;
import com.goikosoft.crawler4j.robotstxt.RobotstxtConfig;
import com.goikosoft.crawler4j.robotstxt.RobotstxtServer;
import google.GoogleSheetsClient;

import java.util.ArrayList;
import java.util.List;

public class WebCrawlerController
{
    private static List<String> seedsList;
    private static List<String> sectionNameList;
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

        for(int i = 0; i < seedsList.size(); i++)
        {
            Crawler.sectionName = sectionNameList.get(i);
            controller.addSeed(seedsList.get(i));
            controller.start(Crawler.class, crawlers);
        }

    }

    // This is unfortunately clunky. We need to pass the parameters via gradle command in this exact order:
    // seed, depth, crawlers, projectName, sectionName
    private static void setArgs(String args[])
    {
        seedsList = splitSeeds(args[0]);
        crawlDepth = Integer.decode(args[1]);
        crawlers = Integer.decode(args[2]);
        GoogleSheetsClient.setSheetNamePrefix(args[3]);
        sectionNameList = splitSectionNames(args[4]);
    }

    // We will pass a parameter that is a comma separated string of URL seeds. We will need to split by comma.
    // This includes, in the same order, section names
    private static ArrayList splitSeeds(String seeds)
    {
        ArrayList<String> seedsList = new ArrayList<>();
        for(String s : seeds.split(","))
        {
            seedsList.add(s);
        }
        return seedsList;
    }

    private static ArrayList splitSectionNames(String sectionNames)
    {
        ArrayList<String> sectionsList = new ArrayList<>();
        for(String s : sectionNames.split(","))
        {
            sectionsList.add(s);
        }
        return sectionsList;
    }
}
