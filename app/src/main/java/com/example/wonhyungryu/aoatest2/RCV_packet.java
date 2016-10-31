package com.example.wonhyungryu.aoatest2;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.example.wonhyungryu.aoatest2.conversion_LE.arrayRealLen;

/**
 * Created by wonhyung.ryu on 2016-09-29.
 */

public class RCV_packet {
    private short STARTFRAME = 0;
    private byte sender = ID_NONE; // ID_TPDV, ID_TPCR
    private byte receiver = ID_NONE;
    private short mID = 0; // message ID
    private short dlength = 0; // data length
    private int ENDFRAME = 0;
    private byte [] data;

    private static final String TAG = "[AOATest]";

    public short getSTARTFRAME() {
        return STARTFRAME;
    }
    public byte getSender() {
        return sender;
    }
    public byte getReceiver() {
        return receiver;
    }
    public short getmID() {
        return mID;
    }
    public byte[] getData() {
        return data;
    }
    public short getDlength() {
        return dlength;
    }
    public int getENDFRAME() {
        return ENDFRAME;
    }

    public int pktParse(byte[] buf) {
        conversion_LE cle = new conversion_LE();
        STARTFRAME = cle.byteToShort_LE(buf, 0);
        if(STARTFRAME != 0xDD){
            Log.i(TAG, "START FRAME incorrect! "+ STARTFRAME + " "+(buf[0]&0xFF) +" "+(buf[1]&0xFF));
            return -1;
        }
        sender = buf[2];
        Log.i(TAG, "sender : "+ sender);

        receiver = buf[3];
        if(receiver != ID_TPCR && receiver!= ID_TPDV && receiver != ID_ALL){
            Log.i(TAG, "I'm not receiver!"+receiver);
            return -2;
        }

        mID = cle.byteToShort_LE(buf, mIDAddr);
        Log.i(TAG, "mID : "+ (int)mID);

        dlength = cle.byteToShort_LE(buf, datalenAddr);
        //Log.i(TAG, "data length : "+ (int)dlength);

        data = new byte[dlength];
        System.arraycopy(buf, 8, data, 0, dlength);

        ENDFRAME = cle.byteToInt_LE(buf, dataAddr+ dlength );

        if(ENDFRAME != 0xE0) {
            Log.i(TAG, "END FRAME incorrect!"+ ENDFRAME);
            return -3;
        }

        return 0;
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

    // message ID
    public static final byte TPDV_HMS_HVAC_CONTROL = 0x02;
    public static final byte TPCR_HMS_HVAC_CONTROL = 0x04;

    public static final byte HMS_COMMON_STEERINGWHEEL_CONTROL = 0x01;
    public static final byte HMS_COMMON_JOGDIAL_CONTROL = 0x02;
    public static final byte HMS_COMMON_SYSTEM_CHECKING = 0x03;
    public static final byte HMS_COMMON_DRIVING_INFO = 0x04;
    public static final byte HMS_COMMON_NAVI_GUIDANCE_INFO = 0x05;
    public static final byte HMS_COMMON_NAVI_GUIDANCE_STARTED = 0x06;
    public static final byte HMS_COMMON_NAVI_GUIDANCE_FINISHED = 0x07;
    public static final byte HMS_COMMON_AUTONOMOUS_DRIVING = 0x08;
    public static final byte HMS_COMMON_MANUAL_DRIVING = 0x09;
    public static final byte HMS_COMMON_MODE_READY_COUNTDOWN = 0x0A;
    public static final byte HMS_COMMON_DRIVER_INFO = 0x0B;
    public static final byte HMS_COMMON_DRIVER_STATUS_INFO = 0x0C;
    public static final byte HMS_COMMON_SAFETY_LEVEL_INFO = 0x0D;
    public static final byte HMS_COMMON_SURROUNDING_VEHICLE_INFO = 0x0E;
    public static final byte HMS_COMMON_SCENARIO_INFO = 0x0F;
    public static final byte HMS_COMMON_RECOMMEND_DRIVING_GUIDE = 0x10;
    public static final byte HMS_COMMON_HVAC_INFO = 0x11;
    public static final byte HMS_COMMON_MUSIC_INFO = 0x12;
    public static final byte HMS_COMMON_DISPLAY_DANGER_INFO = 0x13;
    public static final byte HMS_COMMON_DISPLAY_DANGER_ALARM = 0x14;
    public static final byte HMS_COMMON_GPS_INFO = 0x15;

    public static final byte HMS_TPDV_DISPLAY_GOAL_MAP = 0x16;
    public static final byte HMS_TPDV_DISPLAY_CURR_MAP = 0x17;
    public static final byte HMS_TPCR_PLAY_CONTENTS_INFO = 0x18;

    public static final byte HMS_COMMON_START_INTRO = 0x2D;
    public static final byte HMS_COMMON_START_OUTRO = 0x2E;
}

class SURROUNDING_VEHICLE_n_INFO{
    public short VehicleId; // 타겟 ID    0xFF : Unknown(Ultra)
    public short Type; // "타겟 타입 "0x02 : 정적 교통 표지판, 0x04 : 정적 물체, 0x08 : 정적 방해물, 0x10 : 동적 물체, 0x20 : 차량, 0x40 : 오토바이(자전거), 0x80 : 보행자 " 0xFF : Unknown
    public int SensorId; // "센서 아이디            300000 : 전방    300001 : 전방    300002 : 후방    300003 : 측면    300004 : 측면    300005 : 측면    300006 : 측면"
                            // 400000 : Front Ultra    400001 : Front left Ultra    400002 : Front Ligth Ultra    400003 : Left Ultra    400004 : Right Ultra    400005 : Rear Ultra    400006 : Rear Left Ultra    400007 : Rear Right Ultra
    public double Latitude; // 타겟 위도
    public double Longitude; // 타겟 경도
    public double Altitude; // 타겟 고도
    public float Heading; // 타겟 방향      -50000 : Unknown
    public float azimuthInVehicle; // 자차 헤딩 기준 상대차량 위치 각도    자차 기준 : 반시계방향 0 ~ 180    자차 기준 : 시계방향 0 ~ -180    ex ) -5도 ---> 내 차 기준으로 시계방향으로 5도 각도에 상대차량 있음
    public float DistanceToCollision; // 자차와 타겟간의 직선 거리
    public float AbsoluteSpeed; // 타겟 속도    -50000 : Unknown
    public float RelativeSpeed; // 상대 속도    -50000 : Unknown
    public int LaneId; // 상대차량 위치의 Lane ID
    public float RoadGap; // "도로 중앙으로 부터 상대차량위치    음수 : 도로 중앙에서 오른쪽으로 00m 떨어짐    양수 : 도로 중앙에서 왼쪽으로 00m 떨어짐"
}

class rcv_HMS_COMMON_SURROUNDING_VEHICLE_INFO {
    private short Count;
    private short TargetID; // 충동 예상 Vehicle ID
    ArrayList<SURROUNDING_VEHICLE_n_INFO> n_VEHICLE_INFO = new ArrayList<SURROUNDING_VEHICLE_n_INFO>();

