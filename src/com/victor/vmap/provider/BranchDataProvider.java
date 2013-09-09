package com.victor.vmap.provider;

import java.util.HashMap;

import com.yachi.library_yachi.VLog;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * @firm 长沙江泓信息技术有限公司
 * 
 * @author xiaoyl
 * @date 2013-07-13
 * 
 * @file 数据库的Provider类
 * 
 */
public class BranchDataProvider extends ContentProvider {
	/**网点管理表*/
	public static final String TABLE_BRANCH_MANAGE = "branch_manage";

	/**id*/
	public static final String _ID = "_id";
	/**branch_id*/
	public static final String UID = "uid";
	/**branch   name*/
	public static final String BRANCH_NAME = "branch_name";
	/** 地址*/
	public static final String BRANCH_ADD = "branch_add";
	/** geotable_id*/
	public static final String GEOTABLE_ID = "geotable_id";
	/**类型*/
	public static final String BRANCH_TYPE = "branch_type";
	/** province*/
	public static final String PROVINCE = "province";
	/** city*/
	public static final String CITY = "city";
	/** distrct*/
	public static final String DISTRICT = "district";
	/**branch创建时间*/
	public static final String CREATE_TIME = "create_time";
	/**branch修改时间*/
	public static final String MODIFY_TIME = "modify_time";
	/**图片地址*/
	public static final String IMAGE_URL = "image_url";
	/**纬度*/
	public static final String LATITUDE = "latitude";
	/**经度*/
	public static final String LONGITUDE = "longitude";
	/**网址*/
	public static final String WEB_URL = "web_url";
	/**预留*/
	public static final String STRING_PARAMS = "string_params";

	private static HashMap<String, String> searchMap; 

	
	/**
	 * Authority for Uris
	 * 数据库的一些数据的Provider类
	 */
	public static final String AUTHORITY = BranchDataProvider.class.getName();
	/**Branch管理表*/
	public static final Uri CONTENT_URI_BRANCHMANAGE = Uri.parse("content://" + AUTHORITY + "/"
			+ Schema.TABLE_BRANCH_MANAGE);  
	/**Branch管理表*/
	public static final Uri CONTENT_URI_BRANCHSEARCH = Uri.parse("content://" + AUTHORITY + "/"
			+ Schema.TABLE_BRANCH_SEARCH);  

	
	/**Uri匹配器*/
	private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	static {
		sUriMatcher.addURI(AUTHORITY, Schema.TABLE_BRANCH_MANAGE, Schema.URI_CODE_BRANCHMANAGE);
		sUriMatcher.addURI(AUTHORITY, Schema.TABLE_BRANCH_SEARCH, Schema.URI_CODE_BRANCHSEARCH);

		searchMap = new HashMap<String, String>();
		searchMap.put(BRANCH_NAME, BRANCH_NAME +" AS " + SearchManager.SUGGEST_COLUMN_TEXT_1); 
		searchMap.put(_ID, _ID);  
		searchMap.put(UID,UID + " AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID); 
		searchMap.put(BRANCH_ADD,BRANCH_ADD + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_2); 
		
	}
	/** 内部类*/
	private TagDBHelp dbHelp;
	/** Uri*/
	private Uri currentUri;
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelp.getWritableDatabase();
		String tblName = getTblName(uri);
		if (tblName != null) {
			getContext().getContentResolver().notifyChange(currentUri, null);
			return db.delete(tblName, selection, selectionArgs);
		}
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		
		switch (sUriMatcher.match(uri)) {
		         case Schema.URI_CODE_BRANCHMANAGE:
		             return Schema.TABLE_BRANCH_MANAGE;
		         case Schema.URI_CODE_BRANCHSEARCH:
		             return Schema.TABLE_BRANCH_SEARCH;
		         default:
		            throw new IllegalArgumentException("Unknown URI " + uri);
		        }

	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbHelp.getWritableDatabase();
		long rowId = -1;
		String tblName = getTblName(uri);
		if (tblName != null) {
			rowId = db.insert(tblName, null, values);
			if (rowId > 0) {
				Uri insertedDownloadUri = ContentUris.withAppendedId(currentUri, rowId);
				getContext().getContentResolver().notifyChange(insertedDownloadUri, null);
				return insertedDownloadUri;
			}
		}

		return null;
	}

