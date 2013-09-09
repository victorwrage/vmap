package com.victor.vmap.provider;

import java.util.ArrayList;
import java.util.Vector;

import com.victor.vmap.control.BranchModel;
import com.yachi.library_yachi.VLog;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * @firm 长沙江泓信息技术有限公司
 * 
 * @author xiaoyl
 * @date 2013-07-13
 * 
 * @file DBhelper
 * 
 */
public class BranchDbHelper {
	private Context context;
	private ContentResolver contentResolver;
	private static BranchDbHelper instance;

	/**
	 * 构造方法
	 * 
	 * @param context
	 */
	private BranchDbHelper(Context context) {
		this.context = context;
		try {
			contentResolver = this.context.getContentResolver();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			contentResolver = this.context.getContentResolver();
		}
	}

	/**
	 * get TagsDbHelper静态实例对象
	 * 
	 * @param context
	 * @return
	 */
	public static BranchDbHelper getInstance(Context context) {
		if (instance == null) {
			VLog.e("DBHelp getInstance");
			instance = new BranchDbHelper(context);
		}
		return instance;
	}

	/**
	 * 查找是否表中含有此UID
	 * 
	 * @return
	 */
	synchronized public boolean existCardId(String uid) {
		boolean result = false;
		String where = BranchDataProvider.UID + "= '" + uid + "'";
		Cursor cursor = contentResolver.query(
				BranchDataProvider.CONTENT_URI_BRANCHMANAGE, new String[]{},
				where, null, null);
		if (cursor != null) {
			result = cursor.moveToFirst();
			cursor.close();
			cursor = null;
		}
		VLog.v("existBranch---" + result + uid);
		return result;
	}

	/**
	 * 数据库插入Tag
	 * 
	 * @param item
	 * @return
	 */
	synchronized public boolean insertBranchItem(BranchModel item) {
		boolean result = false;
		ContentValues initValues = new ContentValues();
		initValues.put(BranchDataProvider.UID, item.getUid());
		initValues.put(BranchDataProvider.BRANCH_NAME, item.getName());
		initValues.put(BranchDataProvider.GEOTABLE_ID, item.getGeotable_id());
		initValues.put(BranchDataProvider.BRANCH_TYPE, item.getBranch_type());
		initValues.put(BranchDataProvider.LATITUDE, item.getLatitude());

		initValues.put(BranchDataProvider.LONGITUDE, item.getLongitude());
		initValues.put(BranchDataProvider.BRANCH_ADD, item.getAddr());

		initValues.put(BranchDataProvider.PROVINCE, item.getProvince());
		initValues.put(BranchDataProvider.CITY, item.getCity());
		initValues.put(BranchDataProvider.DISTRICT, item.getDistrict());

		initValues.put(BranchDataProvider.CREATE_TIME, item.getCreate_time());
		initValues.put(BranchDataProvider.MODIFY_TIME, item.getModify_time());

		initValues.put(BranchDataProvider.IMAGE_URL, item.getImageurl());

		initValues.put(BranchDataProvider.WEB_URL, item.getWebUrl());
		initValues.put(BranchDataProvider.STRING_PARAMS, "");
		Uri uri = contentResolver.insert(
				BranchDataProvider.CONTENT_URI_BRANCHMANAGE, initValues);
		if (uri != null) {
			result = true;
		}
		VLog.e("db insertBranch " + result + item.getName());
		return result;
	}

	/**
	 * 数据库更新Tag
	 * 
	 * @param item
	 * @return
	 */
	synchronized public boolean updateBranchItem(BranchModel item) {
		boolean result = false;
		String where = BranchDataProvider.UID + "=" + item.getUid();
		ContentValues initValues = new ContentValues();

		initValues.put(BranchDataProvider.BRANCH_NAME, item.getName());
		initValues.put(BranchDataProvider.BRANCH_TYPE, item.getBranch_type());
		initValues.put(BranchDataProvider.BRANCH_ADD, item.getAddr());
		initValues.put(BranchDataProvider.IMAGE_URL, item.getImageurl());

		initValues.put(BranchDataProvider.LATITUDE, item.getLatitude());

		initValues.put(BranchDataProvider.LONGITUDE, item.getLongitude());
		initValues.put(BranchDataProvider.PROVINCE, item.getProvince());
		initValues.put(BranchDataProvider.CITY, item.getCity());
		initValues.put(BranchDataProvider.CREATE_TIME, item.getModify_time());
		initValues.put(BranchDataProvider.MODIFY_TIME, item.getModify_time());
		initValues.put(BranchDataProvider.WEB_URL, item.getWebUrl());
		int c = contentResolver.update(
				BranchDataProvider.CONTENT_URI_BRANCHMANAGE, initValues, where,
				null);
		if (c != 0) {
			result = true;
		}
		VLog.e("db updateBranch " + result);
		return result;
	}

