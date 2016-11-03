package com.example.wonhyungryu.aoatest2;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

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

    boolean acc_closed = true;
    boolean usb_detech = true;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final String LOGDIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/";
    static FileOutputStream AOAlogFileStream;

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

        // Assume thisActivity is the current activity
        int permissionCheck = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }

        String LOGFILE = "AOAlog" + Long.toString(System.currentTimeMillis() / 1000L) + ".txt";
        File lfile = new File(LOGDIR+ LOGFILE);

        try {
            if (!lfile.exists()) {
                lfile.createNewFile();
            }
            AOAlogFileStream = new FileOutputStream(lfile);
        } catch (Exception e) {
            e.printStackTrace();
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
                Log.e(TAG, "Send Packet");

            }
        });

        Button mBTN_S2 = (Button) findViewById(R.id.BTN_S2);
        mBTN_S2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                closeAccessory();
                Log.e(TAG, "closeAccessory");
            }
        });

        Button mBTN_S3 = (Button) findViewById(R.id.BTN_S3);
        mBTN_S3.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                UsbAccessory[] accessories = mUsbManager.getAccessoryList();
                UsbAccessory accessory = (accessories == null ? null : accessories[0]);
/*                if (accessory != null) {
                    result_win_log("USB Host connected", true);
                    Log.d(TAG, "USB Host connected");
                    if (mUsbManager.hasPermission(accessory)) {*/
                        openAccessory(accessory);
