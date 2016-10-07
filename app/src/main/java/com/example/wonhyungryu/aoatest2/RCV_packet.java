package com.example.wonhyungryu.aoatest2;

import android.util.Log;

/**
 * Created by wonhyung.ryu on 2016-09-29.
 */

public class RCV_packet {
    private char STARTFRAME = 0;
    private byte sender = ID_NONE; // ID_TPDV, ID_TPCR
    private byte receiver = ID_NONE;
    private char mID = 0; // message ID
    private char dlength = 0; // data length
    private int ENDFRAME = 0;
    private byte [] data;

    private static final String TAG = "[AOATest]";

    public char getSTARTFRAME() {
        return STARTFRAME;
    }

    public byte getSender() {
        return sender;
    }

    public byte getReceiver() {
        return receiver;
    }

    public char getmID() {
        return mID;
    }

    public byte[] getData() {
        return data;
    }

    public char getDlength() {
        return dlength;
    }

    public int getENDFRAME() {
        return ENDFRAME;
    }

    public int pktParse(byte[] buf) {

        conversion_LE cle = new conversion_LE();
        STARTFRAME = cle.byteToChar_LE(buf, 0);
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

        mID = cle.byteToChar_LE(buf, mIDAddr);
        Log.i(TAG, "mID : "+ mID);

        dlength = cle.byteToChar_LE(buf, datalenAddr);
        Log.i(TAG, "data length : "+ dlength);

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

    public static final byte HMS_COMMON_NAVI_GUIDANCE_INFO = 0x05;
    public static final byte HMS_COMMON_AUTONOMOUS_DRIVING = 0x08;
    public static final byte HMS_COMMON_MANUAL_DRIVING = 0x09;
    public static final byte HMS_COMMON_MODE_READY_COUNTDOWN = 0x0A;
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

    public static final byte HMS_COMMON_STEERINGWHEEL_CONTROL = 0x01;
    public static final byte HMS_COMMON_JOGDIAL_CONTROL = 0x02;
    public static final byte HMS_COMMON_SYSTEM_CHECKING = 0x03;
    public static final byte HMS_COMMON_DRIVING_INFO = 0x04;
    public static final byte HMS_COMMON_NAVI_GUIDANCE_STARTED = 0x06;
    public static final byte HMS_COMMON_NAVI_GUIDANCE_FINISHED = 0x07;
    public static final byte HMS_COMMON_DRIVER_INFO = 0x0B;

}

class rcv_HMS_COMMON_SURROUNDING_VEHICLE_INFO {
    private char Count;
    private char VehicleID;
    private byte VehicleType; //"0x02 : 정적 교통 표지판, 0x04 : 정적 물체, 0x08 : 정적 방해물, 0x10 : 동적 물체, 0x20 : 차량, 0x40 : 오토바이(자전거), 0x80 : 보행자 "
    private char[] VehicleName = new char [4];
    private double VehicleLatitude;
    private double VehicleLogitude;
    private double VehicleAltitude;
    private float VehicleHeading;
    private float VehicleDistanceToCollision;	// 직선 거리
    private float VehicleAbsoluteSpeed;
    private float VehicleRelativeSpeed;
    private char TargetID;	// 충동 예상 Vehicle ID

    rcv_HMS_COMMON_SURROUNDING_VEHICLE_INFO(byte[] data){
        conversion_LE kk = new conversion_LE();
        int offset = 0;
        Count = kk.byteToChar_LE (data, offset); offset += 2;
        VehicleID = kk.byteToChar_LE (data, offset); offset += 2;
        VehicleType = data[offset];  offset += 1;
        VehicleName = kk.byteToCharArray_LE(data, offset, 8); offset += 8;
        VehicleLatitude = kk.byteToDouble_LE(data, offset); offset += 8;
        VehicleLogitude = kk.byteToDouble_LE(data, offset); offset += 8;
        VehicleAltitude = kk.byteToDouble_LE(data, offset); offset += 8;
        VehicleHeading = kk.byteToFloat_LE(data, offset); offset += 4;
        VehicleDistanceToCollision = kk.byteToFloat_LE(data, offset); offset += 4;
        VehicleAbsoluteSpeed = kk.byteToFloat_LE(data, offset); offset += 4;
        VehicleAbsoluteSpeed = kk.byteToFloat_LE(data, offset); offset += 4;
        TargetID = kk.byteToChar_LE (data, offset);
    }

