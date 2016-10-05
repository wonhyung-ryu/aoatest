package com.example.wonhyungryu.aoatest2;

/**
 * Created by wonhyung.ryu on 2016-10-05.
 */

public class TPCR_HMS_HVAC_CONTROL extends Tx_Packet {
    private char TPCR_HMS_HVAC_CONTROL_dataLen = 9;

    private byte DriverTemp;
    private byte PassengerTemp;
    private byte FanSpeed;
    private byte AirFlow;
    private byte AC;
    private byte SeatHeat;
    private byte Auto;
    private byte FrontDefrost;
    private byte RearDefrost;

    private byte reserved1;
    private byte reserved2;
    private byte reserved3;

    private byte [] data = new byte[TPCR_HMS_HVAC_CONTROL_dataLen+12];

    TPCR_HMS_HVAC_CONTROL(){
        // start frame...consider Byte order
        charToBytes_LE(STARTFRAME, data, 0);

        sender = ID_TPCR;
        receiver = ID_TPDV;
        data[senderAddr] = sender;
        data[receiverAddr] = receiver;

        // message ID...consider Byte order
        charToBytes_LE((char)0x04, data, mIDAddr);

        // data length...consider Byte order
        charToBytes_LE(TPCR_HMS_HVAC_CONTROL_dataLen, data, datalenAddr);

        // end frame...consider Byte order
        intToBytes_LE(ENDFRAME, data, TPCR_HMS_HVAC_CONTROL_dataLen+8);
    }
    //운전석 온도 설정 18	32
    public void setDriverTemp(byte driverTemp) {
        data[dataAddr] = driverTemp;
    }
    //조수석 온도 설정 18	32
    public void setPassengerTemp(byte passengerTemp) {
        data[dataAddr+1] = passengerTemp;
    }
    //풍량 0	5
    public void setFanSpeed(byte fanSpeed) {
        data[dataAddr+2] = fanSpeed;
    }
    /* "0x01 : Center
        0x02 : Center & Foot
        0x03 : Foot
        0x04 : Defrost & Foot" */
    public void setAirFlow(byte airFlow) {
        data[dataAddr+3] = airFlow;
    }

    //"0x01 : On    0x00 : Off"
    public void setAC(byte AC) {
        data[dataAddr+4] = AC;
    }

    //"0x01 : On     0x00 : Off"
    public void setSeatHeat(byte seatHeat) {
        data[dataAddr+5] = seatHeat;
    }
    //"0x01 : On     0x00 : Off"
    public void setAuto(byte auto) {
        data[dataAddr+6] = auto;
    }
    //"0x01 : On     0x00 : Off"
    public void setFrontDefrost(byte frontDefrost) {
        data[dataAddr+7] = frontDefrost;
    }
    //"0x01 : On     0x00 : Off"
    public void setRearDefrost(byte rearDefrost) {
        data[dataAddr+8] = rearDefrost;
    }

    public byte[] getPacket(){
        return data;
    }
}
