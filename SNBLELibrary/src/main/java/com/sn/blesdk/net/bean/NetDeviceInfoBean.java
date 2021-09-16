package com.sn.blesdk.net.bean;

import java.util.List;

/**
 * 作者:东芝(2018/1/8).
 * 功能:所有我们支持的设备信息列表
 */

public class NetDeviceInfoBean {

    /**
     * ret : 0
     * message :
     * timestamp : 1515350865
     * data : [{"id":1,"type":"1","version":40,"download_url":"https://api.core.iwear88.com/uploads/mSc2F-0cVBw2ZVg_KJKj27nwQtR_cD4v.zip","update_time":"2018-01-07 11:06:26","describe":"X9","head":5,"customid":227,"device_name":"X9","apptype":1,"function":5,"chip":"nrf52832"},{"id":4,"type":"2","version":40,"download_url":"https://api.core.iwear88.com/uploads/j0rMVGX9fGjbl2Lrx4i4WV657icf9nz-.img","update_time":"2018-01-07 11:05:05","describe":"Sone","head":5,"customid":537,"device_name":"Sone","apptype":1,"function":3,"chip":"da14585"},{"id":5,"type":"4","version":11,"download_url":"https://api.core.iwear88.com/uploads/Q_8kZB3fJujzGc20vVyJ-yHkdqdvzWQx.zip","update_time":"2018-01-07 11:00:47","describe":"祥云安卓","head":0,"customid":0,"device_name":"","apptype":1,"function":0,"chip":"nrf52832"},{"id":6,"type":"3","version":12,"download_url":"https://api.core.iwear88.com/uploads/GGltAVO_nE3nal-i0k4fOn6kGgXiwZUz.zip","update_time":"2018-01-07 11:12:38","describe":"H10Pro","head":5,"customid":2077,"device_name":"H10pro","apptype":1,"function":7,"chip":"nrf52832"},{"id":7,"type":"5","version":11,"download_url":"https://api.core.iwear88.com/uploads/SPacOebIqiu-4RdC21yft5mRsYynE0S_.zip","update_time":"2018-01-07 11:15:50","describe":"祥云","head":0,"customid":0,"device_name":"","apptype":1,"function":0,"chip":"nrf52832"},{"id":8,"type":"6","version":0,"download_url":"","update_time":"2018-01-07 11:00:41","describe":"","head":0,"customid":0,"device_name":"X1","apptype":1,"function":7,"chip":"nrf52832"},{"id":9,"type":"7","version":0,"download_url":"","update_time":"2018-01-07 11:03:57","describe":"","head":5,"customid":2333,"device_name":"X1pro","apptype":1,"function":7,"chip":"nrf52832"},{"id":10,"type":"8","version":0,"download_url":"","update_time":"2018-01-07 11:11:19","describe":"","head":5,"customid":1555,"device_name":"X7pro","apptype":1,"function":7,"chip":"nrf52832"},{"id":11,"type":"9","version":0,"download_url":"","update_time":"2018-01-07 11:05:34","describe":"","head":5,"customid":1813,"device_name":"X9pro","apptype":1,"function":7,"chip":"nrf52832"},{"id":12,"type":"10","version":0,"download_url":"","update_time":"2018-01-07 11:13:02","describe":"","head":5,"customid":2845,"device_name":"X10plus","apptype":1,"function":7,"chip":"nrf52832"},{"id":13,"type":"11","version":0,"download_url":"","update_time":"2018-01-07 11:00:30","describe":"","head":5,"customid":2589,"device_name":"X1plus","apptype":1,"function":7,"chip":"nrf52832"},{"id":14,"type":"12","version":0,"download_url":"","update_time":"2018-01-07 11:00:28","describe":"","head":5,"customid":3089,"device_name":"S5","apptype":1,"function":1,"chip":"nrf52832"},{"id":15,"type":"13","version":0,"download_url":"","update_time":"2018-01-07 11:08:26","describe":"瑞迪构科技有限公司  \r项目共享健身","head":5,"customid":1301,"device_name":"X9","apptype":1,"function":5,"chip":"nrf52832"},{"id":16,"type":"14","version":0,"download_url":"","update_time":"2018-01-07 11:09:17","describe":"微知科技有限公司\r 项目智慧养老 汉中","head":5,"customid":1045,"device_name":"X9","apptype":1,"function":5,"chip":"nrf52832"},{"id":18,"type":"15","version":0,"download_url":"","update_time":"2018-01-07 11:15:24","describe":"3.0协议\r只出货500，不再生产","head":5,"customid":797,"device_name":"X10pro","apptype":1,"function":7,"chip":"nrf52832"}]
     */

    private int ret;
    private String message;
    private int timestamp;
    private List<DeviceInfo> data;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public List<DeviceInfo> getData() {
        return data;
    }

    public void setData(List<DeviceInfo> data) {
        this.data = data;
    }

}
