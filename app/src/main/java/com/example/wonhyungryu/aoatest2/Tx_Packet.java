package com.example.wonhyungryu.aoatest2;

import android.util.Log;

/**
 * Created by wonhyung.ryu on 2016-10-04.
 */

public class Tx_Packet {
    public char STARTFRAME = 0xDD;
    public byte sender; // ID_TPDV, ID_TPCR
    public byte receiver;
    public char dlength; // data length

    public int ENDFRAME = 0xE0;

    public void charToBytes_LE(char input, byte[] output, int offset  ) {
        output[0+offset] = (byte) (input&0x00FF);
        output[1+offset] = (byte) ((input&0xFF00) >> 8);
    }

    public void intToBytes_LE(int input, byte[] output , int offset ) {
        output[0+offset] = (byte) (input&0x000000FF);
        output[1+offset] = (byte) ((input&0x0000FF00) >> 8);
        output[2+offset] = (byte) ((input&0x00FF0000) >> 16);
        output[3+offset] = (byte) ((input&0xFF000000) >> 24);
    }

    public void doubleToBytes_LE(double input, byte[] output, int offset  ) {
        long bits = Double.doubleToLongBits(input);

        for(int i = 0; i < 8; i++) {
            output[i + offset] = (byte) ((bits >> (i * 8)) & 0xff);
        }

    }

    public static final byte senderAddr = 2;
    public static final byte receiverAddr = 3;
    public static final byte mIDAddr = 4;
    public static final byte datalenAddr = 6;
    public static final byte dataAddr = 8;

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


