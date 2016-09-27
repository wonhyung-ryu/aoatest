package com.example.wonhyungryu.aoatest2;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
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

import static android.R.attr.value;

public class MainActivity extends AppCompatActivity implements Runnable {

    String res_buf = new String();
    int result_line = 0;
    private static final String ACTION_USB_PERMISSION = "com.exam.aoatest.action.USB_PERMISSION";
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
        enableControls(false);

        // public void sendCommand(byte receiver, byte[] ID, byte[] datalen, byte[] data)
        Button mBTN_S1=(Button)findViewById(R.id.BTN_S1);
        mBTN_S1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                sendCommand((byte) 0x12, new byte[]{0x00, 0x01}, new byte[]{0x00, 0x02}, new byte[]{(byte) 0xEF, (byte) 0xFF});
                //result_win_log("SEND 1", true);
            }
        });

        Button mBTN_S2=(Button)findViewById(R.id.BTN_S2);
        mBTN_S2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                sendCommand((byte) 0x13, new byte[]{0x00, 0x02}, new byte[]{0x00, 0x1E},
                        new byte[]{(byte) 0x11, (byte) 0x43, (byte) 0xA1, (byte) 0x32, (byte) 0x24, (byte) 0x07, (byte) 0xB4, (byte) 0xFC,
                                (byte) 0x43, (byte) 0xFF, (byte) 0x24, (byte) 0xFF, (byte) 0xFF, (byte) 0x07, (byte) 0xFC, (byte) 0xB4,
                                (byte) 0xEF, (byte) 0x43, (byte) 0x24, (byte) 0xFF, (byte) 0x07, (byte) 0xFF, (byte) 0xB4, (byte) 0xFF,
                                (byte) 0x24, (byte) 0xFC, (byte) 0x07, (byte) 0x43, (byte) 0x24, (byte) 0xFC});
            }
        });

        Button mBTN_S3=(Button)findViewById(R.id.BTN_S3);
        mBTN_S3.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                sendCommand((byte) 0xFF, new byte[]{0x00, 0x03}, new byte[]{0x00, 0x05}, new byte[]{(byte) 0x11, (byte) 0x10, (byte) 0x09, (byte) 0x08, (byte) 0x07});
            }
        });
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            result_win_log("BroadcastReceiver", true);
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
                    result_win_log("ACTION_USB_ACCESSORY_DETACHED", true);
                    closeAccessory();
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if (mInputStream != null && mOutputStream != null) {
            return;
        }

        UsbAccessory[] accessories = mUsbManager.getAccessoryList();
        UsbAccessory accessory = (accessories == null ? null : accessories[0]);
        if (accessory != null) {
            result_win_log("USB Host connected", true);
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
            enableControls(true);
        } else {
            Log.d(TAG, "accessory open fail");
            result_win_log("accessory open fail", true);
        }
    }

    private void closeAccessory() {
        enableControls(false);

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
        byte[] buffer = new byte[20];
        int i,j;

        while (ret >= 0) {
            try {
                ret = mInputStream.read(buffer);
            } catch (IOException e) {
                break;
            }

            j = buffer[5]<<8+buffer[6]+8;  // total packet length
            for (int k = 0; k<j; k++) {
                result_win_log("0x" + toHexString(buffer[k]) + " ", false);
            }
            i = 0;
            while (i < ret) {
                int len = ret - i;
/*
                switch (buffer[i]) {
                    case 0x1:
                        if (len >= 3) {
                            Message m = Message.obtain(mHandler, MESSAGE_SWITCH);
                            m.obj = new SwitchMsg(buffer[i + 1], buffer[i + 2]);
                            mHandler.sendMessage(m);
                        }
                        i += 3;
                        break;

                    case 0x4:
                        if (len >= 3) {
                            Message m = Message.obtain(mHandler,
                                    MESSAGE_TEMPERATURE);
                            m.obj = new TemperatureMsg(composeInt(buffer[i + 1],
                                    buffer[i + 2]));
                            mHandler.sendMessage(m);
                        }
                        i += 3;
                        break;

                    case 0x5:
                        if (len >= 3) {
                            Message m = Message.obtain(mHandler, MESSAGE_LIGHT);
                            m.obj = new LightMsg(composeInt(buffer[i + 1],
                                    buffer[i + 2]));
                            mHandler.sendMessage(m);
                        }
                        i += 3;
                        break;

                    case 0x6:
                        if (len >= 3) {
                            Message m = Message.obtain(mHandler, MESSAGE_JOY);
                            m.obj = new JoyMsg(buffer[i + 1], buffer[i + 2]);
                            mHandler.sendMessage(m);
                        }
                        i += 3;
                        break;

                    default:
                        Log.d(TAG, "unknown msg: " + buffer[i]);
                        i = len;
                        break;
                }*/
            }

        }
    }

    protected void enableControls(boolean enable) {
    }

    private int composeInt(byte hi, byte lo) {
        int val = (int) hi & 0xff;
        val *= 256;
        val += (int) lo & 0xff;
        return val;
    }
