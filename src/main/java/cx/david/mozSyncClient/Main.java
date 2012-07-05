package cx.david.mozSyncClient;

import cx.david.mozSyncClient.services.Storage;

import java.util.List;
import java.util.Map;

/**
 * @author @davidrapin
 */
public class Main
{
    public static void main(String[] args) throws Exception
    {

    }

    private static void testConnect(String username, String password, String base32SyncKey) throws Exception
    {
        Storage sto = new Storage(username, password, base32SyncKey);

        List<Object> bks = sto.getCollection("bookmarks");
        System.out.println(Storage.json_encode(bks));

        Map<String, Object> bk = sto.getCollectionWBO("bookmarks", bks.get(bks.size() - 1) + "");
        System.out.println(Storage.json_encode(bk));
        System.out.println(":)");

//        System.out.println(au.getCollectionsInfo());
//        System.out.println(au.getCollectionCounts());
//        System.out.println(au.getCollectionUsage());

    }


}
