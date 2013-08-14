/** 
 * @Filename MapViewFragment.java 
 * @Description TODO 
 * @Version 1.0
 * @Author xiaoyl
 * @Creation 2013-8-14 上午10:12:57   
 * @Copyright Copyright © 2009 - 2013 Victor.All Rights Reserved.
 **/
package com.victor.vmap;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.yachi.library_yachi.VToast;

/**
 * @ClassName ActivityMapView
 * @Description TODO
 * @Version 1.0
 * @Creation 2013-8-14 上午10:12:57
 * @Mender xiaoyl
 * @Modification 2013-8-14 上午10:12:57
 **/
public class ActivityMapView extends Activity {
	Context context;
	BMapManager mBMapManager = null;
	MapView mMapView = null;
	PopupOverlay pop = null;
	public static final String strKey = "FE95801d772e14d0b5ec69cb125ba77c";
     
	/** 
	 * @Name onCreate
	 * @Description TODO 
	 * @param savedInstanceState
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * @Date 2013-8-14 下午1:24:59
	**/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		initEngineManager(this);
		// 注意：请在试用setContentView前初始化BMapManager对象，否则会报错
		setContentView(R.layout.activity_main);
		mMapView = (MapView) findViewById(R.id.bmapsView);
		mMapView.setBuiltInZoomControls(true);
		// 设置启用内置的缩放控件
		MapController mMapController = mMapView.getController();
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放

		mMapController.setZoom(12);// 设置地图zoom级别
	}
	
	
	public void initEngineManager(Context context) {
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(context);
		}

		if (!mBMapManager.init(strKey, new MyGeneralListener())) {
			VToast.toast(context, "BMapManager  初始化错误!");
		}
	}
	
	@Override
	public void onPause() {
		mMapView.onPause();
		if (mBMapManager != null) {
			mBMapManager.stop();
		}
		removeAllMarker();
		super.onPause();
	}
	
	@Override
	public void onResume() {
		mMapView.onResume();
		if (mBMapManager != null) {
			mBMapManager.start();
		}
		addAllMarker();
		super.onResume();
	
	}
	
	@Override
	public void onDestroy() {
		mMapView.destroy();
		if (mBMapManager != null) {
			mBMapManager.destroy();
			mBMapManager = null;
		}
		super.onDestroy();
	}
	
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				VToast.toast(context, "您的网络出错啦！");
			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				VToast.toast(context, "输入正确的检索条件！");
			}
		}

		@Override
		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				VToast.toast(context, "请在 DemoApplication.java文件输入正确的授权Key！");
			}
		}
	}
	
	/**
	 * 删除所有标记
	 */
	public void removeAllMarker() {
		mMapView.getOverlays().clear();
		mMapView.refresh();

	}

	/**
	 * 添加所有标记
	 */
	public void addAllMarker() {
		VMapApplication app = (VMapApplication) getApplication();
		List<ContentModel> list = MapConstant.getList();
		mMapView.getOverlays().clear();
		OverlayIcon ov = new OverlayIcon(null, mMapView);
		for (ContentModel content : list) {
			int latitude = (int) (content.getLatitude() * 1000000);
			int longitude = (int) (content.getLongitude() * 1000000);

			Drawable d = getResources().getDrawable(R.drawable.icon_marka);
			OverlayItem item = new OverlayItem(
					new GeoPoint(latitude, longitude), content.getName(),
					content.getAddr());
			item.setMarker(d);
			ov.addItem(item);
		}
		mMapView.getOverlays().add(ov);
		mMapView.refresh();
		



		// 北京的中心，无定位时的地图中心
		int cLat = 39909230;
		int cLon = 116397428;
		if (app.currlocation == null) {
			mMapView.getController().setCenter(new GeoPoint(cLat, cLon));
		} else if (list != null && list.size() >= 1) {
			ContentModel c = (ContentModel)list.get(0);
			int currLat = (int) (c.getLatitude() * 1000000);
			int currLon = (int) (c.getLongitude() * 1000000);
			mMapView.getController().setCenter(new GeoPoint(currLat, currLon));
		}
	}
	
	/**
	 * 地图覆盖物，用于显示标记
	 *
	 */
	class OverlayIcon extends ItemizedOverlay<OverlayItem> {
		public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
		private Context mContext = null;
		PopupOverlay pop = null;

		final List<ContentModel> list = MapConstant.getList();

		private int clickedTapIndex = -1;
		/** 
		 * @Name OverlayIcon 
		 * @Description TODO 
		 * @param drawable
		 * @param mapview
		**/
		public OverlayIcon(Drawable drawable, MapView mapview) {
			super(drawable, mapview);
			this.mContext = context;
			pop = new PopupOverlay(mMapView, new PopupClickListener() {

				/*
				 * 标记的弹出框被点击后回调
				 * (non-Javadoc)
				 * @see com.baidu.mapapi.map.PopupClickListener#onClickedPopup()
				 */
				@Override
				public void onClickedPopup(int i) {
					String webUrl = list.get(clickedTapIndex).getWebUrl();

					Intent intent = new Intent();
					intent.setAction("android.intent.action.VIEW");
					Uri content_url = Uri.parse(webUrl);
					intent.setData(content_url);
					startActivity(intent);
					
				    //调用百度统计接口
				    //VMapApplication.getInstance().callStatistics();

				}
			});


		}

		/*
		 * 覆盖物点击回调
		 * (non-Javadoc)
		 * @see com.baidu.mapapi.map.ItemizedOverlay#onTap(int)
		 */
		protected boolean onTap(int index) {
			if(index >= list.size()){
				//点击自己位置marker，不做任何处理
				return true;
			}
			clickedTapIndex = index;
			View popview = LayoutInflater.from(mContext).inflate(
					R.layout.marker_pop, null);
			TextView textV = (TextView) popview.findViewById(R.id.text_pop);
			String text = list.get(index).getName();
			textV.setText(text);

			pop.showPopup(convertViewToBitMap(popview), mGeoList.get(index)
					.getPoint(), 28);
			super.onTap(index);
			return false;
		}

		/*
		 * 覆盖物其他区域点击回调 
		 */
		public boolean onTap(GeoPoint pt, MapView mapView) {
			if (pop != null) {
				clickedTapIndex = -1;
				pop.hidePop();
			}
			super.onTap(pt, mapView);
			return false;
		}

		@Override
		protected OverlayItem createItem(int i) {
			return mGeoList.get(i);
		}

		@Override
		public int size() {
			return mGeoList.size();
		}

		public void addItem(OverlayItem item) {
			mGeoList.add(item);

		}

		public void removeItem(int index) {
			mGeoList.remove(index);

		}

		private Bitmap convertViewToBitMap(View v) {
			// 启用绘图缓存
			v.setDrawingCacheEnabled(true);
			// 调用下面这个方法非常重要，如果没有调用这个方法，得到的bitmap为null
			v.measure(MeasureSpec.makeMeasureSpec(210, MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(120, MeasureSpec.EXACTLY));
			// 这个方法也非常重要，设置布局的尺寸和位置
			v.layout(0, 0, v.getMeasuredWidth() +20, v.getMeasuredHeight());
			// 获得绘图缓存中的Bitmap
			v.buildDrawingCache();
			return v.getDrawingCache();
		}
	}

}
