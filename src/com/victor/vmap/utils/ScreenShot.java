/** 
 * @Filename ScreenShot.java 
 * @Description TODO 
 * @Version 1.0
 * @Author xiaoyl
 * @Creation 2013-8-30 下午3:16:09   
 * @Copyright Copyright © 2009 - 2013 Victor.All Rights Reserved.
 **/
package com.victor.vmap.utils;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.yachi.library_yachi.utils.VUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;
/**
 * @ClassName ScreenShot
 * @Description TODO
 * @Version 1.0
 * @Creation 2013-8-30 下午3:16:09
 * @Mender xiaoyl
 * @Modification 2013-8-30 下午3:16:09
 **/
public class ScreenShot {
	// 获取指定Activity的截屏，保存到png文件
	public static Bitmap takeScreenShot(Activity activity) {
		// View是你需要截图的View
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();
		// 获取状态栏高度
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		System.out.println(statusBarHeight);
		// 获取屏幕长和高
		// 去掉标题栏 //Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
		int[] reso =  VUtils.getPhoneResolution(activity);
		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight,reso[0], reso[1]
				- statusBarHeight);
		view.destroyDrawingCache();
		return b;
	}

}
