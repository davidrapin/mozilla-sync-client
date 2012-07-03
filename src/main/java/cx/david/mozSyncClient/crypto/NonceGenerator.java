package cx.david.mozSyncClient.crypto;

import org.mozilla.android.sync.crypto.Utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Date: 03/07/12 at 00:37
 *
 * @author david
 */
public class NonceGenerator
{
    private SecureRandom sr;

    public NonceGenerator()
    {
        try
        {
            sr = SecureRandom.getInstance("SHA1PRNG");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }

    public String getNonce(int length)
    {
        byte[] nonce = new byte[length/2];
        sr.nextBytes(nonce);
        return Utils.byte2hex(nonce).toLowerCase();
    }
}
