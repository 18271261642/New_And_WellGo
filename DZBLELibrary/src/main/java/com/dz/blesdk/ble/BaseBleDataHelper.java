package com.dz.blesdk.ble;

import java.util.Arrays;

public  class BaseBleDataHelper {


	/**
	 * 两个byte[]内容是否相等
	 * @return
	 */
	public static boolean equals(byte[] array1, byte[] array2) {
		return Arrays.equals(array1, array2);
	}

	/**
	 * byte[]的16进制 内容是否startWith resouseHex字符串的16进制
	 * 
	 * @param buffer
	 * @param headHex
	 *            (大小写不区分)
	 * @return
	 */
	public static boolean startWith(byte[] buffer,String headHex) {
		return toHexString(buffer).startsWith(headHex);
	}

	/**
	 * 剪切字节数组某部分并合并字节 用int装...
	 * 如何理解?
	 * 比如 buffer = new byte[]{0x05,0x1D,0xFA,0xAA,0xBB,0xCC};
	 * 先取 newBuffer = new byte[]{0x05,0x1D,0xFA,0xAA};
	 * 然后 我要取第0~3个字节 把这个newBuffer 合并成 int d = 0x051DFAAA;
	 * 则传入
	 * byteNum = 这里是4个字节合并
	 * begin =0 从零开始
	 * end = 3 第3个字节
	 * @param buffer
	 * @param byteNum 字节合并数
	 * @param begin 起始位置
	 * @param end 结束位置
	 * @return
	 */
	public static int subBytesToInt(byte[] buffer,int byteNum,int begin,int end) {
		byte[] bytes = subBytesToBytes(buffer, begin, end);
		return bytes2Int(bytes,byteNum);
	}

	/**
	 * 字节数组某部分并合并字节 用int装...
	 * @param buffer
	 * @param byteNum 字节合并数
	 * @return
	 */
	private static int bytes2Int(byte[] buffer,int byteNum) {
		int intValue = 0;
		for (int i = 0; i < buffer.length; i++) {
			intValue += (buffer[i] & 0xFF) << (8 * ((byteNum-1) - i));
		}
		return intValue;
	}
	/**
	 * 字节数组某部分抽取出来
	 * @param buffer
	 * @param begin 起始位置
	 * @param end 结束位置
	 * @return
	 */
	public static byte[] subBytesToBytes(byte[] buffer,int begin,int end) {
		byte[] bytes = new byte[end-begin+1];
		for (int i = begin; i <=end; i++) {
			bytes[i-begin] = buffer[i];
		}
		return bytes;
	}


	/**
	 * 打印byte[]
	 *
	 * @param bytes
	 * @return
	 */

	public static String toByteString(byte[] bytes) {
		return Arrays.toString(bytes);
	}

	/**
	 * byte[]转16进制
	 * 
	 * @param bytes
	 * @return
	 */
	public static String toHexString(byte[] bytes) {
		if (bytes != null && bytes.length > 0) {
			StringBuilder sb = new StringBuilder(bytes.length);
			for (byte byteChar : bytes) {
				sb.append(String.format("%02X", byteChar));
			}
			return sb.toString();
		}
		return null;
	}


	/**
	 * 字符串转char再转16进制 通用用于发送文字给设备显示
	 * @param charString
	 * @return
	 */
	public static String charToHex(String charString) {
		char[] charArray = charString.toCharArray();
		StringBuilder sb = new StringBuilder(charArray.length);
		for (char c : charArray) {
			sb.append(Integer.toHexString((int) c));
		}
		return sb.toString();
	}

	/**
	 * 十六进制字符串转换成bytes[]
	 */
	public static byte[] hexToBytes(String hex) {
		int m = 0, n = 0;
		int l = hex.length() / 2;
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {
			m = i * 2 + 1;
			n = m + 1;
			ret[i] = uniteBytes(hex.substring(i * 2, m), hex.substring(m, n));
		}
		return ret;
	}
	
	private static byte uniteBytes(String src0, String src1) {
		byte b0 = Byte.decode("0x" + src0).byteValue();
		b0 = (byte) (b0 << 4);
		byte b1 = Byte.decode("0x" + src1).byteValue();
		byte ret = (byte) (b0 | b1);
		return ret;
	}

	 
}
