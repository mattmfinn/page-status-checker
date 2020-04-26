### Project Description

This is a simple web crawler implementation using Crawler4j. It will check the status of HTTP requests on a site,
recording erroneous status codes in a Google Sheet. It will ignore positive status codes (299 or below), as well as
status codes 401, 403 & 405. These codes are likely due to the crawler not having authentication.

#### The project requires the following command line inputs:
**seed:** The page(s) you wish to start the crawl from. For example, https://example.com/

**crawlDepth:** How deep you want to crawl. A crawl depth of 2, for example, would go from the top level page -> one layer
deeper -> 2nd and last layer deep.

**crawlers:** How many crawlers you wish to instantiate at once. Be careful with this setting as to not place a high load
on the website under test.

**projectName:** This will name the spreadsheet with the project name at the beginning of the name

**sectionName:** If you have multiple seeds, it is more clear to have a section name. If you have multiple seeds, the
order in which you place the section names should match the order in which you place the seeds.

#### Sample gradle command to run the project:

gradle crawl -Pseed=https://example.com/ -PcrawlDepth=1 -Pcrawlers=200 -PprojectName=Example_ -PsectionName=Home

