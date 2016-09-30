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
    private char STARTFRAME = 0x00DD;
    private byte sender; // ID_TPDV, ID_TPCR
    private byte receiver;
    private char mID; // message ID
    private char dlength; // data length
    private int ENDFRAME = 0xE0;
    private byte [] data;

    private static final String TAG = "[AOATest]";

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

    public int pktParse(byte[] buf) {
        // consider little-endian
        STARTFRAME = (char)((byte)(buf[1]<<8)+buf[0]);
        if(STARTFRAME != 0xDD){
            Log.i(TAG, "START FRAME incorrect!"+ STARTFRAME);
            return -1;
        }
        sender = buf[2];
        Log.i(TAG, "sender : "+ sender);

        receiver = buf[3];
        if(receiver != ID_TPCR && receiver!= ID_TPDV && receiver != ID_ALL){
            Log.i(TAG, "I'm not receiver!"+receiver);
            return -2;
        }

        // consider little-endian
        mID = (char)((byte)(buf[5]<<8)+buf[4]);
        Log.i(TAG, "mID : "+ mID);
        // consider little-endian
        dlength = (char)((byte)(buf[7]<<8)+buf[6]);
        Log.i(TAG, "data length : "+ dlength);

        data = new byte[dlength];
        System.arraycopy(buf, 8, data, 0, dlength);

        ENDFRAME = (char)(((byte)buf[8+dlength]<<24)+((byte)buf[8+dlength+1]<<16)+((byte)buf[8+dlength+2]<<8)+(byte)buf[8+dlength+3]);
        if(ENDFRAME != 0xE0) {
            Log.i(TAG, "END FRAME incorrect!"+ ENDFRAME);
            return -3;
        }

        return 0;
    }

}
