package com.soda.mozsync;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author @davidrapin
 */
public class Main
{
    public static void main(String[] args) throws URISyntaxException, IOException, HttpException, NoSuchAlgorithmException
    {
        syncLogin();
    }

    private static void syncLogin() throws URISyntaxException, HttpException, IOException
    {
        // USER API
        // https://<server name>/<api pathname>/<version>/<username>/<further instruction>
        String version = "1.0";
        String server = "auth.services.mozilla.com";
        String pathName = "user";
        String userName = "david.rapin@gmail.com";
        String uri = "/" + pathName + "/" + version + "/" + getWeaveID(userName);
        System.out.println("uri:" + uri);

        HttpClient c = new DefaultHttpClient();
        HttpHost host = new HttpHost(server, 443, "https");
        HttpGet get = new HttpGet(uri);
        HttpResponse response = c.execute(host, get);

        String out = readResponse(response);
        System.out.println("R:" + response.getStatusLine() + ":" + out);
    }

    private static String readResponse(HttpResponse response) throws IOException
    {
        final char[] buffer = new char[0x10000];
        StringBuilder out = new StringBuilder();
        String contentType = response.getEntity().getContentType().getValue();
        String charset = getCharset(contentType);
        InputStreamReader streamReader = new InputStreamReader(response.getEntity().getContent(), charset);
        int read;
        do {
            read = streamReader.read(buffer, 0, buffer.length);
            if (read > 0) {
                out.append(buffer, 0, read);
            }
        } while (read >= 0);
        return out.toString();
    }

    public static final Pattern charset = Pattern.compile("^.*charset=(.+)$");

    private static String getCharset(String contentType)
    {
        Matcher m = charset.matcher(contentType);
        if (m.matches() && m.groupCount() == 1) {
            return m.group(1);
        } else {
            return "UTF-8";
        }
    }

    public static String getWeaveID(String email) throws UnsupportedEncodingException
    {
        byte[] sha = DigestUtils.sha(email.getBytes("UTF-8"));
        Base32 b32 = new Base32(64, new byte[]{ }, false);
        return b32.encodeToString(sha).toLowerCase();
    }


}
