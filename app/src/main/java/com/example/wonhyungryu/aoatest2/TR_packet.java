package com.example.wonhyungryu.aoatest2;

/**
 * Created by wonhyung.ryu on 2016-09-29.
 */

public class TR_packet {
    private char STARTFRAME = 0x00DD;
    private byte sender; // ID_TPDV, ID_TPCR
    private byte receiver;
    private char mID; // message ID
    private char dlength; // data length

    private int ENDFRAME = 0xE0;

    private byte [] pkt_buf;

    public void packet_init(char data_length) {
        this.dlength = data_length;
        pkt_buf = new byte[data_length+12];
        // consider Byte order
        pkt_buf[1] = (byte) ((STARTFRAME & 0xFF00)>>8);
        pkt_buf[0] = (byte) (STARTFRAME & 0x00FF);

        // consider Byte order
        pkt_buf[7] = (byte) ((data_length & 0xFF00)>>8);
        pkt_buf[6] = (byte) (data_length & 0xFF);

        // consider Byte order
        pkt_buf[data_length+11] = (byte) ((ENDFRAME & 0xFF000000)>>24);
        pkt_buf[data_length+10] = (byte) ((ENDFRAME & 0x00FF0000)>>16);
        pkt_buf[data_length+9] = (byte) ((ENDFRAME & 0x0000FF00)>>8);
        pkt_buf[data_length+8] = (byte) (ENDFRAME & 0x000000FF);
    }

    public void setSender(byte sender) {
        this.sender = sender;
        pkt_buf[2] = (byte) sender;
    }

    public void setReceiver(byte receiver) {
        this.receiver = receiver;
        pkt_buf[3] = (byte) receiver;
    }

    public void setmID(char mID) {
        this.mID = mID;
        pkt_buf[5] = (byte) ((mID & 0xFF00)>>8);
        pkt_buf[4] = (byte) (mID & 0xFF);
    }

    public void setData(byte[] data) {
        System.arraycopy(data, 0, pkt_buf, 8, this.dlength);
    }

    public byte[] getPkt_buf(){
        return pkt_buf;
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