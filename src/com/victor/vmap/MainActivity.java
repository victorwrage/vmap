package com.victor.vmap;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MKOLUpdateElement;
import com.baidu.mapapi.map.MKOfflineMap;
import com.baidu.mapapi.map.MKOfflineMapListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.UMSsoHandler;
import com.umeng.update.UmengUpdateAgent;
import com.victor.vmap.control.BranchModel;
import com.victor.vmap.provider.BranchDbHelper;
import com.victor.vmap.utils.MapConstant;
import com.yachi.library_yachi.VLog;
import com.yachi.library_yachi.VToast;
import com.yachi.library_yachi.utils.ApplicationInfoUtil;
import com.yachi.library_yachi.utils.VUtils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * 地图界面
 * 
 * @author xiaoyl
 * 
 */
public class MainActivity extends BaseActivity {

	/** 关于、视图、设置 对话框 */
	private Dialog aboutDialog, layer_Dialog, setting_Dialog;
	/** 描点标记、定位标记 */
	private OverlayTest ov_markA, ov_markB, ov_markC, ov_markD, ovt_location;
	private PopupOverlay pop;
	/** 底部布局的高度（用于显示Geotable参考） */
	private int width, height, bottom_height;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		VLog.v("MainActivity----onCreate");
		context = this;
		VLog.setDEBUG(true);
		db_helper = BranchDbHelper.getInstance(context);
		app = (VMapApplication) VMapApplication.getInstance();
		app.addActivitys(this);

		UmengUpdateAgent.update(this);// 加入更新
		UmengUpdateAgent.setUpdateOnlyWifi(false);// 设置非WIFI可以更新
		MobclickAgent.onError(this);// 加入出错报告	
		initEngineManager(this);
		
		MapConstant.mapActivity = this;
		setContentView(R.layout.activity_main);
		resolveIntent(getIntent());

