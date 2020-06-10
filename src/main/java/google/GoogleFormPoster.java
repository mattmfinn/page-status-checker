package google;

import model.PageStatusResult;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static model.Constant.*;
import static org.apache.http.impl.client.HttpClients.createDefault;

public class GoogleFormPoster
{
    private String formUrl;
    private CloseableHttpClient client;
    public float numberOfPagesAppended = 0;

    public GoogleFormPoster(String formUrl)
    {
        this.formUrl = formUrl;
    }

    public void appendStatusResult(PageStatusResult pageStatusResult)
    {
            client = createDefault();

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(CRAWL_TYPE_COLUMN, pageStatusResult.crawlType));
            params.add(new BasicNameValuePair(SECTION_NAME_COLUMN, pageStatusResult.sectionName));
            params.add(new BasicNameValuePair(REFERRING_URL_COLUMN, pageStatusResult.referringURL));
            params.add(new BasicNameValuePair(WEB_URL_COLUMN, pageStatusResult.webURL));
            params.add(new BasicNameValuePair(STATUS_CODE_COLUMN, String.valueOf(pageStatusResult.statusCode)));
            params.add(new BasicNameValuePair(STATUS_CODE_DESCRIPTION_COLUMN, pageStatusResult.statusCodeDescription));
            post(params);

            closeClient(client);
            numberOfPagesAppended++;
    }

    public void appendStatistics(String totalPages, String passedPages, String failedPages, String passRatePercent)
    {
        client = createDefault();

        // Note that we will have to make the field for status code in the form optional, as here it is blank
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(SECTION_NAME_COLUMN, totalPages));
        params.add(new BasicNameValuePair(REFERRING_URL_COLUMN, passedPages));
        params.add(new BasicNameValuePair(WEB_URL_COLUMN, failedPages));
        params.add(new BasicNameValuePair(STATUS_CODE_COLUMN, passRatePercent));
        post(params);

        closeClient(client);
    }

    private CloseableHttpResponse post(List<NameValuePair> params)
    {
        HttpPost httpPost = new HttpPost(formUrl);
        CloseableHttpResponse response = null;

        try
        {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            response = client.execute(httpPost);
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return response;
    }
    private void closeClient(CloseableHttpClient client)
    {
        if(client != null)
        {
            try
            {
                client.close();
            } catch (IOException e)
            {
                System.out.println(e.getMessage());
            }
        }
    }

}
