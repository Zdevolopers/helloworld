package z.cloud.utils;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;

/**
 * @Description: 加解密工具类
 * @Author: zming
 * @Date: 2019/9/19
 */
public class EncryptUtils {

    private static Logger log = LoggerFactory.getLogger(EncryptUtils.class);

    //------------------------Base64--Begin--------------------------
    private static final BASE64Encoder encoder = new BASE64Encoder();
    private static final BASE64Decoder decoder = new BASE64Decoder();

    /**
     * Base64加密
     *
     * @param toEncodeContent
     * @return
     */
    public static String encryptByBase64(String toEncodeContent) {
        if (toEncodeContent == null || "".equals(toEncodeContent)) {
            return null;
        }
        return encoder.encode(toEncodeContent.getBytes());
    }

    /**
     * Base64解密
     *
     * @param toDecodeContent
     * @return
     */
    public static String decryptByBase64(String toDecodeContent) {
        if (toDecodeContent == null || "".equals(toDecodeContent)) {
            return null;
        }
        byte[] buf = null;
        try {
            buf = decoder.decodeBuffer(toDecodeContent);
        } catch (IOException e) {
            log.info("EncryptUtil-->decryptByBase64 Base64解密失败！", e);
        }
        return new String(buf);
    }
    //------------------------Base64--End----------------------------

    //------------------------DES--Begin-----------------------------
    /**
     * 加密工具
     */
    private static Cipher desEncryptCipher = null;
    /**
     * 解密工具
     */
    private static Cipher desDecryptCipher = null;

