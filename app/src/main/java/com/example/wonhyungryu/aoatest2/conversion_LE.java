package com.example.wonhyungryu.aoatest2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by wonhyung.ryu on 2016-10-06.
 */

public class conversion_LE {
    public char byteToChar_LE(byte[] input, int offset ) {
        return (char)(((input[offset+1]&0xFF)<<8) + (input[offset]&0xFF));
    }

    public short byteToShort_LE(byte[] input, int offset ) {
        return (short)(((input[offset+1]&0xFF)<<8) + (input[offset]&0xFF));
    }

    public int byteToInt_LE(byte[] input, int offset ) {
        return (int)(((input[offset+3]&0xFF)<<24)+((input[offset+2]&0xFF)<<16)+((input[offset+1]&0xFF)<<8)+(input[offset+0]&0xFF));
    }

    public float byteToFloat_LE(byte[] input, int offset) {
        byte[] tmp = new byte[4];
        System.arraycopy(input, offset, tmp, 0, 4);
        return ByteBuffer.wrap(tmp).order(ByteOrder.LITTLE_ENDIAN ).getFloat();
    }

    public double byteToDouble_LE (byte[] input, int offset ) {
        byte[] tmp = new byte[8];
        System.arraycopy(input, offset, tmp, 0, 8);
        return ByteBuffer.wrap(tmp).order(ByteOrder.LITTLE_ENDIAN ).getDouble();
    }

    public double byteTolong_LE (byte[] input, int offset ) {
        byte[] tmp = new byte[8];
        System.arraycopy(input, offset, tmp, 0, 8);
        return ByteBuffer.wrap(tmp).order(ByteOrder.LITTLE_ENDIAN ).getLong();
    }

    public char[] byteToCharArray_LE(byte[] input, int offset, int length_byte){
        char[] tmp = new char[length_byte/2];
        for (int i=0; i<length_byte/2; i++){
            tmp[i] = byteToChar_LE(input, offset+2*i);
        }
        return  tmp;
    }

}