	/**
	 * 数据库删除Tag
	 * 
	 * @param item
	 * @return
	 */
	synchronized public void deleteTagItem(BranchModel item) {
		String where = BranchDataProvider.UID + "= '" + item.getUid() + "'";
		int i = contentResolver.delete(
				BranchDataProvider.CONTENT_URI_BRANCHMANAGE, where, null);
		VLog.e("db deleteBranch " + i);
	}

	/**
	 * 获取BranchModel
	 * 
	 * @return
	 */
	synchronized public ArrayList<BranchModel> getBranchs() {
		ArrayList<BranchModel> items = new ArrayList<BranchModel>();
		BranchModel item = null;
		String orderBy = BranchDataProvider.CREATE_TIME + " desc";
		Cursor cursor = contentResolver.query(
				BranchDataProvider.CONTENT_URI_BRANCHMANAGE, new String[]{},
				null, null, orderBy);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					item = builtBranchItem(cursor);
					items.add(item);
				} while (cursor.moveToNext());
			}
			cursor.close();
			cursor = null;
		}
		VLog.v("db getBranchs " + items.size());
		return items;
	}

	/**
	 * 获取BranchModel
	 * 
	 * @return
	 */
	synchronized public BranchModel getBranchsByName(String uid) {
		BranchModel item = null;
		String where = uid + " =  " + BranchDataProvider.UID;

		Cursor cursor = contentResolver.query(
				BranchDataProvider.CONTENT_URI_BRANCHMANAGE, new String[]{},
				where, null, null);
		if (cursor != null) {
			item = builtBranchItem(cursor);
		}
		cursor.close();
		cursor = null;

		VLog.v("db getBranchs by UID  " + item.getName());
		return item;
	}

	/**
	 * 获取BranchModel
	 * 
	 * @return
	 */
	synchronized public ArrayList<BranchModel> getBranchsByGeotableId(
			String geotable_id) {
		ArrayList<BranchModel> items = new ArrayList<BranchModel>();
		BranchModel item = null;
		String where = BranchDataProvider.GEOTABLE_ID + "=" + geotable_id;
		String orderBy = BranchDataProvider.CREATE_TIME + " desc";
		Cursor cursor = contentResolver.query(
				BranchDataProvider.CONTENT_URI_BRANCHMANAGE, new String[]{},
				where, null, orderBy);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					item = builtBranchItem(cursor);
					items.add(item);
				} while (cursor.moveToNext());
			}
			cursor.close();
			cursor = null;
		}
		VLog.v("db getBranchs by Name  " + items.size());
		return items;
	}
	/**
	 * 组装TagItem
	 * 
	 * @param cursor
	 * @return
	 */
	synchronized private BranchModel builtBranchItem(Cursor cursor) {
		BranchModel item = new BranchModel();
		item.setUid(cursor.getString(cursor
				.getColumnIndex(BranchDataProvider.UID)));
		item.setName(cursor.getString(cursor
				.getColumnIndex(BranchDataProvider.BRANCH_NAME)));
		item.setAddr(cursor.getString(cursor
				.getColumnIndex(BranchDataProvider.BRANCH_ADD)));
		item.setBranch_type(cursor.getInt(cursor
				.getColumnIndex(BranchDataProvider.BRANCH_TYPE)));
		item.setCreate_time(cursor.getString(cursor
				.getColumnIndex(BranchDataProvider.CREATE_TIME)));
		item.setModify_time(cursor.getString(cursor
				.getColumnIndex(BranchDataProvider.MODIFY_TIME)));
		item.setImageurl(cursor.getString(cursor
				.getColumnIndex(BranchDataProvider.IMAGE_URL)));
		item.setGeotable_id(cursor.getString(cursor
				.getColumnIndex(BranchDataProvider.GEOTABLE_ID)));
		item.setLatitude(cursor.getString(cursor
				.getColumnIndex(BranchDataProvider.LATITUDE)));
		item.setLongitude(cursor.getString(cursor
				.getColumnIndex(BranchDataProvider.LONGITUDE)));
		item.setProvince(cursor.getString(cursor
				.getColumnIndex(BranchDataProvider.PROVINCE)));
		item.setCity(cursor.getString(cursor
				.getColumnIndex(BranchDataProvider.CITY)));
		item.setDistrict(cursor.getString(cursor
				.getColumnIndex(BranchDataProvider.DISTRICT)));
		item.setWebUrl(cursor.getString(cursor
				.getColumnIndex(BranchDataProvider.WEB_URL)));

		VLog.v("builtBranchItem--" + item.getName());
		return item;
	}

	/**
	 * 清除数据库信息
	 * 
	 * @throws Exception
	 */
	public void reinitializationDataBaseTable() throws Exception {
		contentResolver.delete(BranchDataProvider.CONTENT_URI_BRANCHMANAGE,
				null, null);
	}
}