    public char getCount() {
        return Count;
    }
    public char getVehicleID() {
        return VehicleID;
    }
    public byte getVehicleType() {
        return VehicleType;
    }
    public char[] getVehicleName() {
        return VehicleName;
    }
    public double getVehicleLatitude() {
        return VehicleLatitude;
    }
    public double getVehicleLogitude() {
        return VehicleLogitude;
    }
    public double getVehicleAltitude() {
        return VehicleAltitude;
    }
    public float getVehicleHeading() {
        return VehicleHeading;
    }
    public float getVehicleDistanceToCollision() {
        return VehicleDistanceToCollision;
    }
    public float getVehicleAbsoluteSpeed() {
        return VehicleAbsoluteSpeed;
    }
    public float getVehicleRelativeSpeed() {
        return VehicleRelativeSpeed;
    }
    public char getTargetID() {
        return TargetID;
    }
}

class rcv_HMS_COMMON_SCENARIO_INFO {
    private byte Rain; // "0x00 : 맑음            0x01 : 폭우"
    private byte Snow; // "0x00 : 맑음            0x01 : 폭설"
    private byte Fog; // "0x00 : 맑음            0x01 : 진한 안개"
    private byte Cloud; // "0x00 : 맑음            0x01 : 폭풍구름"
    private byte WaterOnRoad; // 도로 강수량 0 ~ 20mm
    private byte SnowOnRoad; // 도로 강설량 0 ~ 20mm
    private byte DayTime; // 0 ~ 24 시간

    rcv_HMS_COMMON_SCENARIO_INFO(byte[] data){
        conversion_LE kk = new conversion_LE();
        int offset = 0;
        Rain = data[offset];  offset += 1;
        Snow = data[offset];  offset += 1;
        Fog = data[offset];  offset += 1;
        Cloud = data[offset];  offset += 1;
        WaterOnRoad = data[offset];  offset += 1;
        SnowOnRoad = data[offset];  offset += 1;
        DayTime = data[offset];
    }

    public byte getRain() {
        return Rain;
    }
    public byte getSnow() {
        return Snow;
    }
    public byte getFog() {
        return Fog;
    }
    public byte getCloud() {
        return Cloud;
    }
    public byte getWaterOnRoad() {
        return WaterOnRoad;
    }
    public byte getSnowOnRoad() {
        return SnowOnRoad;
    }
    public byte getDayTime() {
        return DayTime;
    }
}

class rcv_HMS_COMMON_RECOMMEND_DRIVING_GUIDE {
    private byte SpeedUp; // "0x00 : 불가능            0x01 : 가능    0x02 : 추천"
    private byte SpeedDown; // "0x00 : 불가능            0x01 : 가능    0x02 : 추천"
    private byte LaneChange2Left; // "0x00 : 불가능            0x01 : 가능    0x02 : 추천"
    private byte LaneChange2Right; // "0x00 : 불가능            0x01 : 가능    0x02 : 추천"

