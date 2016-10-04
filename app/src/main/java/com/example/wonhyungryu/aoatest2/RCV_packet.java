package com.example.wonhyungryu.aoatest2;

import android.util.Log;

import static com.example.wonhyungryu.aoatest2.TR_packet.ID_ALL;
import static com.example.wonhyungryu.aoatest2.TR_packet.ID_NONE;
import static com.example.wonhyungryu.aoatest2.TR_packet.ID_TPCR;
import static com.example.wonhyungryu.aoatest2.TR_packet.ID_TPDV;

/**
 * Created by wonhyung.ryu on 2016-09-29.
 */

public class RCV_packet {
    private char STARTFRAME = 0;
    private byte sender = ID_NONE; // ID_TPDV, ID_TPCR
    private byte receiver = ID_NONE;
    private char mID = 0; // message ID
    private char dlength = 0; // data length
    private int ENDFRAME = 0;
    private byte [] data;

    private static final String TAG = "[AOATest]";

    public char getSTARTFRAME() {
        return STARTFRAME;
    }

    public byte getSender() {
        return sender;
    }

    public byte getReceiver() {
        return receiver;
    }

    public char getmID() {
        return mID;
    }

    public byte[] getData() {
        return data;
    }

    public char getDlength() {
        return dlength;
    }

    public int getENDFRAME() {
        return ENDFRAME;
    }

    public int pktParse(byte[] buf) {
        // consider Byte order
        STARTFRAME = (char)(((buf[1]&0xFF)<<8)+(buf[0]&0xFF));
        if(STARTFRAME != 0x00DD){
            Log.i(TAG, "START FRAME incorrect! "+ STARTFRAME + " "+(buf[0]&0xFF) +" "+(buf[1]&0xFF));
            return -1;
        }
        sender = buf[2];
        Log.i(TAG, "sender : "+ sender);

        receiver = buf[3];
        if(receiver != ID_TPCR && receiver!= ID_TPDV && receiver != ID_ALL){
            Log.i(TAG, "I'm not receiver!"+receiver);
            return -2;
        }

        // consider Byte order
        mID = (char)(((buf[5]&0xFF)<<8)+(buf[4]&0xFF));
        Log.i(TAG, "mID : "+ mID);
        // consider Byte order
        dlength = (char)(((buf[7]&0xFF)<<8)+(buf[6]&0xFF));
        Log.i(TAG, "data length : "+ dlength);

        data = new byte[dlength];
        System.arraycopy(buf, 8, data, 0, dlength);

        // consider Byte order
        ENDFRAME = (((buf[8+dlength+3]&0xFF)<<24)+((buf[8+dlength+2]&0xFF)<<16)+((buf[8+dlength+1]&0xFF)<<8)+(buf[8+dlength+0]&0xFF));
        if(ENDFRAME != 0xE0) {
            Log.i(TAG, "END FRAME incorrect!"+ ENDFRAME);
            return -3;
        }

        return 0;
    }

}
