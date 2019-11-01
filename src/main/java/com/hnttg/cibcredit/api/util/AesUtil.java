package com.hnttg.cibcredit.api.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AesUtil {

    public static final String aes_key_algorithm = "AES";

    public static final String cipher_algorithm = "AES/ECB/PKCS5Padding";

    public void AesAction(String infile, String outfile, String aesKey, String flag) throws Exception {
        String privateKey = aesKey;
        if (flag.equals("1")) {
            encryptHand(new File(infile), new File(outfile), privateKey);
        } else if (flag.equals("2")) {
            decryptHand(new File(infile), new File(outfile), privateKey);
        }
    }

    public static byte[] encrypt(String content, String keyPath) throws NoSuchAlgorithmException {
        SecretKeySpec key = null;
        try {
            key = new SecretKeySpec(keyPath.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            byte[] byteContent = content.getBytes("GBK");
            cipher.init(1, key);
            return cipher.doFinal(byteContent);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decrypt(byte[] content, String keyPath) throws NoSuchAlgorithmException {
        SecretKeySpec key = null;
        try {
            key = new SecretKeySpec(keyPath.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(2, key);
            return cipher.doFinal(content);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字节转换字符串
     * 
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte[] buf) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = String.valueOf('0') + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 字符串转字节
     * 
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * 文件加密
     * 
     * @param file1
     * @param file2
     * @param keyPath
     * @return
     */
    public static String encryptHand(File file1, File file2, String keyPath) {
        String result = "";
        String line = "";

        String encryptResultStr = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file1), "GBK"));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2), "GBK"));
            try {
                while ((line = br.readLine()) != null) {
                    byte[] bt = encrypt(String.valueOf(result) + line, keyPath);
                    encryptResultStr = parseByte2HexStr(bt);
                    bw.write(encryptResultStr);
                    bw.newLine();
                }
                bw.flush();
                bw.close();
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                bw.close();
                br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 文件解密
     * 
     * @param file1
     * @param file2
     * @param keyPath
     */
    public static void decryptHand(File file1, File file2, String keyPath) {
        String line = "";
        byte[] decryptFrom = null;
        byte[] decryptResult = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file1), "GBK"));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2), "GBK"));
            try {
                while ((line = br.readLine()) != null) {

                    String result = "";
                    decryptFrom = parseHexStr2Byte(String.valueOf(result) + line);
                    decryptResult = decrypt(decryptFrom, keyPath);
                    result = new String(decryptResult, "GBK");
                    bw.write(result);
                    bw.newLine();
                }
                bw.flush();
                bw.close();
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                bw.close();
                br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
