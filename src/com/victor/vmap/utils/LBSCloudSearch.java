package com.victor.vmap.utils;

import java.net.HttpURLConnection;
import org.json.JSONObject;
import com.victor.vmap.SplashActivity;
import com.victor.vmap.VMapApplication;
import com.yachi.library_yachi.VLog;
import com.yachi.library_yachi.utils.HttpRequest;
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
	public static  boolean request(String requestURL) {
		if (IsBusy)
			return false;
		IsBusy = true;
		Handler handler = ((VMapApplication) VMapApplication.getInstance())
				.getHandler();
		int count = retry;
		while (count > 0) {
			try {
                 if(!requestURL.startsWith("http")){
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
					Message msgTmp = handler
							.obtainMessage(SplashActivity.MSG_NET_SUCC);
					// msgTmp.obj = result;

					JSONObject json = new JSONObject(result);
					Utils.parser(json);
					msgTmp.sendToTarget();

					break;
				} else {
					Message msgTmp = handler
							.obtainMessage(SplashActivity.MSG_NET_STATUS_ERROR);
					msgTmp.obj = "HttpStatus error";
					msgTmp.sendToTarget();
				}
			} catch (Exception e) {
				handler.sendEmptyMessage(SplashActivity.MSG_NET_STATUS_ERROR);
				VLog.e("网络异常，请检查网络后重试！");
				e.printStackTrace();
			}
			count--;
		}
		if (count <= 0 && handler != null) {
			Message msgTmp = handler
					.obtainMessage(SplashActivity.MSG_NET_TIMEOUT);
			msgTmp.sendToTarget();
		}
		IsBusy = false;
		return true;
	}

}
