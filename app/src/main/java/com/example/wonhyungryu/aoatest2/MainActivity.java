package com.example.wonhyungryu.aoatest2;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements Runnable {

    String res_buf = new String();
    int result_line = 0;
    private static final String ACTION_USB_PERMISSION = "com.example.wonhyungryu.aoatest2.action.USB_PERMISSION";
    private static final String TAG = "[AOATest]";

    private UsbManager mUsbManager;
    private PendingIntent mPermissionIntent;
    private boolean mPermissionRequestPending;

    UsbAccessory mAccessory;
    ParcelFileDescriptor mFileDescriptor;
    FileInputStream mInputStream;
    FileOutputStream mOutputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsbManager = UsbManager.getInstance(this);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        registerReceiver(mUsbReceiver, filter);

        if (getLastNonConfigurationInstance() != null) {
            mAccessory = (UsbAccessory) getLastNonConfigurationInstance();
            openAccessory(mAccessory);
        }

        Button mBTN_S1=(Button)findViewById(R.id.BTN_S1);
        mBTN_S1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                makeData();
                //result_win_log("SEND 1", true);
            }
        });
/*
        Button mBTN_S2=(Button)findViewById(R.id.BTN_S2);
        mBTN_S2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                byte []buf = {(byte) 0x11, (byte) 0x43, (byte) 0xA1, (byte) 0x32, (byte) 0x24, (byte) 0x07, (byte) 0xB4, (byte) 0xFC,(byte) 0x43, (byte) 0xBC};

                TR_packet transPkt = new TR_packet();

                transPkt.packet_init((char)buf.length);
                transPkt.setSender(transPkt.ID_DM);
                transPkt.setReceiver(transPkt.ID_ALL);
                transPkt.setmID((char)0x44);
                transPkt.setData(buf);

                Log.i(TAG, "Tx data: " + byteArrayToHex(transPkt.getPkt_buf()));

                RCV_packet rPkt = new RCV_packet();
                int err = rPkt.pktParse(transPkt.getPkt_buf());
                if (err != 0) {
                    Log.i(TAG, "Receive ERROR : "+err);
                    for (int k = 0; k<buf.length+12; k++) {
                        Log.d(TAG,"0x" + toHexString(transPkt.getPkt_buf()[k]) + " ");
                    }
                }
                result_win_log( "STARTFRAME "+ (rPkt.getSTARTFRAME()&0xFFFF),true);
                result_win_log( "Sender "+ new Byte(rPkt.getSender()).toString(),true);
                result_win_log( "Receiver "+ new Byte(rPkt.getReceiver()).toString(),true);
                result_win_log( "mID "+ (rPkt.getmID()&0xFFFF),true);
                result_win_log( "ENDFRAME "+ (rPkt.getENDFRAME()&0xFFFFFFFF),true);

            }
        });

        Button mBTN_S3=(Button)findViewById(R.id.BTN_S3);
        mBTN_S3.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                makeData();
            }
        });*/
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbAccessory accessory = UsbManager.getAccessory(intent);
                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        openAccessory(accessory);
                    } else {
                        Log.d(TAG, "permission denied for accessory " + accessory);
                        result_win_log("permission denied for accessory" + accessory, true);
                    }
                    mPermissionRequestPending = false;
                }
            } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                UsbAccessory accessory = UsbManager.getAccessory(intent);
                if (accessory != null && accessory.equals(mAccessory)) {
                    Log.d(TAG, "ACTION_USB_ACCESSORY_DETACHED");
                    result_win_log("ACTION_USB_ACCESSORY_DETACHED", true);
                    closeAccessory();
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        UsbAccessory[] accessories = mUsbManager.getAccessoryList();
        UsbAccessory accessory = (accessories == null ? null : accessories[0]);
        if (accessory != null) {
            result_win_log("USB Host connected", true);
            Log.d(TAG, "USB Host connected");
            if (mUsbManager.hasPermission(accessory)) {
                openAccessory(accessory);
            } else {
                synchronized (mUsbReceiver) {
                    if (!mPermissionRequestPending) {
                        mUsbManager.requestPermission(accessory,
                                mPermissionIntent);
                        mPermissionRequestPending = true;
                    }
                }
            }
        } else {
            Log.d(TAG, "mAccessory is null");
            result_win_log("mAccessory is null", true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        closeAccessory();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mUsbReceiver);
        super.onDestroy();
    }

    private void openAccessory(UsbAccessory accessory) {
        mFileDescriptor = mUsbManager.openAccessory(accessory);
        if (mFileDescriptor != null) {
            mAccessory = accessory;
            FileDescriptor fd = mFileDescriptor.getFileDescriptor();
            mInputStream = new FileInputStream(fd);
            mOutputStream = new FileOutputStream(fd);
            Thread thread = new Thread(null, this, "AOATest");
            thread.start();
            Log.d(TAG, "accessory opened");
            result_win_log("accessory opened", true);
        } else {
            Log.d(TAG, "accessory open fail");
            result_win_log("accessory open fail", true);
        }
    }

    private void closeAccessory() {
        try {
            if (mFileDescriptor != null) {
                mFileDescriptor.close();
            }
        } catch (IOException e) {
        } finally {
            mFileDescriptor = null;
            mAccessory = null;
        }
    }

    public void run() {
        int ret = 0;
        byte[] buffer = new byte[16384];

        while (ret >= 0) {
            try {
                ret = mInputStream.read(buffer);
                Log.d(TAG, "mInputStream.read : "+ ret);
                Log.d(TAG, "Rx data: " + byteArrayToHex(buffer));
            } catch (IOException e) {
                break;
            }

            if (ret > 0) {
                RCV_packet rPkt = new RCV_packet();
                int err = rPkt.pktParse(buffer);
                if (err != 0) {
                    Log.i(TAG, "Receive ERROR : " + err);
                    for (int k = 0; k < 50; k++) {
                        Log.d(TAG, "0x" + toHexString(buffer[k]) + " ");
                    }
                }
                Log.d(TAG, "STARTFRAME " + (rPkt.getSTARTFRAME() & 0xFFFF));
                Log.d(TAG, "Sender " + Integer.toString(rPkt.getSender() & 0xFF));
                Log.d(TAG, "Receiver " + Integer.toString(rPkt.getReceiver() & 0xFF));
                Log.d(TAG, "mID " + (rPkt.getmID() & 0xFFFF));
                Log.d(TAG, "ENDFRAME " + (rPkt.getENDFRAME() & 0xFFFFFFFF));
            }

        }
    }

    private int composeInt(byte hi, byte lo) {
        int val = (int) hi & 0xff;
        val *= 256;
        val += (int) lo & 0xff;
        return val;
    }

    // Transfer Example Code
    void makeData () {
        byte []buf = {(byte) 26, (byte) 24, (byte) 3, (byte) 0x02, (byte) 0x01,
                        (byte) 0x00, (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0};

        TR_packet transPkt = new TR_packet();

        transPkt.packet_init((char)10);
        transPkt.setSender(transPkt.ID_TPDV);
        transPkt.setReceiver(transPkt.ID_TPCR);
        transPkt.setmID((char)0x02);
        transPkt.setData(buf);

        for (int k1 = 0; k1<12+10; k1++) {
            result_win_log("0x" + toHexString(transPkt.getPkt_buf()[k1]) + " ", false);
        }
        sendCommand(transPkt.getPkt_buf());
    }

    public void sendCommand(byte[] buffer) {

        if (mOutputStream != null) {
            try {
                mOutputStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "write failed", e);
                result_win_log("Write failed",true);
            }
        }
    }


    public static String byteArrayToHex(byte[] ba) {
        StringBuffer sb = new StringBuffer(ba.length * 2);
        String hexNumber;
        for (int x = 0; x < ba.length; x++) {
            hexNumber = "0" + Integer.toHexString(0xff & ba[x]);
            sb.append(hexNumber.substring(hexNumber.length() - 2));
        }
        return sb.toString();
    }

    private void result_win_log (String lmsg, boolean yn) {
        TextView mResult = (TextView) findViewById(R.id.result);
        if (result_line >= 25) {
            result_line = 0;
            mResult.setText("");
        }
        if (yn){
            res_buf = mResult.getText().toString() + "\n" + lmsg;
            result_line += 1;
        } else {
            res_buf = mResult.getText().toString() + lmsg;
        }
        mResult.setText(res_buf);
    }

    public static String toHexString(byte b) {
        char[] digits = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                'U', 'V', 'W', 'X', 'Y', 'Z'
        };
        char[] buf = new char[2]; // We always want two digits.
        buf[0] = digits[(b >> 4) & 0xf];
        buf[1] = digits[b & 0xf];

        return new String(buf, 0 ,2);
    }

}
