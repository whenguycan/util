package com.kaltsit.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Author wangcy
 * @Date 2021/6/10 17:41
 */
public class MD5Hash {

    public String toHex(byte[] input) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            return bytes2Hex(md.digest(input));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    String bytes2Hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(Integer.toHexString(0xff & b));
        }
        return sb.toString();
    }

}
