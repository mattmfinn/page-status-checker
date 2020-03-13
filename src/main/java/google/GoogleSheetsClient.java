package google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import data.PageStatusResult;

import java.io.*;
import java.security.GeneralSecurityException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

public class GoogleSheetsClient
{
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final File DATA_STORE_DIR = new File(System.getProperty("user.home"),
            ".credentials/sheets.googleapis.com-crawler");
    private final static String SECRETS_LOCATION = "/home/matt/IdeaProjects/page-status-checker/secrets.json";
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String APPLICATON_NAME = "Page Status Checker";
    private static final String sheetNamePrefix = "Projectname_";
    private static final String sheetNamePostfix = "_Page_Status_Results";
    private static Sheets service;
    private static final String valueInputOption = "RAW";
    private static final String range = "Sheet1";

    public void populateNewSpreadsheet(List<PageStatusResult> pageStatusResultList) throws IOException, GeneralSecurityException
    {
        // Capture the ID, and set the header columns
        final String SPREADSHEET_ID = createNewSheet();

        // Set the headers of the spreadsheet based on the data object's field names
        ValueRange headers = new ValueRange()
                .setValues(Arrays.asList(
                        Arrays.asList("URL", "Status Code", "Status Description")));
        appendData(headers, SPREADSHEET_ID, range);

        // Iterate over the results, and append to the spreadsheet
        for (PageStatusResult p : pageStatusResultList)
        {
            if(p.statusCode > 399)
            {
                ValueRange result = new ValueRange()
                        .setValues(Arrays.asList(
                                Arrays.asList(p.webURL, p.statusCode, p.statusDescription)));
                appendData(result, SPREADSHEET_ID, range);
            }
        }
    }

    private static void appendData(ValueRange values, String SPREADSHEET_ID, String range) throws IOException
    {
        // Set the body to a ValueRange and write to the columns
        AppendValuesResponse body = service.spreadsheets().values()
                .append(SPREADSHEET_ID, range, values)
                .setValueInputOption(valueInputOption)
                .setInsertDataOption("INSERT_ROWS")
                .execute();
    }

    /**
     * Creates an authorized Credentials object
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException
    {
        // Load secrets file for authorization
        File file = new File(SECRETS_LOCATION);
        InputStream inputStream = new FileInputStream(file);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(inputStream));

        // Build and trigger authorization request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(DATA_STORE_DIR))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private static Sheets createService() throws IOException, GeneralSecurityException
    {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATON_NAME)
                .build();
    }

    // Create a new, blank spreadsheet
    private static String createNewSheet() throws IOException, GeneralSecurityException
    {
        // Create the service to allow for creating the sheet via API
        service = createService();
        Spreadsheet spreadsheet = new Spreadsheet().setProperties(
                new SpreadsheetProperties().setTitle(createSheetName()));
        spreadsheet = service.spreadsheets().create(spreadsheet)
                .setFields("spreadsheetId")
                .execute();
        return spreadsheet.getSpreadsheetId();
    }

    private static String createSheetName()
    {
        Format format = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        return sheetNamePrefix + format.format(new Date()) + sheetNamePostfix;
    }
}
