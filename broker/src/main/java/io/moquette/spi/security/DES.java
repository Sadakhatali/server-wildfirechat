package io.moquette.spi.security;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class DES {
	private static final String Encrypt_Password = "abcdefgh";
    private static byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8 };

    private static byte[] aes_key= {0x00,0x11,0x22,0x33,0x44,0x55,0x66,0x77,0x78,0x79,0x7A,0x7B,0x7C,0x7D,0x7E,0x7F};
    public static String decryptDES(String decryptString) throws Exception {
        byte[] byteMi = Base64.getDecoder().decode(decryptString);
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(Encrypt_Password.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
        byte decryptedData[] = cipher.doFinal(byteMi);

        return new String(decryptedData);
    }
    public static void init(byte[] secret) {
        if (secret != null && secret.length == 16) {
            aes_key = new byte[16];
            for (int i = 0; i < 16; i++) {
                aes_key[i] = secret[i];
            }
        } else {
            System.out.println("Error int key error, secret incorrect");
        }
    }
    public static String encryptDES(String encryptString) throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(Encrypt_Password.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
        byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
        return new String(Base64.getEncoder().encode(encryptedData));
    }

	public static byte[] encrypt(byte[] datasource) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		SecureRandom random = new SecureRandom();
		DESKeySpec desKey = new DESKeySpec(Encrypt_Password.getBytes());
		// ??????????????????????????????????????????DESKeySpec?????????
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher??????????????????????????????
		Cipher cipher = Cipher.getInstance("DES");
		// ??????????????????Cipher??????
		cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
		// ??????????????????????????????
		// ????????????????????????
		return cipher.doFinal(datasource);
	}

	/**
	 * ??????
	 * 
	 * @param src
	 *            byte[]
	 *            String
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] src) throws Exception {
		// DES?????????????????????????????????????????????
		SecureRandom random = new SecureRandom();
		// ????????????DESKeySpec??????
		DESKeySpec desKey = new DESKeySpec(Encrypt_Password.getBytes());
		// ????????????????????????
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		// ???DESKeySpec???????????????SecretKey??????
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher??????????????????????????????
		Cipher cipher = Cipher.getInstance("DES");
		// ??????????????????Cipher??????
		cipher.init(Cipher.DECRYPT_MODE, securekey, random);
		// ????????????????????????
		return cipher.doFinal(src);
	}
    public static byte[] AESEncrypt(String sSrc, String userKey) {
        return AESEncrypt(sSrc.getBytes(), userKey);
    }

    public static byte[] AESEncrypt(byte[] tobeencrypdata, byte[] aesKey) {
        if (aesKey == null) {
            System.out.print("Key??????null");
            return null;
        }
        // ??????Key?????????16???
        if (aesKey.length != 16) {
            System.out.print("Key????????????16???");
            return null;
        }


        try {
            SecretKeySpec skeySpec = new SecretKeySpec(aesKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"??????/??????/????????????"
            IvParameterSpec iv = new IvParameterSpec(aesKey);//??????CBC???????????????????????????iv?????????????????????????????????
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            //2018.1.1 0:0:0 ??????????????????
            int curhour = (int) ((System.currentTimeMillis()/1000 - 1514736000)/3600);

            byte[] tobeencrypdatawithtime = new byte[tobeencrypdata.length + 4];
            byte byte0 = (byte)(curhour & 0xFF);
            tobeencrypdatawithtime[0] = byte0;

            byte byte1 = (byte)((curhour & 0xFF00) >> 8);
            tobeencrypdatawithtime[1] = byte1;

            byte byte2 = (byte)((curhour & 0xFF0000) >> 16);
            tobeencrypdatawithtime[2] = byte2;

            byte byte3 = (byte)((curhour & 0xFF) >> 24);
            tobeencrypdatawithtime[3] = byte3;

            System.arraycopy(tobeencrypdata, 0, tobeencrypdatawithtime, 4, tobeencrypdata.length);


            byte[] encrypted = cipher.doFinal(tobeencrypdatawithtime);
            return encrypted;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static byte[] AESEncrypt(byte[] tobeencrypdata, String userKey) {
        byte[] aesKey = aes_key;
        if (userKey != null && !userKey.isEmpty()) {
            aesKey = convertUserKey(userKey);
        }
        return AESEncrypt(tobeencrypdata, aesKey);
    }

    public static int getUnsignedByte (byte data){      //???data????????????????????????0~255 (0xFF ???BYTE)???
        return data&0x0FF ;
    }

    private static byte[] convertUserKey(String userKey) {
        byte[] key = new byte[16];
        for (int i = 0; i < 16; i++) {
            key[i] = (byte) (userKey.charAt(i) & 0xFF);
        }
        return key;
    }

    public static byte[] AESDecrypt(byte[] sSrc, String userKey, boolean checkTime) {
        try {

            byte[] aesKey = aes_key;
            if (userKey != null && !userKey.isEmpty()) {
                aesKey = convertUserKey(userKey);
            }
            // ??????Key????????????
            if (aesKey == null) {
                System.out.print("Key??????null");
                return null;
            }
            // ??????Key?????????16???
            if (aesKey.length != 16) {
                System.out.print("Key????????????16???");
                return null;
            }

            SecretKeySpec skeySpec = new SecretKeySpec(aesKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(aesKey);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            try {
                byte[] original = cipher.doFinal(sSrc);
                int hours = 0;

                if (original.length > 4) {
                    hours += getUnsignedByte(original[3]);
                    hours <<= 8;

                    hours += getUnsignedByte(original[2]);
                    hours <<= 8;

                    hours += getUnsignedByte(original[1]);
                    hours <<= 8;

                    hours += getUnsignedByte(original[0]);

                    //2018.1.1 0:0:0 ??????????????????
                    int curhour = (int) ((System.currentTimeMillis()/1000 - 1514736000)/3600);

                    if (curhour - hours > 24 && checkTime) {
                        return null;
                    }
                    byte[] neworiginal = new byte[original.length - 4];
                    System.arraycopy(original, 4, neworiginal, 0, neworiginal.length);
                    return neworiginal;
                }
                return null;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }
}