		initView();
		initOffLineMap();
		MapConstant.IsEntered = true;
	}

	@Override
	public void onPause() {
		mMapView.onPause();
		if (mBMapManager != null) {
			mBMapManager.stop();
		}
		super.onPause();
		MobclickAgent.onPause(this);
		removeAllMarker();
	}

	@Override
	public void onResume() {
		mMapView.onResume();
		if (mBMapManager != null) {
			mBMapManager.start();
		}
		super.onResume();
		MobclickAgent.onResume(this);
		addAllMarker();
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

	/**
	 * @Name refreshMapView 
	 * @Description TODO  刷新地图
	*
	 */
	public void refreshMapView(){
		removeAllMarker();
		addAllMarker();
	}
	
	/**
	 * @Name resolveIntent
	 * @Description TODO 响应搜索点击
	 * @param intent
	 **/
	private void resolveIntent(Intent intent) {
		VLog.v("resolveIntent--" + intent.getAction());
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			String uid = intent.getDataString().trim().toLowerCase();
			BranchModel bm = db_helper.getBranchsByName(uid);
			if (bm != null) {
				GeoPoint point = new GeoPoint(
						Integer.parseInt(bm.getLatitude()), Integer.parseInt(bm
								.getLongitude()));
				mMapView.getController().animateTo(point);
				showDetail(bm.getName(), point);
			}
		}
	}

	/**
	 * @Name initOffLineMap
	 * @Description TODO 初始化离线地图
	 **/
	private void initOffLineMap() {
		VLog.v("MainActivity--initOffLineMap");
		final MKOfflineMap mOffline = new MKOfflineMap(); // 申明变量
		MapController mMapController = mMapView.getController();
		// 写在onCreate函数里
		// offline 实始化方法用更改。
		mOffline.init(mMapController, new MKOfflineMapListener() {
			@Override
			public void onGetOfflineMapState(int type, int state) {
				switch (type) {
					case MKOfflineMap.TYPE_DOWNLOAD_UPDATE : {
						MKOLUpdateElement update = mOffline
								.getUpdateInfo(state);
						VToast.toast(context, String.format("%s : %d%%",
								update.cityName, update.ratio));
					}
						break;
					case MKOfflineMap.TYPE_NEW_OFFLINE :
						VLog.v(String.format("add offlinemap num:%d", state));
						break;
					case MKOfflineMap.TYPE_VER_UPDATE :
						VLog.v(String.format("new offlinemap ver"));
						break;
				}
			}
		});

	}

	/**
	 * @Name initView
	 * @Description TODO 初始化布局中的视图
	 **/
	private void initView() {
		VLog.v("MainActivity--initView");
		mMapView = (MapView) findViewById(R.id.bmapsView);
		ImageView view_iv = (ImageView) findViewById(R.id.main_frame_iv);
		EditText search_et = (EditText) findViewById(R.id.main_search_iv);
		ImageView focus_iv = (ImageView) findViewById(R.id.main_focus_iv);

		view_iv.setOnClickListener(this);
		search_et.setOnClickListener(this);
		focus_iv.setOnClickListener(this);
		RadioGroup geo_rg = (RadioGroup) findViewById(R.id.bottom_geo_rg);

		geo_rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				switch (checkedId) {
					case R.id.bottom_geo_all :
						removeAllMarker();
						addAllMarker();
						break;
					case R.id.bottom_geo_a :
						removeAllMarker();
						addMarker(0);
						break;
					case R.id.bottom_geo_b :
						removeAllMarker();
						addMarker(1);
						break;
					case R.id.bottom_geo_c :
						removeAllMarker();
						addMarker(2);
						break;
					case R.id.bottom_geo_d :
						removeAllMarker();
						addMarker(3);
						break;
				}
			}
		});
		mMapView.setBuiltInZoomControls(true);
		mMapView.regMapViewListener(mBMapManager, new MapShotCutListener());
		MapController mMapController = mMapView.getController();
		mMapController.setZoom(12);

		width = VUtils.getPhoneResolution(context)[0];
		bottom_height = height = (int) (VUtils.getPhoneResolution(context)[1] * 0.2);
	}

	/**
	 * 删除所有标记
	 */
	public void removeAllMarker() {
		VLog.v("MainActivity--removeAllMarker");
		mMapView.getOverlays().remove(ov_markA);
		mMapView.getOverlays().remove(ov_markB);
		mMapView.getOverlays().remove(ov_markC);
		mMapView.getOverlays().remove(ov_markD);
		mMapView.refresh();
	}

	/**
	 * 添加所有标记
	 */
	public void addAllMarker() {
		VLog.v("MainActivity--addAllMarker");
		addMarker(0);
		addMarker(1);
		addMarker(2);
		addMarker(3);

	}

	/**
	 * @Name addMarker
	 * @Description TODO
	 **/
	private void addMarker(int index) {
		VLog.v("MainActivity--addMarker-" + index);
		OverlayTest ov_mark = ov_markA;
		int marker_res = R.drawable.icon_gcoding;
		switch (index) {
			case 0 :
				marker_res = R.drawable.icon_share_loc_add;
				break;
			case 1 :
				// marker_res = R.drawable.icon_share_loc_add;
				break;
			case 2 :
				marker_res = R.drawable.icon_mylocal_favd;
				break;
			case 3 :
				marker_res = R.drawable.icon_share_loc_add;
				break;
		}
		if (MapConstant.cate_branchs != null
				&& MapConstant.cate_branchs.size() != 0) {

			ov_mark = new OverlayTest(null, mMapView, false);

			for (BranchModel content : MapConstant.cate_branchs.get(index)) {
				int latitude = (int) (Double.valueOf(content.getLatitude()) * 1000000);
				int longitude = (int) (Double.valueOf(content.getLongitude()) * 1000000);

				Drawable d = getResources().getDrawable(marker_res);
				OverlayItem item = new OverlayItem(new GeoPoint(latitude,
						longitude), content.getName(), content.getAddr());
				item.setMarker(d);
				ov_mark.addItem(item);
			}
			switch (index) {
				case 0 :
					ov_markA = ov_mark;
					break;
				case 1 :
					ov_markB = ov_mark;
					break;
				case 2 :
					ov_markC = ov_mark;
					break;
				case 3 :
					ov_markD = ov_mark;
					break;
			}
			mMapView.getOverlays().add(ov_mark);
			mMapView.refresh();
		}
	}

	/**
	 * 视角移到当前位置
	 * 
	 * @Name focusLoacation
	 * @Description TODO
	 * 
	 */
	public void focusLocation() {
		VLog.v("MainActivity--focusLocation");
		if (MapConstant.getCurrlocation() == null) {
			return;
		}
		int lat = (int) (MapConstant.getCurrlocation().getLatitude() * 1000000);
		int lon = (int) (MapConstant.getCurrlocation().getLongitude() * 1000000);

		VLog.v("focusLocation-" + lat + "," + lon);
		if (ovt_location != null) {
			// mMapView.getOverlays().remove(ovt_location);
		}
		ovt_location = new OverlayTest(null, mMapView, true);

		Drawable d = getResources().getDrawable(R.drawable.icon_myloc);
		OverlayItem item = new OverlayItem(new GeoPoint(lat, lon), MapConstant
				.getCurrlocation().getPoi(), MapConstant.getCurrlocation()
				.getAddrStr());
		item.setMarker(d);
		ovt_location.addItem(item);
		mMapView.getOverlays().add(ovt_location);
		mMapView.refresh();

		mMapView.getController().animateTo(new GeoPoint(lat, lon));
	}

	/*
	 * 要处理overlay点击事件时需要继承ItemizedOverlay 不处理点击事件时可直接生成ItemizedOverlay.
	 */
	class OverlayTest extends ItemizedOverlay<OverlayItem> {
		// 用MapView构造ItemizedOverlay
		boolean isLoc = false;

		public OverlayTest(Drawable mark, MapView mapView, boolean isLoc) {
			super(mark, mapView);
			this.isLoc = isLoc;
		}

		protected boolean onTap(int index) {
			mMapView.getController().animateTo(getItem(index).getPoint());
			showDetail(getItem(index).getTitle(), getItem(index).getPoint());
			pop = new PopupOverlay(mMapView, new PopupClickListener() {
				@Override
				public void onClickedPopup(int index) {
					// 在此处理pop点击事件，index为点击区域索引,点击区域最多可有三个

				}
			});

			return true;
		}

		public boolean onTap(GeoPoint pt, MapView mapView) {
			// 在此处理MapView的点击事件，当返回 true时
			if (pop != null) {
				pop.hidePop();
			}
			super.onTap(pt, mapView);
			return false;
		}
	}

	/**
	 * 弹出该点窗口
	 * 
	 * @Name showDetail
	 * @Description TODO
	 * 
	 */
	protected void showBottom() {
		VLog.v("MainActivity--showBottom");
		final View v = View.inflate(context, R.layout.bottom_menu, null);
		LinearLayout geo = (LinearLayout) v.findViewById(R.id.bottom_geo_lay);
		LinearLayout share = (LinearLayout) v
				.findViewById(R.id.bottom_share_lay);
		LinearLayout about = (LinearLayout) v
				.findViewById(R.id.bottom_about_lay);
		geo.setOnClickListener(this);
		share.setOnClickListener(this);
		about.setOnClickListener(this);

		VLog.v("showBottom---" + width);
		VLog.v("showBottom---" + height);
		PopupWindow window = new PopupWindow(v, width, height);
		window.setOutsideTouchable(true);

		// 设置整个popupwindow的样式。
		window.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.map_layer_background));
		// 使窗口里面的空间显示其相应的效果，比较点击button时背景颜色改变。
		// 如果为false点击相关的空间表面上没有反应，但事件是可以监听到的。
		// listview的话就没有了作用。
		window.setAnimationStyle(R.style.SlideBottomAnimationLong);
		window.setFocusable(true);
		window.update();
		window.showAtLocation(mMapView, Gravity.CENTER_HORIZONTAL
				| Gravity.BOTTOM, 0, 0);

	}

	/**
	 * 弹出该点窗口
	 * 
	 * @Name showDetail
	 * @Description TODO
	 * 
	 */
	private void showDetail(String title, GeoPoint point) {
		VLog.v("MainActivity--showDetail");
		View v = View.inflate(context, R.layout.point_detail, null);

		Button btn_left = (Button) v.findViewById(R.id.point_left_btn);
		Button btn_right = (Button) v.findViewById(R.id.point_right_btn);
		btn_left.setOnClickListener(this);
		btn_right.setOnClickListener(this);
		TextView tv_title = (TextView) v.findViewById(R.id.point_detail_tv);
		TextView tv_add = (TextView) v.findViewById(R.id.point_add_tv);
		if (title == null)
			title = "未填写";
		tv_title.setText(title);
		tv_add.setText(point.getLatitudeE6() + ":" + point.getLongitudeE6());

		PopupWindow window = new PopupWindow(v, width, height);

		// 设置整个popupwindow的样式。
		window.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.map_layer_background));
		// 使窗口里面的空间显示其相应的效果，比较点击button时背景颜色改变。
		// 如果为false点击相关的空间表面上没有反应，但事件是可以监听到的。
		// listview的话就没有了作用。
		window.setAnimationStyle(R.style.SlideBottomAnimation);
		window.setFocusable(true);
		window.update();
		window.showAtLocation(mMapView, Gravity.CENTER_HORIZONTAL
				| Gravity.BOTTOM, 0, 0);

	}

	/**
	 * @Name onClick
	 * @Description TODO
	 * @param arg0
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 * @Date 2013-8-16 上午10:07:34
	 **/
	@Override
	public void onClick(View view) {

		switch (view.getId()) {
			case R.id.frame_plain_iv :
				mMapView.setSatellite(false);
				layer_Dialog.dismiss();
				break;
			case R.id.frame_satellite_iv :
				mMapView.setSatellite(true);
				layer_Dialog.dismiss();
				break;
			case R.id.main_frame_iv :
				showLayer();
				break;
			case R.id.main_focus_iv :
				focusLocation();
				break;
			case R.id.setting_about_btn :
				setting_Dialog.dismiss();
				showAbout();
				break;
			case R.id.bottom_geo_lay :// 选择网点
				showGeotableSel();
				break;
			case R.id.point_left_btn :// 分享
				mMapView.getCurrentMap();
				showLoading();
				break;
			case R.id.point_right_btn :// 关于
				showAbout();
				break;
			case R.id.main_search_iv :// 搜索
				onSearchRequested();
				break;
		}
	}

	/**
	 * @Name showGeotableSel
	 * @Description TODO
	 **/
	private void showGeotableSel() {
		VLog.v("MainActivity--showGeotableSel");
		final View v = View.inflate(context, R.layout.bottom_geo, null);
		RadioGroup geo_rg = (RadioGroup) v.findViewById(R.id.bottom_geo_rg);
		geo_rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case 0 :
						removeAllMarker();
						addAllMarker();
						break;
					case 1 :
						removeAllMarker();
						addMarker(0);
						break;
					case 2 :
						removeAllMarker();
						addMarker(1);
						break;
					case 3 :
						removeAllMarker();
						addMarker(2);
						break;
					case 4 :
						removeAllMarker();
						addMarker(3);
						break;
				}

			}
		});

		PopupWindow window = new PopupWindow(v, width, height);
		window.setOutsideTouchable(true);
		window.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.map_layer_background));
		window.setAnimationStyle(R.style.SlideBottomAnimationLong);
		window.setFocusable(true);
		window.update();
		window.showAtLocation(mMapView, Gravity.CENTER_HORIZONTAL
				| Gravity.BOTTOM, 0, bottom_height);
	}

	/**
	 * @Name showLoading
	 * @Description TODO
	 **/
	private void showLoading() {
		loading = new Dialog(context, R.style.myDialog);
		View view = View.inflate(context, R.layout.mydialog_layout_round, null);
		TextView tv_tip = (TextView) view.findViewById(R.id.mydialog_message);

		tv_tip.setText("正在截图");
		loading.setContentView(view);
		loading.setCancelable(true);
		loading.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {

			}
		});
		loading.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/**
		 * 使用SSO必须添加，指定获取授权信息的回调页面，并传给SDK进行处理
		 */
		UMSsoHandler sinaSsoHandler = controller.getConfig()
				.getSinaSsoHandler();
		if (sinaSsoHandler != null
				&& requestCode == UMSsoHandler.DEFAULT_AUTH_ACTIVITY_CODE) {
			sinaSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	/**
	 * @Name showSetting
	 * @Description TODO
	 **/
	private void showSetting() {
		setting_Dialog = new Dialog(this);
		View layer_view = LayoutInflater.from(this).inflate(
				R.layout.dialog_setting_lay, null);

		setting_Dialog.setCanceledOnTouchOutside(true);
		setting_Dialog.setCancelable(true);
		setting_Dialog.setContentView(layer_view);
		setting_Dialog.setTitle("设置");

		Button frame_plain_btn = (Button) layer_view
				.findViewById(R.id.setting_frame_btn);

		Button frame_about_btn = (Button) layer_view
				.findViewById(R.id.setting_about_btn);

		frame_plain_btn.setOnClickListener(this);
		frame_about_btn.setOnClickListener(this);

		final Spinner spinner = (Spinner) layer_view
				.findViewById(R.id.setting_sections_sp);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.shop_type, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setTag(spinner.getSelectedItemPosition());
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if ((Integer) spinner.getTag() != position) {
					spinner.setTag(position);
					setting_Dialog.dismiss();
				}

			}

			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		Window dialogWindow = setting_Dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);

		dialogWindow.setAttributes(lp);
		setting_Dialog.show();

	}

	/**
	 * @Name showLayer
	 * @Description TODO
	 **/
	private void showLayer() {
		layer_Dialog = new Dialog(this, R.style.dialog);
		View layer_view = LayoutInflater.from(this).inflate(
				R.layout.dialog_frame_lay, null);

		layer_Dialog.setCanceledOnTouchOutside(true);
		layer_Dialog.setCancelable(true);
		layer_Dialog.setContentView(layer_view);

		ImageView frame_plain_btn = (ImageView) layer_view
				.findViewById(R.id.frame_plain_iv);

		ImageView frame_satellite_btn = (ImageView) layer_view
				.findViewById(R.id.frame_satellite_iv);

		frame_plain_btn.setOnClickListener(this);

		frame_satellite_btn.setOnClickListener(this);

		Window dialogWindow = layer_Dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);

		dialogWindow.setAttributes(lp);
		layer_Dialog.show();

	}

	/**
	 * @Name showAbout
	 * @Description TODO
	 **/
	private void showAbout() {
		aboutDialog = new Dialog(this);
		View view = LayoutInflater.from(this).inflate(
				R.layout.dialog_about_lay, null);
		aboutDialog.setCanceledOnTouchOutside(true);
		aboutDialog.setCancelable(true);
		aboutDialog.setContentView(view);
		aboutDialog.setTitle("关于");

		TextView tv_version = (TextView) view.findViewById(R.id.helper_tv);

		tv_version.setText("版本号："
				+ ApplicationInfoUtil
						.getInstance(VMapApplication.getInstance())
						.getVersionName());

		Window dialogWindow = aboutDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);

		// lp.x = 100; // 新位置X坐标
		// lp.y = 100; // 新位置Y坐标

		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
		int width = 4 * localDisplayMetrics.widthPixels / 5;
		lp.width = width;
		// lp.height = 400; // 高度
		lp.alpha = 0.9f; // 透明度
		dialogWindow.setAttributes(lp);
		aboutDialog.show();
	}

}