    rcv_HMS_COMMON_RECOMMEND_DRIVING_GUIDE(byte[] data){
        conversion_LE kk = new conversion_LE();
        int offset = 0;
        SpeedUp = data[offset];  offset += 1;
        SpeedDown = data[offset];  offset += 1;
        LaneChange2Left = data[offset];  offset += 1;
        LaneChange2Right = data[offset];
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
    private char Dist2Goal; // m
    private int Time2Goal; // second
    private char Dist2GP; // m
    private int Time2GP; // second
    private char TotalDist; // m
    private byte TBTCode;
    private byte SpeedLimit; // km
    private char[] CurrentRoadName = new char[32];
    private char[] DirectionRoadName = new char[32];
    private char ADStartDistOffset; // m. 현재 AD 구간이 시작하는 거리
    private char ADDist; // m. 현재 AD 구간의 총 거리

    rcv_HMS_COMMON_NAVI_GUIDANCE_INFO(byte[] data){
        conversion_LE kk = new conversion_LE();
        int offset = 0;
        Dist2Goal = kk.byteToChar_LE (data, offset); offset += 2;
        Time2Goal = kk.byteToInt_LE(data, offset); offset += 4;
        Dist2GP = kk.byteToChar_LE (data, offset); offset += 2;
        Time2GP = kk.byteToInt_LE(data, offset); offset += 4;
        TotalDist = kk.byteToChar_LE (data, offset); offset += 2;
        TBTCode = data[offset]; offset += 1;
        SpeedLimit = data[offset]; offset += 1;
        CurrentRoadName = kk.byteToCharArray_LE(data, offset, 64); offset += 64;
        DirectionRoadName = kk.byteToCharArray_LE(data, offset, 64); offset += 64;
        ADStartDistOffset = kk.byteToChar_LE(data, offset); offset += 2;
        ADDist = kk.byteToChar_LE(data, offset);
    }

    public char getDist2Goal() {
        return Dist2Goal;
    }
    public int getTime2Goal() {
        return Time2Goal;
    }
    public char getDist2GP() {
        return Dist2GP;
    }
    public int getTime2GP() {
        return Time2GP;
    }
    public char getTotalDist() {
        return TotalDist;
    }
    public byte getTBTCode() {
        return TBTCode;
    }
    public byte getSpeedLimit() {
        return SpeedLimit;
    }
    public char[] getCurrentRoadName() {
        return CurrentRoadName;
    }
    public char[] getDirectionRoadName() {
        return DirectionRoadName;
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

    rcv_HMS_COMMON_AUTONOMOUS_DRIVING(byte[] data){
        Command = data[0];
    }

    public byte getCommand() {
        return Command;
    }
}

class rcv_HMS_COMMON_MANUAL_DRIVING {
    private byte Command; // "0x01 : 수동주행 시작 0x02 : 수동주행 종료"

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

    rcv_HMS_COMMON_MODE_READY_COUNTDOWN(byte[] data){
        Count = data[0];
        Mode = data[1];
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

    rcv_HMS_COMMON_DRIVER_STATUS_INFO(byte[] data){
        Gaze = data[0];
        Sleep = data[1];
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
        DriverTemp = data[0];
        PassengerTemp = data[1];
        FanSpeed = data[2];
        AirFlow = data[3];
        AC = data[4];
        DriverSeatHeat = data[5];
        PassengerSeatHeat = data[6];
        Auto = data[7];
        FrontDefrost = data[8];
        RearDefrost = data[9];
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
        DriverTemp = data[0];
        PassengerTemp = data[1];
        FanSpeed = data[2];
        AirFlow = data[3];
        AC = data[4];
        DriverSeatHeat = data[5];
        PassengerSeatHeat = data[6];
        Auto = data[7];
        FrontDefrost = data[8];
        RearDefrost = data[9];
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
        DriverTemp = data[0];
        PassengerTemp = data[1];
        FanSpeed = data[2];
        AirFlow = data[3];
        AC = data[4];
        DriverSeatHeat = data[5];
        PassengerSeatHeat = data[6];
        Auto = data[7];
        FrontDefrost = data[8];
        RearDefrost = data[9];
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
    private char[] Title = new char[32];
    private char[] Artist = new char[32];
    private char[] Album = new char[32];
    private char[] Genr = new char[32];
    private float Position;
    private float Duration;
    private char Index;

    rcv_HMS_COMMON_MUSIC_INFO(byte[] data) {
        conversion_LE kk = new conversion_LE();
        int offset = 0;
        Title = kk.byteToCharArray_LE(data, offset, 64);        offset += 64;
        Artist = kk.byteToCharArray_LE(data, offset, 64);        offset += 64;
        Album = kk.byteToCharArray_LE(data, offset, 64);        offset += 64;
        Genr = kk.byteToCharArray_LE(data, offset, 64);        offset += 64;
        Position = kk.byteToFloat_LE(data, offset);        offset += 4;
        Duration = kk.byteToFloat_LE(data, offset);        offset += 4;
        Index = kk.byteToChar_LE(data, offset);
    }

    public char[] getTitle() {
        return Title;
    }
    public char[] getArtist() {
        return Artist;
    }
    public char[] getAlbum() {
        return Album;
    }
    public char[] getGenr() {
        return Genr;
    }
    public float getPosition() {
        return Position;
    }
    public float getDuration() {
        return Duration;
    }
    public char getIndex() {
        return Index;
    }
}

class rcv_HMS_COMMON_DISPLAY_DANGER_INFO{
    private byte IconCode; // "Icon 종류(TBD) 0x01 : 도로 상태 이상    0x02 : 날씨 변화    0x03 : 사고 발생    0x04 : 강제 수동 전환"
    private char param; // Icon별 추가 정보

    rcv_HMS_COMMON_DISPLAY_DANGER_INFO(byte[] data) {
        conversion_LE kk = new conversion_LE();
        int offset = 0;
        IconCode = data[offset];        offset += 1;
        param = kk.byteToChar_LE(data, offset);
    }

    public byte getIconCode() {
        return IconCode;
    }
    public char getParam() {
        return param;
    }
}

class rcv_HMS_COMMON_DISPLAY_DANGER_ALARM{
    private char Display; // "0x01 : Display1, 0x02 : Display2, 0x04 : Display3, 0x08 : Display4, 0x10 : Display5, 0x20 : LED, 0x40 : ARHUD, 0x80 : LEFT SIDE MIRROR, 0x100 : RIGHT SIDE MIRROR"
    private byte Sound; // "0x01 : 위험도 1             0x02 : 위험도 2            0x03 : 위험도 3"
    private char Interval; // 표시 간격(ms)

    rcv_HMS_COMMON_DISPLAY_DANGER_ALARM(byte[] data) {
        conversion_LE kk = new conversion_LE();
        int offset = 0;
        Display = kk.byteToChar_LE(data, offset);        offset += 2;
        Sound = data[offset];        offset += 1;
        Interval = kk.byteToChar_LE(data, offset);
    }

    public char getDisplay() {
        return Display;
    }
    public byte getSound() {
        return Sound;
    }
    public char getInterval() {
        return Interval;
    }
}

class rcv_HMS_COMMON_GPS_INFO{
    private double Latitude;
    private double Longitude;
    private double Altitude;
    private float Heading;
    private float Speed;

    rcv_HMS_COMMON_GPS_INFO(byte[] data) {
        conversion_LE kk = new conversion_LE();
        int offset = 0;
        Latitude = kk.byteToDouble_LE(data, offset);        offset += 8;
        Longitude = kk.byteToDouble_LE(data, offset);        offset += 8;
        Altitude = kk.byteToDouble_LE(data, offset);        offset += 8;
        Heading = kk.byteToFloat_LE(data, offset);        offset += 4;
        Speed = kk.byteToFloat_LE(data, offset);
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
}

class rcv_HMS_TPDV_DISPLAY_GOAL_MAP{
    private double Latitude; // 표시용 목적지 위도
    private double Longitude; // 표시용 목적지 경도
    private byte GoalType;
    private char[] Name = new char[32];

    rcv_HMS_TPDV_DISPLAY_GOAL_MAP(byte[] data) {
        conversion_LE kk = new conversion_LE();
        int offset = 0;
        Latitude = kk.byteToDouble_LE(data, offset);        offset += 8;
        Longitude = kk.byteToDouble_LE(data, offset);        offset += 8;
        GoalType = data[offset];        offset += 1;
        Name = kk.byteToCharArray_LE(data, offset, 64);
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
    public char[] getName() {
        return Name;
    }
}

class rcv_HMS_TPDV_DISPLAY_CURR_MAP{
    private double Latitude; // 표시용 목적지 위도
    private double Longitude; // 표시용 목적지 경도
    private char[] Name = new char[32];

    rcv_HMS_TPDV_DISPLAY_CURR_MAP(byte[] data) {
        conversion_LE kk = new conversion_LE();
        int offset = 0;
        Latitude = kk.byteToDouble_LE(data, offset);        offset += 8;
        Longitude = kk.byteToDouble_LE(data, offset);        offset += 8;
        Name = kk.byteToCharArray_LE(data, offset, 64);
    }

    public double getLatitude() {
        return Latitude;
    }
    public double getLongitude() {
        return Longitude;
    }
    public char[] getName() {
        return Name;
    }
}
