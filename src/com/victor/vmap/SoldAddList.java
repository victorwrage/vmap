/** 
 * @Filename SoldAddList.java 
 * @Description TODO 
 * @Version 1.0
 * @Author xiaoyl
 * @Creation 2013-8-14 下午5:29:37   
 * @Copyright Copyright © 2009 - 2013 Victor.All Rights Reserved.
**/
package com.victor.vmap;

import java.util.HashMap;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockListActivity;
import com.baidu.lbs.duanzu.DemoApplication;
import com.baidu.lbs.duanzu.bdapi.LBSCloudSearch;

/** 
 * @ClassName SoldAddList 
 * @Description TODO 
 * @Version 1.0
 * @Creation 2013-8-14 下午5:29:37 
 * @Mender xiaoyl
 * @Modification 2013-8-14 下午5:29:37 
 **/
public class SoldAddList extends SherlockListActivity {

	/** 
	 * @Name onCreate
	 * @Description TODO 
	 * @param savedInstanceState
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * @Date 2013-8-14 下午5:31:10
	**/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadMoreData();
		
	}

	/**
	 * 加载更多数据
	 */
	private void loadMoreData() {
		HashMap<String, String> filterParams = VMapApplication.getInstance().getFilterParams();
		filterParams.put("page_index", (list.size()/10 + 1) + "");
		// search type 为 -1，将保持当前的搜索类型
		LBSCloudSearch.request(-1,filterParams, DemoApplication.getInstance().getHandler(), DemoApplication.networkType);
	}
	
	
}
