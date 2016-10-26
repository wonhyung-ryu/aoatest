package com.example.wonhyungryu.aoatest2;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by wonhyung.ryu on 2016-10-06.
 */

public class conversion_LE {
    public char byteToChar_LE(byte[] input, int offset ) {
        try {
            return (char) (((input[offset + 1] & 0xFF) << 8) + (input[offset] & 0xFF));
        } catch (Exception e) {
            Log.e("AOA", "Exception !!!! byteToChar_LE ");
            return 0;
        }
    }

    public short byteToShort_LE(byte[] input, int offset ) {
        try {
            return (short)(((input[offset+1]&0xFF)<<8) + (input[offset]&0xFF));
        } catch (Exception e) {
            Log.e("AOA", "Exception !!!! byteToShort_LE ");
            return 0;
        }
    }

    public int byteToInt_LE(byte[] input, int offset ) {
        try {
            return (int)(((input[offset+3]&0xFF)<<24)+((input[offset+2]&0xFF)<<16)+((input[offset+1]&0xFF)<<8)+(input[offset+0]&0xFF));
        } catch (Exception e) {
            Log.e("AOA", "Exception !!!! byteToInt_LE ");
            return 0;
        }
    }

    public float byteToFloat_LE(byte[] input, int offset) {
        byte[] tmp = new byte[4];
        try {
            System.arraycopy(input, offset, tmp, 0, 4);
        } catch (Exception e) {
            Log.e("AOA", "Exception !!!! byteToFloat_LE ");
        }
        return ByteBuffer.wrap(tmp).order(ByteOrder.LITTLE_ENDIAN ).getFloat();
    }

    public double byteToDouble_LE (byte[] input, int offset ) {
        byte[] tmp = new byte[8];
        try {
            System.arraycopy(input, offset, tmp, 0, 8);
        } catch (Exception e) {
            Log.e("AOA", "Exception !!!! byteToDouble_LE ");
        }
        return ByteBuffer.wrap(tmp).order(ByteOrder.LITTLE_ENDIAN ).getDouble();
    }

    public double byteTolong_LE (byte[] input, int offset ) {
        byte[] tmp = new byte[8];
        try {
            System.arraycopy(input, offset, tmp, 0, 8);
        } catch (Exception e) {
            Log.e("AOA", "Exception !!!! byteTolong_LE ");
        }
        return ByteBuffer.wrap(tmp).order(ByteOrder.LITTLE_ENDIAN ).getLong();
    }

    public char[] byteToCharArray_LE(byte[] input, int offset, int length_byte){
            char[] tmp = new char[length_byte / 2];
        try {
            for (int i = 0; i < length_byte / 2; i++) {
                tmp[i] = byteToChar_LE(input, offset + 2 * i);
            }
        } catch (Exception e) {
            Log.e("AOA", "Exception !!!! byteToCharArray_LE ");
        }
        return  tmp;
    }

    static int arrayRealLen(byte[] barray, int offset, int barray_len){
        int j=barray_len;
        for (int i=0; i<barray_len; i++){
            if (barray[offset + i] == 0){
                j=i;
                break;
            }
        }
        return j;
    }
}
