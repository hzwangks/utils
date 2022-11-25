package com.utils.controller;

import com.utils.util.EncryptionPropertyConfig;
import com.zl.jwzh.yun.Dxyzm;
import org.jasypt.encryption.StringEncryptor;

public class test {
    public static void main(String[] args) {
        StringEncryptor p = new EncryptionPropertyConfig().stringEncryptor();
        String pass = p.encrypt("YyCwy@123456");
        System.out.println(pass);
        System.out.println(p.decrypt(pass));

        Dxyzm ser =new Dxyzm();
        ser.dxyzmyzToMap("******","3f1440a38e12bcc541dca2206f53bbb0");

    }
}
