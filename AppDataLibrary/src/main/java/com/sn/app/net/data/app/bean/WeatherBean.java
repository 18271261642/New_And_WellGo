package com.sn.app.net.data.app.bean;

import com.sn.utils.IF;

/**
 * Author:Created by Administrator on 2017/12/28 14:38.
 */

public class WeatherBean {

    /**
     * ret : 0
     * message :
     * timestamp : 1514415556
     * data : {"id":13,"lat":"22.629657","lon":"113.866707","update_time":"2017-12-28 14:48:16","city":"深圳市","cond_code_d":"101","cond_code_n":"101","cond_txt_d":"多云","cond_txt_n":"多云","hum":"56","pcpn":"0.0","pop":"92","pres":"1020","tmp_max":"21","tmp_min":"17","uv_index":"2","vis":"7","wind_deg":"59","wind_dir":"东北风","wind_sc":"微风","wind_spd":"8","date":"2017-12-28","cond_txt":"多云","tmp":"20"}
     */

    private int ret;
    private String message;
    private int timestamp;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 13
         * lat : 22.629657
         * lon : 113.866707
         * update_time : 2017-12-28 14:48:16
         * city : 深圳市
         * cond_code_d : 101
         * cond_code_n : 101
         * cond_txt_d : 多云
         * cond_txt_n : 多云
         * hum : 56
         * pcpn : 0.0
         * pop : 92
         * pres : 1020
         * tmp_max : 21
         * tmp_min : 17
         * uv_index : 2
         * vis : 7
         * wind_deg : 59
         * wind_dir : 东北风
         * wind_sc : 微风
         * wind_spd : 8
         * date : 2017-12-28
         * cond_txt : 多云
         * tmp : 20
         */

        private int id;
        private String lat;
        private String lon;
        private String update_time;
        private String city;
        private String cond_code;
        private String cond_code_d;
        private String cond_code_n;
        private String cond_txt_d;
        private String cond_txt_n;
        private String hum;
        private String pcpn;
        private String pop;
        private String pres;
        private int tmp_max;
        private int tmp_min;
        private int tmp;
        private String uv_index;
        private String vis;
        private String wind_deg;
        private String wind_dir;
        private String wind_sc;
        private String wind_spd;
        private String date;
        private String cond_txt;

        public String getCond_code() {
            return cond_code;
        }

        public void setCond_code(String cond_code) {
            this.cond_code = cond_code;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCond_code_d() {
            return cond_code_d;
        }



        public int getCond_code_type() {
            int type = 0xFF;
            String cond_code = getCond_code();
            if (IF.isEmpty(cond_code)) {
                cond_code= getCond_code_d();
            }
            if (IF.isEmpty(cond_code)) {
                cond_code= getCond_code_n();
            }
            switch (Integer.parseInt(cond_code)) {
                case 100://晴
                    type = 0x01;
                    break;
                case 101://多云
                case 102:
                case 103:
                case 201:
                case 202:
                case 203:
                case 204:
                    type = 0x02;
                    break;
                case 400://雪
                case 401:
                case 402:
                case 403:
                case 404:
                case 405:
                case 406:
                case 407:
                    type = 0x14;
                    break;
                case 104://阴天
                case 200:
                case 205:
                case 206:
                case 207:
                case 208:
                case 210:
                case 500:
                case 501:
                case 502:
                case 503:
                case 504:
                case 507:
                case 508:
                    type = 0x03;
                    break;
                case 209://雨
                case 211:
                case 212:
                case 213:
                case 300:
                case 301:
                case 302:
                case 304:
                case 305:
                case 306:
                case 307:
                case 308:
                case 309:
                case 310:
                case 311:
                case 312:
                case 313:
                    type = 0x07;
                    break;
                case 900://无
                case 901:
                case 999:
                    type = 0xFF;
                    break;
            }
            return type;
        }

        public void setCond_code_d(String cond_code_d) {
            this.cond_code_d = cond_code_d;
        }

        public String getCond_code_n() {
            return cond_code_n;
        }

        public void setCond_code_n(String cond_code_n) {
            this.cond_code_n = cond_code_n;
        }

        public String getCond_txt_d() {
            return cond_txt_d;
        }

        public void setCond_txt_d(String cond_txt_d) {
            this.cond_txt_d = cond_txt_d;
        }

        public String getCond_txt_n() {
            return cond_txt_n;
        }

        public void setCond_txt_n(String cond_txt_n) {
            this.cond_txt_n = cond_txt_n;
        }

        public String getHum() {
            return hum;
        }

        public void setHum(String hum) {
            this.hum = hum;
        }

        public String getPcpn() {
            return pcpn;
        }

        public void setPcpn(String pcpn) {
            this.pcpn = pcpn;
        }

        public String getPop() {
            return pop;
        }

        public void setPop(String pop) {
            this.pop = pop;
        }

        public String getPres() {
            return pres;
        }

        public void setPres(String pres) {
            this.pres = pres;
        }

        public int getTmp_max() {
            return tmp_max;
        }

        public void setTmp_max(int tmp_max) {
            this.tmp_max = tmp_max;
        }

        public int getTmp_min() {
            return tmp_min;
        }

        public void setTmp_min(int tmp_min) {
            this.tmp_min = tmp_min;
        }

        public String getUv_index() {
            return uv_index;
        }

        public void setUv_index(String uv_index) {
            this.uv_index = uv_index;
        }

        public String getVis() {
            return vis;
        }

        public void setVis(String vis) {
            this.vis = vis;
        }

        public String getWind_deg() {
            return wind_deg;
        }

        public void setWind_deg(String wind_deg) {
            this.wind_deg = wind_deg;
        }

        public String getWind_dir() {
            return wind_dir;
        }

        public void setWind_dir(String wind_dir) {
            this.wind_dir = wind_dir;
        }

        public String getWind_sc() {
            return wind_sc;
        }

        public void setWind_sc(String wind_sc) {
            this.wind_sc = wind_sc;
        }

        public String getWind_spd() {
            return wind_spd;
        }

        public void setWind_spd(String wind_spd) {
            this.wind_spd = wind_spd;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getCond_txt() {
            return cond_txt;
        }

        public void setCond_txt(String cond_txt) {
            this.cond_txt = cond_txt;
        }

        public int getTmp() {
            return tmp;
        }

        public void setTmp(int tmp) {
            this.tmp = tmp;
        }
    }
}
