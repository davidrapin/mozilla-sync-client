package cx.david.mozSyncClient.services;

import cx.david.mozSyncClient.client.HttpClient;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpException;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Date: 03/07/12 at 01:25
 *
 * @author david
 */
public class Auth
{
    // Constants for Account Authentication.
    public static final String AUTH_NODE_DEFAULT = "auth.services.mozilla.com";
    public static final String AUTH_NODE_PATHNAME = "user/";
    public static final String AUTH_NODE_VERSION = "1.0/";
    public static final String AUTH_NODE_SUFFIX = "node/weave";
    public static final String AUTH_SERVER_VERSION = "1.1";
    public static final String AUTH_SERVER_SUFFIX = "info/collections/";

    private HttpClient client;
    private String server = AUTH_NODE_DEFAULT;

    public Auth(String server)
    {
        if (server != null) this.server = server;
        this.client = new HttpClient(this.server, true, 443);
    }

    public String simpleAuth(String username, String password) throws IOException, HttpException
    {
        String usernameHash = Registration.getWeaveID(username);
        return client.getBody(
            makeAuthRequestUrl(usernameHash),
            new BasicHeader("Authorization", makeAuthHeader(usernameHash, password)),
            new BasicHeader("Host", server)
        );
    }

    public String makeAuthHeader(String usernameHash, String password)
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

    public String makeAuthRequestUrl(String usernameHash)
    {
        String uri = "/" + AUTH_SERVER_VERSION + "/" + usernameHash + "/" + AUTH_SERVER_SUFFIX;
        System.out.println("uri:" + uri);
        return uri;
    }
}
