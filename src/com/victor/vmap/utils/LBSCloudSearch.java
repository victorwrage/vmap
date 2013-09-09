package com.victor.vmap.utils;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.victor.vmap.R;
import com.victor.vmap.SplashActivity;
import com.victor.vmap.VMapApplication;
import com.victor.vmap.control.BranchModel;
import com.victor.vmap.provider.BranchDbHelper;
import com.yachi.library_yachi.VLog;
import com.yachi.library_yachi.VToast;
import com.yachi.library_yachi.utils.HttpRequest;
import com.yachi.library_yachi.utils.VUtils;

import android.os.Handler;
import android.os.Message;

/**
 * 百度云检索使用类
 * 
 * @author Lu.Jian
 * 
 */
public class LBSCloudSearch {

	// 百度云检索API URI
	private static final String SEARCH_URI_NEARBY = "http://api.map.baidu.com/geosearch/v2/nearby?";
	private static final String SEARCH_URI_LOCAL = "http://api.map.baidu.com/geosearch/v2/local?";

	public static final int SEARCH_TYPE_NEARBY = 1;
	public static final int SEARCH_TYPE_LOCAL = 2;

	private static int TIME_OUT = 12000;
	private static int retry = 3;
	private static boolean IsBusy = false;

	/**
	 * 云检索访问
	 * 
	 * @param filterParams
	 *            访问参数，key为filter时特殊处理。
	 * @param handler
	 *            数据回调Handler
	 * @param networkType
	 *            手机联网类型
	 * @return
	 */
	public static boolean request(String requestURL) {
		if (IsBusy)
			return false;
		IsBusy = true;
		Handler handler = ((VMapApplication) VMapApplication.getInstance())
				.getHandler();
		int count = retry;
		while (count > 0) {
			try {
				if (!requestURL.startsWith("http")) {
					requestURL = SEARCH_URI_LOCAL + requestURL;
				}
				VLog.v("request url:" + requestURL);

				HttpRequest request = HttpRequest.get(HttpRequest
						.encode(requestURL));
				request.acceptJson();

				request.connectTimeout(TIME_OUT);
				if (VMapApplication.getInstance().networkType.equals("cmwap")) {
					request.useProxy("10.0.0.172", 80);
				} else if (VMapApplication.getInstance().networkType
						.equals("ctwap")) {
					request.useProxy("10.0.0.200", 80);
				}
				String result = request.body();
				int status = request.code();
				if (status == HttpURLConnection.HTTP_OK) {

					// msgTmp.obj = result;

					JSONObject json = new JSONObject(result);
					if (json.getString("status").equals("102")) {
						VLog.e("LBS密钥验证错误");
						handler.sendEmptyMessage(SplashActivity.MSG_NET_FAILED);
						return false;
					}
					Message msgTmp = handler
							.obtainMessage(SplashActivity.MSG_NET_SUCC);
					Utils.parser(json);
					updateDatabase();
					if (MapConstant.UPDATING_GEOTABLE_SEQ>4) {
						msgTmp.sendToTarget();
						MapConstant.mapActivity.refreshMapView();
					}
					break;
				} else {
					Message msgTmp = handler
							.obtainMessage(SplashActivity.MSG_NET_STATUS_ERROR);
					msgTmp.obj = "HttpStatus error";
					msgTmp.sendToTarget();

				}
			} catch (Exception e) {
				if (!VUtils.isConnect(VMapApplication.getInstance()
						.getApplicationContext())) {
					VLog.e("网络未连接");
				}
				handler.sendEmptyMessage(SplashActivity.MSG_NET_FAILED);
				e.printStackTrace();
				return false;

			}
			count--;
		}
		if (count <= 0 && handler != null) {
			Message msgTmp = handler
					.obtainMessage(SplashActivity.MSG_NET_TIMEOUT);
			if(MapConstant.UPDATING_GEOTABLE_SEQ>4){
			  msgTmp.sendToTarget();
			}
		}
		IsBusy = false;
		return true;
	}

	/**
	 * 更新/插入数据库
	 */
	private static void updateDatabase() {
		BranchDbHelper db_helper = BranchDbHelper.getInstance(VMapApplication
				.getInstance().getApplicationContext());
		for (BranchModel item : MapConstant.getList()) {
			if (db_helper.existCardId(item.getUid())) {
				db_helper.updateBranchItem(item);
			} else {
				db_helper.insertBranchItem(item);
			}
		}

		List<BranchModel> db_items = db_helper.getBranchs();
		if (db_items.size() > MapConstant.getList().size()) {
			for (BranchModel item : db_items) {
				if (db_helper.existCardId(item.getUid())) {
					db_helper.deleteTagItem(item);
				}
			}
		}
		MapConstant.cate_branchs = new ArrayList<ArrayList<BranchModel>>();
		ArrayList<BranchModel> listA = db_helper
				.getBranchsByGeotableId(MapConstant.geotable_id_pool[0]);
		ArrayList<BranchModel> listB = db_helper
				.getBranchsByGeotableId(MapConstant.geotable_id_pool[1]);
		ArrayList<BranchModel> listC = db_helper
				.getBranchsByGeotableId(MapConstant.geotable_id_pool[2]);
		ArrayList<BranchModel> listD = db_helper
				.getBranchsByGeotableId(MapConstant.geotable_id_pool[3]);
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
		MapConstant.cate_branchs.add(listA);
		MapConstant.cate_branchs.add(listB);
		MapConstant.cate_branchs.add(listC);
		MapConstant.cate_branchs.add(listD);
	}

}
