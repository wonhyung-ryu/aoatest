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

        Button mBTN_S1 = (Button) findViewById(R.id.BTN_S1);
        mBTN_S1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                TPDV_HMS_HVAC_CONTROL tmp = new TPDV_HMS_HVAC_CONTROL();
                tmp.setDriverTemp((byte) 21);
                tmp.setRearDefrost((byte) 1);
                tmp.setFrontDefrost((byte) 0);
                tmp.setAuto((byte) 1);
                tmp.setDriverSeatHeat((byte) 1);
                tmp.setPassengerSeatHeat((byte) 0);
                tmp.setAC((byte) 0);
                tmp.setAirFlow((byte) 0x02);
                tmp.setFanSpeed((byte) 4);
                tmp.setPassengerTemp((byte) 10);

                sendCommand(tmp.getPacket());
            }
        });

        Button mBTN_S2 = (Button) findViewById(R.id.BTN_S2);
        mBTN_S2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                TPCR_HMS_HVAC_CONTROL tmp = new TPCR_HMS_HVAC_CONTROL();
                tmp.setDriverTemp((byte) 21);
                tmp.setRearDefrost((byte) 1);
                tmp.setFrontDefrost((byte) 0);
                tmp.setAuto((byte) 1);
                tmp.setDriverSeatHeat((byte) 1);
                tmp.setPassengerSeatHeat((byte) 0);
                tmp.setAC((byte) 0);
                tmp.setAirFlow((byte) 0x02);
                tmp.setFanSpeed((byte) 4);
                tmp.setPassengerTemp((byte) 20);

                sendCommand(tmp.getPacket());
            }
        });

        Button mBTN_S3 = (Button) findViewById(R.id.BTN_S3);
        mBTN_S3.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
