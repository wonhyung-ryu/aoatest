package com.example.wonhyungryu.aoatest2;

import android.util.Log;

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


    //public class ModuleID
    public static final byte ID_NONE = 0x00;
    public static final byte ID_CM = 0x01; // Connection Manager
    public static final byte ID_CRM = 0x02; // Context Recognition Manager
    public static final byte ID_PM	 = 0x03; // Priority Manager
    public static final byte ID_DM = 0x04; // Decision Manager
    public static final byte ID_VM	= 0x05; // Display Manager
    public static final byte ID_SM	= 0x06; // Sound Manager
    public static final byte ID_HM	= 0x07; // Haptic Manager
    public static final byte ID_CLUSTER =	0x08; // Cluster
    public static final byte ID_SCANER = 0x09; // SCANeR PC
    public static final byte ID_LGEDSM = 0x0A; // LGE DSM PC
    public static final byte ID_SMARTEYE = 0x0B; // SmartEye PC
    public static final byte ID_ARHUD = 0x0C; // AR-HUD Controller
    public static final byte ID_LED = 0x0D; // LED
    public static final byte ID_TPDV = 0x0E; // Touch Pad 1, display 4
    public static final byte ID_TPCR = 0x0F; // Touch Pad 2, display 5
    public static final byte ID_JOGDIAL = 0x10; // Jog Dial
    public static final byte ID_STEERING = 0x11; // Steering Wheel
    public static final byte ID_IVI = 0x12; // IVI (Navigation, Media)
    public static final byte ID_HMS = 0x13; // HMS
    public static final byte ID_ALL = (byte) 0xFF; // ALL
}
