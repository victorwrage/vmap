package com.victor.vmap.control;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * @firm 长沙江泓信息技术有限公司
 * 
 * @author xiaoyl
 * @date 2013-07-20
 * 
 * @file 小数据存读Preference操作类
 * 
 */
public class VMapSharedPreference {
	/** 小数据配置存储类*/
	private static VMapSharedPreference instance;
	
	private Context context;
	/** KEY 保存是否初始化*/
	private static final String FIRST_ENTER = "first_enter";
       
       /**
   	 * 构造方法
   	 * @param context
   	 */
   	private VMapSharedPreference(Context context) {
   		this.context = context;
   	}

   	/**
   	 * 获得GoMarketPreferences静态实例对象
   	 * @param context
   	 * @return
   	 */
   	public static VMapSharedPreference getInstance(Context context) {
   		if (instance == null) {
   			instance = new VMapSharedPreference(context);
   		}
   		return instance;
   	}
   	/**
	 * get SharedPreferences
	 * @return
	 */
	private SharedPreferences getSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
	/**
	 * get Editor
	 * @return
	 */
	private Editor getEditor() {
		SharedPreferences pref = getSharedPreferences();
		return pref.edit();
	}
   	/**
	 * 是否未初始化数据
	 * @return
	 */
	public boolean isFirstEnter() {
		SharedPreferences pref = getSharedPreferences();
		return pref.getBoolean(FIRST_ENTER, true);

	}
	/**
	 * 设置是否未初始化数据
	 * @return
	 */
	public boolean setFirstEnter(boolean b) {
		Editor editor = getEditor();
		editor.putBoolean(FIRST_ENTER, b);
		return editor.commit();
	}
}
