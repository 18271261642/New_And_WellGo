package com.truescend.gofit.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.sn.utils.LanguageUtil;
import com.sn.utils.view.DIYViewUtil;
import com.truescend.gofit.R;

import java.util.Locale;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

/**
 * 东芝 标题布局
 * 支持任意定制的标题布局
 */

public class TitleLayout extends AppBarLayout {
    private TextView tvTitleText;
    private LinearLayout llTitleLeft;
    private LinearLayout llTitleRight;
    private LinearLayout llTitleCenter;
    private LayoutInflater inflater;
    private int dimensionPixelSize;
    private boolean isRTL;

    public TitleLayout(Context context) {
        super(context);/////////////////////////////////////////////////////////////////
        init();
    }

    public TitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_title, this);
        tvTitleText = (TextView) findViewById(R.id.tvTitleText);
        llTitleLeft = (LinearLayout) findViewById(R.id.llTitleLeft);
        llTitleRight = (LinearLayout) findViewById(R.id.llTitleRight);
        llTitleCenter = (LinearLayout) findViewById(R.id.llTitleCenter);

        setTitleShow(true);

        inflater = LayoutInflater.from(getContext());
        dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.title_height);

        isRTL = LanguageUtil.isRTL();
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == INVISIBLE)//不能使用INVISIBLE属性
        {
            visibility = GONE;
        }
        super.setVisibility(visibility);
    }

    public boolean isTitleShow() {
        return getVisibility() == VISIBLE;
    }


    /**
     * 设置标题显示(默认显示)
     *
     * @param enable
     */
    public void setTitleShow(boolean enable) {
        setVisibility(enable ? VISIBLE : GONE);
    }

    /**
     * 标题
     */
    public void setTitle(String title) {
        tvTitleText.setText(title);
    }

    public void setTitle(int titleResId) {
        tvTitleText.setText(titleResId);
    }

    public void setTitleOnClickListener(View.OnClickListener listener) {
        tvTitleText.setOnClickListener(listener);
    }


    public String getTitle() {
        return tvTitleText.getText().toString();
    }


    public void setLeftIconFinishActivity(final Activity activity) {
        addLeftItem(ItemBuilder.Builder().setIcon(R.mipmap.icon_arrow_left).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        }));
    }




    public void addLeftItem(ItemBuilder builder) {
        addLeftItem(builder, 0);
    }

    public void addRightItem(ItemBuilder builder) {
        addRightItem(builder, llTitleRight.getChildCount());
    }

    public void addRightItem(ItemBuilder builder, int index) {

                View item = createItem(builder);
                llTitleRight.addView(item, index);

    }
    public void addLeftItem(ItemBuilder builder, int index) {

            View item = createItem(builder);
            llTitleLeft.addView(item, index);

    }
    public void addLeftItem(View item) {
        llTitleLeft.addView(item, 0);
    }

    public void addRightItem(View item) {
        llTitleRight.addView(item, llTitleRight.getChildCount());
    }

    public void addCenterItem(View item) {
        if (tvTitleText.getVisibility() == VISIBLE) {
            tvTitleText.setVisibility(GONE);
        }
        if (llTitleCenter.getVisibility() != VISIBLE) {
            llTitleCenter.setVisibility(VISIBLE);
        }
        llTitleCenter.addView(item);
    }


    public void setTextWithTag(String itemTag, CharSequence text) {
        View viewWithTag = findViewWithTag(itemTag);
        if (viewWithTag instanceof TextView) {
            ((TextView) viewWithTag).setText(text);
        }
    }

    private View createItem(final ItemBuilder builder) {
        View itemView;

        if (builder.customView != null && builder.layoutType == ItemBuilder.LAYOUT_TYPE_CUSTOMVIEW) {
            itemView = builder.customView;
            //给自定义布局设定 宽高
            itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dimensionPixelSize));
        } else if (builder.layoutType == ItemBuilder.LAYOUT_TYPE_TEXT_9_PATCH) {
            itemView = inflater.inflate(R.layout.item_title_text_9_patch_button, null, false);
        } else {
            itemView = inflater.inflate(R.layout.item_title_text_icon_button, null, false);
        }

        ////////////////////////////////////////////////////////////////////////////////////////
        //--------------------------------------图片按钮-----------------------------------
        ////////////////////////////////////////////////////////////////////////////////////////
        ImageView ivTitleIconButton = null;
        if (builder.icon != null) {
            ivTitleIconButton = itemView.findViewById(R.id.ivTitleIconButton);
            if (ivTitleIconButton != null) {
                ivTitleIconButton.setImageDrawable(builder.icon);
                ivTitleIconButton.setTag(builder.ivtag);
            }
        }
        if (builder.iconResId != 0) {
            ivTitleIconButton = itemView.findViewById(R.id.ivTitleIconButton);
            if (ivTitleIconButton != null) {
                ivTitleIconButton.setImageResource(builder.iconResId);
                ivTitleIconButton.setTag(builder.ivtag);
            }
        }
        if (ivTitleIconButton != null && builder.size != 0) {
            ViewGroup.LayoutParams layoutParams = ivTitleIconButton.getLayoutParams();
            float screenDensity = DIYViewUtil.getScreenDensity(ivTitleIconButton.getResources());
            layoutParams.width = Math.round(builder.size * screenDensity);
            layoutParams.height = Math.round(builder.size * screenDensity);
            ivTitleIconButton.setLayoutParams(layoutParams);
        }
        ////////////////////////////////////////////////////////////////////////////////////////
        //--------------------------------------文本-----------------------------------
        ////////////////////////////////////////////////////////////////////////////////////////
        TextView tvTitleTextButton = null;
        if (builder.text != null) {
            tvTitleTextButton = itemView.findViewById(R.id.tvTitleTextButton);
            if (tvTitleTextButton != null) {
                tvTitleTextButton.setText(builder.text);
                tvTitleTextButton.setTag(builder.tvtag);
            }
        }

        if (builder.strResId != 0) {
            tvTitleTextButton = itemView.findViewById(R.id.tvTitleTextButton);
            if (tvTitleTextButton != null) {
                tvTitleTextButton.setText(builder.strResId);
                tvTitleTextButton.setTag(builder.tvtag);
            }
        }
        //处理一下
        if (tvTitleTextButton != null) {
            tvTitleTextButton.setTextColor(builder.textColor);
            tvTitleTextButton.setTextSize(builder.textSize);
            TextPaint textPaint = tvTitleTextButton.getPaint();
            textPaint.setFakeBoldText(builder.isBold);
            // 点9图处理
            if (builder.layoutType == ItemBuilder.LAYOUT_TYPE_TEXT_9_PATCH) {
                if (builder.icon != null) {
                    tvTitleTextButton.setBackground(builder.icon);
                }
                if (builder.iconResId != 0) {
                    tvTitleTextButton.setBackgroundResource(builder.iconResId);
                }
                if (builder.size != 0) {
                    ViewGroup.LayoutParams layoutParams = tvTitleTextButton.getLayoutParams();
                    float screenDensity = DIYViewUtil.getScreenDensity(tvTitleTextButton.getResources());
                    layoutParams.width = Math.round(builder.size * screenDensity);
                    layoutParams.height = Math.round(builder.size * screenDensity);
                    tvTitleTextButton.setLayoutParams(layoutParams);
                }
            }
            final TextView finalTvTitleTextButton = tvTitleTextButton;
            final int maxwidth = getResources().getDimensionPixelOffset(R.dimen.title_height) * 2;
            tvTitleTextButton.setMaxWidth(maxwidth);
            tvTitleTextButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    finalTvTitleTextButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int width = finalTvTitleTextButton.getWidth();
                    if(maxwidth>=width) {
                        finalTvTitleTextButton.setSingleLine(false);
                        finalTvTitleTextButton.setMaxLines(2);
                        finalTvTitleTextButton.getPaint().setTextSize(finalTvTitleTextButton.getPaint().getTextSize() * 0.7f);
                    }
                }
            });

        }
        ////////////////////////////////////////////////////////////////////////////////////////
        //--------------------------------------事件-----------------------------------
        ////////////////////////////////////////////////////////////////////////////////////////
        if (builder.onClickListener != null) {
            itemView.setOnClickListener(builder.onClickListener);
        }
        return itemView;
    }

    private void refitText(TextView tv, String text, int textWidth) {
        if (textWidth > 0) {
            Paint testPaint = new Paint();
            testPaint.set(tv.getPaint());
            // 获得当前TextView的有效宽度
            int availableWidth = textWidth - this.getPaddingStart()
                    - this.getPaddingEnd();
            float[] widths = new float[text.length()];
            Rect rect = new Rect();
            testPaint.getTextBounds(text, 0, text.length(), rect);
            // 所有字符串所占像素宽度
            int textWidths = rect.width();
            float cTextSize = tv.getTextSize();// 这个返回的单位为px
            while (textWidths > availableWidth) {
                cTextSize = cTextSize - 1;
                testPaint.setTextSize(cTextSize);// 这里传入的单位是px
                textWidths = testPaint.getTextWidths(text, widths);
            }
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, cTextSize);// 这里制定传入的单位是px
        }
    }

    ;

    public static class ItemBuilder {
        private Drawable icon;
        private int iconResId;
        private CharSequence text;
        private int strResId;
        private int textColor = 0xFF000000;
        private int textSize = 15;
        private boolean isBold = false;
        private View customView;
        private View.OnClickListener onClickListener;
        private static final int LAYOUT_TYPE_TEXT_ICON = 0;
        private static final int LAYOUT_TYPE_TEXT_9_PATCH = 1;
        private static final int LAYOUT_TYPE_CUSTOMVIEW = 2;
        private int layoutType = LAYOUT_TYPE_TEXT_ICON;
        private int size;
        private String tvtag;
        private String ivtag;

        private ItemBuilder() {
        }

        public static ItemBuilder Builder() {
            return new ItemBuilder();
        }

        public ItemBuilder setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
            return this;
        }

        /**
         * View尺寸 比率
         * 这个值是乱传的 你可以多次传入测试  比较接近UI效果时 则写死即可
         * 当切换不同的设备分辨率,我会自动适配屏幕的,放心传,不适配算我输
         *
         * @param ratio
         */
        public ItemBuilder setIconSizeRatio(int ratio) {
            this.size = ratio;
            return this;
        }

        public ItemBuilder setText(CharSequence text) {
            this.text = text;
            return this;
        }

        public ItemBuilder setText(@StringRes int strResId) {
            this.strResId = strResId;
            return this;
        }

        /**
         * 设置View的tag 您可以在后面 通过标题布局 View.findViewWithTag(tag) 找到这个View的对象
         *
         * @param tag
         * @return
         */
        public ItemBuilder setTextViewTag(String tag) {
            this.tvtag = tag;
            return this;
        }

        /**
         * 传入自定义的布局
         * 如果你想免去外部setIcon setText等烦恼,你需要在自定义的布局里定义以下两个ID 则我会自动识别并辅值(类似你定义ArrayAdapter时,需要在自定义布局指定android_id:text1 一样道理!)
         * ivTitleIconButton
         * tvTitleTextButton
         * 当然 如果你是完全自定义 你可以不按这个规则来 但setIcon setText等全部无效 只有setOnClickListener才有效果 这个要注意!
         *
         * @param customView
         */
        public ItemBuilder setCustomView(View customView) {
            this.customView = customView;
            layoutType = LAYOUT_TYPE_CUSTOMVIEW;
            return this;
        }

        /**
         * 9妹图模式
         *
         * @param enable
         * @return
         */
        public ItemBuilder set9PatchModelEnable(boolean enable) {
            if (enable) {
                layoutType = LAYOUT_TYPE_TEXT_9_PATCH;
            } else if (layoutType == LAYOUT_TYPE_TEXT_9_PATCH) {
                layoutType = LAYOUT_TYPE_TEXT_ICON;
            }
            return this;
        }

        public ItemBuilder setIcon(Drawable icon) {
            this.icon = icon;
            return this;
        }

        /**
         * 设置View的tag 您可以在后面 通过标题布局 View.findViewWithTag(tag) 找到这个View的对象
         *
         * @param tag
         * @return
         */
        public ItemBuilder setIconViewTag(String tag) {
            this.ivtag = tag;
            return this;
        }

        public ItemBuilder setIcon(@DrawableRes int resId) {
            this.iconResId = resId;
            return this;
        }

        public ItemBuilder setTextColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public ItemBuilder setTextSize(int textSize) {
            this.textSize = textSize;
            return this;
        }

        public ItemBuilder setTextStyle(boolean isBold) {
            this.isBold = isBold;
            return this;
        }

    }


}
