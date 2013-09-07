/** 
 * @Filename BaseActivity.java 
 * @Description TODO 
 * @Version 1.0
 * @Author xiaoyl
 * @Creation 2013-9-7 下午3:33:16   
 * @Copyright Copyright © 2009 - 2013 Victor.All Rights Reserved.
**/
package com.victor.vmap;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.UMWXHandler;
import com.umeng.socialize.media.UMImage;
import com.umeng.update.UmengUpdateAgent;
import com.victor.vmap.control.BranchModel;
import com.victor.vmap.provider.BranchDbHelper;
import com.victor.vmap.utils.MapConstant;
import com.yachi.library_yachi.VLog;
import com.yachi.library_yachi.VToast;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/** 
 * @ClassName BaseActivity 
 * @Description TODO 
 * @Version 1.0
 * @Creation 2013-9-7 下午3:33:16 
 * @Mender xiaoyl
 * @Modification 2013-9-7 下午3:33:16 
 **/
public class BaseActivity extends Activity  implements OnClickListener {
	protected BMapManager mBMapManager = null;
	protected MapView mMapView = null;
	protected VMapApplication app;
	protected Context context;
	protected Dialog loading;
	protected UMSocialService controller;
	protected BranchDbHelper db_helper;
	/** 所有Geotable ID*/
	protected String[] geotable_ids;
	
