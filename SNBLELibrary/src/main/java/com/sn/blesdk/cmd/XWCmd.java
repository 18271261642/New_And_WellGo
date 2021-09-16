package com.sn.blesdk.cmd;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.sn.app.db.data.config.DeviceConfigBean;
import com.sn.app.db.data.config.DeviceConfigDao;
import com.sn.app.db.data.config.bean.RemindConfig;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.entity.WallpaperPackage;
import com.sn.blesdk.interfaces.ICmd;
import com.sn.blesdk.utils.WallpaperUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * 作者:东芝(2017/11/21).
 * 功能:先微协议
 * 里面的方法功能参数具体描述 在ICmd中有写 这里不写了
 */
public class XWCmd implements ICmd {

    private static final int MAX_LENGTH = 20;

    @Override
    public byte[] setDeviceReStart() {
        return getByteWithCount(MAX_LENGTH, 0, 1);
    }

    @Override
    public byte[] getDeviceMacInfo() {
        return getByteWithCount(MAX_LENGTH, 1, 1);
    }

    @Override
    public byte[] getDeviceInfoCmd0() {
        return getByteWithCount(MAX_LENGTH, 1, 2);
    }

    @Override
    public byte[] getDeviceInfoCmd1() {
        return getByteWithCount(MAX_LENGTH, 1, 3);
    }

    @Override
    public byte[] getAlarmReminderInfo() {
        return getByteWithCount(MAX_LENGTH, 1, 4);
    }

    @Override
    public byte[] getScheduleReminderInfo() {
        return getByteWithCount(MAX_LENGTH, 1, 5);
    }

    @Override
    public byte[] getDrinkReminderInfo() {
        return getByteWithCount(MAX_LENGTH, 1, 6);
    }

    @Override
    public byte[] getSedentaryReminderInfo() {
        return getByteWithCount(MAX_LENGTH, 1, 7);
    }

    @Override
    public byte[] getDeviceTimeInfo() {
        return getByteWithCount(MAX_LENGTH, 1, 8);
    }

    @Override
    public byte[] getDeviceOtherInfo() {
        return getByteWithCount(MAX_LENGTH, 1, 0x0A);
    }

    @Override
    public byte[] getDeviceNightModeInfo() {
        return getByteWithCount(MAX_LENGTH, 1, 0x0B);
    }

    @Override
    public byte[] getScheduleReminderTagInfo() {
        return getByteWithCount(MAX_LENGTH, 1, 0x0C);
    }

    @Override
    public byte[] getAirPressureAltitudeTemperature() {
        return getByteWithCount(MAX_LENGTH, 1, 0x0D);
    }

    @Override
    public byte[] setWeatherInfo(int weather, int curTem, int maxTem, int minTem, int dayOffset) {
        byte[] buffer = new byte[MAX_LENGTH];
        buffer[0] = 0x05;
        buffer[1] = 0x02;
        buffer[2] = 0x0E;
        buffer[3] = (byte) weather;
        buffer[6] = (byte) curTem;
        buffer[8] = (byte) maxTem;
        buffer[10] = (byte) minTem;
        buffer[15] = (byte) dayOffset;
        return buffer;
    }

    @Override
    public byte[] setDeviceAirUpdate() {
        return getByteWithCount(MAX_LENGTH, 0, 2);
    }

    @Override
    public byte[] syncRealTimeSportData() {
        return getByteWithCount(MAX_LENGTH, 7, 1);
    }

    @Override
    public byte[] syncRealTimeHeartRateData() {
        return getByteWithCount(MAX_LENGTH, 7, 2);
    }

    @Override
    public byte[] syncHistorySportData() {
        return getByteWithCount(MAX_LENGTH, 7, 3);
    }

    @Override
    public byte[] syncHistorySleepData() {
        return getByteWithCount(MAX_LENGTH, 7, 4);
    }

    @Override
    public byte[] syncHistoryHeartRateData() {
        return getByteWithCount(MAX_LENGTH, 7, 7);
    }

