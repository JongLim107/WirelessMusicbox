package com.shenqu.wirelessmbox.tools;

public class ByteUtils {
    private static final String TAG = "ByteUtils";

    /**
     * 将二进制“01”字符串转换为十六进制的“012AF”字符 高位在前，低位在后
     *
     * @param bString
     * @return
     */
    private static byte bits2byte(String bString) {
        byte result = 0;
        for (int i = bString.length() - 1, j = 0; i >= 0; i--, j++) {
            result += (Byte.parseByte(bString.charAt(i) + "") * Math.pow(2, j));
        }
        return result;
    }

    /**
     * 将二进制“01”字符串转换为十六进制的“012AF”字符 低位在前，高位在后
     *
     * @param bString
     * @return
     */
    private static byte bits2byteReverse(String bString) {
        byte result = 0;
        for (int i = 0, j = 0; i < 8; i++, j++) {
            result += (Byte.parseByte(bString.charAt(i) + "") * Math.pow(2, j));
        }
        return result;
    }

    /**
     * 将十六进制的“012AF”字符转换为二进制的"01"字符串，高位在前，低位在后 该方法等同于Integer.toBinaryString(b)
     *
     * @param ch
     * @return
     */
    private static String byte2bits(byte ch) {
        int z = ch;
        z |= 256;
        String str = Integer.toBinaryString(z);
        int len = str.length();
        return str.substring(len - 8, len);
    }

    /**
     * 将十六进制的“012AF”字符转换为二进制的"01"字符串 低位在前，高位在后 该方法等同于Integer.toBinaryString(b)
     *
     * @param ch
     * @return
     */
    private static String byte2bitsReverse(byte ch) {
        //Log.e(TAG, "byte2bitsReverse() ch = " + ch);
        int z = ch;
        z |= 256;
        String str = Integer.toBinaryString(z);
        int len = str.length();
        String result = str.substring(len - 8, len);
        StringBuffer sb = new StringBuffer(result);
        //Log.e(TAG, "byte2bitsReverse() return = " + sb.reverse().toString());
        return sb.reverse().toString();
    }

    /**
     * 将二进制字符串转换为字节数组 高位在前，低位在后
     *
     * @param keysString
     * @return
     */
    private static byte[] getBytesFromString(String keysString, boolean isReverse) {
        //Log.i(TAG, "getBytesFromString() String " + keysString);
        if (keysString.length() % 8 != 0)
            return null;

        int size = keysString.length() / 8;
        byte[] keys = new byte[size];
        for (int k = 0; k < size * 8; k += 8) {
            String bString = keysString.substring(k, k + 8);
            if (isReverse)
                keys[k / 8] = bits2byteReverse(bString);
            else
                keys[k / 8] = bits2byte(bString);
        }
        //Log.i(TAG, "getBytesFromString() return " + keys);
        return keys;
    }

    /**
     * 将24个有效字符“012AF”等 转化为 二进制字符串 高位在前，低位在后
     *
     * @param bytes
     * @return
     */
    public static String getStringFromBytes(byte[] bytes, boolean isReverse) {
        //Log.w(TAG, "getStringFromBytes() byte[] "+ bytes);
        StringBuffer sb = new StringBuffer();
        //24个有效字符
        for (byte key : bytes) {
            if (isReverse)
                sb.append(byte2bitsReverse(key));
            else
                sb.append(byte2bits(key));
        }

        //Log.w(TAG, "getStringFromBytes() return "+ sb);
        return sb.toString();
    }


    /**
     * int到byte[]
     * @param i
     * @return
     */
    public static byte[] getBytesFromInteger(int i) {
        byte[] result = new byte[4];
        //由高位到低位
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }

    /**
     * 字符串分割打印
     *
     * @param receiveData
     * @param division
     */
    public static String printStringDivision(byte[] receiveData, int division) {
        //Log.d(TAG, "printStringDivision receiveData " + receiveData.toString());
        String info = HexCodec.HexEncodeStr(receiveData, false);
        if (division == 0)
            return info;
        //Log.d(TAG, "HexCodec.HexEncodeStr receiveData " + info);
        StringBuffer sb = new StringBuffer();
        int size = info.length() / division;
        int remainder = info.length() % division;
        for (int i = 0; i < size; i++) {
            sb.append(info.substring(i * division, (i + 1) * division) + " ");
        }
        if (remainder != 0)
            sb.append(info.substring(size * division, info.length()) + " ");
        //Log.i(TAG, "printStringDivision() " + sb.toString());
        return sb.toString();
    }

}

