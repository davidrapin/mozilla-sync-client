/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Android Sync Client.
 *
 * The Initial Developer of the Original Code is
 * the Mozilla Foundation.
 * Portions created by the Initial Developer are Copyright (C) 2011
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * Jason Voll
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package org.mozilla.android.sync.crypto;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


/*
 * A standards-compliant implementation of RFC 5869
 * for HMAC-based Key Derivation Function.
 * HMAC uses HMAC SHA256 standard.
 *
 * see http://tools.ietf.org/html/rfc5869
 */
public class HKDF
{

    /**
     * Used for conversion in cases in which you *know* the encoding exists.
     *
     * @param input the input string
     * @return the input string's bytes using UTF-8
     */
    public static byte[] bytes(String input)
    {
        try
        {
            return input.getBytes("UTF-8");
        }
        catch (java.io.UnsupportedEncodingException e)
        {
            return null;
        }
    }

    public static final int BLOCK_SIZE = 256 / 8;
    public static final byte[] HMAC_INPUT = bytes("Sync-AES_256_CBC-HMAC256");

    /**
     * Step 1 of RFC 5869
     * Get sha256-HMAC Bytes
     *
     * @param salt message
     * @param ikm  input keyring material
     * @return PRK (Pseudo-Random Key)
     * @throws java.security.InvalidKeyException
     *          if the key is an invalid HMAC-SHA256 key.
     * @throws CryptoException
     *          if HMAC-SHA256 was not found on the system
     */
    public static byte[] hkdfExtract(byte[] salt, byte[] ikm)
        throws InvalidKeyException, CryptoException
    {
        return digestBytes(ikm, makeHMACHasher(salt));
    }

    /**
     * Step 2 of RFC 5869.
     *
     * @param prk  Pseudo-Random Key (from step 1)
     * @param info info
     * @param len  length
     * @return OKM (output keyring material).
     * @throws java.security.InvalidKeyException
     *          if the key is an invalid HMAC-SHA256 key.
     * @throws CryptoException
     *          if HMAC-SHA256 was not found on the system
     */
    public static byte[] hkdfExpand(byte[] prk, byte[] info, int len)
        throws InvalidKeyException, CryptoException
    {

        Mac hmacHasher = makeHMACHasher(prk);
        byte[] t = {};
        byte[] tn = {};

        int iterations = (int) Math.ceil(((double) len) / ((double) BLOCK_SIZE));
        for (int i = 0; i < iterations; i++)
        {
            tn = digestBytes(
                Utils.concatAll(tn, info, Utils.hex2Byte(Integer.toHexString(i + 1))),
                hmacHasher
            );
            t = Utils.concatAll(t, tn);
        }

        return Arrays.copyOfRange(t, 0, len);
    }

    /**
     * Make HMAC key
     *
     * @param key The salt
     * @return The HMAC-Key
     */
    public static Key makeHMACKey(byte[] key)
    {
        if (key.length == 0)
        {
            key = new byte[BLOCK_SIZE];
        }
        return new SecretKeySpec(key, "HmacSHA256");
    }

    /**
     * Make an HMAC hasher
     *
     * @param key A HMAC-Key
     * @return An HMAC Hasher
     * @throws java.security.InvalidKeyException
     *          if the key is an invalid HMAC-SHA256 key.
     * @throws CryptoException
     *          if HMAC-SHA256 was not found on the system
     */
    public static Mac makeHMACHasher(byte[] key)
        throws InvalidKeyException, CryptoException
    {
        try
        {
            Mac hmacHasher = Mac.getInstance("hmacSHA256");
            hmacHasher.init(makeHMACKey(key));
            return hmacHasher;
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new CryptoException(e);
        }
    }

    /**
     * Hash bytes with given hasher
     *
     * @param message message to hash
     * @param hasher  HMAC hasher
     * @return hashed byte[].
     */
    public static byte[] digestBytes(byte[] message, Mac hasher)
    {
        hasher.update(message);
        byte[] ret = hasher.doFinal();
        hasher.reset();
        return ret;
    }
}
