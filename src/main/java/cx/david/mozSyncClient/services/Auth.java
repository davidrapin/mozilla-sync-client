package cx.david.mozSyncClient.services;

import cx.david.mozSyncClient.client.HttpClient;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * http://docs.services.mozilla.com/storage/apis-1.1.html
 * Date: 03/07/12 at 01:25
 *
 * @author david
 */
public class Auth
{
    // Constants for Account Authentication.
    public static final String NODE_DEFAULT = "auth.services.mozilla.com";
    public static final String NODE_VERSION = "1.1";
//    public static final String AUTH_SERVER_SUFFIX = "storage/";

    private final HttpClient client;
    private final String usernameHash;
    private final String password;
    private String server = NODE_DEFAULT;

    public Auth(String server, String username, String password)
    {
        this.usernameHash = Registration.getWeaveID(username);
        this.password = password;
        if (server != null) this.server = server;
        this.client = new HttpClient(this.server, true, 443);
    }

    public String getCollectionsInfo() throws IOException, HttpException
    {
        return get("info/collections", false);
    }

    public String getCollectionUsage() throws IOException, HttpException
    {
        return get("info/collection_usage", false);
    }

    public String getCollectionCounts() throws IOException, HttpException
    {
        return get("info/collection_counts", false);
    }

    public String getCollection(String collection) throws IOException, HttpException
    {
        return get("storage/" + collection, false);
    }

    public String getCollectionWBO(String collection, String wboId)
    {
        return get("storage/" + collection + "/" + wboId, false);
    }

    protected String get(String path, boolean showHeaders)
    {
        try
        {
            HttpResponse r = client.get(
                makeAuthRequestUrl(usernameHash, path),
                new BasicHeader("Authorization", makeAuthHeader(usernameHash, password)),
                new BasicHeader("Host", server)
            );
            return (showHeaders ? HttpClient.readResponseHeaders(r) + "\n" : "") + HttpClient.readResponseBody(r);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private String makeAuthHeader(String usernameHash, String password)
    {
        try
        {
            return "Basic " + Base64.encodeBase64String((usernameHash + ":" + password).getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }

    private String makeAuthRequestUrl(String usernameHash, String path)
    {
        return "/" + NODE_VERSION + "/" + usernameHash + "/" + path;
    }

}
