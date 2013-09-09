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
	/**正在更新的Geotable_id*/
	public static  int UPDATING_GEOTABLE_SEQ = 0;
	
	/***/
	public static  String[] geotable_id_pool;
	
	/**移动端 key*/
	public static final String CLIENT_AK = "FE95801d772e14d0b5ec69cb125ba77c";
	//public static final String mapKey = "81f8a6039d6819798e583732a8004b79";
	/**服务端key*/
	public static final String SERVER_AK =  "81f8a6039d6819798e583732a8004b79";
	//public static final String strKey = "FE95801d772e14d0b5ec69cb125ba77c";
	/**微信APP key*/
	public static final String WX_KEY = "wx6e3d98ab86eb8acd";
	/**请求区域*/
	public static final String REGION_REQUEST= "长沙市";
	/** 分享标题*/
	public static final String socialShareTitle = "雅驰电子湘行一卡通";
	/** 微信分享标题*/
	public static final String wxShareTitle = "来自雅驰电子的分享";
	/** 微信内容分享小题*/
	public static final String wxContentShareTitle = "雅驰湘行一卡通";
	/** 微信内容分享点击网址*/
	public static final String wxContentShareUrl =  "http://www.ycic.com.cn/";
	/** 数据库的网点集合*/
	public static  ArrayList<ArrayList<BranchModel>> cate_branchs;
	// 定位结果
	private static BDLocation currlocation = null;
	public static boolean IsEntered = false;

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
