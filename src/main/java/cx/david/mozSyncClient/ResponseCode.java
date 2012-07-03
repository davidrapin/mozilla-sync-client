package cx.david.mozSyncClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Date: 02/07/12 at 20:11
 *
 * @author david
 */
public class ResponseCode
{
    private static Map<String, String> texts = new HashMap<String, String>();
    static {
        texts.put("1" , "Illegal method/protocol");
        texts.put("2" , "Incorrect/missing CAPTCHA");
        texts.put("3" , "Invalid/missing username");
        texts.put("4" , "Attempt to overwrite data that can’t be overwritten (such as creating a user ID that already exists)");
        texts.put("5" , "User ID does not match account in path");
        texts.put("6" , "JSON parse failure");
        texts.put("7" , "Missing password field");
        texts.put("8" , "Invalid Weave Basic Object");
        texts.put("9" , "Requested password not strong enough");
        texts.put("10", "Invalid/missing password reset code");
        texts.put("11", "Unsupported function");
        texts.put("12", "No email address on file");
        texts.put("13", "Invalid collection");
        texts.put("14", "(1.1 and up) User over quota");
        texts.put("15", "The email does not match the username");
        texts.put("16", "Client upgrade required");
    }

    public static String getText(String code)
    {
        return texts.get(code);
    }
}
