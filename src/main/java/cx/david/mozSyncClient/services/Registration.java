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
    public static final String VERSION = "1.0";

    private final HttpClient client;


    public Registration(String server)
    {
        this.client = new HttpClient(server, true, 443);
    }

    public boolean userExists(String username) throws Exception
    {
        String uri = "/user/" + VERSION + "/" + getWeaveID(username);
        String response = client.getBody(uri);
        return "1".equals(response);
    }

    public String getWeaveNode(String username) throws IOException, HttpException
    {
        String uri = "/user/" + VERSION + "/" + getWeaveID(username) + "/node/weave";
        return client.getBody(uri);
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
