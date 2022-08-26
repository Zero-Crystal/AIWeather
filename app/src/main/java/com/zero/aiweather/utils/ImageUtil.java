package com.zero.aiweather.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.zero.aiweather.R;
import com.zero.aiweather.application.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtil {

    public static Drawable getBackgroundDrawable(int position) {
        Drawable bgDrawable;
        switch (position) {
            case 0:
                bgDrawable = MyApplication.getContext().getDrawable(R.drawable.bg_img1);
                break;
            case 1:
                bgDrawable = MyApplication.getContext().getDrawable(R.drawable.bg_img2);
                break;
            case 2:
                bgDrawable = MyApplication.getContext().getDrawable(R.drawable.bg_img3);
                break;
            case 3:
                bgDrawable = MyApplication.getContext().getDrawable(R.drawable.bg_img4);
                break;
            case 4:
                bgDrawable = MyApplication.getContext().getDrawable(R.drawable.bg_img5);
                break;
            case 5:
                bgDrawable = MyApplication.getContext().getDrawable(R.drawable.bg_img6);
                break;
            default:
                bgDrawable = MyApplication.getContext().getDrawable(R.drawable.bg_sun);
                break;
        }
        return bgDrawable;
    }

    /**
     * 保存图片至相册
     * */
    public static boolean saveImageToGallery(Bitmap bitmap) {
        // 首先保存图片
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "aiWeather";
        File appDir = new File(filePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "wallpaper" + 1024 + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();
            //把文件插入到系统图库
            MediaStore.Images.Media.insertImage(MyApplication.getContext().getContentResolver(), file.getAbsolutePath(), fileName, null);
            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            MyApplication.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                ToastUtil.showToast("图片保存成功");
                return true;
            } else {
                ToastUtil.showToast("图片保存失败");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ToastUtil.showToast("图片保存失败");
        return false;
    }
}
