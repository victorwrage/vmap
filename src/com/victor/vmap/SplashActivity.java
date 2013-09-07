package com.victor.vmap;

import java.util.ArrayList;
import java.util.List;

import com.victor.vmap.control.BranchModel;
import com.victor.vmap.control.BranchRequestModel;
import com.victor.vmap.control.FetchManager;
import com.victor.vmap.provider.BranchDbHelper;
import com.victor.vmap.utils.MapConstant;
import com.yachi.library_yachi.VToast;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SplashActivity extends Activity {
	private VMapApplication app;
	private Context context;
	private BranchDbHelper db_helper;
	/** 设置超时 */
	public static final int MSG_NET_TIMEOUT = 100;
	public static final int MSG_NET_STATUS_ERROR = 200;
	public static final int MSG_NET_SUCC = 1;
	/** 记录为空 */
	public static final int MSG_EMPTY = 300;
	private int count = 0;
	/*
	 * 处理网络请求
	 */
	private final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			count++;
			switch (msg.what) {
				case MSG_NET_TIMEOUT :
					break;
				case MSG_EMPTY :
					// VToast.toast(context, "记录为空");
					break;
				case MSG_NET_STATUS_ERROR :

					break;
				case MSG_NET_SUCC :

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
					if (count > 4) {
						finish();
						startActivity(new Intent(context, MainActivity.class));
					}
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
		String[] geotable_id_pool = getResources().getStringArray(
				R.array.geotable_ids);
		List<BranchRequestModel> request_pool = new ArrayList<BranchRequestModel>();

		request_pool.add(new BranchRequestModel(geotable_id_pool[0]));
		request_pool.add(new BranchRequestModel(geotable_id_pool[1]));
		request_pool.add(new BranchRequestModel(geotable_id_pool[2]));
		request_pool.add(new BranchRequestModel(geotable_id_pool[3]));

		MapConstant.setRequest_list(request_pool);

		FetchManager manager = FetchManager.getInstance(context);
		manager.startFetchData();

	}

}
