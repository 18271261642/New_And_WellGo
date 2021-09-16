package com.truescend.gofit.pagers.track;

import com.truescend.gofit.pagers.track.bean.PathMapItem;

import java.util.List;

/**
 * Author:Created by 泽鑫 on 2017/12/12 11:06.
 */

public class ITrackContract {

    interface IView
    {
        void onUpdateTrackItemData(List<PathMapItem> list);
    }

    interface IPresenter
    {
        void requestLoadTrackItemData();
    }
}