    rcv_HMS_COMMON_SURROUNDING_VEHICLE_INFO(byte[] data){
        conversion_LE kk = new conversion_LE();
        int offset = 0;
        n_VEHICLE_INFO.clear();

        Count = kk.byteToShort_LE(data, offset); offset += 2;
        TargetID = kk.byteToShort_LE (data, offset); offset += 2;

        if (Count != 0) {
            for (int i = 0; i < Count; i++) {
                SURROUNDING_VEHICLE_n_INFO n_info = new SURROUNDING_VEHICLE_n_INFO();

                n_info.VehicleId = kk.byteToShort_LE(data, offset);                offset += 2;
                n_info.Type = kk.byteToShort_LE(data, offset);                offset += 2;
                n_info.SensorId = kk.byteToInt_LE(data, offset);                offset += 4;
                n_info.Latitude = kk.byteToDouble_LE(data, offset);                offset += 8;
                n_info.Longitude = kk.byteToDouble_LE(data, offset);                offset += 8;
                n_info.Altitude = kk.byteToDouble_LE(data, offset);                offset += 8;
                n_info.Heading = kk.byteToFloat_LE(data, offset);                offset += 4;
                n_info.azimuthInVehicle = kk.byteToFloat_LE(data, offset);                offset += 4;
                n_info.DistanceToCollision = kk.byteToFloat_LE(data, offset);                offset += 4;
                n_info.AbsoluteSpeed = kk.byteToFloat_LE(data, offset);                offset += 4;
                n_info.RelativeSpeed = kk.byteToFloat_LE(data, offset);                offset += 4;
                n_info.LaneId = kk.byteToInt_LE(data, offset);                offset += 4;
                n_info.RoadGap = kk.byteToFloat_LE(data, offset);                offset += 4;

                n_VEHICLE_INFO.add (n_info);
            }
        }
    }

    public short getCount() {
        return Count;
    }
    public short getTargetID() {
        return TargetID;
    }
    public SURROUNDING_VEHICLE_n_INFO getSURROUNDING_VEHICLE_n_INFO (int n) {
        return n_VEHICLE_INFO.get(n);
    }
}

class rcv_HMS_COMMON_SCENARIO_INFO {
    private float Rain; // 0.0 : 맑음 ~ 1.0 : 폭우    0.0 ~ 0.5, Light    0.5 ~ 1.0 Heavy
    private float Snow; // 0.0 : 맑음 ~ 1.0 : 폭설    0.0 ~ 0.5            0.5 ~ 1.0 Hail
    private float Fog; // 안개로 인한 가시거리 : 0 ~ 50000    -1 : 안개 없음
    private float Cloud; // 0.0 : 맑음 ~ 1.0 : 폭풍구름    0.0 ~ 0.5            0.5 ~ 1.0 Cloudy
    private float WaterOnRoad; // 도로 강수량 0.0 ~ 20.0 mm
    private float SnowOnRoad; // 도로 강설량 0.0 ~ 20.0 mm
    private float DayTime; // 0.0 ~ 24.0 시간
    private float Wind; // 바람 속도 0 ~ 100    0 ~ 50 Light    51 ~ 100 Windy
    private int AutonomousRoad; // "0x00 수동주행 도로            0x01 자율주행 도로"
    private int TransitionTime; // 6초 -> 0초 수동 -> 자율 모드 전환 시간
    private int AutonomousMode; // "0x00 수동주행 모드            0x01 자율주행 모드"
    private int IsTunnel; // "0x00 : 일반도로            0x01 : 터널구간"
    private int stopTime; // "0초 -> 6초 터널내 사고발생지점에서 정차후    경과시간"

