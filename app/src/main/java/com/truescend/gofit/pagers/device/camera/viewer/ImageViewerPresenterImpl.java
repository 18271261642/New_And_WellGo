package com.truescend.gofit.pagers.device.camera.viewer;

import com.truescend.gofit.pagers.base.BasePresenter;

/**
 * Author:Created by 泽鑫 on 2017/12/15 14:47.
 */

public class ImageViewerPresenterImpl extends BasePresenter<IImageViewerContract.IView> implements IImageViewerContract.IPresenter {
    private IImageViewerContract.IView view;

    public ImageViewerPresenterImpl(IImageViewerContract.IView view){
        this.view = view;
    }

}
