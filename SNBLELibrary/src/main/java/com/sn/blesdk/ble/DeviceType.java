package com.sn.blesdk.ble;

import android.text.TextUtils;

import com.dz.blesdk.ble.BaseBleDataHelper;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.blesdk.BuildConfig;
import com.sn.blesdk.net.DeviceNetReq;
import com.sn.blesdk.net.bean.DeviceInfo;
import com.sn.blesdk.net.bean.NetDeviceInfoBean;
import com.sn.blesdk.storage.DeviceStorage;
import com.sn.net.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:东芝(2017/8/8).
 * 描述:
 */
public class DeviceType {

    private static List<DeviceInfo> list = null;

    /**
     * 异步重加载设备信息
     */
    public static void asyncReLoadDeviceInfo() {
        DeviceNetReq.getApi().queryAllDevices().enqueue(new OnResponseListener<NetDeviceInfoBean>() {
            @Override
            public void onResponse(NetDeviceInfoBean body) throws Throwable {
                List<DeviceInfo> data = body.getData();
                DeviceStorage.setDeviceInfoJson(JsonUtil.toJson(data));
                list = null;//置空 以便下次使用getDeviceInfo时 刷新list内容


            }

            @Override
            public void onFailure(int ret, String msg) {

            }
        });
    }

    private static List<DeviceInfo> getDeviceInfo() {
        if (list != null && !list.isEmpty()) {//只遍历一次 除非请求了asyncReLoadDeviceInfo
            return list;
        }
        String deviceMessagesJson = DeviceStorage.getDeviceInfoJson();
        if (!TextUtils.isEmpty(deviceMessagesJson)) {
            list = JsonUtil.toListBean(deviceMessagesJson, DeviceInfo.class);
        }
        if (list == null) {
            list = new ArrayList<>();
        }
        if (!list.isEmpty()) {
            ArrayList<DeviceInfo> deviceInfos = new ArrayList<>(list);
            list.clear();
            for (int i = 0; i < deviceInfos.size(); i++) {
                DeviceInfo deviceInfo = deviceInfos.get(i);
                int apptype = deviceInfo.getApptype();

                //如果后台配置apptype后, 这里仍然有问题, 不用看代码了, 一定是后台配置错了, 后台可能有多台同名不同广播id的手环

                //    app         3  2  1  0
                //------------------------------
                //SwissFitLite    1
                //Lexe               1
                //WellGO                1
                //GetFitPro                1
                boolean isSwissFitLite = BuildConfig.APP_ID == 4 && ((apptype >> 3) & 0x01) == 1;
                boolean isLexe = BuildConfig.APP_ID == 3 && ((apptype >> 2) & 0x01) == 1;
                boolean isWellGO = BuildConfig.APP_ID == 2 && ((apptype >> 1) & 0x01) == 1;
                boolean isGetFit = BuildConfig.APP_ID == 1 && ((apptype >> 0) & 0x01) == 1;

                //警告请忽略 BuildConfig.APP_ID是动态编译生成的 编译器误认为是固定值所以警告
                if (isWellGO || isGetFit || isLexe || isSwissFitLite) {
                    //把Customid(manufacturer) 解析得到 设备支持的功能
                    setDeviceSupportInfo(deviceInfo, (short) deviceInfo.getAdv_id());
                    list.add(deviceInfo);
                }
            }
        }
        return list;
    }

    private static void setDeviceSupportInfo(DeviceInfo deviceInfo, int manufacturer) {
        deviceInfo.setSupportHeartRate(isSupported(manufacturer, 0));
        deviceInfo.setSupportAirPressure(isSupported(manufacturer, 1));
        deviceInfo.setSupportTemperature(isSupported(manufacturer, 1));//体温
        deviceInfo.setSupportBloodOxygen(isSupported(manufacturer, 2));
        deviceInfo.setSupportBloodPressure(isSupported(manufacturer, 3));
        deviceInfo.setSupportMessagePush(isSupported(manufacturer, 4));
        deviceInfo.setSupportECG(isSupported(manufacturer, 5));
    }


    //1010000011101

    public static DeviceInfo getCurrentDeviceInfo() {
        int manufacturer = getDeviceAdvId();
        String name = getDeviceName();
        DeviceInfo deviceInfo = getDeviceInfo(manufacturer);
        //得不到厂商ID 就取名称
        if (deviceInfo == null) {
            deviceInfo = getDeviceInfo(name);
        }
        if (deviceInfo != null) {
            setDeviceSupportInfo(deviceInfo, manufacturer);

            //旧Sone
            if (isSOne(deviceInfo.getDevice_name()) && !deviceInfo.isSupportHeartRate()) {
                deviceInfo.setSupportHeartRate(true);
                deviceInfo.setSupportBloodOxygen(false);
                deviceInfo.setSupportBloodPressure(true);
            }
            //旧X9
            if (isX9(deviceInfo.getDevice_name()) && !deviceInfo.isSupportHeartRate()) {
                deviceInfo.setSupportHeartRate(true);
                deviceInfo.setSupportBloodOxygen(true);
                deviceInfo.setSupportBloodPressure(false);
            }

        }
        return deviceInfo;
    }

    public static String getDeviceName() {
        return DeviceStorage.getDeviceName();
    }

    public static String getDeviceMac() {
        return DeviceStorage.getDeviceMac();
    }

    /**
     * 广播ID
     *
     * @return
     */
    public static int getDeviceAdvId() {

        return DeviceStorage.getDeviceAdvId();
    }

    /**
     * 客户ID
     */
    public static int getDeviceCustomerId() {
        return DeviceStorage.getDeviceCustomerId();
    }

