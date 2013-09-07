package com.victor.vmap;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MKOLUpdateElement;
import com.baidu.mapapi.map.MKOfflineMap;
import com.baidu.mapapi.map.MKOfflineMapListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.UMSsoHandler;
import com.umeng.socialize.controller.UMWXHandler;
import com.umeng.socialize.media.UMImage;
import com.umeng.update.UmengUpdateAgent;
import com.victor.vmap.control.BranchModel;
import com.victor.vmap.provider.BranchDbHelper;
import com.victor.vmap.provider.SearchProvider;
import com.victor.vmap.utils.LBSCloudSearch;
import com.victor.vmap.utils.MapConstant;
import com.victor.vmap.utils.ScreenShot;
import com.victor.vmap.utils.SearchUtils;
import com.victor.vmap.utils.SearchUtils.Word;
import com.victor.vmap.utils.Utils;
import com.yachi.library_yachi.VLog;
import com.yachi.library_yachi.VToast;
import com.yachi.library_yachi.utils.ApplicationInfoUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.SearchRecentSuggestions;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnGenericMotionListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	private VMapApplication app;
	private Context context;
	/** 过滤后的网点集合 */
	private ArrayList<ArrayList<BranchModel>> cate_branchs;
	private UMSocialService controller;
	private BranchDbHelper db_helper;
	private BMapManager mBMapManager = null;
	private Dialog loading;
	private MapView mMapView = null;
	/** 关于、视图、设置 对话框 */
	private Dialog aboutDialog, layer_Dialog, setting_Dialog;
	/** 描点标记、定位标记 */
	private OverlayTest ov_markA, ov_markB, ov_markC, ov_markD, ovt_location;
	private PopupOverlay pop;

	private EditText etdata;
	private Button btnsearch;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		MapConstant.mapActivity = this;
		db_helper = BranchDbHelper.getInstance(context);
		app = (VMapApplication) VMapApplication.getInstance();
		app.addActivitys(this);

		UmengUpdateAgent.update(this);// 加入更新
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		MobclickAgent.onError(this);// 加入出错报告

		initEngineManager(this);

		setContentView(R.layout.activity_main);
		resolveIntent(getIntent());
		initBranchs();
		initView();
		initOffLineMap();

	}

	
	/** 
	 * @Name resolveIntent 
	 * @Description TODO 
	 * @param intent 
	**/
	private void resolveIntent(Intent intent) {
		VLog.v("resolveIntent--"+intent.getAction());
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			SearchUtils.Word theWord = SearchUtils.getInstance().getMatches(
			intent.getDataString().trim().toLowerCase()).get(0);
			launchWord(theWord);
            finish();
		}else{
			
			Button search_btn = (Button) findViewById(R.id.btncall);
			search_btn.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					onSearchRequested();
					return false;
				}
			});
		}
	}
     
	

	/** 
	 * @Name launchWord 
	 * @Description TODO 
	 * @param theWord 
	**/
	private void launchWord(Word theWord) {
		
		
	}


	/**
	 * @Name initBranchs
	 * @Description TODO
	 **/
	private void initBranchs() {
		cate_branchs = new ArrayList<ArrayList<BranchModel>>();
		ArrayList<BranchModel> listA = db_helper
				.getBranchsByGeotableId("31930");
		ArrayList<BranchModel> listB = db_helper
				.getBranchsByGeotableId("32425");
		ArrayList<BranchModel> listC = db_helper
				.getBranchsByGeotableId("31669");
		ArrayList<BranchModel> listD = db_helper
				.getBranchsByGeotableId("32426");
		if (listA == null) {
			listA = new ArrayList<BranchModel>();
		}
		if (listB == null) {
			listB = new ArrayList<BranchModel>();
		}
		if (listC == null) {
			listC = new ArrayList<BranchModel>();
		}
		if (listD == null) {
			listD = new ArrayList<BranchModel>();
		}
		cate_branchs.add(listA);
		cate_branchs.add(listB);
		cate_branchs.add(listC);
		cate_branchs.add(listD);

	}

	/**
	 * @Name initOffLineMap
	 * @Description TODO
	 **/
	private void initOffLineMap() {
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

	@Override
	public void onPause() {
		mMapView.onPause();
		if (mBMapManager != null) {
			mBMapManager.stop();
		}
		mMapView.setKeepScreenOn(false);
		removeAllMarker();
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
		removeAllMarker();
		addAllMarker();
	}

	/**
	 * @Name initSocialShare
	 * @Description TODO
	 **/
	private void initSocialShare(Bitmap arg0) {
		controller = UMServiceFactory.getUMSocialService(
				MainActivity.class.getName(), RequestType.SOCIAL);
		controller.setShareContent("雅驰电子湘行一卡通");// 设置分享文字内容
		controller.setShareMedia(new UMImage(MainActivity.this, arg0));// 设置分享图片内容
		UMWXHandler.WX_APPID = "wx6e3d98ab86eb8acd";// 设置微信的Appid

		// 添加微信平台
		controller.getConfig().supportWXPlatform(MainActivity.this);

		// 添加微信朋友圈
		controller.getConfig().supportWXPlatform(
				MainActivity.this,
				UMServiceFactory.getUMWXHandler(MainActivity.this).setToCircle(
						true));

		UMWXHandler.CONTENT_URL = "http://www.ycic.com.cn/";// 微信图文分享必须设置一个url
															// 默认"http://www.umeng.com"
		UMWXHandler.WX_CONTENT_TITLE = "来自雅驰电子的分享";
		UMWXHandler.WXCIRCLE_CONTENT_TITLE = "雅驰湘行一卡通";
		controller.openShare(MainActivity.this, false);

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
			VToast.toast(context, "mBMapManager  初始化错误!");
		}
	}

	/**
	 * @Name initView
	 * @Description TODO 初始化布局中的视图
	 **/
	private void initView() {
		mMapView = (MapView) findViewById(R.id.bmapsView);
		ImageView location_iv = (ImageView) findViewById(R.id.main_focus_iv);
		location_iv.setOnClickListener(this);
		mMapView.setBuiltInZoomControls(true);
		mMapView.regMapViewListener(mBMapManager, new MapShotCutListener());
		MapController mMapController = mMapView.getController();
		mMapController.setZoom(12);
		ImageView setting = (ImageView) findViewById(R.id.main_setting_iv);
		setting.setOnClickListener(this);


		etdata = (EditText) findViewById(R.id.etdata);
		btnsearch = (Button) findViewById(R.id.btncall);
		btnsearch.setOnClickListener(this);

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
		new AlertDialog.Builder(MainActivity.this)
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

	/**
	 * 删除所有标记
	 */
	public void removeAllMarker() {
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

		addMarker(0);
		addMarker(1);
		addMarker(2);
		addMarker(3);

		focusMarker();
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
		} else if (cate_branchs != null && cate_branchs.size() >= 1) {
			for (ArrayList<BranchModel> items : cate_branchs) {
				if (items.size() > 0) {
					BranchModel c = (BranchModel) items.get(0);
					int currLat = (int) (Double.valueOf(c.getLatitude()) * 1000000);
					int currLon = (int) (Double.valueOf(c.getLongitude()) * 1000000);
					mMapView.getController().setCenter(
							new GeoPoint(currLat, currLon));
					break;
				}
			}
		}
	}

	/**
	 * @Name addMarker
	 * @Description TODO
	 **/
	private void addMarker(int index) {
		OverlayTest ov_mark = ov_markA;
		int marker_res = R.drawable.icon_gcoding;
		switch (index) {
			case 0 :
				ov_mark = ov_markA;
				marker_res = R.drawable.icon_share_loc_add;
				break;
			case 1 :
				ov_mark = ov_markB;
				// marker_res = R.drawable.icon_share_loc_add;
				break;
			case 2 :
				ov_mark = ov_markC;
				marker_res = R.drawable.icon_mylocal_favd;
				break;
			case 3 :
				ov_mark = ov_markD;
				marker_res = R.drawable.icon_share_loc_add;
				break;
		}
		if (cate_branchs != null && cate_branchs.size() != 0) {
			if (ov_mark != null) {
				mMapView.getOverlays().remove(ov_mark);
			}
			ov_mark = new OverlayTest(null, mMapView, false);

			for (BranchModel content : cate_branchs.get(index)) {
				int latitude = (int) (Double.valueOf(content.getLatitude()) * 1000000);
				int longitude = (int) (Double.valueOf(content.getLongitude()) * 1000000);

				Drawable d = getResources().getDrawable(marker_res);
				OverlayItem item = new OverlayItem(new GeoPoint(latitude,
						longitude), content.getName(), content.getAddr());
				item.setMarker(d);
				ov_mark.addItem(item);
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

		mMapView.getController().setCenter(new GeoPoint(lat, lon));
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

			showDetail(getItem(index).getTitle());
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
	private void showBottom() {
		View v = View.inflate(context, R.layout.bottom_menu, null);

		PopupWindow window = new PopupWindow(v, 500, 260);
		window.setOutsideTouchable(true);

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
	 * 弹出该点窗口
	 * 
	 * @Name showDetail
	 * @Description TODO
	 * 
	 */
	private void showDetail(String title) {
		View v = View.inflate(context, R.layout.point_detail, null);

		Button btn_left = (Button) v.findViewById(R.id.point_left_btn);
		btn_left.setOnClickListener(this);
		TextView tv_title = (TextView) v.findViewById(R.id.point_detail_tv);
		tv_title.setText(title);
		PopupWindow window = new PopupWindow(v, 500, 260);

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
			case R.id.main_focus_iv :

				int lat = (int) (MapConstant.getCurrlocation().getLatitude() * 1000000);
				int lon = (int) (MapConstant.getCurrlocation().getLongitude() * 1000000);
				VLog.v("focusLocation-" + lat + "," + lon);
				mMapView.getController().setCenter(new GeoPoint(lat, lon));
				break;
			case R.id.main_setting_iv :
				showSetting();
				break;
			case R.id.setting_frame_btn :
				showLayer();
				setting_Dialog.dismiss();
				break;
			case R.id.setting_about_btn :
				setting_Dialog.dismiss();
				showAbout();
				break;
			case R.id.point_left_btn :// 分享
				mMapView.getCurrentMap();
				showLoading();
				break;
			case R.id.btncall :// 搜索
				

				break;
		}
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
		loading.setCancelable(false);
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
				switch (position) {
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
				VToast.toast(context, "您的网络出错啦！");
			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				VToast.toast(context, "输入正确的检索条件！");
			}
		}

		@Override
		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				VToast.toast(context, "请输入正确的授权Key！");
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
			// TODO Auto-generated method stub

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
			// TODO Auto-generated method stub
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
			// TODO Auto-generated method stub

		}

		/**
		 * @Name onMapLoadFinish
		 * @Description TODO
		 * @see com.baidu.mapapi.map.MKMapViewListener#onMapLoadFinish()
		 * @Date 2013-9-3 上午10:10:05
		 **/
		@Override
		public void onMapLoadFinish() {
			// TODO Auto-generated method stub

		}

		/**
		 * @Name onMapMoveFinish
		 * @Description TODO
		 * @see com.baidu.mapapi.map.MKMapViewListener#onMapMoveFinish()
		 * @Date 2013-9-3 上午10:10:05
		 **/
		@Override
		public void onMapMoveFinish() {
			// TODO Auto-generated method stub

		}

	}

}
