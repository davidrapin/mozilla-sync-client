package cx.david.mozSyncClient;

import cx.david.mozSyncClient.services.Auth;
import cx.david.mozSyncClient.services.Registration;

import java.net.URL;

/**
 * @author @davidrapin
 */
public class Main
{
    public static void main(String[] args) throws Exception
    {
        //test();
    }

    private static void test(String username, String password) throws Exception
    {
        // USER API
        Registration rs = new Registration("auth.services.mozilla.com");
        System.out.println(username + " exists : " + rs.userExists(username));
        URL node = new URL(rs.getWeaveNode(username));
        System.out.println(username + " weave node : " + node.getHost());
        Auth au = new Auth(node.getHost());
        System.out.println(au.simpleAuth(username, password));


    }





}
