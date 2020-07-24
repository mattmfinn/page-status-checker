package crawl;

import com.goikosoft.crawler4j.crawler.CrawlConfig;
import com.goikosoft.crawler4j.crawler.CrawlController;
import com.goikosoft.crawler4j.crawler.authentication.AuthInfo;
import com.goikosoft.crawler4j.crawler.authentication.BasicAuthInfo;
import com.goikosoft.crawler4j.fetcher.PageFetcher;
import com.goikosoft.crawler4j.robotstxt.RobotstxtConfig;
import com.goikosoft.crawler4j.robotstxt.RobotstxtServer;
import google.GoogleFormPoster;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class WebCrawlerController
{
    private static List<String> seedsList;
    private static List<String> sectionNameList;
    private static int crawlDepth;
    private static int crawlers;
    private static String isBasicAuth;
    private static String basicAuthUser;
    private static String basicAuthPass;
    public static String formUrl;
    // We want a clean decimal, max of 2 places
    private static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public static void main(String args[]) throws Exception
    {
        // Set the args from the command line
        setArgs(args);

        CrawlConfig config = new CrawlConfig();
        config.setHaltOnError(false);
        config.setMaxDepthOfCrawling(crawlDepth);
        config.setCrawlStorageFolder("~/storage");

        //Basic Auth implementation
        if(isBasicAuth.toLowerCase().equals("true"))
        {
            AuthInfo basicAuth = new BasicAuthInfo(basicAuthUser, basicAuthPass, seedsList.get(0));
            config.addAuthInfo(basicAuth);
        }

        for(int i = 0; i < seedsList.size(); i++)
        {
            Crawler.sectionName = sectionNameList.get(i);

            /*
                Connection errors if these are not 'refreshed' before the next run
                We need to keep the section name accurate, so we are iterating over each pair
                Of seed <=> sectionName pairs, requiring a new call to the controller.start() method
             */

            PageFetcher pageFetcher = new PageFetcher(config);
            RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
            RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);

            // Start the first in the series of crawls, as specified in the params through Gradle
            CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
            controller.addSeed(seedsList.get(i));
            controller.start(Crawler.class, crawlers);
        }

        // Only do this if negative results were found & appended so we do not manipulate a null value
        if(Crawler.numberOfCrawledPages > 0)
        {
            Crawler.googleFormPoster.appendStatistics("Number of pages tested: " + Crawler.numberOfCrawledPages,
                    "Pages that passed: " +
                            String.valueOf(Crawler.numberOfCrawledPages - Crawler.googleFormPoster.numberOfPagesAppended),
                    "Pages that failed: " + Crawler.googleFormPoster.numberOfPagesAppended,
                    "Failure Rate: " +
                            String.valueOf((decimalFormat.format(
                                    Crawler.googleFormPoster.numberOfPagesAppended / Crawler.numberOfCrawledPages
                                            * 100))) + "%");
        }
        else
        {
            Crawler.googleFormPoster.appendStatistics("Number of pages tested: " + Crawler.numberOfCrawledPages,
                    "Pages that passed: " + Crawler.numberOfCrawledPages,
                    "Pages that failed: " + Crawler.googleFormPoster.numberOfPagesAppended,
                    "Pass Rate: 100%");
        }
    }

    // This is unfortunately clunky. We need to pass the parameters via gradle command in this exact order:
    // seed, depth, crawlers, projectName, sectionName
    private static void setArgs(String args[])
    {
        seedsList = splitArgByComma(args[0]);
        crawlDepth = Integer.decode(args[1]);
        crawlers = Integer.decode(args[2]);
        sectionNameList = splitArgByComma(args[3]);
        isBasicAuth = args[4];
        basicAuthUser = args[5];
        basicAuthPass = args[6];
        formUrl = args[7];
    }

    // We will pass a parameter that is a comma separated string of URL seeds. We will need to split by comma.
    // This includes, in the same order, section names
    private static ArrayList splitArgByComma(String arg)
    {
        ArrayList<String> argList = new ArrayList<>();
        for(String s : arg.split(","))
        {
            argList.add(s);
        }
        return argList;
    }
}
