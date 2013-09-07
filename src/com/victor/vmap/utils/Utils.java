/** 
 * @Filename Utils.java 
 * @Description TODO 
 * @Version 1.0
 * @Author xiaoyl
 * @Creation 2013-9-2 上午9:52:23   
 * @Copyright Copyright © 2009 - 2013 Victor.All Rights Reserved.
 **/
package com.victor.vmap.utils;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;

import com.victor.vmap.SplashActivity;
import com.victor.vmap.VMapApplication;
import com.victor.vmap.control.BranchModel;
import com.yachi.library_yachi.VLog;
import com.yachi.library_yachi.VToast;

/**
 * @ClassName Utils
 * @Description TODO
 * @Version 1.0
 * @Creation 2013-9-2 上午9:52:23
 * @Mender xiaoyl
 * @Modification 2013-9-2 上午9:52:23
 **/
public class Utils {

	/*
	 * 解析返回数据
	 */
	public static void parser(JSONObject json) {
		VMapApplication app = (VMapApplication) VMapApplication.getInstance();
		List<BranchModel> list = MapConstant.getList();

		VLog.v("parser--json" + json.toString());

		try {
			JSONArray jsonArray = json.getJSONArray("contents");
			if (jsonArray != null && jsonArray.length() <= 0) {
				app.getHandler().sendEmptyMessage(
						SplashActivity.MSG_NET_TIMEOUT);
			} else {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
					BranchModel content = new BranchModel();

					content.setUid(jsonObject2.getString("uid"));
					content.setGeotable_id(jsonObject2.getString("geotable_id"));
					content.setProvince(jsonObject2.getString("province"));
					content.setCity(jsonObject2.getString("city"));
					content.setDistrict(jsonObject2.getString("district"));
					content.setCreate_time(jsonObject2.getString("create_time"));

					if (jsonObject2.has("modify_time")) {
						content.setModify_time(jsonObject2
								.getString("modify_time"));
					}

					if (jsonObject2.has("image_url")) {
						content.setImageurl(jsonObject2.getString("image_url"));
					}
					if (jsonObject2.has("web_url")) {
						content.setWebUrl(jsonObject2.getString("web_url"));
					}

					content.setName(jsonObject2.getString("title"));
					content.setAddr(jsonObject2.getString("address"));
					content.setDistance(jsonObject2.getString("distance") + "米");
					JSONArray locArray = jsonObject2.getJSONArray("location");
					double latitude = locArray.getDouble(1);
					double longitude = locArray.getDouble(0);
					content.setLatitude(String.valueOf(latitude));
					content.setLongitude(String.valueOf(longitude));
					float results[] = new float[1];
					if (MapConstant.getCurrlocation() != null) {
						Location.distanceBetween(MapConstant.getCurrlocation()
								.getLatitude(), MapConstant.getCurrlocation()
								.getLongitude(), latitude, longitude, results);
					}
					content.setDistance((int) results[0] + "米");

					switch (Integer.parseInt(content.getGeotable_id())) {
						case 31930 :
							content.setBranch_type(0);
							break;
						case 32425 :
							content.setBranch_type(1);
							break;
						case 31669 :
							content.setBranch_type(2);
							break;
						case 32426 :
							content.setBranch_type(3);
							break;

					}
					list.add(content);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
