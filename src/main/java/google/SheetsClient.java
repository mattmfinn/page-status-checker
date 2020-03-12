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
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SheetsClient
{
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final File DATA_STORE_DIR = new File(System.getProperty("user.home"),
            ".credentials/sheets.googleapis.com-crawler");
    private final static String SECRETS_LOCATION = "./src/main/resources/secrets.json";
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String APPLICATON_NAME = "Page Status Checker";
    private static final String sheetNamePrefix = "Projectname_";
    private static final String sheetNamePostfix = "_Page_Status_Results";

    public void populateNewSpreadsheet() throws IOException, GeneralSecurityException
    {
        createNewSheet();
    }

    /**
     * Creates an authorized Credentials object
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException
    {
        // Load secrets file for authorization
        InputStream inputStream = SheetsClient.class.getResourceAsStream(SECRETS_LOCATION);
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
        Sheets service = createService();
        Spreadsheet spreadsheet = new Spreadsheet().setProperties(
                new SpreadsheetProperties().setTitle(createSheetName()));
        spreadsheet = service.spreadsheets().create(spreadsheet)
                .setFields("spreadsheetId")
                .execute();
        return spreadsheet.getSpreadsheetId();
    }

    private static String createSheetName()
    {
        Format format = new SimpleDateFormat("MM/dd/yy");
        return sheetNamePrefix + format.format(new Date()) + sheetNamePostfix;
    }
}