    @Override
    public byte[] syncHistoryBloodPressureData() {
        return getByteWithCount(MAX_LENGTH, 7, 8);
    }

    @Override
    public byte[] syncHistoryBloodOxygenData() {
        return getByteWithCount(MAX_LENGTH, 7, 9);
    }

    @Override
    public byte[] setHighSpeedTransportStatus(boolean enable) {
        byte[] buffer = new byte[MAX_LENGTH];
        buffer[0] = 0x05;
        buffer[1] = 0x02;
        buffer[2] = 0x11;
        buffer[3] = (byte) (enable ? 0x01 : 0x00);
        return buffer;
    }

    @Override
    public List<WallpaperPackage> createWallpaperPackage(Bitmap src) {
        if (Thread.currentThread().getName().equalsIgnoreCase("main")) {
            throw new IllegalThreadStateException("The method is not allowed to call in the UI thread");
        }
        byte[] bytes = WallpaperUtil.BMP2RGB565bytes(src);
        int packageCount = Math.round(bytes.length * 1.0f / 504);
        List<WallpaperPackage> wallpaperPackages = new ArrayList<>();
        for (int i = 0; i < bytes.length; i += 504) {
            if (i + 504 < bytes.length) {
                byte[] pck = new byte[512];
                System.arraycopy(bytes, i, pck, 8, 504);
                int index = wallpaperPackages.isEmpty() ? 0 : wallpaperPackages.size();
                updateWallpaperPackagesHead(index, packageCount, pck);
                wallpaperPackages.add(new WallpaperPackage(getBytes(pck)));
            } else {
                byte[] pck = new byte[8 + bytes.length - i];
                System.arraycopy(bytes, i, pck, 8, pck.length - 8);
                int index = wallpaperPackages.isEmpty() ? 0 : wallpaperPackages.size();
                updateWallpaperPackagesHead(index, packageCount, pck);
                wallpaperPackages.add(new WallpaperPackage(getBytes(pck)));
            }
        }
        return wallpaperPackages;
    }


    private void updateWallpaperPackagesHead(long packageIndex, long packageCount, byte[] bytes) {
        int total = 0;
        for (int i = 8; i < bytes.length; i++) {
            total += bytes[i];
        }

        long headVersion4Bits = 0;//暂时为0

        long packageLength = bytes.length - 8;
        long errFlag = 0;
        long ackFlag = 0;
        long reserve = 0;
        long bits4_14_14 = (((headVersion4Bits << 14) | packageCount) << 14) | packageIndex;
        long bits10_1_1_4 = (((((packageLength << 1) | errFlag) << 1) | ackFlag) << 4) | reserve;


        bytes[0] = 0x05;//产品
        bytes[1] = (byte) (total & 0xff);//CRC校验

        bytes[2] = (byte) ((bits4_14_14 >> 24) & 0xFF);


        bytes[3] = (byte) ((bits4_14_14 >> 16) & 0xFF);
        bytes[4] = (byte) ((bits4_14_14 >> 8) & 0xFF);
        bytes[5] = (byte) (bits4_14_14 & 0xFF);

        bytes[6] = (byte) ((bits10_1_1_4 >> 8) & 0xFF);
        bytes[7] = (byte) (bits10_1_1_4 & 0xFF);

    }

    private List<byte[]> getBytes(byte[] pck) {
        List<byte[]> pckbytes20 = new ArrayList<>();
        for (int j = 0; j < pck.length; j += 20) {
            if (j + 20 < pck.length) {
                byte[] cpck = new byte[20];
                System.arraycopy(pck, j, cpck, 0, cpck.length);
                pckbytes20.add(cpck);
            } else {
                byte[] cpck = new byte[pck.length - j];
                System.arraycopy(pck, j, cpck, 0, cpck.length);
                pckbytes20.add(cpck);
            }
        }
        return pckbytes20;
    }

//    @Override
//    public byte[] getHistoryBloodPressureData() {
//        return getByteWithCount(MAX_LENGTH, 7, 8);
//    }
//
//    @Override
//    public byte[] getHistoryBloodOxygenData() {
//        return getByteWithCount(MAX_LENGTH, 7, 9);
//    }

