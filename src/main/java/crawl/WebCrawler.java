package crawl;

import google.SheetsClient;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class WebCrawler
{
    public static void main(String args[]) throws IOException, GeneralSecurityException
    {
        SheetsClient sheetsClient = new SheetsClient();
        sheetsClient.populateNewSpreadsheet();
    }
}