    rcv_HMS_COMMON_SCENARIO_INFO(byte[] data){
        conversion_LE kk = new conversion_LE();
        int offset = 0;
        Rain = kk.byteToFloat_LE(data, offset); offset += 4;
        Snow = kk.byteToFloat_LE(data, offset); offset += 4;
        Fog = kk.byteToFloat_LE(data, offset); offset += 4;
        Cloud = kk.byteToFloat_LE(data, offset); offset += 4;
        WaterOnRoad = kk.byteToFloat_LE(data, offset); offset += 4;
        SnowOnRoad = kk.byteToFloat_LE(data, offset); offset += 4;
        DayTime = kk.byteToFloat_LE(data, offset); offset += 4;
        Wind = kk.byteToFloat_LE(data, offset); offset += 4;
        AutonomousRoad = kk.byteToInt_LE(data, offset); offset += 4;
        TransitionTime = kk.byteToInt_LE(data, offset); offset += 4;
        AutonomousMode = kk.byteToInt_LE(data, offset); offset += 4;
        IsTunnel = kk.byteToInt_LE(data, offset); offset += 4;
        stopTime = kk.byteToInt_LE(data, offset); offset += 4;
    }

    public float getRain() {
        return Rain;
    }
    public float getSnow() {
        return Snow;
    }
    public float getFog() {
        return Fog;
    }
    public float getCloud() {
        return Cloud;
    }
    public float getWaterOnRoad() {
        return WaterOnRoad;
    }
    public float getSnowOnRoad() {
        return SnowOnRoad;
    }
    public float getDayTime() {
        return DayTime;
    }
    public float getWind() {
        return Wind;
    }
    public int getAutonomousRoad() {
        return AutonomousRoad;
    }
    public int getTransitionTime() {
        return TransitionTime;
    }
    public int getAutonomousMode() {
        return AutonomousMode;
    }
    public int getIsTunnel() {
        return IsTunnel;
    }
    public int getStopTime() {
        return stopTime;
    }
}

class rcv_HMS_COMMON_RECOMMEND_DRIVING_GUIDE {
    private byte SpeedUp; // "0x00 : 불가능            0x01 : 가능    0x02 : 추천"
    private byte SpeedDown; // "0x00 : 불가능            0x01 : 가능    0x02 : 추천"
    private byte LaneChange2Left; // "0x00 : 불가능            0x01 : 가능    0x02 : 추천"
    private byte LaneChange2Right; // "0x00 : 불가능            0x01 : 가능    0x02 : 추천"

    rcv_HMS_COMMON_RECOMMEND_DRIVING_GUIDE(byte[] data){

        int offset = 0;
        SpeedUp = data[offset];  offset += 1;
        SpeedDown = data[offset];  offset += 1;
        LaneChange2Left = data[offset];  offset += 1;
        LaneChange2Right = data[offset];  offset += 1;
    }

    public byte getSpeedUp() {
        return SpeedUp;
    }
    public byte getSpeedDown() {
        return SpeedDown;
    }
    public byte getLaneChange2Left() {
        return LaneChange2Left;
    }
    public byte getLaneChange2Right() {
        return LaneChange2Right;
    }
}

class rcv_HMS_COMMON_NAVI_GUIDANCE_INFO {
    private short Dist2Goal; // m
    private short Dist2GP; // m
    private int Time2Goal; // second
    private int Time2GP; // second
    private short TotalDist; // m
    private byte TBTCode;
    private byte SpeedLimit; // km
    private byte[] CurrentRoadName;// = new byte[64];
    private byte[] DirectionRoadName;// = new byte[64];
    private char ADStartDistOffset; // m. 현재 AD 구간이 시작하는 거리
    private char ADDist; // m. 현재 AD 구간의 총 거리

    rcv_HMS_COMMON_NAVI_GUIDANCE_INFO(byte[] data){
        conversion_LE kk = new conversion_LE();
        int offset = 0, noZeroLen=0;

        Dist2Goal = kk.byteToShort_LE (data, offset); offset += 2;
        Dist2GP = kk.byteToShort_LE (data, offset); offset += 2;
        Time2Goal = kk.byteToInt_LE(data, offset); offset += 4;
        Time2GP = kk.byteToInt_LE(data, offset); offset += 4;
        TotalDist = kk.byteToShort_LE (data, offset); offset += 2;
        TBTCode = data[offset]; offset += 1;
        SpeedLimit = data[offset]; offset += 1;
//        CurrentRoadName = kk.byteToCharArray_LE(data, offset, 64); offset += 64;
//        DirectionRoadName = kk.byteToCharArray_LE(data, offset, 64); offset += 64;
        noZeroLen = arrayRealLen(data, offset, 64);
        CurrentRoadName = new byte[noZeroLen];
        System.arraycopy(data, offset, CurrentRoadName, 0, noZeroLen); offset += 64;
        noZeroLen = arrayRealLen(data, offset, 64);
        DirectionRoadName = new byte[noZeroLen];
		System.arraycopy(data, offset, DirectionRoadName, 0, noZeroLen); offset += 64;

        ADStartDistOffset = kk.byteToChar_LE(data, offset); offset += 2;
        ADDist = kk.byteToChar_LE(data, offset);  offset += 2;
    }

