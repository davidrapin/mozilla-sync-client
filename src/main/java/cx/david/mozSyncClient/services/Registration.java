package cx.david.mozSyncClient.services;

import cx.david.mozSyncClient.client.HttpClient;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Date: 02/07/12 at 23:56
 * see http://docs.services.mozilla.com/reg/apis.html
 *
 * @author david
 */
public class Registration
{
    public static final String REG_SERVER_PATH = "user";
    public static final String REG_SERVER_VERSION = "1.0";
    public static final String REG_SERVER_SUFFIX_NODE = "node/weave";

    private final HttpClient client;

    public Registration(String server)
    {
        this.client = new HttpClient(server, true, 443);
    }

    public boolean userExists(String username) throws Exception
    {
        String uri = makeURI(username, null);
        return "1".equals(client.getBody(uri));
    }

    public String getWeaveNode(String username) throws IOException, HttpException
    {
        String uri = makeURI(username, REG_SERVER_SUFFIX_NODE);
        return client.getBody(uri);
    }

    private String makeURI(String username, String suffix)
    {
        return "/" + REG_SERVER_PATH + "/" + REG_SERVER_VERSION + "/"
               + getWeaveID(username) + (suffix == null ? "" : "/" + suffix);
    }


    public static String getWeaveID(String email)
    {
        try
        {
            byte[] sha = DigestUtils.sha(email.getBytes("UTF-8"));
            Base32 b32 = new Base32(64, new byte[]{}, false);
            return b32.encodeToString(sha).toLowerCase();
        }
        catch (UnsupportedEncodingException e) { throw new RuntimeException(e); }
    }
}
