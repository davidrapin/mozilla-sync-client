package cx.david.mozSyncClient.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpException;
import org.mozilla.android.sync.crypto.*;

import java.io.IOException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.util.List;
import java.util.Map;

/**
 * Date: 03/07/12 at 00:31
 *
 * @author david
 */
public class Storage
{
    public final Auth auth;
    private final String username;
    private final String base32SyncKey;

    private KeyBundle bulkKeyBundle;
    private Map<String, Object> metaGlobal;

    public Storage(String username, String password, String base32SyncKey) throws Exception
    {
        this.username = username;
        this.base32SyncKey = base32SyncKey;

        Registration rs = new Registration("auth.services.mozilla.com");
        if (!rs.userExists(username))
        {
            throw new RuntimeException("user '" + username + "' does not seem to exist.");
        }

        URL storageNode = new URL(rs.getWeaveNode(username));
        //System.out.println(username + " weave node : " + storageNode.getHost());
        auth = new Auth(storageNode.getHost(), username, password);

        metaGlobal = getAndDecryptCollectionWBO("meta", "global", null);
    }

    public Map<String, Object> getCollectionWBO(String collection, String wboId) throws IOException, CryptoException, InvalidKeyException
    {
        if (bulkKeyBundle == null) fetchBulkKeyBundle();

        return getAndDecryptCollectionWBO(collection, wboId, bulkKeyBundle);
    }

    private Map<String, Object> getAndDecryptCollectionWBO(String collection, String wboId, KeyBundle keyBundle) throws IOException, CryptoException
    {
        String wboJsonEncrypted = auth.getCollectionWBO(collection, wboId);
        Map<String, Object> wboEncrypted = json_decode_wbo(wboJsonEncrypted);
        if (keyBundle == null) return wboEncrypted;

        Map<String, Object> wboPayload = (Map<String, Object>) wboEncrypted.get("payload");
        CryptoInfo wboPayloadInfo = new CryptoInfo(
            Utils.decodeBase64((String) wboPayload.get("ciphertext")),
            Utils.decodeBase64((String) wboPayload.get("IV")),
            Utils.hex2Byte((String) wboPayload.get("hmac")),
            keyBundle
        );

        String wboJsonPayload = new String(Cryptographer.decrypt(wboPayloadInfo), "UTF-8");
        return json_decode_map(wboJsonPayload);
    }

    // http://docs.services.mozilla.com/sync/storageformat5.html#format
    private void fetchBulkKeyBundle() throws CryptoException, InvalidKeyException, IOException
    {
        KeyBundle syncKeyBundle = new KeyBundle(username, base32SyncKey);
        Map<String, Object> bkb = getAndDecryptCollectionWBO("crypto", "keys", syncKeyBundle);

        assert "crypto".equals(bkb.get("collection"));
        assert "keys".equals(bkb.get("id"));

        List<String> keys = (List<String>) bkb.get("default");
        bulkKeyBundle = KeyBundle.decodeKeyStrings(keys.get(0), keys.get(1));
    }

    public List<Object> getCollection(String collection) throws IOException, HttpException
    {
        return json_decode_list(auth.getCollection(collection));
    }

    public Map<String, Object> getMetaGlobal()
    {
        return metaGlobal;
    }

    public static String json_encode(Object o) throws IOException
    {
        return mapper.writeValueAsString(o);
    }

    private static Map<String, Object> json_decode_wbo(String jsonWBO) throws IOException
    {
        Map<String, Object> wbo = json_decode_map(jsonWBO);
        wbo.put("payload", json_decode_map((String) wbo.get("payload")));
        return wbo;
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<Object> json_decode_list(String json) throws IOException
    {
        return mapper.readValue(json, new TypeReference<List<Object>>() { });
    }

    public static Map<String, Object> json_decode_map(String json) throws IOException
    {
        return mapper.readValue(json, new TypeReference<Map<String, Object>>() { });
    }
}