    public short getDist2Goal() {
        return Dist2Goal;
    }
    public short getDist2GP() {
        return Dist2GP;
    }
    public int getTime2Goal() {
        return Time2Goal;
    }
    public int getTime2GP() {
        return Time2GP;
    }
    public short getTotalDist() {
        return TotalDist;
    }
    public byte getTBTCode() {
        return TBTCode;
    }
    public byte getSpeedLimit() {
        return SpeedLimit;
    }
    public String getCurrentRoadName() {
        return String.valueOf(Charset.forName("UTF-8").decode(ByteBuffer.wrap(CurrentRoadName)));
    }
    public String getDirectionRoadName() {
        return String.valueOf(Charset.forName("UTF-8").decode(ByteBuffer.wrap(DirectionRoadName)));
    }
    public char getADStartDistOffset() {
        return ADStartDistOffset;
    }
    public char getADDist() {
        return ADDist;
    }
}

class rcv_HMS_COMMON_AUTONOMOUS_DRIVING {
    private byte Command; // "0x01 : 자율주행 시작 0x02 : 자율주행 종료"
    private byte reserved1;
    private byte reserved2;
    private byte reserved3;

    rcv_HMS_COMMON_AUTONOMOUS_DRIVING(byte[] data){
        Command = data[0];
    }

    public byte getCommand() {
        return Command;
    }
}

class rcv_HMS_COMMON_MANUAL_DRIVING {
    private byte Command; // "0x01 : 수동주행 시작 0x02 : 수동주행 종료"
    private byte reserved1;
    private byte reserved2;
    private byte reserved3;

    rcv_HMS_COMMON_MANUAL_DRIVING(byte[] data){
        Command = data[0];
    }

    public byte getCommand() {
        return Command;
    }
}

class rcv_HMS_COMMON_MODE_READY_COUNTDOWN {
    private byte Count; // 0~10
    private byte Mode; // "0x01 : 수동주행  0x02 : 자율주행"
    private byte reserved1;
    private byte reserved2;

    rcv_HMS_COMMON_MODE_READY_COUNTDOWN(byte[] data){
        int offset = 0;

        Count = data[offset];  offset += 1;
        Mode = data[offset];  offset += 1;
    }

    public byte getCount() {
        return Count;
    }
    public byte getMode() {
        return Mode;
    }
}

class rcv_HMS_COMMON_DRIVER_STATUS_INFO {
    private byte Gaze;  // "0x01 : 전방주시 0x02 : 전방주시태만"
    private byte Sleep; // "0x01 : 정상     0x02 : 졸음  0x03 : 깊은 졸음"
    private byte reserved1;
    private byte reserved2;

    rcv_HMS_COMMON_DRIVER_STATUS_INFO(byte[] data){
        int offset = 0;

        Gaze = data[offset];  offset += 1;
        Sleep = data[offset];  offset += 1;
    }

    public byte getGaze() {
        return Gaze;
    }
    public byte getSleep() {
        return Sleep;
    }
}

class rcv_HMS_COMMON_SAFETY_LEVEL_INFO {
    private byte Level; // 0~4
    private byte reserved1;
    private byte reserved2;
    private byte reserved3;

    rcv_HMS_COMMON_SAFETY_LEVEL_INFO(byte[] data){
        Level = data[0];
    }

    public byte getLevel() {
        return Level;
    }
}

class rcv_HMS_COMMON_HVAC_INFO {
    private byte DriverTemp;
    private byte PassengerTemp;
    private byte FanSpeed; // 1~5
    private byte AirFlow; // "0x01 : Center        0x02 : Center & Foot   0x03 : Foot  0x04 : Defrost & Foot"
    private byte AC; // "0x01 : On            0x00 : Off"
    private byte DriverSeatHeat; // "0x01 : On            0x00 : Off"
    private byte PassengerSeatHeat; // "0x01 : On            0x00 : Off"
    private byte Auto; // "0x01 : On            0x00 : Off"
    private byte FrontDefrost; // "0x01 : On            0x00 : Off"
    private byte RearDefrost; // "0x01 : On            0x00 : Off"

    rcv_HMS_COMMON_HVAC_INFO(byte[] data){
        int offset = 0;

        DriverTemp = data[offset];  offset += 1;
        PassengerTemp = data[offset];  offset += 1;
        FanSpeed = data[offset];  offset += 1;
        AirFlow = data[offset];  offset += 1;
        AC = data[offset];  offset += 1;
        DriverSeatHeat = data[offset];  offset += 1;
        PassengerSeatHeat = data[offset];  offset += 1;
        Auto = data[offset];  offset += 1;
        FrontDefrost = data[offset];  offset += 1;
        RearDefrost = data[offset];  offset += 1;
    }

    public byte getDriverTemp() {
        return DriverTemp;
    }
    public byte getPassengerTemp() {
        return PassengerTemp;
    }
    public byte getFanSpeed() {
        return FanSpeed;
    }
    public byte getAirFlow() {
        return AirFlow;
    }
    public byte getAC() {
        return AC;
    }
    public byte getDriverSeatHeat() {
        return DriverSeatHeat;
    }
    public byte getPassengerSeatHeat() {
        return PassengerSeatHeat;
    }
    public byte getAuto() {
        return Auto;
    }
    public byte getFrontDefrost() {
        return FrontDefrost;
    }
    public byte getRearDefrost() {
        return RearDefrost;
    }
}

class rcv_TPCR_HMS_HVAC_CONTROL {
    private byte DriverTemp;
    private byte PassengerTemp;
    private byte FanSpeed; // 1~5
    private byte AirFlow; // "0x01 : Center        0x02 : Center & Foot   0x03 : Foot  0x04 : Defrost & Foot"
    private byte AC; // "0x01 : On            0x00 : Off"
    private byte DriverSeatHeat; // "0x01 : On            0x00 : Off"
    private byte PassengerSeatHeat; // "0x01 : On            0x00 : Off"
    private byte Auto; // "0x01 : On            0x00 : Off"
    private byte FrontDefrost; // "0x01 : On            0x00 : Off"
    private byte RearDefrost; // "0x01 : On            0x00 : Off"

