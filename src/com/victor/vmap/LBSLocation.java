package com.victor.vmap;

import android.location.Location;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.yachi.library_yachi.VLog;

/**
 * 百度定位API使用类，启动定位，当返回定位结果是停止定位
 * 
 * @author Lu.Jian
 * 
 */
public class LBSLocation {

	private static LBSLocation location = null;
	private static VMapApplication app = null;

	private MyLocationListenner myListener = new MyLocationListenner();
	public LocationClient mLocationClient = null;

	public static LBSLocation getInstance(VMapApplication application) {
		app = application;
		if (location == null) {
			location = new LBSLocation(app);
		}

		return location;
	}

	private LBSLocation(VMapApplication app) {
		mLocationClient = new LocationClient(app);
		mLocationClient.registerLocationListener(myListener);
		mLocationClient.start();
	}

	/**
	 * 开始定位请求，结果在回调中
	 */
	public void startLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.disableCache(true);// 禁止启用缓存定位
		mLocationClient.setLocOption(option);
		mLocationClient.requestLocation();
	}

	/**
	 * 监听函数，有新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			VLog.v("location---"+location);
			app.currlocation = location;
			mLocationClient.stop();	
		}

		public void onReceivePoi(BDLocation poiLocation) {

		}
	}
}
