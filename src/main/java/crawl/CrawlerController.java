package crawl;

import com.goikosoft.crawler4j.crawler.CrawlConfig;
import com.goikosoft.crawler4j.crawler.CrawlController;
import com.goikosoft.crawler4j.fetcher.PageFetcher;
import com.goikosoft.crawler4j.robotstxt.RobotstxtConfig;
import com.goikosoft.crawler4j.robotstxt.RobotstxtServer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CrawlerController
{
    private static List<String> seedsList;
    private static List<String> sectionNameList;
    private static int crawlDepth;
    private static int crawlers;
    private static CrawlConfig config;
    public static String formUrl;
    // We want a clean decimal, max of 2 places
    private static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public static void main(String args[]) throws Exception
    {
        // Set the args from the command line
        setArgs(args);

        config = new CrawlConfig();
        config.setHaltOnError(false);
        config.setMaxDepthOfCrawling(crawlDepth);
        config.setCrawlStorageFolder("~/storage");

        pageCrawlSeeds(seedsList.size() -1);

        // Only do this if negative results were found & appended so we do not manipulate a null value
        if(PageCrawler.numberOfCrawledPages > 0)
        {
            PageCrawler.googleFormPoster.appendStatistics("Number of pages tested: " + PageCrawler.numberOfCrawledPages,
                    "Pages that passed: " +
                            String.valueOf(PageCrawler.numberOfCrawledPages - PageCrawler.googleFormPoster.numberOfPagesAppended),
                    "Pages that failed: " + PageCrawler.googleFormPoster.numberOfPagesAppended,
                    "Failure Rate: " +
                            String.valueOf((decimalFormat.format(
                                    PageCrawler.googleFormPoster.numberOfPagesAppended / PageCrawler.numberOfCrawledPages
                                            * 100))) + "%");
        }
        else
        {
            PageCrawler.googleFormPoster.appendStatistics("Number of pages tested: " + PageCrawler.numberOfCrawledPages,
                    "Pages that passed: " + PageCrawler.numberOfCrawledPages,
                    "Pages that failed: " + PageCrawler.googleFormPoster.numberOfPagesAppended,
                    "Pass Rate: 100%");
        }

        // Now, crawl the images
        imageCrawlSeeds(seedsList.size() - 1);

        if(ImageCrawler.numberOfCrawledImages > 0)
        {
            ImageCrawler.googleFormPoster.appendStatistics("Number of images tested: " + ImageCrawler.numberOfCrawledImages,
                    "Images that passed: " +
                    String.valueOf(ImageCrawler.numberOfCrawledImages - ImageCrawler.googleFormPoster.numberOfPagesAppended),
                    "Images that failed: " + ImageCrawler.googleFormPoster.numberOfPagesAppended,
                    "Failure Rate: " +
                    String.valueOf((decimalFormat.format(ImageCrawler.googleFormPoster.numberOfPagesAppended
                            / ImageCrawler.numberOfCrawledImages * 100))) + "%");
        }
        else
        {
            ImageCrawler.googleFormPoster.appendStatistics("Number of images tested: " + ImageCrawler.numberOfCrawledImages,
                    "Images that passed: " + ImageCrawler.numberOfCrawledImages,
                    "Images that failed: " + ImageCrawler.googleFormPoster.numberOfPagesAppended,
                    "Pass Rate: 100%");
        }
    }

    private static void pageCrawlSeeds(int index)
    {
        PageCrawler.sectionName = sectionNameList.get(index);

        try
        {
            PageFetcher pageFetcher = new PageFetcher(config);
            RobotstxtServer robotstxtServer = new RobotstxtServer(new RobotstxtConfig(), pageFetcher);

            CrawlController pageController = new CrawlController(config, pageFetcher, robotstxtServer);
            pageController.addSeed(seedsList.get(index));
            pageController.start(ImageCrawler.class, crawlers);
        } catch (Exception e) { System.out.println(e.getMessage()); }

        if(index >= 1) pageCrawlSeeds(index - 1);
    }

    private static void imageCrawlSeeds(int index)
    {
        ImageCrawler.sectionName = sectionNameList.get(index);

        try
        {
            PageFetcher pageFetcher = new PageFetcher(config);
            RobotstxtServer robotstxtServer = new RobotstxtServer(new RobotstxtConfig(), pageFetcher);

            CrawlController imageController = new CrawlController(config, pageFetcher, robotstxtServer);
            imageController.addSeed(seedsList.get(index));
            imageController.start(ImageCrawler.class, crawlers);
        } catch (Exception e) { System.out.println(e.getMessage()); }

        if(index >= 1) imageCrawlSeeds(index - 1);
    }

    // This is unfortunately clunky. We need to pass the parameters via gradle command in this exact order:
    // seed, depth, crawlers, projectName, sectionName
    private static void setArgs(String args[])
    {
        seedsList = splitArgByComma(args[0]);
        crawlDepth = Integer.decode(args[1]);
        crawlers = Integer.decode(args[2]);
        sectionNameList = splitArgByComma(args[3]);
        formUrl = args[4];
        if(seedsList.size() != sectionNameList.size()) throw new IllegalArgumentException("The number of seed URLs" +
                "must match the number of section names! The section name defines the specific domain/subdomain of" +
                "the seed URL, and is used to filter the results.");
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