    rcv_TPCR_HMS_HVAC_CONTROL(byte[] data){
        int offset = 0;

        DriverTemp = data[offset];  offset += 1;
        PassengerTemp = data[offset];  offset += 1;
        FanSpeed = data[offset];  offset += 1;
        AirFlow = data[offset];  offset += 1;
        AC = data[offset];  offset += 1;
        DriverSeatHeat = data[offset];  offset += 1;
        PassengerSeatHeat = data[offset];  offset += 1;
        Auto = data[offset];  offset += 1;
        FrontDefrost = data[offset];  offset += 1;
        RearDefrost = data[offset];  offset += 1;
    }

    public byte getDriverTemp() {
        return DriverTemp;
    }
    public byte getPassengerTemp() {
        return PassengerTemp;
    }
    public byte getFanSpeed() {
        return FanSpeed;
    }
    public byte getAirFlow() {
        return AirFlow;
    }
    public byte getAC() {
        return AC;
    }
    public byte getDriverSeatHeat() {
        return DriverSeatHeat;
    }
    public byte getPassengerSeatHeat() {
        return PassengerSeatHeat;
    }
    public byte getAuto() {
        return Auto;
    }
    public byte getFrontDefrost() {
        return FrontDefrost;
    }
    public byte getRearDefrost() {
        return RearDefrost;
    }
}

class rcv_TPDV_HMS_HVAC_CONTROL{
    private byte DriverTemp;
    private byte PassengerTemp;
    private byte FanSpeed; // 1~5
    private byte AirFlow; // "0x01 : Center        0x02 : Center & Foot   0x03 : Foot  0x04 : Defrost & Foot"
    private byte AC; // "0x01 : On            0x00 : Off"
    private byte DriverSeatHeat; // "0x01 : On            0x00 : Off"
    private byte PassengerSeatHeat; // "0x01 : On            0x00 : Off"
    private byte Auto; // "0x01 : On            0x00 : Off"
    private byte FrontDefrost; // "0x01 : On            0x00 : Off"
    private byte RearDefrost; // "0x01 : On            0x00 : Off"

    rcv_TPDV_HMS_HVAC_CONTROL(byte[] data){
        int offset = 0;

        DriverTemp = data[offset];  offset += 1;
        PassengerTemp = data[offset];  offset += 1;
        FanSpeed = data[offset];  offset += 1;
        AirFlow = data[offset];  offset += 1;
        AC = data[offset];  offset += 1;
        DriverSeatHeat = data[offset];  offset += 1;
        PassengerSeatHeat = data[offset];  offset += 1;
        Auto = data[offset];  offset += 1;
        FrontDefrost = data[offset];  offset += 1;
        RearDefrost = data[offset];  offset += 1;
    }

    public byte getDriverTemp() {
        return DriverTemp;
    }
    public byte getPassengerTemp() {
        return PassengerTemp;
    }
    public byte getFanSpeed() {
        return FanSpeed;
    }
    public byte getAirFlow() {
        return AirFlow;
    }
    public byte getAC() {
        return AC;
    }
    public byte getDriverSeatHeat() {
        return DriverSeatHeat;
    }
    public byte getPassengerSeatHeat() {
        return PassengerSeatHeat;
    }
    public byte getAuto() {
        return Auto;
    }
    public byte getFrontDefrost() {
        return FrontDefrost;
    }
    public byte getRearDefrost() {
        return RearDefrost;
    }
}

class rcv_HMS_COMMON_MUSIC_INFO {
    private byte[] Title; // = new char[32];
    private byte[] Artist; // = new char[32];
    private byte[] Album; // = new char[32];
    private byte[] Genr; // = new char[32];
    private float Position;
    private float Duration;
    private short Index;
    private byte reserved1;
    private byte reserved2;

    rcv_HMS_COMMON_MUSIC_INFO(byte[] data) {
        conversion_LE kk = new conversion_LE();
        int offset = 0, noZeroLen = 0;

        noZeroLen = arrayRealLen(data, offset, 64);
        Title = new byte[noZeroLen];
        System.arraycopy(data, offset, Title, 0, noZeroLen); offset += 64;
        noZeroLen = arrayRealLen(data, offset, 64);
        Artist = new byte[noZeroLen];
        System.arraycopy(data, offset, Artist, 0, noZeroLen); offset += 64;
        noZeroLen = arrayRealLen(data, offset, 64);
        Album = new byte[noZeroLen];
        System.arraycopy(data, offset, Album, 0, noZeroLen); offset += 64;
        noZeroLen = arrayRealLen(data, offset, 64);
        Genr = new byte[noZeroLen];
        System.arraycopy(data, offset, Genr, 0, noZeroLen); offset += 64;

        Position = kk.byteToFloat_LE(data, offset);        offset += 4;
        Duration = kk.byteToFloat_LE(data, offset);        offset += 4;
        Index = kk.byteToShort_LE(data, offset);        offset += 2;
    }

