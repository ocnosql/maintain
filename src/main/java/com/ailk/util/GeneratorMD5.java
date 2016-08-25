package com.ailk.util;

import com.ailk.oci.ocnosql.common.rowkeygenerator.RowKeyGenerator;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class GeneratorMD5 implements RowKeyGenerator {
    public GeneratorMD5() { }

    @Override
    public String generate(String s, String s1, String[] strings, int[] ints, String s2) {
        return null;
    }

    public static void main(String[] var0) {
       // String var1 = var0[0];
        GeneratorMD5 var2 = new GeneratorMD5();
        System.out.println("201411 before MD5 HASH = " + var2.generate("18376452507"));
        System.out.println("201411 after  MD5 HASH = " + var2.generatePrefix("18376452507"));

    }

    public Object generate(String oriRowKey) {
        return this.generatePrefix(oriRowKey) + oriRowKey;
    }

    public String generateOldPrefix(String var1) {
        String var2 = null;

        try {
            MessageDigest var3 = MessageDigest.getInstance("MD5");
            var3.reset();
            var3.update(var1.getBytes());
            byte[] var4 = var3.digest();
            StringBuffer var5 = new StringBuffer();
            byte[] var6 = var4;
            int var7 = var4.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                byte var9 = var6[var8];
                var5.append(Integer.toHexString(var9 & 255));
            }

            var2 = var5.toString();
            return var2.substring(1, 2) + var2.substring(3, 4) + var2.substring(5, 6);
        } catch (NoSuchAlgorithmException var10) {
            throw new RuntimeException("failed init MD5 instance.", var10);
        }
    }

    @Override
    public String generatePrefix(String var1) {
        if(var1 == null) {
            ;
        }

        char[] var3 = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'b', 'd', 'e', 'f'};
        byte[] var4 = var1.getBytes();
        MessageDigest var5 = null;

        try {
            var5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var12) {
            ;
        }

        var5.update(var4);
        byte[] var6 = var5.digest();
        int var7 = var6.length;
        char[] var8 = new char[var7 * 2];
        int var9 = 0;

        for(int var10 = 0; var10 < var7; ++var10) {
            byte var11 = var6[var10];
            var8[var9++] = var3[var11 >>> 4 & 15];
            var8[var9++] = var3[var11 & 15];
        }

        String var13 = new String(var8);
        return var13.substring(1, 2) + var13.substring(3, 4) + var13.substring(5, 6);
    }
}
