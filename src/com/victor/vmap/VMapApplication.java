/** 
 * @Filename VMapApplication.java 
 * @Description TODO 
 * @Version 1.0
 * @Author xiaoyl
 * @Creation 2013-8-13 下午4:25:05   
 * @Copyright Copyright © 2009 - 2013 Victor.All Rights Reserved.
**/
package com.victor.vmap;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.victor.vmap.control.LBSLocation;
import com.yachi.library_yachi.VApplication;



/** 
 * @ClassName VMapApplication 
 * @Description TODO 
 * @Version 1.0
 * @Creation 2013-8-13 下午4:25:05 
 * @Mender xiaoyl
 * @Modification 2013-8-13 下午4:25:05 
 **/
public class VMapApplication extends VApplication {
	protected static VMapApplication instance;
	private Handler handler;
	public  String networkType;
	
	public static VMapApplication getInstance() {
		return instance;
	}
	
	/** 
	 * @Name onCreate
	 * @Description TODO 
	 * @see com.yachi.library_yachi.VApplication#onCreate()
	 * @Date 2013-8-14 下午2:56:12
	**/
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		networkType = getNetworkType();
		// 启动定位
		LBSLocation.getInstance(this).startLocation();
	}

	/**
	 * 设置手机网络类型，wifi，cmwap，ctwap，用于联网参数选择
	 * @return
	 */
	static String getNetworkType() {
		String networkType = "wifi";
		ConnectivityManager manager = (ConnectivityManager) instance
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo netWrokInfo = manager.getActiveNetworkInfo();
		if (netWrokInfo == null || !netWrokInfo.isAvailable()) {
			// 当前网络不可用
			return "";
		}

		String info = netWrokInfo.getExtraInfo();
		if ((info != null)
				&& ((info.trim().toLowerCase().equals("cmwap"))
						|| (info.trim().toLowerCase().equals("uniwap"))
						|| (info.trim().toLowerCase().equals("3gwap")) || (info
						.trim().toLowerCase().equals("ctwap")))) {
			// 上网方式为wap
			if (info.trim().toLowerCase().equals("ctwap")) {
				// 电信
				networkType = "ctwap";
			} else {
				networkType = "cmwap";
			}
		}
		return networkType;
	}
	
	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	
}
