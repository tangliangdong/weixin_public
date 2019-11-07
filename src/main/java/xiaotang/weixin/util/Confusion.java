package xiaotang.weixin.util;

import com.gonghui.pay.common.appencryption.Base64;
import com.gonghui.pay.common.exceptions.ValidationException;
import org.apache.log4j.Logger;

import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2016/10/9 0009.
 */
public class Confusion {
    private static Map map_16 = new HashMap();

    private static final Logger logger = Logger.getLogger(Confusion.class);

    // 向量
    private final static String iv = "82114756";
    // 加解密统一使用的编码方式
    private final static String encoding = "utf-8";

    public static void initMap() {
        char[] ch = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'a', 'b', 'c', 'd', 'e', 'f'};
        int[] num = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 10, 11, 12, 13, 14, 15};
        for (int i = 0; i < ch.length; i++) {
            map_16.put(ch[i], num[i]);
        }
    }

    //加密
    public static String encoder(String sou, String min) {
        initMap();
        Integer minLen = min.length();
        Integer souLen = sou.length();
        Integer chLen = souLen;
        char[] c = new char[minLen + souLen];
        for (int in = 0; in < souLen; in++) {
            c[in] = sou.charAt(in);
        }
        for (int i = 0; i < minLen; i++) {
            chLen = chLen + 1;
            char part = min.charAt(i);
            char partIndex = i == minLen - 1 ? min.charAt(0) : min.charAt(i + 1);
            int partNum = (int) map_16.get(partIndex) >= chLen ? chLen - 1 : (int) map_16.get(partIndex);
            for (int k = chLen - 1; k > partNum; k--) {
                c[k] = c[k - 1];
            }
            c[partNum] = part;
        }
        return String.valueOf(c);
    }

    //解密
    public static String decoder(String ki, String min) {
        initMap();
        Integer minLen = min.length();
        Integer kiLen = ki.length();
        char[] c = ki.toCharArray();
        for (int i = minLen - 1; i >= 0; i--) {
            char part = min.charAt(i);
            char partIndex = i == minLen - 1 ? min.charAt(0) : min.charAt(i + 1);
            int partNum = (int) map_16.get(partIndex) >= c.length ? c.length - 1 : (int) map_16.get(partIndex);
            char[] c2 = new char[c.length - 1];
            for (int k = 0; k < c.length; k++) {
                if (k < partNum) {
                    c2[k] = c[k];
                }
                if (k > partNum) {
                    c2[k - 1] = c[k];
                }
            }
            c = c2.clone();
        }
        return String.valueOf(c);
    }

    /**
     * 混淆解密
     * @param httpSession 当前session
     * @param data 密文
     * @param removeKey 解密后是否删除key
     * @return 明文
     */
    public static String decodeFromSession(HttpSession httpSession, String data, boolean removeKey) {
        String randomKey = (String) httpSession.getAttribute("randomKey");
        if (randomKey == null) return null;
        if (removeKey) httpSession.removeAttribute("randomKey");
        return decoder(data, randomKey);
    }
    /**
     * 3DES加密
     *
     * @param plainText 普通文本
     * @return
     * @throws Exception
     */
    public static String encode(String plainText,String key) {
        Key deskey = null;
        DESedeKeySpec spec = null;
        try {
            spec = new DESedeKeySpec(key.getBytes());
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            deskey = keyfactory.generateSecret(spec);

            Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
            byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
            return Base64.encode(encryptData);
        } catch (InvalidKeyException e) {
            throw new ValidationException("数据验证失败");
        } catch (NoSuchPaddingException e) {
            logger.error(e.getMessage(), e);
            throw new ValidationException("数据验证失败");
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            throw new ValidationException("数据验证失败");
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            throw new ValidationException("数据验证失败");
        } catch (IllegalBlockSizeException e) {
            logger.error(e.getMessage(), e);
            throw new ValidationException("数据验证失败");
        } catch (BadPaddingException e) {
            logger.error(e.getMessage(), e);
            throw new ValidationException("数据验证失败");
        } catch (InvalidAlgorithmParameterException e) {
            logger.error(e.getMessage(), e);
            throw new ValidationException("数据验证失败");
        } catch (InvalidKeySpecException e) {
            logger.error(e.getMessage(), e);
            throw new ValidationException("数据验证失败");
        }
    }

    /**
     * 3DES解密
     *
     * @param encryptText 加密文本
     * @return
     * @throws Exception
     */
    public static String decode(String encryptText,String key) {
        Key deskey = null;
        DESedeKeySpec spec = null;
        try {
            spec = new DESedeKeySpec(key.getBytes());
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
            byte[] decryptData = cipher.doFinal(Base64.decode(encryptText));
            return new String(decryptData, encoding);
        } catch (InvalidKeyException e) {
            logger.error(e.getMessage(), e);
            throw new ValidationException("数据验证失败");
        } catch (NoSuchPaddingException e) {
            logger.error(e.getMessage(), e);
            throw new ValidationException("数据验证失败");
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            throw new ValidationException("数据验证失败");
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            throw new ValidationException("数据验证失败");
        } catch (IllegalBlockSizeException e) {
            logger.error(e.getMessage(), e);
            throw new ValidationException("数据验证失败");
        } catch (BadPaddingException e) {
            logger.error(e.getMessage(), e);
            throw new ValidationException("数据验证失败");
        } catch (InvalidAlgorithmParameterException e) {
            logger.error(e.getMessage(), e);
            throw new ValidationException("数据验证失败");
        } catch (InvalidKeySpecException e) {
            logger.error(e.getMessage(), e);
            throw new ValidationException("数据验证失败");
        }

    }

}
