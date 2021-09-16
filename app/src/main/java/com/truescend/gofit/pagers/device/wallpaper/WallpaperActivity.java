package com.truescend.gofit.pagers.device.wallpaper;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.utils.SNLog;
import com.sn.utils.SNToast;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.common.dialog.CommonDialog;
import com.truescend.gofit.pagers.common.dialog.LoadingDialog;
import com.truescend.gofit.pagers.common.dialog.ProgressDialog;
import com.truescend.gofit.pagers.device.bean.ItemWallpaperSetting;
import com.truescend.gofit.utils.PermissionUtils;
import com.truescend.gofit.utils.WallpaperPickerUtil;
import com.truescend.gofit.views.ColorPickerView;
import com.truescend.gofit.views.TitleLayout;
import com.truescend.gofit.views.WallpaperPreviewView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 作者:东芝(2019/4/2).
 * 功能:手环壁纸功能
 */

public class WallpaperActivity extends BaseActivity<WallpaperPresenterImpl, IWallpaperContract.IView> implements IWallpaperContract.IView {

    @BindView(R.id.tvWallpaperTextColor)
    TextView tvWallpaperTextColor;
    //    @BindView(R.id.tvWallpaperTextSize)
//    TextView tvWallpaperTextSize;
//    @BindView(R.id.sbWallpaperTextSize)
//    AppCompatSeekBar sbWallpaperTextSize;
    @BindView(R.id.tvWallpaperTakePhoto)
    TextView tvWallpaperTakePhoto;
    @BindView(R.id.tvWallpaperChoosePhoto)
    TextView tvWallpaperChoosePhoto;
    @BindView(R.id.itemScreenSwitch)
    View itemScreenSwitch;
    @BindView(R.id.itemStepSwitch)
    View itemStepSwitch;
    @BindView(R.id.cpvColorPicker)
    ColorPickerView cpvColorPicker;
    @BindView(R.id.llWallpaperLayout)
    View llWallpaperLayout;
    @BindView(R.id.wpv)
    WallpaperPreviewView wpv;

