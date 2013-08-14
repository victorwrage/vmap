
package com.victor.vmap;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.yachi.library_yachi.VLog;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

/** 
 * @ClassName:	ActivityAbout 
 * @Description:TODO(关于界面) 
 * @author:	xiaoyl
 * @date:	2013-7-20 下午6:27:27 
 *  
 */
public class ActivityAbout extends SherlockActivity {
	public static final String[] DIALOGUE = new String[1];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView( R.layout.helper_lay);

		TextView tv = (TextView) findViewById(R.id.helper_tv);
		try {
			DIALOGUE[0] = "版本号：" + getVersionName() ;

		} catch (Exception e) {
			VLog.e("获取版本出错");
		}
		StringBuilder builder = new StringBuilder();

		for (String dialog : DIALOGUE) {
			builder.append(dialog).append("\n\n");
		}

		tv.setText(builder.toString());

    	getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("关于");
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return false;
	}
	
	/**
	 * 版本号
	 * 
	 * @return
	 * @throws Exception
	 */
	private String getVersionName() throws Exception {
		// 获取packagemanager的实例
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),
				0);
		String version = packInfo.versionName;
		return version;
	}

}
