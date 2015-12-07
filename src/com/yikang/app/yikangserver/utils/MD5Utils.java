package com.yikang.app.yikangserver.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * md5工具类
 */
public class MD5Utils {

	/**
	 * 获取字符串的md5串
	 * 
	 * @param srcStr
	 *            from which to get mds string
	 */
	public static final String toMd5Digist(String srcStr) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] bs = digest.digest(srcStr.getBytes());
			return toHexString(bs);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取字符串的md5的字节数据
	 */
	public static final byte[] toMd5Bytes(String srcStr) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			return digest.digest(srcStr.getBytes());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将字节数组转换成16进制字符组成的字符串
	 * 
	 * @param bytes
	 *            需要转换的字节数组
	 */
	public static final String toHexString(byte[] bytes) {
		StringBuilder builder = new StringBuilder();
		for (byte b : bytes) {
			builder.append(Integer.toHexString(b & 0xff));
		}
		return builder.toString();
	}
}
