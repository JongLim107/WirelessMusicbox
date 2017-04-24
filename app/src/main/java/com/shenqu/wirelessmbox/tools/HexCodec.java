package com.shenqu.wirelessmbox.tools;

public class HexCodec {
	private final static String TAG = "Tools/HexCodec";
	/**
	 * 用于建立十六进制字符的输出的大、小写字符数组*/
	private static final byte[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	private static final byte[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * 将字节数组转换为十六进制字符串
	 * 
	 * @param data byte[]
	 * @param toLowerCase <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式
	 * @return 十六进制String
	 */
	public static String HexEncodeStr(byte[] data, boolean toLowerCase) {
		if(toLowerCase)
			return new String(encodeHex(data, DIGITS_LOWER));
		else
			return new String(encodeHex(data, DIGITS_UPPER));
	}


	/**
	 * 将字节数组转换为十六进制字符数组
	 *
	 * @param data byte[]
	 * @param toDigits 用于控制输出的char[]
	 * @return 十六进制char[]
	 */
	private static byte[] encodeHex(byte[] data, byte[] toDigits) {
		//Log.i(TAG, "encodeHex data.length = " + data.length);
		if(data == null)
			return new byte[0];
		int l = data.length;
		byte[] out = new byte[l << 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
			out[j++] = toDigits[0x0F & data[i]];
			//Log.i(TAG,"----> HexEncode data:" + data[i] + " out:" + out[j-2] + "," + out[j-1]);
		}
		//Log.d(TAG, "encodeHex out = " + out);
		return out;
	}

	/**
	 * 将十六进制字符转换成一个整数
	 *
	 * @param ch 十六进制char
	 * @return 一个整数
	 * @throws RuntimeException 当ch不是一个合法的十六进制字符时，抛出运行时异常
	 */
	private static int char2Digit(char ch) {
		int digit = Character.digit(ch, 16);
		if (digit == -1) {
			throw new RuntimeException("Illegal hexadecimal character " + ch);
		}
		//Log.d(TAG,"char2Digit input ch " + ch + " digit " + digit);
		return digit;
	}

	/**
	 * 将十六进制字符数组转换为字节数组
	 * 
	 * @param data  十六进制char[]
	 * @return byte[]
	 * @throws RuntimeException 如果源十六进制字符数组是一个奇怪的长度，将抛出运行时异常
	 */
	public static byte[] HexDecodeStr(char[] data) {
		//Log.w(TAG, "HexDecodeStr data = " + data);
		int len = data.length;
		if ((len & 0x01) != 0) {
			throw new RuntimeException("Odd number of characters.");
		}

		byte[] out = new byte[len >> 1];

		// two characters form the hex value.
		for (int i = 0, j = 0; j < len; i++) {
			int f = char2Digit(data[j++]) << 4;
			f = f | char2Digit(data[j++]);
			out[i] = (byte) (f & 0xFF);
			//Log.i(TAG,"----> HexDecode byte " + out[i]);
		}
		//Log.w(TAG, "HexDecodeStr out = " + out);
		return out;
	}

}
