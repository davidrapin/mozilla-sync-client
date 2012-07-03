package cx.david.mozSyncClient.client;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import cx.david.mozSyncClient.crypto.NonceGenerator;
import org.mozilla.android.sync.crypto.Cryptographer;
import org.mozilla.android.sync.crypto.Utils;

/**
 * Date: 03/07/12 at 00:40
 * see https://wiki.mozilla.org/Services/Sagrada/ServiceClientFlow
 *
 * @author david
 */
public class MACAuth
{
    private final HttpClient client;
    private final NonceGenerator nonce;

    private String server;
    private int port;

    public MACAuth(String server, int port, String path)
    {
        client = new HttpClient(server, true, port);
        nonce = new NonceGenerator();

        this.server = server;
        this.port = port;
    }

    public String get(String path)
    {

//        1) Obtain the current timestamp in seconds and generate a random nonce string::
//
//           ts = "1330916952"
//           nonce = "6b710bed"
        String timeStamp = System.currentTimeMillis() / 1000 + "";
        String nonce = this.nonce.getNonce(8);
//
//        2) Construct the Normalized Request String by concatenating the following values together with newlines: timestamp, nonce, request method, request URI, server hostname, server port and an empty string for the optional "extension" field::
//
        String normalizedRequestString = timeStamp + "\n" + nonce + "\n"
                                         + "GET" + "\n" + path + "\n" + server + "\n" + port + "\n\n";
//
//        3) Take the HMAC signature of the normalized request string, using the MAC Auth key as the HMAC key and SHA1 as the hash function::
//
//        String mac = Cryptographer..HMAC-SHA1(mac_auth_key, normalized_request_string)
//
//        4) Include the MAC Auth id, timestamp, nonce and hmac signature in the Authorization header of the request::
//
//           GET /user/data
//           Host: example.services.mozilla.com
//           Authorization: MAC id="<mac-auth-id>" ts="1330916952" nonce="6b710bed" mac="<hmac-signature>"
        return null;
    }
}