    /**
     * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位
     *
     * @param arrBTmp
     * @return
     * @throws Exception
     */
    public static Key getKey(byte[] arrBTmp) throws Exception {
        byte[] arrB = new byte[8];
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }
        Key key = new SecretKeySpec(arrB, "DES");
        return key;
    }

    /**
     * 将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0813， 和public static byte[]
     * hexStr2ByteArr(String strIn) 互为可逆的转换过程
     *
     * @param arrB 需要转换的byte数组
     * @return 转换后的字符串
     * @throws Exception 本方法不处理任何异常，所有异常全部抛出
     */
    public static String byteArr2HexStr(byte[] arrB) throws Exception {
        int iLen = arrB.length;
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
        StringBuffer sb = new StringBuffer(iLen * 2);
        for (int i = 0; i < iLen; i++) {
            int intTmp = arrB[i];
            // 把负数转换为正数
            while (intTmp < 0) {
                intTmp = intTmp + 256;
            }
            // 小于0F的数需要在前面补0
            if (intTmp < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(intTmp, 16));
        }
        return sb.toString();
    }

    /**
     * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[] arrB)
     * 互为可逆的转换过程
     *
     * @param strIn 需要转换的字符串
     * @return 转换后的byte数组
     */
    public static byte[] hexStr2ByteArr(String strIn) throws Exception {
        byte[] arrB = strIn.getBytes();
        int iLen = arrB.length;
        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(arrB, i, 2);
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }

    /**
     * DES加密
     *
     * @param strIn   需要加密字符串
     * @param keyByte 密钥
     * @return
     * @throws Exception
     */
    public static String encryptByDes(String strIn, byte[] keyByte) throws Exception {
        if (desEncryptCipher == null) {
            Key key = getKey(keyByte);
            desEncryptCipher = Cipher.getInstance("DES");
            desEncryptCipher.init(Cipher.ENCRYPT_MODE, key);
        }
        return byteArr2HexStr(desEncryptCipher.doFinal(strIn.getBytes()));
    }

    /**
     * DES解密
     *
     * @param strIn   需解密的字符串
     * @param keyByte 密钥
     * @return
     * @throws Exception
     */
    public static String decryptByDes(String strIn, byte[] keyByte) {
        try {

            if (desDecryptCipher == null) {
                Key key = null;
                key = getKey(keyByte);
                desDecryptCipher = Cipher.getInstance("DES");
                desDecryptCipher.init(Cipher.DECRYPT_MODE, key);
            }
            return new String(desDecryptCipher.doFinal(hexStr2ByteArr(strIn)));
        } catch (Exception e) {
            log.info("EncryptUtil-->decryptByDes" + "解密失败！", e);
            return "";
        }
    }

    //----------------------------RSA BEGIN---------------------------

    /**
     * 生成公钥和私钥
     *
     * @throws NoSuchAlgorithmException
     */
    public static HashMap<String, Object> getRSAKeys() throws NoSuchAlgorithmException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(512);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        map.put("public", publicKey);
        map.put("private", privateKey);
        return map;
    }

    /**
     * 使用模和指数生成RSA公钥
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA
     * /None/NoPadding】
     *
     * @param modulus  模
     * @param exponent 指数
     * @return
     */
    public static RSAPublicKey getRSAPublicKey(String modulus, String exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            log.info("EncryptUtil-->RSAPublicKey 生成RSA公钥失败！", e);
            return null;
        }
    }

    /**
     * 使用模和指数生成RSA私钥
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA
     * /None/NoPadding】
     *
     * @param modulus  模
     * @param exponent 指数
     * @return
     */
    public static RSAPrivateKey getRSAPrivateKey(String modulus, String exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            log.info("EncryptUtil-->getRSAPrivateKey 生成RSA私钥失败！", e);
            return null;
        }
    }

    /**
     * 公钥加密
     *
     * @param data
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encryptByRSAPublicKey(String data, RSAPublicKey publicKey)
            throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        // 模长
        int key_len = publicKey.getModulus().bitLength() / 8;
        // 加密数据长度 <= 模长-11
        String[] datas = splitString(data, key_len - 11);
        String mi = "";
        //如果明文长度大于模长-11则要分组加密
        for (String s : datas) {
            mi += bcd2Str(cipher.doFinal(s.getBytes()));
        }
        return mi;
    }

    /**
     * 私钥解密
     *
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String decryptByRSAPrivateKey(String data, RSAPrivateKey privateKey)
            throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        //模长
        int key_len = privateKey.getModulus().bitLength() / 8;
        byte[] bytes = data.getBytes();
        byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
        //如果密文长度大于模长则要分组解密
        String ming = "";
        byte[][] arrays = splitArray(bcd, key_len);
        for (byte[] arr : arrays) {
            ming += new String(cipher.doFinal(arr));
        }
        return ming;
    }

    /**
     * ASCII码转BCD码
     */
    public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
        byte[] bcd = new byte[asc_len / 2];
        int j = 0;
        for (int i = 0; i < (asc_len + 1) / 2; i++) {
            bcd[i] = asc_to_bcd(ascii[j++]);
            bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++]) & 0xff) + (bcd[i] << 4));
        }
        return bcd;
    }

    public static byte asc_to_bcd(byte asc) {
        byte bcd;

        if ((asc >= '0') && (asc <= '9')) {
            bcd = (byte) (asc - '0');
        } else if ((asc >= 'A') && (asc <= 'F')) {
            bcd = (byte) (asc - 'A' + 10);
        } else if ((asc >= 'a') && (asc <= 'f')) {
            bcd = (byte) (asc - 'a' + 10);
        } else {
            bcd = (byte) (asc - 48);
        }
        return bcd;
    }

    /**
     * BCD转字符串
     */
    public static String bcd2Str(byte[] bytes) {
        char temp[] = new char[bytes.length * 2], val;

        for (int i = 0; i < bytes.length; i++) {
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

            val = (char) (bytes[i] & 0x0f);
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
        }
        return new String(temp);
    }

    /**
     * 拆分字符串
     */
    public static String[] splitString(String string, int len) {
        int x = string.length() / len;
        int y = string.length() % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        String[] strings = new String[x + z];
        String str = "";
        for (int i = 0; i < x + z; i++) {
            if (i == x + z - 1 && y != 0) {
                str = string.substring(i * len, i * len + y);
            } else {
                str = string.substring(i * len, i * len + len);
            }
            strings[i] = str;
        }
        return strings;
    }

    /**
     * 拆分数组
     */
    public static byte[][] splitArray(byte[] data, int len) {
        int x = data.length / len;
        int y = data.length % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        byte[][] arrays = new byte[x + z][];
        byte[] arr;
        for (int i = 0; i < x + z; i++) {
            arr = new byte[len];
            if (i == x + z - 1 && y != 0) {
                System.arraycopy(data, i * len, arr, 0, y);
            } else {
                System.arraycopy(data, i * len, arr, 0, len);
            }
            arrays[i] = arr;
        }
        return arrays;
    }

    //---------------------------RSA END-------------------------------

    //----------------------------MD5 BEGIN---------------------------

    /**
     * 对字符串md5加密
     * 返回32位大写字符串
     *
     * @param str
     * @return
     */
    public static String getMD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes("utf-8"));
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            //32位加密
            return buf.toString().toUpperCase();
            // 16位的加密
            //return buf.toString().substring(8, 24);
        } catch (Exception e) {
            log.info("EncryptUtil-->getMD5 对字符串md5加密失败！", e);
            return null;
        }
    }

    /**
     * 加盐md5加密
     *
     * @param str
     * @param salt
     * @return java.lang.String
     * @author zming
     * @date 2019/6/25
     */
    public static String getSaltMD5(String str, String salt) {
        // 加密盐
        StringBuilder saltBuilder = new StringBuilder(salt);
        int len = saltBuilder.length();
        if (len < 16) {
            for (int i = 0; i < 16 - len; i++) {
                saltBuilder.append("0");
            }
        }
        salt = saltBuilder.toString();
        str = md5Hex(str + salt);
        char[] cs = new char[48];
        for (int i = 0; i < 48; i += 3) {
            cs[i] = str.charAt(i / 3 * 2);
            char c = salt.charAt(i / 3);
            cs[i + 1] = c;
            cs[i + 2] = str.charAt(i / 3 * 2 + 1);
        }
        return String.valueOf(cs);
    }

    /**
     * 使用Apache的Hex类实现Hex(16进制字符串和)和字节数组的互转
     *
     * @param str
     * @return java.lang.String
     * @author zming
     * @date 2019/6/25
     */
    private static String md5Hex(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(str.getBytes());
            return new String(new Hex().encode(digest));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
            return "";
        }
    }
    //----------------------------MD5 BEGIN---------------------------

    //----------------------------AES BEGIN---------------------------

    /**
     * AES加密
     *
     * @param sSrc
     * @param sKey
     * @return
     * @throws Exception
     */
    public static String encryptByAes(String sSrc, String sKey, String vi) throws Exception {
        if (sKey == null) {
            System.out.print("Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            System.out.print("Key长度不是16位");
            return null;
        }
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"算法/模式/补码方式"
        IvParameterSpec iv = new IvParameterSpec(vi.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes());

        return new BASE64Encoder().encode(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
    }

    /**
     * AES解密
     *
     * @param sSrc
     * @param sKey
     * @return
     * @throws Exception
     */
    public static String decryptByAes(String sSrc, String sKey, String vi) throws Exception {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                System.out.print("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                System.out.print("Key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(vi.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);//先用base64解密
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original);
                return originalString;
            } catch (Exception e) {
                log.info("EncryptUtil-->decryptByAes AES解密失败！", e);
                return null;
            }
        } catch (Exception ex) {
            log.info("EncryptUtil-->decryptByAes AES解密失败！", ex);
            return null;
        }
    }
    //----------------------------AES END---------------------------
}