    public String getTitle() {
        return String.valueOf(Charset.forName("UTF-8").decode(ByteBuffer.wrap(Title)));
    }
    public String getArtist() {
        return String.valueOf(Charset.forName("UTF-8").decode(ByteBuffer.wrap(Artist)));
    }
    public String getAlbum() {
        return String.valueOf(Charset.forName("UTF-8").decode(ByteBuffer.wrap(Album)));
    }
    public String getGenr() {
        return String.valueOf(Charset.forName("UTF-8").decode(ByteBuffer.wrap(Genr)));
    }
    public float getPosition() {
        return Position;
    }
    public float getDuration() {
        return Duration;
    }
    public short getIndex() {
        return Index;
    }
}

class rcv_HMS_COMMON_DISPLAY_DANGER_INFO{
    private byte Type; // 0x01 : 도로 상태 (상세 상태는 시나리오 정보 참고)    0x02 : 날씨 변화 (상세 상태는 시나리오 정보 참고)
                        // 0x03 : 사고 발생    0x04 : 강제 수동 전환    0x05 : 이상 차량    0x06 : 운전자 졸음    0x07 : 휴게소    0x08 : 전방주시태만    0x09 : 보행자    0x0A : 자전거(오토바이)    0x0B : 터널
                        // 0x0C : 충돌 주의    0x0D : 중앙선 침범    0xFF : 없음
    private byte RiskLevel; // 위험도    0x01 : LEVEL 1            0x02 : LEVEL 2            0x03 : LEVEL 3
    private byte param2; // 0x00 : 도로 상태 좋음    0x01 : 도로 상태 폭우    0x02 : 도로 상태 폭설    0x00 : 날씨 좋음    0x01 : 날씨 약한 비    0x02 : 날씨 강한 비    0x03 : 날씨 눈
                        // 0x04 : 날씨 폭설    0x05 : 날씨 안개    0x06 : 날씨 구름    0x07 : 날씨 짙은 구름    0x08 : 날씨 약한 바람    0x09 : 날씨 강한 바람
    private byte param3; // 0x00 : 위험 방향 없음    0x01 : 위험 방향 전방    0x02 : 위험 방향 후방    0x03 : 위험 방향 좌측방    0x04 : 위험 방향 우측방
    private byte Display; // 0x01 : Display1    0x02 : Display2    0x04 : Display3    0x08 : Display4    0x10 : Display5
                        // 0x20 : LED    0x40 : ARHUD    0x80 : LEFT SIDE MIRROR    0x100 : RIGHT SIDE MIRROR
    private byte reserved1;
    private byte reserved2;
    private byte reserved3;


    rcv_HMS_COMMON_DISPLAY_DANGER_INFO(byte[] data) {

        int offset = 0;
        Type = data[offset];        offset += 1;
        RiskLevel = data[offset];        offset += 1;
        param2 = data[offset];        offset += 1;
        param3 = data[offset];        offset += 1;
        Display = data[offset];        offset += 1;
    }

    public byte getType() {
        return Type;
    }
    public byte getRiskLevel() {
        return RiskLevel;
    }
    public byte getParam2() {
        return param2;
    }
    public byte getParam3() {
        return param3;
    }
    public byte getDisplay() {
        return Display;
    }
}

class rcv_HMS_COMMON_DISPLAY_DANGER_ALARM{
    private short Display; // "0x01 : Display1, 0x02 : Display2, 0x04 : Display3, 0x08 : Display4, 0x10 : Display5, 0x20 : LED, 0x40 : ARHUD, 0x80 : LEFT SIDE MIRROR, 0x100 : RIGHT SIDE MIRROR"
    private short Sound; // "0x01 : 위험도 1             0x02 : 위험도 2            0x03 : 위험도 3"
    private short Haptic;
    private short Interval; // 표시 간격(ms)

    rcv_HMS_COMMON_DISPLAY_DANGER_ALARM(byte[] data) {
        conversion_LE kk = new conversion_LE();
        int offset = 0;
        Display = kk.byteToShort_LE(data, offset);        offset += 2;
        Sound = kk.byteToShort_LE(data, offset);        offset += 2;
        Haptic = kk.byteToShort_LE(data, offset);        offset += 2;
        Interval = kk.byteToShort_LE(data, offset);        offset += 2;
    }

    public short getDisplay() {
        return Display;
    }
    public short getSound() {
        return Sound;
    }
    public short getHaptic() {
        return Haptic;
    }
    public short getInterval() {
        return Interval;
    }
}

class rcv_HMS_COMMON_GPS_INFO{
    private double Latitude;
    private double Longitude;
    private double Altitude;
    private float Heading;
    private float Speed;
    private int LaneID; // 내차량 위치의 Lane ID
    private float RoadGap; // "도로 중앙으로 부터 내차량위치    음수 : 도로 중앙에서 오른쪽으로 00m 떨어짐    양수 : 도로 중앙에서 왼쪽으로 00m 떨어짐"

    rcv_HMS_COMMON_GPS_INFO(byte[] data) {
        conversion_LE kk = new conversion_LE();
        int offset = 0;
        Latitude = kk.byteToDouble_LE(data, offset);        offset += 8;
        Longitude = kk.byteToDouble_LE(data, offset);        offset += 8;
        Altitude = kk.byteToDouble_LE(data, offset);        offset += 8;
        Heading = kk.byteToFloat_LE(data, offset);        offset += 4;
        Speed = kk.byteToFloat_LE(data, offset);        offset += 4;
        LaneID = kk.byteToInt_LE(data, offset);        offset += 4;
        RoadGap = kk.byteToFloat_LE(data, offset);        offset += 4;

    }