    /**
     * 是我们的设备?
     *
     * @param deviceName 设备名
     * @return
     */
    public static boolean isOurDevice(String deviceName) {
        if (!TextUtils.isEmpty(deviceName)) {
            List<DeviceInfo> list = getDeviceInfo();
            for (DeviceInfo deviceMessage : list) {
                if (deviceMessage.getDevice_name().equalsIgnoreCase(deviceName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是我们的设备?
     *
     * @param manufacturer 厂商(客户)ID
     * @return
     */
    public static boolean isOurDevice(short manufacturer) {
        return getDeviceInfo(manufacturer) != null;
    }

    /**
     * 根据客户id,取得设备信息
     *
     * @param manufacturers 厂商(客户)ID 多个
     * @return
     */
    public static DeviceInfo getDeviceInfo(List<Integer> manufacturers) {
        for (Integer manufacturer : manufacturers) {
            DeviceInfo deviceInfo = getDeviceInfo(manufacturer);
            if (deviceInfo != null) {
                return deviceInfo;
            }
        }
        return null;
    }


    /**
     * 根据客户id,取得设备信息
     *
     * @param manufacturer 厂商(客户)ID
     * @return
     */
    public static DeviceInfo getDeviceInfo(int manufacturer) {
        if (manufacturer == 0x5600) {//旧x9
            manufacturer = 0x0115;
        }
        if (manufacturer != 0) {
            List<DeviceInfo> list = getDeviceInfo();
            for (DeviceInfo deviceInfo : list) {
                if (deviceInfo != null && deviceInfo.getAdv_id() == manufacturer) {
                    return deviceInfo;
                }
            }
        }
        return null;
    }

    /**
     * 根据设备名,取得设备信息
     * 该方法使用请慎重! 不是很准确,通常先判断 厂商(客户)ID 得不到时 再通过该方法做兼容式 获取
     *
     * @param deviceName 设备名
     * @return
     */
    public static DeviceInfo getDeviceInfo(String deviceName) {
        if (!TextUtils.isEmpty(deviceName)) {
            //只兼容x9,s1
            if (isSOne(deviceName) || isX9(deviceName)) {
                List<DeviceInfo> list = getDeviceInfo();
                for (DeviceInfo deviceInfo : list) {
                    if (deviceInfo.getDevice_name().equalsIgnoreCase(deviceName)) {
                        return deviceInfo;
                    }
                }
            }
        }
        return null;
    }


    private static boolean isSupported(int function, int bit) {
        return ((function >> bit) & 1) == 1;
    }


    private static boolean isSOne(String name) {
        if (name == null) {
            return false;
        }
        return name.toLowerCase().contains("ione") ||
                name.toLowerCase().contains("d one") ||
                name.toLowerCase().contains("i_one") ||
                name.toLowerCase().contains("sone") ||
                name.toLowerCase().contains("s one") ||
                name.toLowerCase().contains("s_one");
    }

    private static boolean isX10Pro(String name) {
        if (name == null) {
            return false;
        }
        return name.toLowerCase().contains("x10pro") ||
                name.toLowerCase().contains("x10 pro") ||
                name.toLowerCase().contains("h10pro") ||
                name.toLowerCase().contains("h10 pro")
                ;
    }

    private static boolean isX9(String name) {
        if (name == null) {
            return false;
        }
        return name.toLowerCase().contains("x9");
    }

    private static boolean isX1Pro(String name) {
        if (name == null) {
            return false;
        }
        return name.toLowerCase().contains("x1pro") ||
                name.toLowerCase().contains("x1 pro");
    }


    public static boolean isDFUModel(String name) {
        if (name == null || name.length() == 0) {
            return false;
        }
        return name.toLowerCase().contains("dfu") || name.toLowerCase().contains("ota");
    }



    public static String getOTAAdvMac(byte[] adv_data) {
        try {
            int index = 0;
            int len1 = adv_data[index] & 0xff;
            index += len1 + 1;
            int len2 = adv_data[index] & 0xff;
            index += len2 + 1;
            int len3 = adv_data[index] & 0xff;
            index++;
            int type = adv_data[index] & 0xff;
            int startIndex = index;
            StringBuilder sb = new StringBuilder();

            if (type == 0xFF && len3 == 9 &&
                    (adv_data[startIndex + 1] & 0xFF) == 0xFF &&
                    (adv_data[startIndex + 2] & 0xFF) == 0xFF
            ) {
                int l = startIndex + len3;
                for (int i = startIndex + 3; i < l; i++) {
                    sb.append(String.format("%02X", adv_data[i]));
                    if (i < l - 1) {
                        sb.append(":");
                    }
                }
            }
            if (sb.length() == 17) {
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果前面 的正规方法无法获取到, 则利用下面的不正规的办法获取
        String hex = BaseBleDataHelper.toHexString(adv_data);
        String str = "09FFFFFF";
        int beginIndex = hex.indexOf(str);
        String mac_src = hex.substring(beginIndex + str.length(), beginIndex + str.length() + (9 * 2) - 6);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac_src.length(); i += 2) {
            sb.append(mac_src.charAt(i));
            sb.append(mac_src.charAt(i + 1));
            if (i < mac_src.length() - 2) {
                sb.append(":");
            }
        }
        if (sb.length() == 17) {
            return sb.toString();
        }
        return null;
    }

    private static boolean isI6(String name) {
        if (name == null) {
            return false;
        }
        return name.toLowerCase().contains("i6") || name.toLowerCase().contains("bcdhp");
    }


}
