/* Copyright (C) 2025 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class UtilCrypt 
{

    Cipher dcipher;
    // 8-byte Salt
    byte[] salt = {
          (byte) 0xDC, (byte) 0x70, (byte) 0xB4, (byte) 0xC3,
          (byte) 0xC0, (byte) 0x78, (byte) 0x78, (byte) 0x92
      };
    // Iteration count
    int iterationCount = 256;
    // Iteration key length
    int keyLength = 65536;

    public UtilCrypt()
    {

    }

    /**
     * Method to decrypt a string
     * 
     * @param secretKey
     *           Key used to decrypt data
     * @param encryptedText
     *           encrypted text input to decrypt
     * @return Returns plain text after decryption
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.spec.InvalidKeySpecException
     * @throws javax.crypto.NoSuchPaddingException
     * @throws java.security.InvalidKeyException
     * @throws java.security.InvalidAlgorithmParameterException
     * @throws java.io.UnsupportedEncodingException
     * @throws javax.crypto.IllegalBlockSizeException
     * @throws javax.crypto.BadPaddingException
     */
    public String decrypt(String secretKey, String encryptedText)
          throws NoSuchAlgorithmException,
          InvalidKeySpecException,
          NoSuchPaddingException,
          InvalidKeyException,
          InvalidAlgorithmParameterException,
          UnsupportedEncodingException,
          IllegalBlockSizeException,
          BadPaddingException,
          IOException
    {
       KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, iterationCount, keyLength);
       SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
       AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

       // Decryption process;
       dcipher = Cipher.getInstance(key.getAlgorithm());
       dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
       byte[] enc = Base64.getDecoder().decode(encryptedText);
       byte[] utf8 = dcipher.doFinal(enc);
       String charSet = "UTF-8";
       String plainStr = new String(utf8, charSet);
       return plainStr;
    }
 }
