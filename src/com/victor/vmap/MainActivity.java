package com.victor.vmap;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemSelectedListener;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {
	private Context context;
	public static final int MSG_NET_TIMEOUT = 100;
	public static final int MSG_NET_STATUS_ERROR = 200;
	public static final int MSG_NET_SUCC = 1;
	/*
	 * 处理网络请求
	 */
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			progress.setVisibility(View.INVISIBLE);
			switch (msg.what) {
			case MSG_NET_TIMEOUT:
				break;
			case MSG_NET_STATUS_ERROR:
				break;
			case MSG_NET_SUCC:
			
				String result = msg.obj.toString();
				try {
					JSONObject json = new JSONObject(result);
					//parser(json);
				} catch (JSONException e) {
				
					e.printStackTrace();
				}
				break;

			}
		}
	};
	private RelativeLayout progress;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		VMapApplication app = (VMapApplication) VMapApplication.getInstance();
		app.addActivitys(this);
		UmengUpdateAgent.update(MainActivity.this);
		MobclickAgent.onError(this);
         
		setContentView(R.layout.main);
		initTabHost();
		progress = (RelativeLayout) findViewById(R.id.progress);
		initSpinner();
		getRequestParams();
		
		app.setHandler(mHandler);
		// 启动定位
		LBSLocation.getInstance(app).startLocation();
		
	}
	
	private void initTabHost() {
		final TabHost tabHost = getTabHost();

		// 添加列表tab和地图tab
		tabHost.addTab(tabHost.newTabSpec("tab1")
				.setIndicator(getString(R.string.action_label_nearby))
				.setContent(new Intent(this, ActivityMapView.class)));

		TabWidget tabWidget = tabHost.getTabWidget();

		// 将tab的图文组合改为文字显示并调整tab高度
		for (int i = 0; i < tabWidget.getChildCount(); i++) {
			View child = tabWidget.getChildAt(i);

			final TextView tv = (TextView) child
					.findViewById(android.R.id.title);

			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv
					.getLayoutParams();
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0); // 取消文字底边对齐
			params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE); // 设置文字居中对齐

			child.getLayoutParams().height = 60; // hard code
		}

	}
	
	private void initSpinner() {

		final Spinner s1 = (Spinner) findViewById(R.id.spinner1);
		final Spinner s2 = (Spinner) findViewById(R.id.spinner2);

		// 设置区域过滤下拉框
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.sections, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s1.setAdapter(adapter);
		s1.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		// 设置距离过滤下拉框
		adapter = ArrayAdapter.createFromResource(this, R.array.distance,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s2.setAdapter(adapter);
		s2.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}
	
	/*
	 * 添加对back按钮的处理，点击提示退出
	 * (non-Javadoc)
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
	 * 获取云检索参数
	 */
	private HashMap<String, String> getRequestParams() {
		HashMap<String, String> map = new HashMap<String, String>();

		Spinner s1 = (Spinner) findViewById(R.id.spinner1);
		Spinner s2 = (Spinner) findViewById(R.id.spinner2);


		try {
			map.put("region", URLEncoder.encode("长沙", "utf-8"));

			String filter = "";


				// 附件，周边搜索
				RadioGroup filter2 = (RadioGroup) findViewById(R.id.filter2);
				RadioButton rb = (RadioButton) findViewById(filter2
						.getCheckedRadioButtonId());
				String radius = rb.getText().toString();
				radius = radius.substring(0, radius.length() - 1);
				map.put("radius", radius);

				VMapApplication app = (VMapApplication) VMapApplication.getInstance();
				if (app.currlocation != null) {
					map.put("location", app.currlocation.getLongitude() + ","
							+ app.currlocation.getLatitude());
				} else {
					// 无定位数据默认北京中心
					double cLat = 39.909230;
					double cLon = 116.397428;
					map.put("location", cLat + "," + cLon);
				}
			

			map.put("filter", filter);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NotFoundException e) {
			e.printStackTrace();
		}

		MapConstant.setFilterParams(map);

		return map;
	}
	
	/**
	 * @Name onPause
	 * @Description TODO
	 * @see android.app.ActivityGroup#onPause()
	 * @Date 2013-8-14 下午2:04:29
	 **/
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * @Name onResume
	 * @Description TODO
	 * @see android.app.ActivityGroup#onResume()
	 * @Date 2013-8-14 下午2:04:43
	 **/
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	/*
	 * 退出应用程序
	 */
	private void exit() {
		new AlertDialog.Builder(MainActivity.this)
				.setMessage(R.string.msg_exit)
				.setPositiveButton(R.string.msg_ok,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int whichButton) {
								finish();
								android.os.Process
										.killProcess(android.os.Process.myPid());
							}
						}).setNegativeButton(R.string.msg_cancel, null)
				.show();
	}

}