    private ItemWallpaperSetting itemWallpaperSetting;
    private ItemWallpaperSetting itemStepSetting;
    private ProgressDialog progressDialog;
    private List<Point> tempFontList = new ArrayList<>();
    private boolean isConfigModified;
    private boolean isWallpaperModified;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, WallpaperActivity.class));
    }

    @Override
    protected WallpaperPresenterImpl initPresenter() {
        return new WallpaperPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_wallpaper;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {

        initViews();

        if (!SNBLEHelper.isSupportWallpaper()) {
            finish();
            return;
        }

        getPresenter().loadWallpaperInfo();
    }

    private void initViews() {
        itemWallpaperSetting = new ItemWallpaperSetting(itemScreenSwitch);
        itemStepSetting = new ItemWallpaperSetting(itemStepSwitch);
        itemWallpaperSetting.setTitle(R.string.enable_wallpaper_fun);
        itemWallpaperSetting.setIcon(R.mipmap.icon_wallpaper_fun_enable);
        itemStepSetting.setTitle(R.string.enable_wallpaper_step_fun);
        itemStepSetting.setIcon(R.mipmap.icon_wallpaper_step);
        llWallpaperLayout.setVisibility(View.INVISIBLE);

    }

    private void setListener() {
        itemWallpaperSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                llWallpaperLayout.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
                isConfigModified = true;
            }
        });

        itemStepSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                wpv.setStepEnable(isChecked);
                isConfigModified = true;
            }
        });
        cpvColorPicker.setOnColorItemPickListener(new ColorPickerView.OnColorItemPickListener() {
            @Override
            public void onItemSelected(int position, int color) {
                //设置颜色
                wpv.setTimeColor(color);
                wpv.setStepColor(color);
                isConfigModified = true;
            }
        });
        wpv.setOnPreviewViewLocationChangedListener(new WallpaperPreviewView.OnPreviewViewLocationChangedListener() {
            @Override
            public void onStartDrag() {
                isConfigModified = true;
            }

            @Override
            public void onStopDrag() {
                isConfigModified = true;
            }

            @Override
            public void onChanged(PointF time, PointF step) {
                isConfigModified = true;
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        WallpaperPickerUtil.onActivityResult(this, requestCode, resultCode, data, onWallpaperPickerListener);
    }


    private WallpaperPickerUtil.OnWallpaperPickerListener onWallpaperPickerListener = new WallpaperPickerUtil.OnWallpaperPickerListener() {
        @Override
        public void onResult(String mImagePath) {
            Bitmap bitmap = BitmapFactory.decodeFile(mImagePath);
            //设置壁纸和分辨率
            wpv.setWallpaperImage(bitmap);
            isConfigModified = true;
            isWallpaperModified = true;
        }

        @Override
        public void onFailed() {

        }
    };

    @Override
    protected void onCreateTitle(TitleLayout titleLayout) {
        super.onCreateTitle(titleLayout);
        titleLayout.setTitle(R.string.title_change_wallpaper);
        titleLayout.addRightItem(
                TitleLayout.ItemBuilder.Builder()
                        .setText(R.string.content_save)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                saveWallpaperData();
                            }
                        }));
    }


    @Override
    public void onBackPressed() {
        if (isConfigModified) {
            exitAlertDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void exitAlertDialog() {
        CommonDialog.create(this,
                getString(R.string.content_not_saved),
                getString(R.string.content_not_saved_tips),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        WallpaperActivity.super.onBackPressed();
                    }
                }
        ).show();
    }

    private void saveWallpaperData() {
        if (!itemWallpaperSetting.isChecked() && !isConfigModified) {
            onUpdateWallpaperSuccessful();
        } else if (tempFontList.size() >= 2) {
            Point timeFont = tempFontList.get(0);
            Point stepFont = tempFontList.get(1);
            getPresenter().updateWallpaperData(
                    wpv.getWallpaperImageSrc(),
                    wpv.getWallpaperImage(),
                    itemWallpaperSetting.isChecked(),
                    isWallpaperModified,
                    true,
                    itemStepSetting.isChecked(), wpv.getTimeLocation(),
                    wpv.getStepLocation(),
                    timeFont,
                    stepFont, cpvColorPicker.getCurrentColor());

        }
    }

    @OnClick({R.id.tvWallpaperTakePhoto, R.id.tvWallpaperChoosePhoto})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvWallpaperTakePhoto:

                PermissionUtils.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, new PermissionUtils.OnPermissionGrantedListener() {
                    @Override
                    public void onGranted() {
                        WallpaperPickerUtil.fromCamera(WallpaperActivity.this);
                    }

                    @Override
                    public void onDenied() {

                    }
                });
                break;
            case R.id.tvWallpaperChoosePhoto:
                WallpaperPickerUtil.fromPhotoFile(this);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    @Override
    public void onDialogLoading(String msg) {
        LoadingDialog.show(this, msg).setCancelable(true);
    }

    @Override
    public void onDialogDismiss() {
        LoadingDialog.dismiss();
    }

    @Override
    public void onUpdateWallpaperProgressChanged(int cur, int total) {
        if (progressDialog == null) {
            try {
                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle(R.string.content_wallpaper_transmission);
                progressDialog.show();
            } catch (Exception ignored) {
            }
        }
        if(progressDialog!=null) {
            progressDialog.setProgress(cur);
            progressDialog.setMax(total);
        }


    }

    @Override
    public void onUpdateWallpaperSuccessful() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        isWallpaperModified = false;
        isConfigModified = false;
        SNToast.toast(R.string.content_setup_complete, Toast.LENGTH_LONG);
    }

    @Override
    public void onLoadWallpaperInfo(boolean isSupport, boolean isEnable, int screenWidth, int screenHeight, int screenType, boolean isTimeEnable, boolean isStepEnable, List<Point> fontList, Bitmap wallpaperSrc, boolean isDefaultWallpaper, PointF firstLocation, int fontColor) {
        tempFontList.clear();
        tempFontList.addAll(fontList);
        SNLog.i("屏幕尺寸:" + screenWidth + "," + screenHeight);
        SNLog.i("支持屏保:" + isSupport);
        SNLog.i("屏保开关:" + isEnable);
        SNLog.i("时间开关:" + isTimeEnable);
        SNLog.i("记步开关:" + isStepEnable);
        SNLog.i("字体:" + fontList);

        if (isDefaultWallpaper) {
            isWallpaperModified = true;
        }

        itemWallpaperSetting.setChecked(isEnable);
        llWallpaperLayout.setVisibility(isEnable ? View.VISIBLE : View.INVISIBLE);
        itemStepSetting.setChecked(isStepEnable);
        wpv.setStepEnable(isStepEnable);

        wpv.setWallpaperImage(wallpaperSrc, screenWidth, screenHeight);
        if (fontList.size() >= 2) {
            Point timeFont = fontList.get(0);
            Point stepFont = fontList.get(1);
            Point footFont = new Point(stepFont.y + 4, stepFont.y + 4);
            //设置时间和分辨率
            wpv.setTimeImage(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_wallpaper_widget_time), timeFont.x * 5/*时间为5个字符的宽度*/, timeFont.y);
            //设置步数和分辨率
            wpv.setStepImage(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_wallpaper_widget_step), stepFont.x * 5/*步数为5个字符的宽度*/ + footFont.x/*总长*/, footFont.y/*以最大为准*/);
        }
        //时间位置初始值
        wpv.setTimeColor(fontColor);
        wpv.setStepColor(fontColor);
        cpvColorPicker.setPositionFromColor(fontColor);
        wpv.setTimeLocation(firstLocation.x, firstLocation.y);

        wpv.setScreenType(screenType);
        setListener();
    }

    @Override
    public void onLoadWallpaperFailed() {
        finish();
    }

    @Override
    public void onUpdateBleDisconnect() {
        CommonDialog.create(this,
                null,
                getString(R.string.toast_band_is_disconnect),
                null,
                getString(R.string.content_confirm),
                null,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        WallpaperActivity.super.onBackPressed();
                    }
                }
        ).show();
    }


    @Override
    public void onUpdateWallpaperFailed(String msg) {
        SNToast.toast(msg);
    }

}
