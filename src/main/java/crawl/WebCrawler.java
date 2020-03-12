package crawl;

import google.GoogleSheetsClient;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class WebCrawler
{
    public static void main(String args[]) throws IOException, GeneralSecurityException
    {
        GoogleSheetsClient googleSheetsClient = new GoogleSheetsClient();
        googleSheetsClient.populateNewSpreadsheet();
    }
}
