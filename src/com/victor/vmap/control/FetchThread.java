package com.victor.vmap.control;

import com.victor.vmap.utils.LBSCloudSearch;


/**
 * @firm 长沙江泓信息技术有限公司
 * 
 * @author xiaoyl
 * @date 2013-07-19
 * 
 * @file 事物执行线程
 * 
 */
public  class FetchThread extends Thread {
	private BranchRequestModel item;

	public FetchThread(BranchRequestModel item) {
		this.item = item;	
	}

	@Override
	public void run() {
		LBSCloudSearch.request(item.getRequest_url());
		
	}
}
