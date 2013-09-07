/** 
 * @Filename NativeUtil.java 
 * @Description TODO 
 * @Version 1.0
 * @Author xiaoyl
 * @Creation 2013-8-29 下午5:33:09   
 * @Copyright Copyright © 2009 - 2013 Victor.All Rights Reserved.
 **/
package com.victor.vmap.utils;

import java.io.DataOutputStream;
import java.nio.ByteBuffer;

import android.content.Context;
import android.graphics.Bitmap;

import com.yachi.library_yachi.VLog;

/**
 * @ClassName NativeUtil
 * @Description TODO
 * @Version 1.0
 * @Creation 2013-8-29 下午5:33:09
 * @Mender xiaoyl
 * @Modification 2013-8-29 下午5:33:09
 **/
public class NativeUtil {
	private static boolean sLoadSO;

	static {
		try {
			System.loadLibrary("mqq");
			sLoadSO = true;
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			VLog.e("mqq", "load mqq.so error!" + localThrowable.toString());
		}
	}

	private static native byte[] getFrameBuffer();

	public static Bitmap screenshot(Context paramContext) {
		Bitmap localBitmap = null;
		if (sLoadSO)
			
		try {
			byte[] arrayOfByte = getFrameBuffer();
			if (arrayOfByte == null) {
				final Process localProcess = Runtime.getRuntime().exec("su");
				DataOutputStream localDataOutputStream = new DataOutputStream(
						localProcess.getOutputStream());
				localDataOutputStream
						.writeBytes("chmod 666 /dev/graphics/fb0\n");
				localDataOutputStream.writeBytes("exit\n");
				localDataOutputStream.flush();
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							localProcess.waitFor();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
					}
				}).start();
				arrayOfByte = getFrameBuffer();
			}
			if (arrayOfByte != null) {
				ByteBuffer localByteBuffer = ByteBuffer.wrap(arrayOfByte);
				localBitmap = Bitmap.createBitmap(paramContext.getResources()
						.getDisplayMetrics().widthPixels, paramContext
						.getResources().getDisplayMetrics().heightPixels,
						Bitmap.Config.ARGB_8888);
				localBitmap.copyPixelsFromBuffer(localByteBuffer);
			}
			return localBitmap;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return localBitmap;
			
	}

}