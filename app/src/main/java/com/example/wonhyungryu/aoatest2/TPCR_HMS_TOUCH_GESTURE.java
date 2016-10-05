package com.example.wonhyungryu.aoatest2;

/**
 * Created by wonhyung.ryu on 2016-10-05.
 */

public class TPCR_HMS_TOUCH_GESTURE extends Tx_Packet{
    private char TPCR_HMS_TOUCH_GESTURE_dataLen = 2;

    private byte gesture_type;
    private byte finger_count;

    private byte reserved1;
    private byte reserved2;

    private byte [] data = new byte[TPCR_HMS_TOUCH_GESTURE_dataLen+12];

    TPCR_HMS_TOUCH_GESTURE(){
        // start frame...consider Byte order
        charToBytes_LE(STARTFRAME, data, 0);

        sender = ID_TPCR;
        receiver = ID_IVI;
        data[senderAddr] = sender;
        data[receiverAddr] = receiver;

        // message ID...consider Byte order
        charToBytes_LE((char)0x01, data, mIDAddr);

        // data length...consider Byte order
        charToBytes_LE(TPCR_HMS_TOUCH_GESTURE_dataLen, data, datalenAddr);

        // end frame...consider Byte order
        intToBytes_LE(ENDFRAME, data, TPCR_HMS_TOUCH_GESTURE_dataLen+8);
    }

    /*  0x00: tap
        0x01: flick left
        0x02: flick right
        0x03: flick up
        0x04: flick down */
    public void setGesture_type(byte gesture_type) {
        this.gesture_type = gesture_type;
        data[dataAddr] = gesture_type;
    }

    //손가락 개수
    public void setFinger_count(byte finger_count) {
        this.finger_count = finger_count;
        data[dataAddr+1] = finger_count;
    }


    public byte[] getPacket(){
        return data;
    }
}
