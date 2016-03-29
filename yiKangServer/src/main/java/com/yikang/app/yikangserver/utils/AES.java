package com.yikang.app.yikangserver.utils;

import javax.crypto.*;
import javax.crypto.spec.*;

public class AES {
	public static void main(String[] args) throws Exception {
		/*
		 * 加密用的Key 可以用16个字母和数字组成，最好不要用保留字符，虽然不会错，至于怎么裁决，个人看情况而定
		 */
		String cKey =  getKey();
		// 需要加密的字串
		String cSrc = "abcd";
		// 加密
		long lStart = System.currentTimeMillis();
		String enString = AES.decrypt(cSrc, cKey);
		System.out.println("加密后的字串是：" + enString);
		long lUseTime = System.currentTimeMillis() - lStart;
		System.out.println("加密耗时：" + lUseTime + "毫秒");

		// 解密
		lStart = System.currentTimeMillis();
		String DeString = AES.decrypt(enString, cKey);
		System.out.println("解密后的字串是：" + DeString);
		lUseTime = System.currentTimeMillis() - lStart;
		System.out.println("解密耗时：" + lUseTime + "毫秒");

	}
	
//	public static native String getKey();

//	static{
//		System.loadLibrary("mykey");
//	}

	public static String getKey(){
		return "1234567890abcDEF";
	}


	/**
	 * 解密
	 * 
	 * @param sSrc
	 *            明文
	 * @param sKey
	 *            加密密钥
	 * @return 密文
	 * @throws Exception
	 */
	public static String decrypt(String sSrc, String sKey) {
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
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] encrypted1 = hex2byte(sSrc);
			try {
				byte[] original = cipher.doFinal(encrypted1);
				String originalString = new String(original);
				return originalString;
			} catch (Exception e) {
				return null;
			}
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * 加密
	 * 
	 * @param sSrc
	 * @param sKey
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String sSrc, String sKey) throws Exception {
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
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(sSrc.getBytes());
		return byte2hex(encrypted).toLowerCase();
	}

	/**
	 * hex编码转换成字节数组
	 * 
	 * @param strhex
	 * @return
	 */
	public static byte[] hex2byte(String strhex) {
		if (strhex == null) {
			return null;
		}
		int length = strhex.length();
		if (length % 2 == 1) {
			return null;
		}
		byte[] b = new byte[length / 2];
		for (int i = 0; i != length / 2; i++) {
			b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2),
					16);

		}
		return b;
	}

	/**
	 * 将字节数组转换成hex编码
	 * 
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b) {
		StringBuffer hs = new StringBuffer();
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs.append("0").append(stmp);
			} else {
				hs.append(stmp);
			}
		}
		return hs.toString().toUpperCase();
	}

}
