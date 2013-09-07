package com.victor.vmap.control;

import android.graphics.drawable.Drawable;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.victor.vmap.R;
import com.victor.vmap.VMapApplication;
import com.victor.vmap.utils.MapConstant;
import com.yachi.library_yachi.VLog;
import com.yachi.library_yachi.VToast;

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
	 * 取消跟踪定位 
	 * @Name unRegisterLocationListener 
	 * @Description TODO  
	*
	 */
	public void unRegisterLocationListener() {
		if (myListener != null) {
			mLocationClient.unRegisterLocationListener(myListener);
		}
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
			if (location == null) {
				VToast.toast(app, "定位失败了，重新定位中 ");
				startLocation();
				return;
			}

			MapConstant.setCurrlocation(location);
			if (MapConstant.mapActivity != null) {
				MapConstant.mapActivity.focusLocation();
			}
			// VToast.toast(app, "位置为+"+sb.toString());
			VLog.v("location---" + location.getAddrStr());
			mLocationClient.stop();
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}
}
