/*
 * Copyright (C) 2016 R&D Solutions Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.hawkcd.agent.services;

import io.hawkcd.agent.services.interfaces.ISecurityService;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.spec.KeySpec;

public class SecurityService implements ISecurityService {

    private static String IV = "IV_VALUE_16_BYTE";
    private static String PASSWORD = "PASSWORD_VALUE";
    private static String SALT = "SALT_VALUE";

    public String encrypt(String raw) {
        try {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
            byte[] encryptedVal = cipher.doFinal(getBytes(raw));
            byte[] encodedValue = Base64.encodeBase64(encryptedVal);
            String result = getString(encodedValue);
            return result;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public String decrypt(String encrypted) {
        try {
            byte[] decodedValue = Base64.decodeBase64(getBytes(encrypted));
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
            byte[] decryptedValue = cipher.doFinal(decodedValue);
            String result = new String(decryptedValue);
            return result;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private String getString(byte[] bytes) throws UnsupportedEncodingException {
        return new String(bytes, "UTF-8");
    }

    private byte[] getBytes(String str) throws UnsupportedEncodingException {
        return str.getBytes("UTF-8");
    }

    private Cipher getCipher(int mode) throws Exception {
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = getBytes(IV);
        c.init(mode, generateKey(), new IvParameterSpec(iv));
        return c;
    }

    private Key generateKey() throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        char[] password = PASSWORD.toCharArray();
        byte[] salt = getBytes(SALT);

        KeySpec spec = new PBEKeySpec(password, salt, 65536, 128);
        SecretKey tmp = factory.generateSecret(spec);
        byte[] encoded = tmp.getEncoded();
        return new SecretKeySpec(encoded, "AES");
    }
}