/*
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SWITCH:
                    SwitchMsg o = (SwitchMsg) msg.obj;
                    handleSwitchMessage(o);
                    break;

                case MESSAGE_TEMPERATURE:
                    TemperatureMsg t = (TemperatureMsg) msg.obj;
                    handleTemperatureMessage(t);
                    break;

                case MESSAGE_LIGHT:
                    LightMsg l = (LightMsg) msg.obj;
                    handleLightMessage(l);
                    break;

                case MESSAGE_JOY:
                    JoyMsg j = (JoyMsg) msg.obj;
                    handleJoyMessage(j);
                    break;

            }
        }
    };
*/
// sendCommand((byte) 0x12, new byte[]{0x00, 0x01}, new byte[]{0x00, 0x02}, new byte[]{(byte) 0xEF, (byte) 0xFF});

    public void sendCommand(byte receiver, byte[] ID, byte[] datalen, byte[] data) {
        int pack_len = (int)datalen[0]*256 + (int)datalen[1] + 12;
        byte[] buffer = new byte[pack_len];

        buffer[0] = (byte) 0x00; // start frame(2byte)
        buffer[1] = (byte) 0xDD; // start frame
        buffer[2] = (byte) 0x0E; // sender  0x0E (Display 4), 0x0F (display 5)
        buffer[3] = receiver; // receiver 0x12 (IVI)
        buffer[4] = ID[0]; // ID (2byte)
        buffer[5] = ID[1]; // ID
        buffer[6] = datalen[0]; // data length (2byte)
        buffer[7] = datalen[1]; // data length
        buffer[pack_len-4] = (byte)0x00; // end frame
        buffer[pack_len-3] = (byte)0x00; // end frame
        buffer[pack_len-2] = (byte)0x00; // end frame
        buffer[pack_len-1] = (byte)0xE0; // end frame

        Log.i(TAG, "pack_len " + pack_len);
/*
        for (int k=0; k <= 6; k++) {
            Log.i(TAG, "buffer " + buffer[k]);
        }
*/
        if (buffer[6]!= 0 || buffer[7]!= 0) {
            System.arraycopy(data, 0, buffer, 8, pack_len - 12);
        }

        result_win_log("send: ", true);
        for (int k1 = 0; k1<pack_len; k1++) {
            result_win_log("0x" + toHexString(buffer[k1]) + " ", false);
        }

        if (mOutputStream != null) {
            try {
                mOutputStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "write failed", e);
                result_win_log("Write failed",true);
            }
        }
    }
    /*
    public void sendCommand(byte command, byte target, int value) {
        byte[] buffer = new byte[3];
        if (value > 255)
            value = 255;

        buffer[0] = command;
        buffer[1] = target;
        buffer[2] = (byte) value;
        if (mOutputStream != null && buffer[1] != -1) {
            try {
                mOutputStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "write failed", e);
                result_win_log("Write failed",true);
            }
        }
    }
*/
    private void result_win_log (String lmsg, boolean yn) {
        TextView mresult = (TextView) findViewById(R.id.result);
        if (result_line >= 25) {
            result_line = 0;
            mresult.setText("");
        }
        if (yn){
            res_buf = mresult.getText().toString() + "\n" + lmsg;
            result_line += 1;
        } else {
            res_buf = mresult.getText().toString() + lmsg;
        }
        mresult.setText(res_buf);
    }

    public static String toHexString(byte b) {
        char[] digits = UPPER_CASE_DIGITS;
        char[] buf = new char[2]; // We always want two digits.
        buf[0] = digits[(b >> 4) & 0xf];
        buf[1] = digits[b & 0xf];
        return new String(buf, 0 ,2);
    }

    private static final char[] UPPER_CASE_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z'
    };
}
