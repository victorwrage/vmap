/** 
 * @Filename MapConstant.java 
 * @Description TODO 
 * @Version 1.0
 * @Author xiaoyl
 * @Creation 2013-8-14 下午4:15:02   
 * @Copyright Copyright © 2009 - 2013 Victor.All Rights Reserved.
**/
package com.victor.vmap.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.baidu.location.BDLocation;
import com.victor.vmap.MainActivity;
import com.victor.vmap.control.BranchModel;
import com.victor.vmap.control.BranchRequestModel;

import android.app.Activity;

/** 
 * @ClassName MapConstant 
 * @Description TODO 
 * @Version 1.0
 * @Creation 2013-8-14 下午4:15:02 
 * @Mender xiaoyl
 * @Modification 2013-8-14 下午4:15:02 
 **/
public class MapConstant {
	//云检索参数
	private static String filterParams;	
	private static List<BranchModel> list = new ArrayList<BranchModel>();
	private static List<BranchRequestModel> request_list = new ArrayList<BranchRequestModel>();
	/**地图key*/
	public static final String strKey = "FE95801d772e14d0b5ec69cb125ba77c";
	
	// 定位结果
	private static BDLocation currlocation = null;
	
	/**
	 * @return the request_list
	 **/
	public static List<BranchRequestModel> getRequest_list() {
		return request_list;
	}

	/**
	 * @param request_list the request_list to set
	 **/
	public static void setRequest_list(List<BranchRequestModel> request_list) {
		MapConstant.request_list = request_list;
	}

	public static MainActivity mapActivity;
	public static void setList(List<BranchModel> mList) {
		list = mList;
	}

	public static List<BranchModel> getList() {
		return list;
	}
	
	public static String getFilterParams() {
		return filterParams;
	}

	public static void setFilterParams(String mFilterParams) {
		filterParams = mFilterParams;
	}

	/**
	 * @return the currlocation
	 **/
	public static BDLocation getCurrlocation() {
		return currlocation;
	}

	/**
	 * @param currlocation the currlocation to set
	 **/
	public static void setCurrlocation(BDLocation mCurrlocation) {
		currlocation = mCurrlocation;
	}
	
	
}
