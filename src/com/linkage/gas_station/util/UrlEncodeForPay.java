package com.linkage.gas_station.util;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class UrlEncodeForPay {

    public UrlEncodeForPay() {
    }

//    public String urlEncoding(String urlParam, String key) {
//        byte desencoded[] = ThreeDES.encryptMode(key.getBytes(),
//                                                 urlParam.getBytes());
//        byte baseencoded[] = Base64.encode(desencoded);
//        return new String(baseencoded);
//    }

//    public String urlEncoding(String loginName, String provinceCode,
//                              String areaCode, String SysId, String sysNbr,
//                              String key) {
//        String timeStamp = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new
//                Date());
//        String encode = urlEncoding(loginName + "$" + provinceCode + "$" +
//                                    areaCode + "$" + timeStamp + "$" + SysId +
//                                    "$" + sysNbr, key);
//        return encode;
//    }

    public String urlDecoding(String encode, String key) {
        byte srcBytes[] = ThreeDES.decryptMode(key.getBytes(),
                                               Base64.decode(new String(encode)));
        return new String(srcBytes);
    }

//    public String enCoding(String sha1) {
//        String sha1Temp = (new SHA1()).getDigestOfString(sha1.getBytes());
//        return new String(Base64.encode(sha1Temp.getBytes()));
//    }

    public String enCodingForPay(String string, String key) {
        String returnString = "";
        try {
            MessageDigest md = null;
            String strDes = null;
            byte bt[] = string.getBytes("iso8859-1");
            try {
                md = MessageDigest.getInstance("SHA-1");
            } catch (NoSuchAlgorithmException nosuchalgorithmexception) {}
            md.update(bt);
            strDes = bytes2Hex(md.digest());
            String digst = new String(Base64.encode(strDes.getBytes("iso8859-1")));
            String temp = string + "$" + digst;
            //System.out.println("temp:"+temp);
            byte desString[] = ThreeDES.encryptMode(key.getBytes(),
                    temp.getBytes("iso8859-1"));
            //returnString = new String(Base64.encode(desString));
            returnString = parseByte2HexStr(desString);
        } catch (Exception e) {
            returnString = "wrong";
        }
        return returnString;
    }

    public String deCodingForPay(String string, String key, String enc) {
        String returnString = "";
        try {
            byte[] encrypt = parseHexStr2Byte(string);
            //byte encrypt[] = Base64.decode(string);
            byte _encrypt[] = ThreeDES.decryptMode(key.getBytes(), encrypt);
            returnString = new String(_encrypt, enc);
        } catch (Exception e) {
            returnString = "wrong";
        }
        return returnString;
    }

    public String getDigest(String string) {
        MessageDigest md = null;
        String strDes = null;
        byte bt[] = (byte[])null;
        try {
            bt = string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(bt);
        strDes = bytes2Hex(md.digest());
        return new String(Base64.encode(strDes.getBytes()));
    }

    public String byte2hex(byte b[]) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xff);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
            if (n < b.length - 1) {
                hs = hs + ":";
            }
        }

        return hs.toUpperCase();
    }

    /**将二进制转换�?6进制
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**�?6进制转换为二进制
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
                                       16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }


    public String bytes2Hex(byte bts[]) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = Integer.toHexString(bts[i] & 0xff);
            if (tmp.length() == 1) {
                des = des + "0";
            }
            des = des + tmp;
        }
        return des;
    }

}
