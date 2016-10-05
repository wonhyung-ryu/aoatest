package com.example.wonhyungryu.aoatest2;

/**
 * Created by wonhyung.ryu on 2016-10-05.
 */

public class TPCR_HMS_MEDIA_CONTROL extends Tx_Packet {
    private char TPCR_HMS_MEDIA_CONTROL_dataLen = 1;

    private byte command;

    private byte reserved1;
    private byte reserved2;
    private byte reserved3;

    private byte [] data = new byte[TPCR_HMS_MEDIA_CONTROL_dataLen+12];

    TPCR_HMS_MEDIA_CONTROL(){
        // start frame...consider Byte order
        charToBytes_LE(STARTFRAME, data, 0);

        sender = ID_TPCR;
        receiver = ID_IVI;
        data[senderAddr] = sender;
        data[receiverAddr] = receiver;

        // message ID...consider Byte order
        charToBytes_LE((char)0x02, data, mIDAddr);

        // data length...consider Byte order
        charToBytes_LE(TPCR_HMS_MEDIA_CONTROL_dataLen, data, datalenAddr);

        // end frame...consider Byte order
        intToBytes_LE(ENDFRAME, data, TPCR_HMS_MEDIA_CONTROL_dataLen+8);
    }

    /*      0x00: stop
            0x01: play
            0x02: pause
            0x03: previous music
            0x04: next music */
    public void setCommand(byte command) {
        this.command = command;
        data[dataAddr] = command;
    }

    public byte[] getPacket(){
        return data;
    }
}