	/** 显示底部工具栏 */
	private static final int SHOW_BOTTOM = 1000;
	private Timer timer;
	private Handler handler = new Handler() {
		/**
		 * @Name handleMessage
		 * @Description TODO
		 * @param msg
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 * @Date 2013-9-7 下午2:41:04
		 **/
		@Override
		public void handleMessage(Message msg) {
			
			super.handleMessage(msg);
			switch (msg.what) {
				case SHOW_BOTTOM :
					showBottom();
					timer.cancel();
					break;
			}
		}
	};
	TimerTask task = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what = SHOW_BOTTOM;
			handler.sendMessage(message);
		}
	};
	
	/** 
	 * @Name onCreate
	 * @Description TODO 
	 * @param savedInstanceState
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * @Date 2013-9-7 下午3:35:34
	**/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		VLog.setDEBUG(true);
		db_helper = BranchDbHelper.getInstance(context);
		app = (VMapApplication) VMapApplication.getInstance();
		app.addActivitys(this);

		UmengUpdateAgent.update(this);// 加入更新
		UmengUpdateAgent.setUpdateOnlyWifi(false);//设置非WIFI可以更新
		MobclickAgent.onError(this);// 加入出错报告
		geotable_ids = getResources().getStringArray(R.array.geotable_ids);
		initEngineManager(this);
	}
	
	/** 
	 * @Name showBottom 
	 * @Description TODO  显示底部布局
	**/
	protected  void showBottom() {
		
	}

	/**
	 * 初始化地图引擎
	 * 
	 * @Name initEngineManager
	 * @Description TODO
	 * @param context
	 * 
	 */
	public void initEngineManager(Context context) {
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(context);
		}
		if (!mBMapManager.init(MapConstant.strKey, new MapListener())) {
			// VToast.toast(context, "mBMapManager  初始化错误!");
			VLog.e("mBMapManager  初始化错误!");
		}
	}
	
	@Override
	public void onPause() {
		mMapView.onPause();
		if (mBMapManager != null) {
			mBMapManager.stop();
		}
		mMapView.setKeepScreenOn(false);
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onResume() {
		mMapView.onResume();
		if (mBMapManager != null) {
			mBMapManager.start();
		}
		mMapView.setKeepScreenOn(true);
		super.onResume();
		MobclickAgent.onResume(this);
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
	
	/*
	 * 添加对back按钮的处理，点击提示退出 (non-Javadoc)
	 * 
	 * @see android.app.Activity#dispatchKeyEvent(android.view.KeyEvent)
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() != 1) {
			exit();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	
	/*
	 * 退出应用程序
	 */
	private void exit() {
		new AlertDialog.Builder(BaseActivity.this)
				.setMessage(R.string.msg_exit)
				.setIcon(R.drawable.icon)
				.setPositiveButton(R.string.msg_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								app.exitApplication();
							}
						}).setNegativeButton(R.string.msg_cancel, null).show();
	}
	
	/*
	 * 显示警告
	 */
	protected void showDialogEmpty(int msg_res) {
		new AlertDialog.Builder(BaseActivity.this)
				.setMessage(msg_res)
				.setIcon(R.drawable.icon)
				.setPositiveButton(R.string.msg_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								app.exitApplication();
							}
						}).show();
	}

	/**
	 * @Name initSocialShare
	 * @Description TODO
	 **/
	private void initSocialShare(Bitmap arg0) {
		controller = UMServiceFactory.getUMSocialService(
				MainActivity.class.getName(), RequestType.SOCIAL);
		controller.setShareContent(MapConstant.socialShareTitle);// 设置分享文字内容
		controller.setShareMedia(new UMImage(BaseActivity.this, arg0));//
		// 设置分享图片内容
		UMWXHandler.WX_APPID = MapConstant.wxKey;// 设置微信的Appid

		// 添加微信平台
		controller.getConfig().supportWXPlatform(BaseActivity.this);

		// 添加微信朋友圈
		controller.getConfig().supportWXPlatform(
				BaseActivity.this,
				UMServiceFactory.getUMWXHandler(BaseActivity.this).setToCircle(
						true));
	
		UMWXHandler.CONTENT_URL =MapConstant.wxContentShareUrl;// 微信图文分享必须设置一个url
													// 默认"http://www.umeng.com"
		UMWXHandler.WX_CONTENT_TITLE = MapConstant.wxShareTitle;
		UMWXHandler.WXCIRCLE_CONTENT_TITLE = MapConstant.wxContentShareTitle;
		controller.openShare(BaseActivity.this, false);

	}

	
	/**
	 * 常用事件监听，用来处理通常的网络错误，授权验证错误等
	 * 
	 * @ClassName MapListener
	 * @Description TODO
	 * @Version 1.0
	 * @Creation 2013-8-19 下午3:47:48
	 * @Mender xiaoyl
	 * @Modification 2013-8-19 下午3:47:48
	 * 
	 */
	class MapListener implements MKGeneralListener {
		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				VToast.toast(context,R.string.tip_net_error);
			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				VToast.toast(context, R.string.tip_data_error);
			}
		}

		@Override
		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				VToast.toast(context, R.string.tip_key_error);
			}
		}
	}
	
	/**
	 * 截图回调
	 * 
	 * @ClassName MapShotCut
	 * @Description TODO
	 * @Version 1.0
	 * @Creation 2013-9-3 上午10:10:20
	 * @Mender xiaoyl
	 * @Modification 2013-9-3 上午10:10:20
	 * 
	 */
	class MapShotCutListener implements MKMapViewListener {

		/**
		 * @Name onClickMapPoi
		 * @Description TODO
		 * @param arg0
		 * @see com.baidu.mapapi.map.MKMapViewListener#onClickMapPoi(com.baidu.mapapi.map.MapPoi)
		 * @Date 2013-9-3 上午10:10:05
		 **/
		@Override
		public void onClickMapPoi(MapPoi arg0) {
			

		}

		/**
		 * @Name onGetCurrentMap
		 * @Description TODO
		 * @param arg0
		 * @see com.baidu.mapapi.map.MKMapViewListener#onGetCurrentMap(android.graphics.Bitmap)
		 * @Date 2013-9-3 上午10:10:05
		 **/
		@Override
		public void onGetCurrentMap(Bitmap arg0) {
			if(!loading.isShowing()){
				return;
			}
			initSocialShare(arg0);
			loading.dismiss();
		}

		/**
		 * @Name onMapAnimationFinish
		 * @Description TODO
		 * @see com.baidu.mapapi.map.MKMapViewListener#onMapAnimationFinish()
		 * @Date 2013-9-3 上午10:10:05
		 **/
		@Override
		public void onMapAnimationFinish() {
			

		}

		/**
		 * @Name onMapLoadFinish
		 * @Description TODO
		 * @see com.baidu.mapapi.map.MKMapViewListener#onMapLoadFinish()
		 * @Date 2013-9-3 上午10:10:05
		 **/
		@Override
		public void onMapLoadFinish() {
			VLog.v("onMapLoadFinish");
			if (timer == null) {
				timer = new Timer(true);
				timer.schedule(task, 500);
			}
			focusMarker();
		}

		/**
		 * @Name onMapMoveFinish
		 * @Description TODO
		 * @see com.baidu.mapapi.map.MKMapViewListener#onMapMoveFinish()
		 * @Date 2013-9-3 上午10:10:05
		 **/
		@Override
		public void onMapMoveFinish() {

		}
	}

	/**
	 * @Name focusMarker
	 * @Description TODO
	 **/
	private void focusMarker() {
		// 长沙的中心，无定位时的地图中心
		int cLat = 28148494;
		int cLon = 113002065;
		if (MapConstant.getCurrlocation() == null) {
			mMapView.getController().setCenter(new GeoPoint(cLat, cLon));
		} else if (MapConstant.cate_branchs != null && MapConstant.cate_branchs.size() >= 1) {
			for (ArrayList<BranchModel> items : MapConstant.cate_branchs) {
				if (items.size() > 0) {
					BranchModel c = (BranchModel) items.get(0);
					int currLat = (int) (Double.valueOf(c.getLatitude()) * 1000000);
					int currLon = (int) (Double.valueOf(c.getLongitude()) * 1000000);
					mMapView.getController().animateTo(
							new GeoPoint(currLat, currLon));
					break;
				}
			}
		}
	}
	
	/** 
	 * @Name onClick
	 * @Description TODO 
	 * @param v
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 * @Date 2013-9-7 下午3:45:28
	**/
	@Override
	public void onClick(View v) {
		
	}
}
