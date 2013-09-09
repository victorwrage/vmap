package com.victor.vmap;

import java.util.ArrayList;
import java.util.List;

import com.victor.vmap.control.BranchModel;
import com.victor.vmap.control.BranchRequestModel;
import com.victor.vmap.control.FetchManager;
import com.victor.vmap.control.VMapSharedPreference;
import com.victor.vmap.provider.BranchDbHelper;
import com.victor.vmap.utils.MapConstant;
import com.yachi.library_yachi.VLog;
import com.yachi.library_yachi.VToast;
import com.yachi.library_yachi.utils.VUtils;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SplashActivity extends BaseActivity {
	private VMapApplication app;
	private Context context;
	private BranchDbHelper db_helper;
	private VMapSharedPreference pref;
	/** 设置超时 */
	public static final int MSG_NET_TIMEOUT = 100;
	public static final int MSG_NET_STATUS_ERROR = 200;
	public static final int MSG_NET_SUCC = 1;
	/**联网失败，直接进入地图*/
	public static final int MSG_NET_FAILED = 400;
	/** 记录为空 */
	public static final int MSG_EMPTY = 300;

	/*
	 * 处理网络请求
	 */
	private final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
				case MSG_NET_TIMEOUT :
					// VToast.toast(context, "网络超时，网点更新失败！");
					VLog.e("fetch  geotable  data  timeout-"
							+ MapConstant.UPDATING_GEOTABLE_SEQ);
					break;
				case MSG_EMPTY :
					VLog.e("fetch  geotable  data  empty-"
							+ MapConstant.UPDATING_GEOTABLE_SEQ);
					break;
				case MSG_NET_STATUS_ERROR :
					// VToast.toast(context, "网络错误，网点更新失败！");
					VLog.e("fetch  geotable  data  error-"
							+ MapConstant.UPDATING_GEOTABLE_SEQ);
					break;
				case MSG_NET_SUCC :
					VLog.v("fetch  geotable  data  sucess-"
							+ MapConstant.UPDATING_GEOTABLE_SEQ);
				
					enterMapActivity();
					
					break;
				case MSG_NET_FAILED :
					VLog.v("fetch  geotable  data  sucess-"
							+ MapConstant.UPDATING_GEOTABLE_SEQ);
					//enterMapActivity();
					break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;

		app = (VMapApplication) VMapApplication.getInstance();
		app.addActivitys(this);
		app.setHandler(mHandler);
		setContentView(R.layout.activity_splash);
		db_helper = BranchDbHelper.getInstance(context);
		pref = VMapSharedPreference.getInstance(context);
		fetchBranchs();
		/*if (pref.isFirstEnter()) {//第一次初始化等待
			VToast.toast(context, R.string.tip_update_date_first);
		} else {
			pref.setFirstEnter(false);
			new initBranchs().execute("");
		}*/
		enterMapActivity();
	}

	/**
	 * 联网获取网点 
	 */
	private void fetchBranchs() {
		MapConstant.geotable_id_pool = getResources().getStringArray(R.array.geotable_ids);
		List<BranchRequestModel> request_pool = new ArrayList<BranchRequestModel>();

		request_pool.add(new BranchRequestModel(MapConstant.geotable_id_pool[0]));
		request_pool.add(new BranchRequestModel(MapConstant.geotable_id_pool[1]));
		request_pool.add(new BranchRequestModel(MapConstant.geotable_id_pool[2]));
		request_pool.add(new BranchRequestModel(MapConstant.geotable_id_pool[3]));

		MapConstant.setRequest_list(request_pool);

		FetchManager manager = FetchManager.getInstance(context);
		manager.startFetchData();
		
	}

	/**
	 * @Name onResume
	 * @Description TODO
	 * @see android.app.Activity#onResume()
	 * @Date 2013-9-7 下午4:38:45
	 **/
	@Override
	protected void onResume() {
		super.onResume();
		if (!VUtils.isConnect(context)) {
			VToast.toast(context, R.string.tip_update_net_error);
		}
	}

	/**
	 * 进入地图页面
	 * 
	 * @Name enterMapActivity
	 * @Description TODO
	 * 
	 */
	private void enterMapActivity() {
		startActivity(new Intent(context, MainActivity.class));
		finish();
	}

	/**
	 * 
	 * @ClassName initBranchs 
	 * @Description TODO 非首次进入初始化数据库
	 * @Version 1.0
	 * @Creation 2013-9-7 下午5:52:01 
	 * @Mender xiaoyl
	 * @Modification 2013-9-7 下午5:52:01 
	*
	 */
	private class initBranchs extends AsyncTask<String, Integer, String>{

		/** 
		 * @Name doInBackground
		 * @Description TODO 
		 * @param params
		 * @return
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 * @Date 2013-9-7 下午5:51:16
		**/
		@Override
		protected String doInBackground(String... params) {
			
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

			enterMapActivity();
			
			return null;
		}
	}
}