/*
                TPDV_HMS_CHANGE_DEST trs = new TPDV_HMS_CHANGE_DEST();

                trs.setLatitude((double)3.1415926);
                trs.setLongitude((double)0x10);
                trs.setPOIName("서울시성북구 Seoul SungBokGu 012345678");
                sendCommand(trs.getPacket());
                */
                double aa = 3.141592642;
                double bb = 0;

                byte[] barr = new byte[10];
                Tx_Packet tt = new Tx_Packet();
                tt.doubleToBytes_LE(aa, barr, 1);
                Log.i(TAG, byteArrayToHex(barr));

                conversion_LE rr = new conversion_LE();
                bb = rr.byteToDouble_LE(barr, 1);

                Log.i(TAG, "aa=" + String.valueOf(aa));
                Log.i(TAG, "bb=" + String.valueOf(bb));

                float cc = 3.1592642F;
                float dd = 0;

                byte[] carr = new byte[10];
                Tx_Packet tt2 = new Tx_Packet();
                tt2.floatToBytes_LE(cc, carr, 1);
                Log.i(TAG, byteArrayToHex(carr));

                conversion_LE rr2 = new conversion_LE();
                dd = rr2.byteToFloat_LE(carr, 1);

                Log.i(TAG, "cc=" + String.valueOf(cc));
                Log.i(TAG, "dd=" + String.valueOf(dd));

                char[] ca = new char[]{'한', '글', '도', '잘', '되', '나'};
                byte[] darr = new byte[20];
                Tx_Packet tt3 = new Tx_Packet();
                for (int i = 0; i < 6; i++) {
                    tt3.charToBytes_LE(ca[i], darr, 2 + i * 2);
                }
                Log.i(TAG, "ca=" + String.valueOf(ca));
                Log.i(TAG, "ca=" + byteArrayToHex(darr));

                Log.i(TAG, "ca=" + String.valueOf(rr2.byteToCharArray_LE(darr, 2, 12)));

            }
        });
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
                Log.d(TAG, "mInputStream.read : " + ret);
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
                if (rPkt.getSender() == RCV_packet.ID_CM) {
                    switch (rPkt.getmID()) {
                        case RCV_packet.HMS_COMMON_SURROUNDING_VEHICLE_INFO:
                            rcv_HMS_COMMON_SURROUNDING_VEHICLE_INFO rd1 = new rcv_HMS_COMMON_SURROUNDING_VEHICLE_INFO(rPkt.getData());
                            Log.i(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.getCount " + String.valueOf(rd1.getCount()));
                            Log.i(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.getTargetID " + String.valueOf(rd1.getTargetID()));
                            Log.i(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.getVehicleAltitude " + String.valueOf(rd1.getVehicleAltitude()));
                            Log.i(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.getVehicleDistanceToCollision " + String.valueOf(rd1.getVehicleDistanceToCollision()));
                            Log.i(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.getVehicleHeading " + String.valueOf(rd1.getVehicleHeading()));
                            Log.i(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.getVehicleID " + String.valueOf(rd1.getVehicleID()));
                            Log.i(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.getVehicleLatitude " + String.valueOf(rd1.getVehicleLatitude()));
                            Log.i(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.getVehicleLogitude " + String.valueOf(rd1.getVehicleLogitude()));
                            Log.i(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.getVehicleName " + String.valueOf(rd1.getVehicleName()));
                            Log.i(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.getVehicleRelativeSpeed " + String.valueOf(rd1.getVehicleRelativeSpeed()));
                            Log.i(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.getVehicleAbsoluteSpeed " + String.valueOf(rd1.getVehicleAbsoluteSpeed()));
                            Log.i(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.getVehicleType " + String.valueOf(rd1.getVehicleType()));
                            break;

                        case RCV_packet.HMS_COMMON_SCENARIO_INFO:
                            rcv_HMS_COMMON_SCENARIO_INFO rd2 = new rcv_HMS_COMMON_SCENARIO_INFO(rPkt.getData());
                            Log.i(TAG, "rcv_HMS_COMMON_SCENARIO_INFO.getCloud " + String.valueOf(rd2.getCloud()));
                            break;

                        case RCV_packet.HMS_COMMON_RECOMMEND_DRIVING_GUIDE:
                            rcv_HMS_COMMON_RECOMMEND_DRIVING_GUIDE rd3 = new rcv_HMS_COMMON_RECOMMEND_DRIVING_GUIDE(rPkt.getData());
                            Log.i(TAG, "HMS_COMMON_RECOMMEND_DRIVING_GUIDE.getSpeedUp " + String.valueOf(rd3.getSpeedUp()));
                            break;

                        case RCV_packet.HMS_COMMON_NAVI_GUIDANCE_INFO:
                            rcv_HMS_COMMON_NAVI_GUIDANCE_INFO rd4 = new rcv_HMS_COMMON_NAVI_GUIDANCE_INFO(rPkt.getData());
                            Log.i(TAG, "HMS_COMMON_NAVI_GUIDANCE_INFO.getCurrentRoadName " + String.valueOf(rd4.getCurrentRoadName()));
                            break;

                        case RCV_packet.HMS_COMMON_AUTONOMOUS_DRIVING:
                            rcv_HMS_COMMON_AUTONOMOUS_DRIVING rd5 = new rcv_HMS_COMMON_AUTONOMOUS_DRIVING(rPkt.getData());
                            Log.i(TAG, "HMS_COMMON_AUTONOMOUS_DRIVING.getCommand " + String.valueOf(rd5.getCommand()));
                            break;

                        case RCV_packet.HMS_COMMON_MANUAL_DRIVING:
                            rcv_HMS_COMMON_MANUAL_DRIVING rd6 = new rcv_HMS_COMMON_MANUAL_DRIVING(rPkt.getData());
                            Log.i(TAG, "rcv_HMS_COMMON_MANUAL_DRIVING.getCommand " + String.valueOf(rd6.getCommand()));
                            break;

                        case RCV_packet.HMS_COMMON_MODE_READY_COUNTDOWN:
                            rcv_HMS_COMMON_MODE_READY_COUNTDOWN rd7 = new rcv_HMS_COMMON_MODE_READY_COUNTDOWN(rPkt.getData());
                            Log.i(TAG, "rcv_HMS_COMMON_MODE_READY_COUNTDOWN.getCount " + String.valueOf(rd7.getCount()));
                            break;

                        case RCV_packet.HMS_COMMON_DRIVER_STATUS_INFO:
                            rcv_HMS_COMMON_DRIVER_STATUS_INFO rd8 = new rcv_HMS_COMMON_DRIVER_STATUS_INFO(rPkt.getData());
                            Log.i(TAG, "rcv_HMS_COMMON_DRIVER_STATUS_INFO.getGaze " + String.valueOf(rd8.getGaze()));
                            break;

                        case RCV_packet.HMS_COMMON_SAFETY_LEVEL_INFO:
                            rcv_HMS_COMMON_SAFETY_LEVEL_INFO rd9 = new rcv_HMS_COMMON_SAFETY_LEVEL_INFO(rPkt.getData());
                            Log.i(TAG, "rcv_HMS_COMMON_SAFETY_LEVEL_INFO.getGaze " + String.valueOf(rd9.getLevel()));
                            break;

                        case RCV_packet.HMS_COMMON_HVAC_INFO:
                            rcv_HMS_COMMON_HVAC_INFO rd10 = new rcv_HMS_COMMON_HVAC_INFO(rPkt.getData());
                            Log.i(TAG, "rcv_HMS_COMMON_HVAC_INFO.getPassengerTemp " + String.valueOf(rd10.getPassengerTemp()));
                            break;

                        default:
                            Log.i(TAG, "What?? mID = " + String.valueOf(rPkt.getmID()));
                            break;
                    }
                } else if (rPkt.getSender() == RCV_packet.ID_TPCR) {
                    switch (rPkt.getmID()) {
                        case RCV_packet.TPCR_HMS_HVAC_CONTROL:
                            rcv_TPCR_HMS_HVAC_CONTROL rd1 = new rcv_TPCR_HMS_HVAC_CONTROL(rPkt.getData());
                            Log.i(TAG, "rcv_TPCR_HMS_HVAC_CONTROL.getPassengerTemp " + String.valueOf(rd1.getPassengerTemp()));
                            break;

                        default:
                            Log.i(TAG, "What?? mID = " + String.valueOf(rPkt.getmID()));
                            break;
                    }
                } else if (rPkt.getSender() == RCV_packet.ID_TPDV) {
                    switch (rPkt.getmID()) {
                        case RCV_packet.TPDV_HMS_HVAC_CONTROL:
                            rcv_TPDV_HMS_HVAC_CONTROL rd1 = new rcv_TPDV_HMS_HVAC_CONTROL(rPkt.getData());
                            Log.i(TAG, "rcv_TPDV_HMS_HVAC_CONTROL.getPassengerTemp " + String.valueOf(rd1.getPassengerTemp()));
                            break;

                        default:
                            Log.i(TAG, "What?? mID = " + String.valueOf(rPkt.getmID()));
                            break;
                    }
                } else if (rPkt.getSender() == RCV_packet.ID_SM) {
                    switch (rPkt.getmID()) {
                        case RCV_packet.HMS_COMMON_MUSIC_INFO:
                            rcv_HMS_COMMON_MUSIC_INFO rd1 = new rcv_HMS_COMMON_MUSIC_INFO(rPkt.getData());
                            Log.i(TAG, "rcv_HMS_COMMON_MUSIC_INFO.getTitle " + String.valueOf(rd1.getTitle()));
                            break;

                        default:
                            Log.i(TAG, "What?? mID = " + String.valueOf(rPkt.getmID()));
                            break;
                    }
                } else if (rPkt.getSender() == RCV_packet.ID_VM) {
                    switch (rPkt.getmID()) {
                        case RCV_packet.HMS_COMMON_DISPLAY_DANGER_INFO:
                            rcv_HMS_COMMON_DISPLAY_DANGER_INFO rd1 = new rcv_HMS_COMMON_DISPLAY_DANGER_INFO(rPkt.getData());
                            Log.i(TAG, "rcv_HMS_COMMON_DISPLAY_DANGER_INFO.getParam " + String.valueOf(rd1.getParam()));
                            break;

                        case RCV_packet.HMS_COMMON_DISPLAY_DANGER_ALARM:
                            rcv_HMS_COMMON_DISPLAY_DANGER_ALARM rd2 = new rcv_HMS_COMMON_DISPLAY_DANGER_ALARM(rPkt.getData());
                            Log.i(TAG, "rcv_HMS_COMMON_DISPLAY_DANGER_ALARM.getInterval " + String.valueOf(rd2.getInterval()));
                            break;
                        default:
                            Log.i(TAG, "What?? mID = " + String.valueOf(rPkt.getmID()));
                            break;
                    }
                } else {
                    switch (rPkt.getmID()) {
                        case RCV_packet.HMS_COMMON_GPS_INFO:
                            rcv_HMS_COMMON_GPS_INFO rd1 = new rcv_HMS_COMMON_GPS_INFO(rPkt.getData());
                            Log.i(TAG, "rcv_HMS_COMMON_GPS_INFO.getLatitude " + String.valueOf(rd1.getLatitude()));
                            break;

                        case RCV_packet.HMS_TPDV_DISPLAY_GOAL_MAP:
                            rcv_HMS_TPDV_DISPLAY_GOAL_MAP rd2 = new rcv_HMS_TPDV_DISPLAY_GOAL_MAP(rPkt.getData());
                            Log.i(TAG, "rcv_HMS_TPDV_DISPLAY_GOAL_MAP.getLatitude " + String.valueOf(rd2.getLatitude()));
                            break;

                        case RCV_packet.HMS_TPDV_DISPLAY_CURR_MAP:
                            rcv_HMS_TPDV_DISPLAY_CURR_MAP rd3 = new rcv_HMS_TPDV_DISPLAY_CURR_MAP(rPkt.getData());
                            Log.i(TAG, "rcv_HMS_TPDV_DISPLAY_CURR_MAP.getLatitude " + String.valueOf(rd3.getLatitude()));
                            break;

                        default:
                            Log.i(TAG, "What?? sender = " + String.valueOf(rPkt.getSender()));
                            Log.i(TAG, "What?? mID = " + String.valueOf(rPkt.getmID()));
                            break;
                    }
                }
            }

        }
    }

    public void sendCommand(byte[] buffer) {

        if (mOutputStream != null) {
            try {
                mOutputStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "write failed", e);
                result_win_log("Write failed", true);
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

    String res_buf = new String();
    int result_line = 0;

    private void result_win_log(String lmsg, boolean yn) {
        TextView mResult = (TextView) findViewById(R.id.result);
        if (result_line >= 25) {
            result_line = 0;
            mResult.setText("");
        }
        if (yn) {
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

        return new String(buf, 0, 2);
    }

}
