package com.truescend.gofit.pagers.home.sport_mode.bean;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.truescend.gofit.R;


public class ItemSportModeIconUtil {

    public static ImageView getIconImageView(View view) {
        return (ImageView) view.findViewById(R.id.ivIcon);
    }

    public static TextView getContentTextView(View view) {
        return (TextView) view.findViewById(R.id.tvContent);
    }

    public static TextView getDescriptionTextView(View view) {
        return (TextView) view.findViewById(R.id.tvDescription);
    }
}
