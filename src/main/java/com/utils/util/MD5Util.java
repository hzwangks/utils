package com.utils.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

    public static String encryption(String plainText) {

        String md5 = new String();

        try {

            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(plainText.getBytes());

            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");

            for (int offset = 0; offset < b.length; offset++) {

                i = b[offset];

                if (i < 0)

                    i += 256;

                if (i < 16)

                    buf.append("0");

                buf.append(Integer.toHexString(i));

            }

            md5 = buf.toString();

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();

        }

        return md5;

    }
}