    public double getLatitude() {
        return Latitude;
    }
    public double getLongitude() {
        return Longitude;
    }
    public double getAltitude() {
        return Altitude;
    }
    public float getHeading() {
        return Heading;
    }
    public float getSpeed() {
        return Speed;
    }
    public int getLaneID() {
        return LaneID;
    }
    public float getRoadGap() {
        return RoadGap;
    }
}

class rcv_HMS_TPDV_DISPLAY_GOAL_MAP{
    private double Latitude; // 표시용 목적지 위도
    private double Longitude; // 표시용 목적지 경도
    private byte GoalType;
    private byte reserved1;
    private byte reserved2;
    private byte reserved3;
    private byte[] Name; // = new char[32];

    rcv_HMS_TPDV_DISPLAY_GOAL_MAP(byte[] data) {
        conversion_LE kk = new conversion_LE();
        int offset = 0, noZeroLen=0;
        Latitude = kk.byteToDouble_LE(data, offset);        offset += 8;
        Longitude = kk.byteToDouble_LE(data, offset);        offset += 8;
        GoalType = data[offset];        offset += 1;
        reserved1 = data[offset];        offset += 1;
        reserved2 = data[offset];        offset += 1;
        reserved3 = data[offset];        offset += 1;
        noZeroLen = arrayRealLen(data, offset, 64);
        Name = new byte[noZeroLen];
        System.arraycopy(data, offset, Name, 0, noZeroLen); offset += 64;
    }

    public double getLatitude() {
        return Latitude;
    }
    public double getLongitude() {
        return Longitude;
    }
    public byte getGoalType() {
        return GoalType;
    }
    public String getName() {
        return String.valueOf(Charset.forName("UTF-8").decode(ByteBuffer.wrap(Name)));
    }
}

class rcv_HMS_TPDV_DISPLAY_CURR_MAP{
    private double Latitude; // 표시용 목적지 위도
    private double Longitude; // 표시용 목적지 경도
    private byte[] Name; // = new char[32];

    rcv_HMS_TPDV_DISPLAY_CURR_MAP(byte[] data) {
        conversion_LE kk = new conversion_LE();
        int offset = 0, noZeroLen=0;
        Latitude = kk.byteToDouble_LE(data, offset);        offset += 8;
        Longitude = kk.byteToDouble_LE(data, offset);        offset += 8;
        noZeroLen = arrayRealLen(data, offset, 64);
        Name = new byte[noZeroLen];
		System.arraycopy(data, offset, Name, 0, noZeroLen); offset += 64;
    }

    public double getLatitude() {
        return Latitude;
    }
    public double getLongitude() {
        return Longitude;
    }
    public String getName() {
        return String.valueOf(Charset.forName("UTF-8").decode(ByteBuffer.wrap(Name)));
    }
}

class rcv_HMS_TPCR_PLAY_CONTENTS_INFO {
    private byte PlayingContents = 0x02; //"0x01 : music            0x02 : video    0x03 : SNS    0x04 : news    0x05 : photo"
    private byte PlayStatus = 0x02;	// "0x01 : play            0x02 : pause"
    private byte PlayDisplay = 0x01;	//"0x01 : display2            0x02 : display3"
    private byte reserved1;


    rcv_HMS_TPCR_PLAY_CONTENTS_INFO(){};
    rcv_HMS_TPCR_PLAY_CONTENTS_INFO(byte[] data){

        int offset = 0;
        PlayingContents = data[offset];  offset += 1;
        PlayStatus = data[offset];  offset += 1;
        PlayDisplay = data[offset];  offset += 1;
        reserved1 = data[offset];  offset += 1;
    }

    public void setPlayDisplay(byte playDisplay) {
        PlayDisplay = playDisplay;
    }

    public void setPlayingContents(byte playingContents) {
        PlayingContents = playingContents;
    }

    public void setPlayStatus(byte playStatus) {
        PlayStatus = playStatus;
    }

    public byte getPlayingContents() {
        return PlayingContents;
    }
    public byte getPlayStatus() {
        return PlayStatus;
    }
    public byte getPlayDisplay() {
        return PlayDisplay;
    }
}

class rcv_HMS_COMMON_STEERINGWHEEL_CONTROL{
    /* "0x01 : VoiceRecognition
        0x02 : 주행모드(수동/자율) 변경
        0x03 : Volume Up
        0x04 : Volume Down
        0x05 : Mute
        0x06 : Seek Up
        0x07 : Seek Down
        0x08 : Start Call
        0x09 : End Call
        0x0A : Main Menu Call
        0x0B : Cruise
        0x0C : Mode Up(위로 포커스 이동)
        0x0D : Mode Down(아래로 포커스 이동)
        0x0E : Mode OK
        0x0F : RES+
        0x10 : SET-
        0x11 : Lane Keeping
        0x12 : Cruise/Lane Keeping Cancel
        0x13 : Paddle+
        0x14 : Paddle-" */
    private byte Command;
    private byte Param; // unused
    private byte reserved1;
    private byte reserved2;

    rcv_HMS_COMMON_STEERINGWHEEL_CONTROL(byte[] data) {

        int offset = 0;
        Command = data[offset];        offset += 1;
        Param = data[offset];        offset += 1;
    }

    public byte getCommand() {
        return Command;
    }
    public byte getParam() {
        return Param;
    }
}

class rcv_HMS_COMMON_JOGDIAL_CONTROL{
    /* "    0x01 : Rotary Knob Right
            0x02 : Rotary Knob Left
            0x03 : Joystick Top
            0x04 : Joystick Bottom
            0x05 : Joystick Left
            0x06 : Joystick Right
            0x07 : Center Button
            0x08 : Back Button
            0x09 : Home Button
            0x0A : DRIVE Button
            0x0B : VIEW Button
            0x0C : RELAX Button
            0x0D : P Button
            0x0E : D Button
            0x0F : R Button
            0x10 : Hand Gesture Left
            0x11 : Hand Gesture Right
            0x12 : Hand Gesture Hold
            0x13 : Hand Gesture Click
            0x14 : Hand Gesture Double Tab" */
    private byte Command;
    private byte Status; // "0x01 : Button Push            0x02 : Button Release"
    private byte reserved1;
    private byte reserved2;