	@Override
	public boolean onCreate() {
		dbHelp =new TagDBHelp(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteDatabase db = dbHelp.getWritableDatabase();
		String tblName = null;
		String groupBy = null;
		String limit = null;
		
		String query = uri.getLastPathSegment(); 
		if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(query)) { 
		//如果找到符合用户输入的记录 
			SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
			
			builder.setTables(Schema.TABLE_BRANCH_MANAGE);
		//	projectionMap.put(COL_DATE, COL_DATE); 
		//	String where = BranchDataProvider.GEOTABLE_ID + "=" + "31669";
			builder.setProjectionMap(searchMap); 
			if (selectionArgs!= null && selectionArgs.length > 0 &&selectionArgs[0].length() > 0) { 
				VLog.v("qqqqqqqqqqqqqqqq--string--"+selectionArgs[0]);
				/*builder.appendWhere(selectionArgs[0] + " in ( "+   BranchDataProvider.BRANCH_NAME +","+ BranchDataProvider.BRANCH_ADD
						+")");*/
			//	builder.appendWhere(BranchDataProvider.BRANCH_NAME + " LIKE %" + selectionArgs[0]+"%");
			}else{

				VLog.v("qqqqqqqqqqqqqqqq--string--"+query);
			}
			
	//	    builder.appendWhere(BranchDataProvider.BRANCH_NAME + " LIKE %" + query+"%");
			Cursor search_cursor = builder.query(db, null, null, null, groupBy, null, sortOrder);
			VLog.v("qqqqqqqqqqqqqqqq--count--"+search_cursor.getCount());
			return search_cursor;
		} 
		
		switch (sUriMatcher.match(uri)) {
		case Schema.URI_CODE_BRANCHMANAGE:
			tblName = Schema.TABLE_BRANCH_MANAGE;
			break;
		}
		
		if (tblName != null) {
			Cursor cursor = db.query(tblName, projection, selection, selectionArgs, groupBy, null, sortOrder, limit);
			return cursor;
		}
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelp.getWritableDatabase();
		String tblName = getTblName(uri);

		if (tblName != null) {
			getContext().getContentResolver().notifyChange(currentUri, null);
			return db.update(tblName, values, selection, selectionArgs);
		}

		return 0;
	}
	
	/**
	 * 根据uri获取表名
	 * @param uri Uri
	 * @return
	 */
	private String getTblName(Uri uri) {
		String tblName = null;
		switch (sUriMatcher.match(uri)) {
		case Schema.URI_CODE_BRANCHMANAGE:
			tblName = Schema.TABLE_BRANCH_MANAGE;
			currentUri = CONTENT_URI_BRANCHMANAGE;
			break;
		}
		return tblName;
	}
	/**
	 * 创建数据库
	 * @author xiaoyl
	 *@date 2013-07-12
	 */
	public static class TagDBHelp extends SQLiteOpenHelper {
		private static final String NAME = "vmap.db";
		private static final int VERSION = 1;
		
		/** 创建BRANCH管理表语句*/
		public static final String CREATE_TABLE_BRANCH_MANAGE = "create table  if not exists "
				+ TABLE_BRANCH_MANAGE
				+ " ("
				+ _ID
				+ " integer primary key autoincrement, "
				+ UID
				+ " text not null, "
				+ BRANCH_NAME
				+ " text not null, "
				+ GEOTABLE_ID
				+ " text not null, "
				+ BRANCH_TYPE
				+ " int not null, "
				+ LATITUDE
				+ " text, "
				+ LONGITUDE
				+ " text, "
				+ BRANCH_ADD
				+ " text, "
				+ PROVINCE
				+ " text , "
				+ CITY
				+ " text , "
				+ DISTRICT
				+ " text , "
				+ CREATE_TIME
				+ " text , "
				+ MODIFY_TIME
				+ " text , "
				+ IMAGE_URL
				+ " text, "
				+ WEB_URL
				+ " text, "
				+ STRING_PARAMS
				+ " text "	
				+ ")";
		
		/**
		 * 删除BRANCH表
		 */
		static final String DELETE_TABLE_BRANCH_MANAGE = "drop table if exists "+TABLE_BRANCH_MANAGE;

		public TagDBHelp(Context context) {
			super(context, NAME, null, VERSION);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_BRANCH_MANAGE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
			db.execSQL(DELETE_TABLE_BRANCH_MANAGE);
			db.execSQL(CREATE_TABLE_BRANCH_MANAGE);
		}
	}
	
	/**
	 * Represents Data Schema.
	 */
	public static final class Schema{
		/**BRANCH管理表 */
		static final String TABLE_BRANCH_MANAGE = "branch_manage";
		/**BRANCH管理表 */
		static final String TABLE_BRANCH_SEARCH = "search_suggest_query/#";

		/** Codes for UriMatcher 对应BRANCH管理表 */
		static final int URI_CODE_BRANCHMANAGE = 1;
		/** Codes for UriMatcher 对应BRANCH管理表 */
		static final int URI_CODE_BRANCHSEARCH = 2;

	}
}