    @Override
    public byte[] setFindDeviceStatus(boolean status) {
        byte[] buffer = new byte[MAX_LENGTH];
        buffer[0] = 0x05;
        buffer[1] = 0x05;
        buffer[2] = 0x03;
        if (status) {
            buffer[3] = 0x01;
        } else {
            buffer[3] = 0x00;
        }
        return buffer;
    }

    @Override
    public byte[] setDeviceOtherInfo(int power, int light, int antiLost, int healthAutoCheck) {
        byte[] bytes = new byte[MAX_LENGTH];
        bytes[0] = 0x05;
        bytes[1] = 0x02;
        bytes[2] = 0x0a;
        bytes[3] = (byte) power;
        bytes[4] = (byte) light;
        bytes[5] = (byte) antiLost;
        bytes[6] = (byte) healthAutoCheck;
//		bytes[7] 翻腕切屏 未接到需求
        return bytes;
    } @Override
    public byte[] setDeviceOtherInfo(int power, int light, int antiLost, int healthAutoCheck,int temperatureAutoCheck) {
        byte[] bytes = new byte[MAX_LENGTH];
        bytes[0] = 0x05;
        bytes[1] = 0x02;
        bytes[2] = 0x0a;
        bytes[3] = (byte) power;
        bytes[4] = (byte) light;
        bytes[5] = (byte) antiLost;
        bytes[6] = (byte) healthAutoCheck;
        bytes[9] = (byte) temperatureAutoCheck;
//		bytes[7] 翻腕切屏 未接到需求
        return bytes;
    }

    @Override
    public byte[] setDeviceNightModeInfo(int status, int startH, int startM, int endH, int endM) {
        byte[] bytes = new byte[MAX_LENGTH];
        bytes[0] = 0x05;
        bytes[1] = 0x02;
        bytes[2] = 0x0B;
        bytes[3] = (byte) status;
        bytes[4] = (byte) startH;
        bytes[5] = (byte) startM;
        bytes[6] = (byte) endH;
        bytes[7] = (byte) endM;

        return bytes;
    }

