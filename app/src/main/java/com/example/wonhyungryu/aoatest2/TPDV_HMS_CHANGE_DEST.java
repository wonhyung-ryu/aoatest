package com.example.wonhyungryu.aoatest2;

import android.util.Log;

/**
 * Created by wonhyung.ryu on 2016-10-04.
 */

public class TPDV_HMS_CHANGE_DEST extends Tx_Packet {

    private char TPDV_HMS_CHANGE_DEST_dataLen = 80;

    private double latitude;
    private double longitude;
    private char[] POIName = new char[32];

    private byte [] data = new byte[TPDV_HMS_CHANGE_DEST_dataLen+12];

    TPDV_HMS_CHANGE_DEST(){
        // start frame...consider Byte order
        charToBytes_LE(STARTFRAME, data, 0);

        sender = ID_TPDV;
        receiver = ID_IVI;
        data[senderAddr] = sender;
        data[receiverAddr] = receiver;

        // message ID...consider Byte order
        charToBytes_LE((char)0x01, data, mIDAddr);

        // data length...consider Byte order
        charToBytes_LE(TPDV_HMS_CHANGE_DEST_dataLen, data, datalenAddr);

        // end frame...consider Byte order
        intToBytes_LE(ENDFRAME, data, TPDV_HMS_CHANGE_DEST_dataLen+8);
    }

    //latitude of new destination -90.0	~ 90.0
    public void setLatitude(double latitude) {
        this.latitude = latitude;
        doubleToBytes_LE(latitude, data, dataAddr);
    }

    //longitude of new destination -180.0 ~ 180.0
    public void setLongitude(double longitude) {
        this.longitude = longitude;
        doubleToBytes_LE(longitude, data, dataAddr+8);
    }

    public void setPOIName(String sPOIName) {
        if (sPOIName.length() > 64/2) return;

        POIName = sPOIName.toCharArray();
        for (int i=0; i<sPOIName.length(); i++) {
            charToBytes_LE(POIName[i], data, (dataAddr+8+8)+i*2);
        }

    }

    public byte[] getPacket(){
        return data;
    }

}
