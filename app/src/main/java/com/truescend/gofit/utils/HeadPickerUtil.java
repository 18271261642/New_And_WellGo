package com.truescend.gofit.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 作者:东芝(2018/2/23).
 * 功能:头像选择器
 */

public class HeadPickerUtil {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_GALLERY = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    private static File file;
    private static Uri outputUri;

    public static void fromCamera(Activity activity) {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(Environment.getExternalStorageDirectory(), "image.jpg");

        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换

        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, CompatUtil.getUriForFile(activity, file));
        try {
            activity.startActivityForResult(openCameraIntent, TYPE_CAMERA);
        } catch (ActivityNotFoundException ignored) {
            //TODO 相机可能被卸载
        }
    }

    public static void fromPhotoFile(Activity activity) {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        }
        try {
            activity.startActivityForResult(intent, TYPE_GALLERY);
        } catch (ActivityNotFoundException ignored) {
            //TODO 系统可能被精简,没有图库 导致无法裁剪图片, 后期可以自己写个裁剪工具界面 靠人不如靠己
        }
    }


    public static void onActivityResult(final Activity activity, int requestCode, int resultCode, Intent data, final OnHeadPickerListener listener) {
        if (listener == null) throw new NullPointerException("你必须回调OnHeadPickerListener");

        if (resultCode == Activity.RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TYPE_CAMERA:
                    if(file==null)return;
                    Uri uri = CompatUtil.getUriForFile(activity, file);
                    startPhotoZoom(activity, uri); // 开始对图片进行裁剪处理
                    break;
                case TYPE_GALLERY:
                    startPhotoZoom(activity, data.getData()); // 开始对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    String mImagePath = Constant.Path.CACHE_IMAGE + "/image_head.jpg";
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(outputUri));
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(mImagePath));
                        listener.onResult(mImagePath);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        try {
                            String output = getImagePath(activity, data);
                            Bitmap bitmap = BitmapFactory.decodeFile(output);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(mImagePath));
                            listener.onResult(mImagePath);
                        } catch (Throwable e1) {
                            e1.printStackTrace();
                            listener.onFailed();
                        }
                    }
                    break;
            }
        } else {
            listener.onFailed();
        }
    }

    private static void startPhotoZoom(Activity activity, Uri uri) {
        if (uri == null) {
            return;
        }
        File outputFile = new File(Environment.getExternalStorageDirectory(), "image_head.jpg");
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }
        outputUri = Uri.fromFile(outputFile);

        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        //兼容某些 第三方图片选择器 或头像选择器 的处理
        File urlFile = new File(uri.getPath());
        if (urlFile.exists() && urlFile.isFile()) {
            //file:///转成 content:///
            Uri imageContentUri = getImageContentUri(activity, urlFile);
            if (imageContentUri != null) {
                uri = imageContentUri;
            }
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        activity.startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    //把fileUri转换成ContentUri
    private static Uri getImageContentUri(Context context, File imageFile) {
        try {
            String filePath = imageFile.getAbsolutePath();
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media._ID},
                    MediaStore.Images.Media.DATA + "=? ",
                    new String[]{filePath}, null);

            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/images/media");
                return Uri.withAppendedPath(baseUri, "" + id);
            } else {
                if (imageFile.exists()) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, filePath);
                    return context.getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getImagePath(Context context, Intent data) {
        Uri uri = data.getData();
        String imagePath = null;
        if (Build.VERSION.SDK_INT >= 19) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    String id = docId.split(":")[1];
                    String selection = MediaStore.Images.Media._ID + "=" + id;
                    imagePath = getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("" +
                            "content://downloads/public_downloads"), Long.valueOf(docId));
                    imagePath = getImagePath(context, contentUri, null);
                }
            } else if ("content".equals(uri.getScheme())) {
                imagePath = getImagePath(context, uri, null);
            }
        } else {
            imagePath = getImagePath(context, uri, null);
        }
        return imagePath;
    }

    private static String getImagePath(Context context, Uri uri, String seletion) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, seletion, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    public interface OnHeadPickerListener {
        void onResult(String mImagePath);

        void onFailed();
    }
}