    rcv_HMS_COMMON_JOGDIAL_CONTROL(byte[] data) {

        int offset = 0;
        Command = data[offset];        offset += 1;
        Status = data[offset];        offset += 1;
    }

    public byte getCommand() {
        return Command;
    }
    public byte getStatus() {
        return Status;
    }
}

class rcv_HMS_COMMON_SYSTEM_CHECKING{
    private byte Command; // "0x01 : 시작            0x02 : 종료"
    private byte reserved1;
    private byte reserved2;
    private byte reserved3;

    rcv_HMS_COMMON_SYSTEM_CHECKING(byte[] data) {

        int offset = 0;
        Command = data[offset];        offset += 1;
    }

    public byte getCommand() {
        return Command;
    }
}

class rcv_HMS_COMMON_DRIVING_INFO{
    private int PossibleDrivingDistance; // km
    private float Speed; // 0~220km
    private float Power; // 0~100%
    private float Charge; // 0~150%
    private int Gear; // "0x0D : P            0X0E : D    0X0F : R"
    private int Lights; // "자차 Light 정보    0x00 : 좌측 깜박이    0x01 : 우측 깜박이    0x02 : 정지등    0x03 : 하향등    0x04 : 상향등    0x05 : 안개등    0x06 : 후진등"

    rcv_HMS_COMMON_DRIVING_INFO(byte[] data) {
        conversion_LE kk = new conversion_LE();
        int offset = 0;

        PossibleDrivingDistance = kk.byteToInt_LE(data, offset);        offset += 4;
        Speed = kk.byteToFloat_LE(data, offset);        offset += 4;
        Power = kk.byteToFloat_LE(data, offset);        offset += 4;
        Charge = kk.byteToFloat_LE(data, offset);        offset += 4;
        Gear = kk.byteToInt_LE(data, offset);        offset += 4;
        Lights = kk.byteToInt_LE(data, offset);        offset += 4;
    }

    public int getPossibleDrivingDistance() {
        return PossibleDrivingDistance;
    }
    public float getSpeed() {
        return Speed;
    }
    public float getPower() {
        return Power;
    }
    public float getCharge() {
        return Charge;
    }
    public int getGear() {
        return Gear;
    }
    public int getLights() {
        return Lights;
    }
}

class rcv_HMS_COMMON_NAVI_GUIDANCE_STARTED{
    private double StartLatitude;
    private double StartLongitude;
    private double GoalLatitude;
    private double GoalLongitude;
    private byte [] StartName;
    private byte [] GoalName;

    rcv_HMS_COMMON_NAVI_GUIDANCE_STARTED(byte[] data) {
        conversion_LE kk = new conversion_LE();
        int offset = 0, noZeroLen=0;

        StartLatitude = kk.byteToDouble_LE(data, offset);        offset += 8;
        StartLongitude = kk.byteToDouble_LE(data, offset);        offset += 8;
        GoalLatitude = kk.byteToDouble_LE(data, offset);        offset += 8;
        GoalLongitude = kk.byteToDouble_LE(data, offset);        offset += 8;
        noZeroLen = arrayRealLen(data, offset, 64);
        StartName = new byte[noZeroLen];
        System.arraycopy(data, offset, StartName, 0, noZeroLen); offset += 64;
        noZeroLen = arrayRealLen(data, offset, 64);
        GoalName = new byte[noZeroLen];
        System.arraycopy(data, offset, GoalName, 0, noZeroLen); offset += 64;
    }

    public double getStartLatitude() {
        return StartLatitude;
    }
    public double getStartLongitude() {
        return StartLongitude;
    }
    public double getGoalLatitude() {
        return GoalLatitude;
    }
    public double getGoalLongitude() {
        return GoalLongitude;
    }
    public String getStartName() {
        return String.valueOf(Charset.forName("UTF-8").decode(ByteBuffer.wrap(StartName)));
    }
    public String getGoalName() {
        return String.valueOf(Charset.forName("UTF-8").decode(ByteBuffer.wrap(GoalName)));
    }
}

class rcv_HMS_COMMON_NAVI_GUIDANCE_FINISHED{

    rcv_HMS_COMMON_NAVI_GUIDANCE_FINISHED(byte[] data) {

        //int offset = 0;
    }
}

class rcv_HMS_COMMON_DRIVER_INFO{
    private byte[] Name; // = new char[32]; // 운전자 이름

    rcv_HMS_COMMON_DRIVER_INFO(byte[] data) {
        conversion_LE kk = new conversion_LE();
		
        int offset = 0, noZeroLen =0;
		noZeroLen = arrayRealLen(data, offset, 64);
		Name = new byte[noZeroLen];
		System.arraycopy(data, offset, Name, 0, noZeroLen); offset += 64;
    }

    public String getName() {
        return String.valueOf(Charset.forName("UTF-8").decode(ByteBuffer.wrap(Name)));
    }
}

class rcv_HMS_COMMON_START_INTRO {

}

class rcv_HMS_COMMON_START_OUTRO {

}