/*                    } else {
                        synchronized (mUsbReceiver) {
                            if (!mPermissionRequestPending) {
                                mUsbManager.requestPermission(accessory, mPermissionIntent);
                                mPermissionRequestPending = true;
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "mAccessory is null");
                    result_win_log("mAccessory is null", true);
                }*/
            }
        });

        Button mBTN_S4 = (Button) findViewById(R.id.BTN_S4);
        mBTN_S4.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "usb_detach? "+usb_detech);
                Log.d(TAG, "acc_closed? "+acc_closed);
                Log.d(TAG, "getAccessoryList : "+ mUsbManager.getAccessoryList()[0]);

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
                usb_detech = false;

            } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                UsbAccessory accessory = UsbManager.getAccessory(intent);
                if (accessory != null && accessory.equals(mAccessory)) {
                    Log.d(TAG, "ACTION_USB_ACCESSORY_DETACHED");
                    result_win_log("ACTION_USB_ACCESSORY_DETACHED", true);
                    usb_detech = true;
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
                        mUsbManager.requestPermission(accessory, mPermissionIntent);
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
        result_win_log("onPause : closeAccessory", true);
        closeAccessory();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mUsbReceiver);
        closeAccessory();
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
            //result_win_log("accessory opened", true);
            acc_closed = false;
        } else {
            Log.d(TAG, "accessory open fail");
            //result_win_log("accessory open fail", true);
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
            acc_closed = true;
        }
    }

    public void sendCommand(byte[] buffer) {

        if (mOutputStream != null) {
            try {
                mOutputStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "write failed", e);

                rwException.sendEmptyMessage(1);
            }
        }
    }

    Handler rwException = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            UsbAccessory[] accessories = mUsbManager.getAccessoryList();
            UsbAccessory accessory = (accessories == null ? null : accessories[0]);

            switch (msg.what){
                case 1:
                    Log.e(TAG, "write fail _ try acc open");
                    break;

                case 2:
                    Log.e(TAG, "read fail _ try acc open");
                    break;
            }
            if (acc_closed && accessory !=null) {
                openAccessory(accessory);
            }
        }
    };

    public void run() {
        int ret = 0;
        byte[] buffer = new byte[16384];

        while (ret >= 0) {
            try {
                ret = mInputStream.read(buffer);
                //Log.d(TAG, "mInputStream.read : " + ret);
                Log.d(TAG, "Rx data: " + byteArrayToHex(buffer));
            } catch (IOException e) {
                rwException.sendEmptyMessageDelayed(2, 5000);
                break;
            }

            if (ret > 0) {
                RCV_packet rPkt = new RCV_packet();
                int err = rPkt.pktParse(buffer);
                if (err != 0) {
                    Log.i(TAG, "Receive ERROR : " + err);
                }

                Message m = Message.obtain(mHandler);
                m.obj = rPkt;
                mHandler.sendMessage(m);
            }
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            RCV_packet rPkt = (RCV_packet) msg.obj;

            result_win_log(TAG, "rPkt.getSTARTFRAME() : "+String.valueOf((short)rPkt.getSTARTFRAME()));
            result_win_log(TAG, "rPkt.getSender() : "+String.valueOf(rPkt.getSender()));
            result_win_log(TAG, "rPkt.getReceiver() : "+String.valueOf(rPkt.getReceiver()));
            result_win_log(TAG, "rPkt.getmID() : "+String.valueOf((short)rPkt.getmID()));
            result_win_log(TAG, "rPkt.getDlength() : "+String.valueOf((short)rPkt.getDlength()));

            if (rPkt.getSender() == RCV_packet.ID_TPCR) {
                switch (rPkt.getmID()) {
                    case RCV_packet.TPCR_HMS_HVAC_CONTROL:
                        rcv_TPCR_HMS_HVAC_CONTROL rd1 = new rcv_TPCR_HMS_HVAC_CONTROL(rPkt.getData());
                        result_win_log(TAG, "rcv_TPCR_HMS_HVAC_CONTROL.getPassengerTemp " + String.valueOf(rd1.getPassengerTemp()));
                        break;

                    default:
                        result_win_log(TAG, "What?? mID = " + String.valueOf(rPkt.getmID()));
                        Log.i(TAG, "What?? mID = " + String.valueOf(rPkt.getmID()));
                        break;
                }
            } else if (rPkt.getSender() == RCV_packet.ID_TPDV) {
                switch (rPkt.getmID()) {
                    case RCV_packet.TPDV_HMS_HVAC_CONTROL:
                        rcv_TPDV_HMS_HVAC_CONTROL rd1 = new rcv_TPDV_HMS_HVAC_CONTROL(rPkt.getData());
                        result_win_log(TAG, "rcv_TPDV_HMS_HVAC_CONTROL.getPassengerTemp " + String.valueOf(rd1.getPassengerTemp()));
                        break;

                    default:
                        result_win_log(TAG, "What?? mID = " + String.valueOf(rPkt.getmID()));
                        Log.i(TAG, "What?? mID = " + String.valueOf(rPkt.getmID()));
                        break;
                }
            } else  {
                switch (rPkt.getmID()) {
                    case RCV_packet.HMS_COMMON_SURROUNDING_VEHICLE_INFO:
                        rcv_HMS_COMMON_SURROUNDING_VEHICLE_INFO rd1 = new rcv_HMS_COMMON_SURROUNDING_VEHICLE_INFO(rPkt.getData());
                        result_win_log(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.getCount " + String.valueOf(rd1.getCount()));
                        result_win_log(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.getTargetID " + String.valueOf(rd1.getTargetID()));
                        if (rd1.getCount() != 0) {
                            for (int i=0; i<rd1.getCount();i++) {
                                result_win_log(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.getVehicleId " + i + " " + String.valueOf(rd1.getSURROUNDING_VEHICLE_n_INFO(i).VehicleId));
                                result_win_log(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.getType " + i + " " + String.valueOf(rd1.getSURROUNDING_VEHICLE_n_INFO(i).Type));
                                result_win_log(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.getSensorId " + i + " " + String.valueOf(rd1.getSURROUNDING_VEHICLE_n_INFO(i).SensorId));
                                result_win_log(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.getLatitude " + i + " " + String.valueOf(rd1.getSURROUNDING_VEHICLE_n_INFO(i).Latitude));
                                result_win_log(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.getLongitude " + i + " " + String.valueOf(rd1.getSURROUNDING_VEHICLE_n_INFO(i).Longitude));
                                result_win_log(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.getAltitude " + i + " " + String.valueOf(rd1.getSURROUNDING_VEHICLE_n_INFO(i).Altitude));
                                result_win_log(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.getHeading " + i + " " + String.valueOf(rd1.getSURROUNDING_VEHICLE_n_INFO(i).Heading));
                                result_win_log(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.azimuthInVehicle " + i + " " + String.valueOf(rd1.getSURROUNDING_VEHICLE_n_INFO(i).azimuthInVehicle));
                                result_win_log(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.getDistanceToCollision " + i + " " + String.valueOf(rd1.getSURROUNDING_VEHICLE_n_INFO(i).DistanceToCollision));
                                result_win_log(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.getAbsoluteSpeed " + i + " " + String.valueOf(rd1.getSURROUNDING_VEHICLE_n_INFO(i).AbsoluteSpeed));
                                result_win_log(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.getRelativeSpeed " + i + " " + String.valueOf(rd1.getSURROUNDING_VEHICLE_n_INFO(i).RelativeSpeed));
                                result_win_log(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.LaneId " + i + " " + String.valueOf(rd1.getSURROUNDING_VEHICLE_n_INFO(i).LaneId));
                                result_win_log(TAG, "HMS_COMMON_SURROUNDING_VEHICLE_INFO.RoadGap " + i + " " + String.valueOf(rd1.getSURROUNDING_VEHICLE_n_INFO(i).RoadGap));
                            }
                        }
                        break;

                    case RCV_packet.HMS_COMMON_SCENARIO_INFO:
                        rcv_HMS_COMMON_SCENARIO_INFO rd2 = new rcv_HMS_COMMON_SCENARIO_INFO(rPkt.getData());
                        result_win_log(TAG, "rcv_HMS_COMMON_SCENARIO_INFO.getRain " + String.valueOf(rd2.getRain()));
                        result_win_log(TAG, "rcv_HMS_COMMON_SCENARIO_INFO.getSnow " + String.valueOf(rd2.getSnow()));
                        result_win_log(TAG, "rcv_HMS_COMMON_SCENARIO_INFO.getFog " + String.valueOf(rd2.getFog()));
                        result_win_log(TAG, "rcv_HMS_COMMON_SCENARIO_INFO.getCloud " + String.valueOf(rd2.getCloud()));
                        result_win_log(TAG, "rcv_HMS_COMMON_SCENARIO_INFO.getWaterOnRoad " + String.valueOf(rd2.getWaterOnRoad()));
                        result_win_log(TAG, "rcv_HMS_COMMON_SCENARIO_INFO.getSnowOnRoad " + String.valueOf(rd2.getSnowOnRoad()));
                        result_win_log(TAG, "rcv_HMS_COMMON_SCENARIO_INFO.getDayTime " + String.valueOf(rd2.getDayTime()));
                        result_win_log(TAG, "rcv_HMS_COMMON_SCENARIO_INFO.getWind " + String.valueOf(rd2.getWind()));
                        result_win_log(TAG, "rcv_HMS_COMMON_SCENARIO_INFO.getAutonomousRoad " + String.valueOf(rd2.getAutonomousRoad()));
                        result_win_log(TAG, "rcv_HMS_COMMON_SCENARIO_INFO.getTransitionTime " + String.valueOf(rd2.getTransitionTime()));
                        result_win_log(TAG, "rcv_HMS_COMMON_SCENARIO_INFO.getAutonomousMode " + String.valueOf(rd2.getAutonomousMode()));
                        result_win_log(TAG, "rcv_HMS_COMMON_SCENARIO_INFO.getIsTunnel " + String.valueOf(rd2.getIsTunnel()));
                        result_win_log(TAG, "rcv_HMS_COMMON_SCENARIO_INFO.getStopTime " + String.valueOf(rd2.getStopTime()));
                        break;

                    case RCV_packet.HMS_COMMON_RECOMMEND_DRIVING_GUIDE:
                        rcv_HMS_COMMON_RECOMMEND_DRIVING_GUIDE rd3 = new rcv_HMS_COMMON_RECOMMEND_DRIVING_GUIDE(rPkt.getData());
                        result_win_log(TAG, "HMS_COMMON_RECOMMEND_DRIVING_GUIDE.getSpeedUp " + String.valueOf(rd3.getSpeedUp()));
                        result_win_log(TAG, "HMS_COMMON_RECOMMEND_DRIVING_GUIDE.getSpeedDown " + String.valueOf(rd3.getSpeedDown()));
                        result_win_log(TAG, "HMS_COMMON_RECOMMEND_DRIVING_GUIDE.getLaneChange2Left " + String.valueOf(rd3.getLaneChange2Left()));
                        result_win_log(TAG, "HMS_COMMON_RECOMMEND_DRIVING_GUIDE.getLaneChange2Right " + String.valueOf(rd3.getLaneChange2Right()));
                        break;

                    case RCV_packet.HMS_COMMON_NAVI_GUIDANCE_INFO:
                        rcv_HMS_COMMON_NAVI_GUIDANCE_INFO rd4 = new rcv_HMS_COMMON_NAVI_GUIDANCE_INFO(rPkt.getData());
                        Log.e(TAG, "HMS_COMMON_NAVI_GUIDANCE_INFO.getDist2Goal " + String.valueOf((int)rd4.getDist2Goal()));
                        Log.e(TAG, "HMS_COMMON_NAVI_GUIDANCE_INFO.getDist2GP " + String.valueOf((int)rd4.getDist2GP()));
                        result_win_log(TAG, "HMS_COMMON_NAVI_GUIDANCE_INFO.getTime2Goal " + String.valueOf(rd4.getTime2Goal()));
                        result_win_log(TAG, "HMS_COMMON_NAVI_GUIDANCE_INFO.getTime2GP " + String.valueOf(rd4.getTime2GP()));
                        result_win_log(TAG, "HMS_COMMON_NAVI_GUIDANCE_INFO.getTotalDist " + String.valueOf(rd4.getTotalDist()));
                        result_win_log(TAG, "HMS_COMMON_NAVI_GUIDANCE_INFO.getTBTCode " + String.valueOf(rd4.getTBTCode()));
                        result_win_log(TAG, "HMS_COMMON_NAVI_GUIDANCE_INFO.getSpeedLimit " + String.valueOf(rd4.getSpeedLimit()));
                        result_win_log(TAG, "HMS_COMMON_NAVI_GUIDANCE_INFO.getCurrentRoadName " + rd4.getCurrentRoadName());
                        result_win_log(TAG, "HMS_COMMON_NAVI_GUIDANCE_INFO.getDirectionRoadName " + rd4.getDirectionRoadName());
                        result_win_log(TAG, "HMS_COMMON_NAVI_GUIDANCE_INFO.getADStartDistOffset " + String.valueOf((int)rd4.getADStartDistOffset()));
                        result_win_log(TAG, "HMS_COMMON_NAVI_GUIDANCE_INFO.getADDist " + String.valueOf((int)rd4.getADDist()));

                        break;

                    case RCV_packet.HMS_COMMON_AUTONOMOUS_DRIVING:
                        rcv_HMS_COMMON_AUTONOMOUS_DRIVING rd5 = new rcv_HMS_COMMON_AUTONOMOUS_DRIVING(rPkt.getData());
//                        result_win_log(TAG, "HMS_COMMON_AUTONOMOUS_DRIVING.getCommand " + String.valueOf(rd5.getCommand()));
                        result_win_log( "HMS_COMMON_AUTONOMOUS_DRIVING.getCommand " + String.valueOf(rd5.getCommand()), true);
                        Log.i(TAG, "HMS_COMMON_AUTONOMOUS_DRIVING.getCommand " + String.valueOf(rd5.getCommand()));
                        break;

                    case RCV_packet.HMS_COMMON_MANUAL_DRIVING:
                        rcv_HMS_COMMON_MANUAL_DRIVING rd6 = new rcv_HMS_COMMON_MANUAL_DRIVING(rPkt.getData());
//                        result_win_log(TAG, "HMS_COMMON_MANUAL_DRIVING.getCommand " + String.valueOf(rd6.getCommand()));
                        result_win_log( "HMS_COMMON_MANUAL_DRIVING.getCommand " + String.valueOf(rd6.getCommand()), true);
                        Log.i(TAG, "HMS_COMMON_MANUAL_DRIVING.getCommand " + String.valueOf(rd6.getCommand()));
                        break;

                    case RCV_packet.HMS_COMMON_MODE_READY_COUNTDOWN:
                        rcv_HMS_COMMON_MODE_READY_COUNTDOWN rd7 = new rcv_HMS_COMMON_MODE_READY_COUNTDOWN(rPkt.getData());
                        Log.i(TAG, "rcv_HMS_COMMON_MODE_READY_COUNTDOWN.getCount " + String.valueOf(rd7.getCount()));
                        Log.i(TAG, "rcv_HMS_COMMON_MODE_READY_COUNTDOWN.getMode " + String.valueOf(rd7.getMode()));
                        break;

                    case RCV_packet.HMS_COMMON_DRIVER_STATUS_INFO:
                        rcv_HMS_COMMON_DRIVER_STATUS_INFO rd8 = new rcv_HMS_COMMON_DRIVER_STATUS_INFO(rPkt.getData());
                        result_win_log(TAG, "rcv_HMS_COMMON_DRIVER_STATUS_INFO.getGaze " + String.valueOf(rd8.getGaze()));
                        result_win_log(TAG, "rcv_HMS_COMMON_DRIVER_STATUS_INFO.getSleep " + String.valueOf(rd8.getSleep()));
                        break;

                    case RCV_packet.HMS_COMMON_SAFETY_LEVEL_INFO:
                        rcv_HMS_COMMON_SAFETY_LEVEL_INFO rd9 = new rcv_HMS_COMMON_SAFETY_LEVEL_INFO(rPkt.getData());
                        result_win_log(TAG, "rcv_HMS_COMMON_SAFETY_LEVEL_INFO.getLevel " + String.valueOf(rd9.getLevel()));
                        break;

                    case RCV_packet.HMS_COMMON_HVAC_INFO:
                        rcv_HMS_COMMON_HVAC_INFO rd10 = new rcv_HMS_COMMON_HVAC_INFO(rPkt.getData());
                        result_win_log(TAG, "rcv_HMS_COMMON_HVAC_INFO.getDriverTemp " + String.valueOf(rd10.getDriverTemp()));
                        result_win_log(TAG, "rcv_HMS_COMMON_HVAC_INFO.getPassengerTemp " + String.valueOf(rd10.getPassengerTemp()));
                        result_win_log(TAG, "rcv_HMS_COMMON_HVAC_INFO.getFanSpeed " + String.valueOf(rd10.getFanSpeed()));
                        result_win_log(TAG, "rcv_HMS_COMMON_HVAC_INFO.getAirFlow " + String.valueOf(rd10.getAirFlow()));
                        result_win_log(TAG, "rcv_HMS_COMMON_HVAC_INFO.getAC " + String.valueOf(rd10.getAC()));
                        result_win_log(TAG, "rcv_HMS_COMMON_HVAC_INFO.getDriverSeatHeat " + String.valueOf(rd10.getDriverSeatHeat()));
                        result_win_log(TAG, "rcv_HMS_COMMON_HVAC_INFO.getPassengerSeatHeat " + String.valueOf(rd10.getPassengerSeatHeat()));
                        result_win_log(TAG, "rcv_HMS_COMMON_HVAC_INFO.getAuto " + String.valueOf(rd10.getAuto()));
                        result_win_log(TAG, "rcv_HMS_COMMON_HVAC_INFO.getFrontDefrost " + String.valueOf(rd10.getFrontDefrost()));
                        result_win_log(TAG, "rcv_HMS_COMMON_HVAC_INFO.getRearDefrost " + String.valueOf(rd10.getRearDefrost()));
                        break;

                    case RCV_packet.HMS_COMMON_STEERINGWHEEL_CONTROL:
                        rcv_HMS_COMMON_STEERINGWHEEL_CONTROL rd11 = new rcv_HMS_COMMON_STEERINGWHEEL_CONTROL(rPkt.getData());
                        result_win_log(TAG, "rcv_HMS_COMMON_STEERINGWHEEL_CONTROL.getCommand " + String.valueOf(rd11.getCommand()));
                        break;

                    case RCV_packet.HMS_COMMON_JOGDIAL_CONTROL:
                        rcv_HMS_COMMON_JOGDIAL_CONTROL rd12 = new rcv_HMS_COMMON_JOGDIAL_CONTROL(rPkt.getData());
                        result_win_log(TAG, "rcv_HMS_COMMON_JOGDIAL_CONTROL.getCommand " + String.valueOf(rd12.getCommand()));
                        result_win_log(TAG, "rcv_HMS_COMMON_JOGDIAL_CONTROL.getStatus " + String.valueOf(rd12.getStatus()));
                        break;

                    case RCV_packet.HMS_COMMON_SYSTEM_CHECKING:
                        rcv_HMS_COMMON_SYSTEM_CHECKING rd13 = new rcv_HMS_COMMON_SYSTEM_CHECKING(rPkt.getData());
                        result_win_log(TAG, "rcv_HMS_COMMON_SYSTEM_CHECKING.getCommand " + String.valueOf(rd13.getCommand()));
                        break;

                    case RCV_packet.HMS_COMMON_DRIVING_INFO:
                        rcv_HMS_COMMON_DRIVING_INFO rd14 = new rcv_HMS_COMMON_DRIVING_INFO(rPkt.getData());
                        result_win_log(TAG, "rcv_HMS_COMMON_DRIVING_INFO.getCharge " + String.valueOf(rd14.getCharge()));
                        result_win_log(TAG, "rcv_HMS_COMMON_DRIVING_INFO.getGear " + String.valueOf(rd14.getGear()));
                        result_win_log(TAG, "rcv_HMS_COMMON_DRIVING_INFO.getPossibleDrivingDistance " + String.valueOf(rd14.getPossibleDrivingDistance()));
                        result_win_log(TAG, "rcv_HMS_COMMON_DRIVING_INFO.getPower " + String.valueOf(rd14.getPower()));
                        result_win_log(TAG, "rcv_HMS_COMMON_DRIVING_INFO.getSpeed " + String.valueOf(rd14.getSpeed()));
                        result_win_log(TAG, "rcv_HMS_COMMON_DRIVING_INFO.getLights " + String.valueOf(rd14.getLights()));
                        break;

                    case RCV_packet.HMS_COMMON_NAVI_GUIDANCE_STARTED:
                        rcv_HMS_COMMON_NAVI_GUIDANCE_STARTED rd15 = new rcv_HMS_COMMON_NAVI_GUIDANCE_STARTED(rPkt.getData());
                        Log.e(TAG, "rcv_HMS_COMMON_NAVI_GUIDANCE_STARTED.getStartLatitude "+ String.valueOf(rd15.getStartLatitude()));
                        Log.e(TAG, "rcv_HMS_COMMON_NAVI_GUIDANCE_STARTED.getStartLongitude "+ String.valueOf(rd15.getStartLongitude()));
                        Log.e(TAG, "rcv_HMS_COMMON_NAVI_GUIDANCE_STARTED.getGoalLatitude "+ String.valueOf(rd15.getGoalLatitude()));
                        Log.e(TAG, "rcv_HMS_COMMON_NAVI_GUIDANCE_STARTED.getGoalLatitude "+ String.valueOf(rd15.getGoalLatitude()));
                        Log.e(TAG, "rcv_HMS_COMMON_NAVI_GUIDANCE_STARTED.getStartName "+ String.valueOf(rd15.getStartName()));
                        Log.e(TAG, "rcv_HMS_COMMON_NAVI_GUIDANCE_STARTED.getGoalName "+ String.valueOf(rd15.getGoalName()));
                        break;

                    case RCV_packet.HMS_COMMON_NAVI_GUIDANCE_FINISHED:
                        rcv_HMS_COMMON_NAVI_GUIDANCE_FINISHED rd16 = new rcv_HMS_COMMON_NAVI_GUIDANCE_FINISHED(rPkt.getData());
                        result_win_log(TAG, "rcv_HMS_COMMON_NAVI_GUIDANCE_FINISHED. ");
                        break;

                    case RCV_packet.HMS_COMMON_DRIVER_INFO:
                        rcv_HMS_COMMON_DRIVER_INFO rd17 = new rcv_HMS_COMMON_DRIVER_INFO(rPkt.getData());
                        result_win_log(TAG, "rcv_HMS_COMMON_DRIVER_INFO.getName " + rd17.getName());
                        break;

                    case RCV_packet.HMS_COMMON_GPS_INFO:
                        rcv_HMS_COMMON_GPS_INFO rd18 = new rcv_HMS_COMMON_GPS_INFO(rPkt.getData());
                        Log.e(TAG, "rcv_HMS_COMMON_GPS_INFO.getLatitude " + String.valueOf(rd18.getLatitude()));
                        Log.e(TAG, "rcv_HMS_COMMON_GPS_INFO.getLongitude " + String.valueOf(rd18.getLongitude()));
                        Log.e(TAG, "rcv_HMS_COMMON_GPS_INFO.getAltitude " + String.valueOf(rd18.getAltitude()));
                        Log.e(TAG, "rcv_HMS_COMMON_GPS_INFO.getHeading " + String.valueOf(rd18.getHeading()));
                        Log.e(TAG, "rcv_HMS_COMMON_GPS_INFO.getSpeed " + String.valueOf(rd18.getSpeed()));
                        Log.e(TAG, "rcv_HMS_COMMON_DRIVING_INFO.getLaneID " + String.valueOf(rd18.getLaneID()));
                        Log.e(TAG, "rcv_HMS_COMMON_DRIVING_INFO.getRoadGap " + String.valueOf(rd18.getRoadGap()));
                        break;

                    case RCV_packet.HMS_TPDV_DISPLAY_GOAL_MAP:
                        rcv_HMS_TPDV_DISPLAY_GOAL_MAP rd19 = new rcv_HMS_TPDV_DISPLAY_GOAL_MAP(rPkt.getData());
                        result_win_log(TAG, "rcv_HMS_TPDV_DISPLAY_GOAL_MAP.getLatitude " + String.valueOf(rd19.getLatitude()));
                        result_win_log(TAG, "rcv_HMS_TPDV_DISPLAY_GOAL_MAP.getLongitude " + String.valueOf(rd19.getLongitude()));
                        result_win_log(TAG, "rcv_HMS_TPDV_DISPLAY_GOAL_MAP.getGoalType " + String.valueOf(rd19.getGoalType()));
                        result_win_log(TAG, "rcv_HMS_TPDV_DISPLAY_GOAL_MAP.getName " + rd19.getName());
                        break;

                    case RCV_packet.HMS_TPDV_DISPLAY_CURR_MAP:
                        rcv_HMS_TPDV_DISPLAY_CURR_MAP rd20 = new rcv_HMS_TPDV_DISPLAY_CURR_MAP(rPkt.getData());
                        result_win_log(TAG, "rcv_HMS_TPDV_DISPLAY_CURR_MAP.getLatitude " + String.valueOf(rd20.getLatitude()));
                        result_win_log(TAG, "rcv_HMS_TPDV_DISPLAY_CURR_MAP.getLongitude " + String.valueOf(rd20.getLongitude()));
                        result_win_log(TAG, "rcv_HMS_TPDV_DISPLAY_CURR_MAP.getName " + rd20.getName());
                        break;

                    case RCV_packet.HMS_COMMON_START_INTRO:
                        result_win_log(TAG, "HMS_COMMON_START_INTRO " );
                        break;

                    case RCV_packet.HMS_COMMON_START_OUTRO:
                        result_win_log(TAG, "HMS_COMMON_START_OUTRO " );
                        break;

                    case RCV_packet.HMS_COMMON_MUSIC_INFO:
                        rcv_HMS_COMMON_MUSIC_INFO rd21 = new rcv_HMS_COMMON_MUSIC_INFO(rPkt.getData());
                        result_win_log(TAG, "rcv_HMS_COMMON_MUSIC_INFO.getTitle " + rd21.getTitle());
                        result_win_log(TAG, "rcv_HMS_COMMON_MUSIC_INFO.getArtist " + rd21.getArtist());
                        result_win_log(TAG, "rcv_HMS_COMMON_MUSIC_INFO.getAlbum " + rd21.getAlbum());
                        result_win_log(TAG, "rcv_HMS_COMMON_MUSIC_INFO.getGenr " + rd21.getGenr());
                        result_win_log(TAG, "rcv_HMS_COMMON_MUSIC_INFO.getPosition " + String.valueOf(rd21.getPosition()));
                        result_win_log(TAG, "rcv_HMS_COMMON_MUSIC_INFO.getDuration " + String.valueOf(rd21.getDuration()));
                        result_win_log(TAG, "rcv_HMS_COMMON_MUSIC_INFO.getIndex " + String.valueOf(rd21.getIndex()));
                        break;

                    case RCV_packet.HMS_TPCR_PLAY_CONTENTS_INFO:
                        rcv_HMS_TPCR_PLAY_CONTENTS_INFO rd22 = new rcv_HMS_TPCR_PLAY_CONTENTS_INFO(rPkt.getData());
                        result_win_log(TAG, "rcv_HMS_TPCR_PLAY_CONTENTS_INFO.getPlayingContents " + String.valueOf(rd22.getPlayingContents()));
                        result_win_log(TAG, "rcv_HMS_TPCR_PLAY_CONTENTS_INFO.getPlayStatus " + String.valueOf(rd22.getPlayStatus()));
                        result_win_log(TAG, "rcv_HMS_TPCR_PLAY_CONTENTS_INFO.getPlayDisplay " + String.valueOf(rd22.getPlayDisplay()));
                        break;

                    case RCV_packet.HMS_COMMON_DISPLAY_DANGER_INFO:
                        rcv_HMS_COMMON_DISPLAY_DANGER_INFO rd23 = new rcv_HMS_COMMON_DISPLAY_DANGER_INFO(rPkt.getData());
                        result_win_log(TAG, "rcv_HMS_COMMON_DISPLAY_DANGER_INFO.getType " + String.valueOf(rd23.getType()));
                        result_win_log(TAG, "rcv_HMS_COMMON_DISPLAY_DANGER_INFO.getRiskLevel " + String.valueOf(rd23.getRiskLevel()));
                        result_win_log(TAG, "rcv_HMS_COMMON_DISPLAY_DANGER_INFO.getParam2 " + String.valueOf(rd23.getParam2()));
                        result_win_log(TAG, "rcv_HMS_COMMON_DISPLAY_DANGER_INFO.getParam3 " + String.valueOf(rd23.getParam3()));
                        result_win_log(TAG, "rcv_HMS_COMMON_DISPLAY_DANGER_INFO.getDisplay " + String.valueOf(rd23.getDisplay()));
                        break;

                    case RCV_packet.HMS_COMMON_DISPLAY_DANGER_ALARM:
                        rcv_HMS_COMMON_DISPLAY_DANGER_ALARM rd24 = new rcv_HMS_COMMON_DISPLAY_DANGER_ALARM(rPkt.getData());
                        result_win_log(TAG, "rcv_HMS_COMMON_DISPLAY_DANGER_ALARM.getDisplay " + String.valueOf(rd24.getDisplay()));
                        result_win_log(TAG, "rcv_HMS_COMMON_DISPLAY_DANGER_ALARM.getSound " + String.valueOf(rd24.getSound()));
                        result_win_log(TAG, "rcv_HMS_COMMON_DISPLAY_DANGER_ALARM.getHaptic " + String.valueOf(rd24.getHaptic()));
                        result_win_log(TAG, "rcv_HMS_COMMON_DISPLAY_DANGER_ALARM.getInterval " + String.valueOf(rd24.getInterval()));
                        break;
                    default:
                        Log.i(TAG, "What?? sender = " + String.valueOf((int)rPkt.getSender()));
                        Log.i(TAG, "What?? mID = " + String.valueOf((int)rPkt.getmID()));
                        break;
                }
            }

        }
    };

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

/*    private void result_win_log(String tag, String lmsg) {
        Log.i(TAG, lmsg);
    }*/

    private void result_win_log(String tag, String lmsg) {
        LogToFile(lmsg);
    }

/*    private void result_win_log(String tag, String lmsg) {
        TextView mResult = (TextView) findViewById(R.id.result);
        if (result_line >= 25) {
            result_line = 0;
            mResult.setText("");
        }
            res_buf = mResult.getText().toString() + "\n" + lmsg;
            result_line += 1;
        mResult.setText(res_buf);
    }
    */

    public void LogToFile (String llog) {
        try {
            AOAlogFileStream.write((llog+"\n").getBytes());
        } catch (Exception e){
        }
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