    @Override
    public byte[] setDeviceTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        byte[] bytes = getByteWithCount(MAX_LENGTH, 2, 1);
        //年2位
        byte[] buffer = new byte[]{(byte) (year >> 24), (byte) (year >> 16), (byte) (year >> 8), (byte) year};
        bytes[3] = buffer[2];
        bytes[4] = buffer[3];
        //月
        bytes[5] = (byte) month;
        bytes[6] = (byte) day;
        bytes[7] = (byte) hour;
        bytes[8] = (byte) minute;
        bytes[9] = (byte) second;
        return bytes;
    }

    /**
     * 设置基本信息
     *
     * @param gender          0x01男,0x02女
     * @param age
     * @param height          单位CM
     * @param weight          单位kg
     * @param handedness      0x00：左手 0x01：右手
     * @param timeUnit        0x00：24小时制,0x01：12小时制
     * @param distanceUnit    0x00：公制 0x01：英制
     * @param temperatureUnit 0x00：摄氏度 0x01：华摄氏度
     * @param clientLanguage  0x00：中文,0x01：英文
     * @param targetSteps     目标步数
     * @return
     */
    @Override
    public byte[] setDeviceBaseInfo(int gender, int age, float height, float weight, int handedness, int timeUnit, int distanceUnit, int temperatureUnit, int clientLanguage, int targetSteps) {

        byte[] bytes = getByteWithCount(MAX_LENGTH, 2, 2);

        bytes[3] = (byte) gender;
        bytes[4] = (byte) age;
        bytes[5] = (byte) height;
        bytes[6] = (byte) weight;
        bytes[7] = (byte) clientLanguage;
        bytes[8] = (byte) timeUnit;
        bytes[9] = (byte) distanceUnit;
        bytes[10] = 0x01;//安卓
        bytes[11] = (byte) handedness;//安卓
        bytes[12] = (byte) temperatureUnit;//0x00：摄氏度 0x01：华摄氏度
        bytes[13] = (byte) (targetSteps >> 24);
        bytes[14] = (byte) (targetSteps >> 16);
        bytes[15] = (byte) (targetSteps >> 8);
        bytes[16] = (byte) targetSteps;

        return bytes;
    }

    /**
     * 闹钟提醒
     *
     * @param num    序号(从0开始)
     * @param status 0x01 开启,0x00关闭
     * @param cycle  重复周期 [Bit0-Bit6 则周日周一周二...周六]
     * @param hour   小时集
     * @param minute 分钟
     * @param tag    标签
     * @return
     */
    @Override
    public byte[] setAlarmReminderInfo(int num, int status, int cycle, int hour, int minute, int tag) {
        //序号（0-5）+开关+重复+时+分+标签
        byte[] bytes = getByteWithCount(MAX_LENGTH, 2, 4);
        bytes[3] = (byte) num;               //序号
        bytes[4] = (byte) status;            //开关
        bytes[5] = (byte) cycle;             //重复周期
        bytes[6] = (byte) hour;              //小时
        bytes[7] = (byte) minute;            //分钟
        bytes[8] = (byte) tag;               //标签
        return bytes;
    }

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public byte[] setScheduleReminderInfo(int status, int num, String date, int tag) {
        byte[] bytes = getByteWithCount(MAX_LENGTH, 2, 5);

        bytes[3] = (byte) num;
        bytes[4] = (byte) status;

        Calendar calendar = Calendar.getInstance();
        try {

            calendar.setTimeInMillis(format.parse(date).getTime());
        } catch (Exception e) {
            throw new IllegalArgumentException("非法参数错误,date 日期请使用yyyy-MM-dd HH:mm 格式");
        }
        //年月日 时分
        bytes[5] = (byte) (calendar.get(Calendar.YEAR) - 2000); //2017只取17,注意 这不是指 2017的低位
        bytes[6] = (byte) (calendar.get(Calendar.MONTH) + 1);
        bytes[7] = (byte) calendar.get(Calendar.DAY_OF_MONTH);
        bytes[8] = (byte) calendar.get(Calendar.HOUR_OF_DAY);
        bytes[9] = (byte) calendar.get(Calendar.MINUTE);
        bytes[10] = (byte) tag;

        return bytes;
    }

    @Override
    public byte[] setDrinkReminderInfo(int status, int cycle, int sHour, int sMinute, int eHour, int eMinute, int intervalTime) {
        byte[] bytes = getByteWithCount(MAX_LENGTH, 2, 6);
        bytes[3] = (byte) status;
        bytes[4] = (byte) cycle;
        bytes[5] = (byte) sHour;
        bytes[6] = (byte) sMinute;
        bytes[7] = (byte) eHour;
        bytes[8] = (byte) eMinute;
        bytes[17] = (byte) intervalTime;
        return bytes;
    }

    @Override
    public byte[] setSedentaryReminderInfo(int status, int cycle, int sHour, int sMinute, int eHour, int eMinute, int intervalTime) {
        byte[] bytes = getByteWithCount(MAX_LENGTH, 2, 7);
        bytes[3] = (byte) status;
        bytes[4] = (byte) cycle;
        bytes[3] = (byte) status;
        bytes[4] = (byte) cycle;
        bytes[5] = (byte) sHour;
        bytes[6] = (byte) sMinute;
        bytes[7] = (byte) eHour;
        bytes[8] = (byte) eMinute;
        bytes[17] = (byte) intervalTime;
        return bytes;
    }


    @Override
    public byte[] setHeartRateStatus(int status) {
        byte[] buffer = new byte[MAX_LENGTH];
        buffer[0] = 0x05;
        buffer[1] = 0x05;
        buffer[2] = 0x01;
        buffer[3] = (byte) status;
        return buffer;
    }


    @Override
    public byte[] setBloodOxygenStatus(int status) {
        byte[] buffer = new byte[MAX_LENGTH];
        buffer[0] = 0x05;
        buffer[1] = 0x05;
        buffer[2] = 0x04;
        buffer[3] = (byte) status;
        return buffer;
    }

    @Override
    public byte[] setBloodPressureStatus(int status) {
        byte[] buffer = new byte[MAX_LENGTH];
        buffer[0] = 0x05;
        buffer[1] = 0x05;
        buffer[2] = 0x05;
        buffer[3] = (byte) status;
        return buffer;
    }

    @Override
    public byte[] setCallStatus(int status) {
        byte[] buffer = new byte[MAX_LENGTH];
        buffer[0] = 0x05;
        buffer[1] = 0x05;
        buffer[2] = 0x06;
        buffer[3] = (byte) status;
        return buffer;
    }

    @Override
    public byte[] setCameraStatus(int status) {
        byte[] buffer = new byte[MAX_LENGTH];
        buffer[0] = 0x05;
        buffer[1] = 0x05;
        buffer[2] = 0x02;
        buffer[3] = (byte) status;
        return buffer;
    }

    @Override
    public List<byte[]> setCallReminderMessage(String title, String phoneNumber) {

        byte[] buffer = getPhoneNumber(title, phoneNumber);
        List<byte[]> byteSet = new LinkedList<>();
        int j = 0;
        int index = 0;
        byte[] str = new byte[15];
        int length = buffer.length;
        for (int i = 0; i < length; i++) {
            str[j] = buffer[i];
            j++;
            if (j == 15 || i == length - 1) {
                byte[] bytes = new byte[MAX_LENGTH];
                bytes[0] = 0x05;
                bytes[1] = 0x04;
                bytes[2] = 0x01;
                bytes[3] = (byte) index;
                bytes[4] = (byte) j;
                for (int l = 0; l < str.length; l++) {
                    bytes[5 + l] = str[l];
                    str[l] = 0;
                }
                byteSet.add(bytes);
                index += 1;
                j = 0;
            }
        }
        byte[] end = endByte(1, 0x00);
        byteSet.add(end);
        return byteSet;
    }

    @Override
    public List<byte[]> setSMSMessage(String title, String content) {
        title = cutShortString(title);
        content = cutShortString(content);
        List<byte[]> byteSet = new LinkedList<>();
        int j = 0;
        int index = 0;
        byte[] str = new byte[15];
        byte[] buffer = getSMS(title, content);
        int length = buffer.length;
        for (int i = 0; i < length; i++) {
            str[j] = buffer[i];
            j++;
            if (j == 15 || i == length - 1) {
                byte[] bytes = new byte[MAX_LENGTH];
                bytes[0] = 0x05;
                bytes[1] = 0x04;
                bytes[2] = 0x02;
                bytes[3] = (byte) index;
                bytes[4] = (byte) j;
                for (int l = 0; l < str.length; l++) {
                    bytes[5 + l] = str[l];
                    str[l] = 0;
                }
                byteSet.add(bytes);
                index += 1;
                j = 0;
            }
        }
        byte[] end = endByte(2, 0x00);
        byteSet.add(end);
        return byteSet;
    }

    @Override
    public List<byte[]> setAppMessage(String packageName, String title, String content) {
        title = cutShortString(title);
        content = cutShortString(content);

        List<byte[]> byteSet = new LinkedList<>();
        byte[] buffer = getMessage(title, content);
        int j = 0;
        int index = 0;
        byte[] str = new byte[15];
        int length = buffer.length;
        for (int i = 0; i < length; i++) {
            str[j] = buffer[i];
            j++;
            if (j == 15 || i == length - 1) {
                byte[] bytes = new byte[20];
                bytes[0] = 0x05;
                bytes[1] = 0x04;
                bytes[2] = 0x03;
                bytes[3] = (byte) index;
                bytes[4] = (byte) j;
                for (int l = 0; l < str.length; l++) {
                    bytes[5 + l] = str[l];
                    str[l] = 0;
                }
                byteSet.add(bytes);
                index += 1;
                j = 0;
            }
        }
        byte[] end = endByte(3, getAppType(packageName));
        byteSet.add(end);
        return byteSet;


        //TODO 我的算法:
//        ByteBuffer buffer = ByteBuffer.allocate(title.length() * 2 + content.length() * 2 + 2);
//
//        int len = title.length() ;
//        for (int i = 0; i < len; i++) {
//            char c = title.charAt(i);
//            buffer.put((byte) (c>>8));
//            buffer.put((byte) (c&0x00FF));
//        }
//        buffer.put((byte) 0xFF);
//        buffer.put((byte) 0xFF);
//        len = content.length() ;
//        for (int i = 0; i < len; i++) {
//            char c = content.charAt(i);
//            buffer.put((byte) (c>>8));
//            buffer.put((byte) (c&0x00FF));
//        }
//        byte[] array = buffer.array();
//
//        int index = 0;
//        for (int i = 0; i < array.length; i+=15) {
//            byte[] bytes = new byte[20];
//            bytes[0]=0x05;
//            bytes[1]=0x04;
//            bytes[2]=0x03;
//            bytes[3]= (byte) index;//序号
//            int data_size = array.length-i>15?15:array.length-i;
//            bytes[4]= (byte) data_size;//长度
//            System.arraycopy(array,i,bytes,5,data_size);
//            list.add(SNBLEHelper.toHexString(bytes));
//            SNBLEHelper.sendCMD(bytes);
//            index++;
//        }
//        SNBLEHelper.sendCMD(new byte[]{0x05,0x04,0x03, (byte)(0xff)});
    }

    private int getAppType(String packageName) {
        try {
            DeviceConfigDao deviceConfigDao = DeviceConfigDao.get(DeviceConfigDao.class);
            DeviceConfigBean deviceConfigBean = deviceConfigDao.queryForUser(AppUserUtil.getUser().getUser_id());
            RemindConfig.Apps apps = deviceConfigBean.getRemindConfig().findRemindAppPush(packageName);
            if(apps!=null) {
                switch (apps.getAppName()) {
                    case "Email":
                        return 0x02;
                    case "FaceBook":
                        return 0x03;
                    case "Wechat":
                        return 0x04;
                    case "Line":
                        return 0x05;
                    case "Weibo":
                        return 0x06;
                    case "Linkedln":
                        return 0x07;
                    case "QQ":
                        return 0x08;
                    case "WhatsApp":
                        return 0x09;
                    case "Viber":
                        return 0x0A;
                    case "Instagram":
                        return 0x0B;
//                case "GooglePlus": return 0x01;
                    default:
                        return 0x01;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0x01;
    }

    private String cutShortString(String str) {
        int len = str.length();
        if (len > 200) {
            str = str.substring(0, 200);
        }
        return str;
    }

    @Override
    public byte[] setScheduleReminderTagInfo(int num, String tag) {
        return new byte[0];
    }


    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------以下是私有函数-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 指定长度
     *
     * @param count
     * @param commandID
     * @param functionKey
     * @return
     */
    private static byte[] getByteWithCount(int count, int commandID, int functionKey) {
        byte[] bytes = new byte[count];
        bytes[0] = 0x05;
        bytes[1] = (byte) commandID;
        bytes[2] = (byte) functionKey;

        return bytes;
    }


    /**
     * 获取时间的byte[]
     *
     * @param time 时间  08:30
     * @return byte[ 0x08,0x1b]
     */
    private static byte[] getByte(String time) {

        byte[] bytes = new byte[2];

        if (TextUtils.isEmpty(time)) {
            return bytes;
        }

        if (!time.contains(":") && time.length() != 5) {
            return bytes;
        }

        String[] timeArray = time.split(":");

        String hourString = timeArray[0];
        String minuteString = timeArray[1];
//		int hour = hourString.startsWith("0") ? Integer.parseInt(hourString) : Integer.parseInt(String.valueOf(hourString.charAt(1)));
//		int minute = minuteString.startsWith("0") ? Integer.parseInt(minuteString) : Integer.parseInt(String.valueOf(minuteString.charAt(1)));
        int hour = Integer.parseInt(hourString);
        int minute = Integer.parseInt(minuteString);

        bytes[0] = (byte) hour;
        bytes[1] = (byte) minute;

        return bytes;

    }


    private byte[] endByte(int function_key, int appType) {
        byte[] end = new byte[5];
        end[0] = 0x05;
        end[1] = 0x04;
        end[2] = (byte) function_key;
        end[3] = (byte) 255;
        end[4] = (byte) appType;
        return end;
    }


    /**
     * 来电
     *
     * @param phoneNumber
     * @return
     */
    private byte[] getPhoneNumber(String name, String phoneNumber) {
        int count = 0;
        int l = 0;
        String n = getUnicode(name);
        String p = getUnicode(phoneNumber);
        int nLength = n.length();
        int pLength = p.length();
        byte[] bytes = new byte[(nLength + pLength) / 2 + 2];
        for (int i = 0; i < nLength; i = i + 2) {
            int num = Integer.parseInt(n.substring(i, i + 2), 16);
            bytes[l] = (byte) num;
            l++;
        }
        bytes[l] = (byte) 255;
        bytes[1 + l] = (byte) 255;
        for (int i = 0; i < pLength; i = i + 2) {
            int num = Integer.parseInt(p.substring(i, i + 2), 16);
            bytes[2 + l + count] = (byte) num;
            count++;
        }
        return bytes;
    }

    /**
     * 短信提醒
     *
     * @param name
     * @param contents
     * @return
     */
    private byte[] getSMS(String name, String contents) {
        int count = 0;
        int l = 0;
        String n = getUnicode(name);
        String p = getUnicode(contents);
        int nLength = n.length();
        int pLength = p.length();
        byte[] bytes = new byte[(nLength + pLength) / 2 + 2];
        for (int i = 0; i < nLength; i = i + 2) {
            int num = Integer.parseInt(n.substring(i, i + 2), 16);
            bytes[l] = (byte) num;
            l++;
        }
        bytes[l] = (byte) 255;
        bytes[1 + l] = (byte) 255;
        for (int i = 0; i < pLength; i = i + 2) {
            int num = Integer.parseInt(p.substring(i, i + 2), 16);
            bytes[2 + l + count] = (byte) num;
            count++;
        }
        return bytes;
    }

    /**
     * 微信、QQ
     *
     * @param name
     * @param content
     * @return
     */
    private byte[] getMessage(String name, String content) {
        int count = 0;
        int l = 0;
        String n = getUnicode(name);
        String c = getUnicode(content);
        int nLength = n.length();
        int cLength = c.length();
        byte[] bytes = new byte[(nLength + cLength) / 2 + 2];
        for (int i = 0; i < nLength; i = i + 2) {
            int num = Integer.parseInt(n.substring(i, i + 2), 16);
            bytes[l] = (byte) num;
            l++;
        }
        bytes[l] = (byte) 255;
        bytes[1 + l] = (byte) 255;
        for (int i = 0; i < cLength; i = i + 2) {
            int num = Integer.parseInt(c.substring(i, i + 2), 16);
            bytes[2 + l + count] = (byte) num;
            count++;
        }
        return bytes;
    }

    /**
     * String转unicode
     */
    public String getUnicode(String src) {
        String str = "";
        for (int i = 0; i < src.length(); i++) {
            // 取出每一个字符
            int c = src.charAt(i);
            // 转换为unicode
            str += String.format("%04X", c);

        }
        return str;
    }


    @Override
    public byte[] getWallpaperScreenInfo() {
        return getByteWithCount(MAX_LENGTH, 0x01, 0x14);
    }

    @Override
    public byte[] getWallpaperFontInfo() {
        return getByteWithCount(MAX_LENGTH, 0x01, 0x15);
    }

    @Override
    public byte[] setWallpaperEnable(boolean enable) {
        byte[] buffer = new byte[MAX_LENGTH];
        buffer[0] = 0x05;
        buffer[1] = 0x02;
        buffer[2] = 0x12;
        buffer[3] = (byte) (enable ? 0x01 : 0x02);
        return buffer;
    }

    /**
     * @param enable
     * @param isHorizontal
     * @param fontWidth
     * @param fontHeight
     * @param colorRgb888
     * @param x
     * @param y
     * @return Ox00:保留
     * 0x01:设置成功
     * 0x02:显示方式不支持
     * 0x03:需要显示的字体分辨率不支持
     * 0x04:需要显示的字体颜色不支持
     * 0x05:X 轴，Y 轴起始坐标点异常
     * 0x06:X 轴，Y 轴起始坐标点正常，根据计算，已越界
     * 0x07:与其他屏保效果块重叠
     */
    @Override
    public byte[] setWallpaperTimeInfo(boolean enable, boolean isHorizontal, int fontWidth, int fontHeight, int colorRgb888, int x, int y) {
        byte[] buffer = new byte[MAX_LENGTH];
        buffer[0] = 0x05;
        buffer[1] = 0x02;
        buffer[2] = 0x13;
        buffer[3] = (byte) (enable ? 0x01 : 0x02);
        buffer[4] = (byte) (isHorizontal ? 0x01 : 0x02);
        buffer[5] = (byte) fontWidth;
        buffer[6] = (byte) fontHeight;
        int colorRgb565 = WallpaperUtil.RGB888ToRGB565(colorRgb888);
        buffer[7] = (byte) (colorRgb565 >> 8);
        buffer[8] = (byte) (colorRgb565 & 0xff);
        buffer[9] = (byte) (x >> 8);
        buffer[10] = (byte) (x & 0xff);
        buffer[11] = (byte) (y >> 8);
        buffer[12] = (byte) (y & 0xff);
        return buffer;
    }

    @Override
    public byte[] setWallpaperStepInfo(boolean enable, boolean isHorizontal, int fontWidth, int fontHeight, int colorRgb888, int x, int y) {
        byte[] buffer = new byte[MAX_LENGTH];
        buffer[0] = 0x05;
        buffer[1] = 0x02;
        buffer[2] = 0x14;
        buffer[3] = (byte) (enable ? 0x01 : 0x02);
        buffer[4] = (byte) (isHorizontal ? 0x01 : 0x02);
        buffer[5] = (byte) fontWidth;
        buffer[6] = (byte) fontHeight;
        int colorRgb565 = WallpaperUtil.RGB888ToRGB565(colorRgb888);
        buffer[7] = (byte) (colorRgb565 >> 8);
        buffer[8] = (byte) (colorRgb565 & 0xff);
        buffer[9] = (byte) (x >> 8);
        buffer[10] = (byte) (x & 0xff);
        buffer[11] = (byte) (y >> 8);
        buffer[12] = (byte) (y & 0xff);
        return buffer;
    }

    @Override
    public byte[] setSyncRealTimeSportDataRealTimeCallback(boolean realTime) {
        byte[] buffer = new byte[MAX_LENGTH];
        buffer[0] = 0x05;
        buffer[1] = 0x02;
        buffer[2] = 0x15;
        buffer[3] = (byte) (realTime ? 0x01 : 0x00);
        return buffer;
    }

    @Override
    public byte[] syncHistorySportModelData() {
        return getByteWithCount(MAX_LENGTH, 0x07, 0x0B);
    }

    @Override
    public byte[] syncHistoryTemperatureData() {
        return  getByteWithCount(MAX_LENGTH, 0x07, 0x0C);
    }

}
