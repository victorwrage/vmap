/** 
 * @Filename MapConstant.java 
 * @Description TODO 
 * @Version 1.0
 * @Author xiaoyl
 * @Creation 2013-8-14 下午4:15:02   
 * @Copyright Copyright © 2009 - 2013 Victor.All Rights Reserved.
**/
package com.victor.vmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
	private static HashMap<String, String> filterParams;	
	private static List<ContentModel> list = new ArrayList<ContentModel>();
	
	
	public static void setList(List<ContentModel> mList) {
		list = mList;
	}

	public static List<ContentModel> getList() {
		return list;
	}
	
	public static HashMap<String, String> getFilterParams() {
		return filterParams;
	}

	public static void setFilterParams(HashMap<String, String> mFilterParams) {
		filterParams = mFilterParams;
	}
}