/**
 public static void main(String[] args) throws Exception {
 //--------------------RSA--------------------
 HashMap<String, Object> map = EncryptUtil.getRSAKeys();
 //-----------------------生成公私钥-----------------------------
 RSAPublicKey publicKey = (RSAPublicKey) map.get("public");
 RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");
 //-------------------------------------------------------------
 //模
 //        String modulus = "118970187093733358642708832611891350365379503654008101104446375492153381324430414848560866695613358016859161182312435541144892475641027906824395303485799439138276049861126751892962049904972736304119281463235202443517830809199688774985899766117146978662310913842754727827215777603306673535790451461970415666353";
 String modulus = publicKey.getModulus().toString();
 System.out.println("获取公钥的模：" + modulus);
 //公钥指数
 //        String public_exponent = "65537";
 String public_exponent = publicKey.getPublicExponent().toString();
 System.out.println("获取公钥指数：" + public_exponent);
 //私钥指数
 //        String private_exponent = "95839465151191733975339316992611865702430854256578081050540097870031245968282096402486701515096925957857079430248333527545426221088363341941101210728791068121450555202501146879991913339027255222092183136638263691982952941141017428340796036895303759623524628667865365931795868618019766412865922619036961713873";
 String private_exponent = privateKey.getPrivateExponent().toString();
 System.out.println("获取私钥指数：" + private_exponent);
 //-------------------------------------------------------------
 //--------------------以下测试---------------------------------
 //明文
 String ming = "FC5553841B81DF96B15534E6CA992F08";
 //使用模和指数生成公钥和私钥
 RSAPublicKey pubKey = EncryptUtil.getRSAPublicKey(modulus, public_exponent);
 RSAPrivateKey priKey = EncryptUtil.getRSAPrivateKey(modulus, private_exponent);
 //加密后的密文
 System.err.println("加密时间begin：" + new Date());
 String mi = EncryptUtil.encryptByRSAPublicKey(ming, pubKey);
 System.err.println("获取密文：" + mi);
 System.err.println("加密时间end  ：" + new Date());
 //解密后的明文
 System.err.println("解密时间begin：" + new Date());
 ming = EncryptUtil.decryptByRSAPrivateKey(mi, priKey);
 //        System.err.println("获取明文：" + ming);
 System.err.println("解密时间end  ：" + new Date());
 //----------------------------------RSA end-----------------------------------
 }**/
