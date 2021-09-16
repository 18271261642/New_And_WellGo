package com.truescend.gofit.pagers.device.camera;

/**
 * Author:Created by 泽鑫 on 2017/12/15 14:47.
 */

public class IRemoteCameraContract {

    interface IView{
        void updateTakePhoto();

		void exitRemoteCamera();
	}

    interface IPresenter{
        void requestStartTakePhoto();
        void requestExitTakePhoto();
    }
}
