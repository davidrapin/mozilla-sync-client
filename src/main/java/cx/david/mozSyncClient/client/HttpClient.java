package cx.david.mozSyncClient.client;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: 03/07/12 at 00:07
 *
 * @author david
 */
public class HttpClient
{
    private final org.apache.http.client.HttpClient client;
    private final HttpHost host;
//    private final String server;

    public HttpClient(String server, boolean https, int port)
    {
//        this.server = server;
        this.client = new DefaultHttpClient();
        this.host = new HttpHost(server, port, https ? "https" : "http");
    }

    private HttpResponse get(String uri) throws IOException, HttpException
    {
        return get(uri, new Header[]{});
    }

    private HttpResponse get(String uri, Header... headers) throws IOException, HttpException
    {
        try
        {
            HttpGet get = new HttpGet(uri);
            if (headers != null && headers.length > 0) {
                for (Header h : headers) get.setHeader(h);
            }
            System.out.println("-->" + get.getRequestLine().toString());
            return client.execute(host, get);
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException(e);
        }
    }

    public String getBody(String uri, BasicHeader... headers) throws IOException, HttpException
    {
        return readResponseBody(get(uri, headers));
    }

    public String getBody(String uri) throws IOException, HttpException
    {
        return readResponseBody(get(uri));
    }




    public static String readResponseHeaders(HttpResponse response)
    {
        String s = response.getStatusLine() + "";
        Header[] hs = response.getAllHeaders();
        for (Header h : hs)
        {
            s += "\n'" + h.getName() + "':'" + h.getValue() + "'";
        }
        return s;
    }

    public static String readResponseBody(HttpResponse response) throws IOException
    {
        final char[] buffer = new char[0x10000];
        StringBuilder out = new StringBuilder();
        String contentType = response.getEntity().getContentType().getValue();
        String charset = getCharset(contentType);
        InputStreamReader streamReader = new InputStreamReader(response.getEntity().getContent(), charset);
        int read;
        do
        {
            read = streamReader.read(buffer, 0, buffer.length);
            if (read > 0)
            {
                out.append(buffer, 0, read);
            }
        }
        while (read >= 0);
        return out.toString();
    }

    public static final Pattern charset = Pattern.compile("^.*charset=(.+)$");

    public static String getCharset(String contentType)
    {
        Matcher m = charset.matcher(contentType);
        if (m.matches() && m.groupCount() == 1)
        {
            return m.group(1);
        }
        else
        {
            return "UTF-8";
        }
    }
}
