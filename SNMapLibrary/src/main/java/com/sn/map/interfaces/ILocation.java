package com.sn.map.interfaces;

import com.sn.map.bean.SNLocation;

/**
 * 作者:东芝(2017/12/27).
 * 功能:定位基类
 */

public interface ILocation {
     void start();
     void stop();
     SNLocation getLastLocation();

     void setLocationListener(com.sn.map.interfaces.ILocation.LocationListener listener);


     interface LocationListener{
          void onLocationChanged(SNLocation location);
          void onSignalChanged(int level);
     }
}
