/** 
 * @Filename SearchProvider.java 
 * @Description TODO 
 * @Version 1.0
 * @Author xiaoyl
 * @Creation 2013-9-3 上午9:23:27   
 * @Copyright Copyright © 2009 - 2013 Victor.All Rights Reserved.
 **/
package com.victor.vmap.provider;

import java.util.List;

import com.victor.vmap.utils.SearchUtils;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

/**
 * @ClassName SearchProvider
 * @Description TODO
 * @Version 1.0
 * @Creation 2013-9-3 上午9:23:27
 * @Mender xiaoyl
 * @Modification 2013-9-3 上午9:23:27
 **/
public class SearchProvider extends ContentProvider {
	// 记住这个哦
	public final static String AUTHORITY = "com.victor.vmap.provider.SearchProvider";

	/**
	 * @Name delete
	 * @Description TODO
	 * @param uri
	 * @param selection
	 * @param selectionArgs
	 * @return
	 * @see android.content.ContentProvider#delete(android.net.Uri,
	 *      java.lang.String, java.lang.String[])
	 * @Date 2013年9月7日 上午9:50:47
	 **/
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		return 0;
	}
	/**
	 * @Name getType
	 * @Description TODO
	 * @param uri
	 * @return
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 * @Date 2013年9月7日 上午9:50:47
	 **/
	@Override
	public String getType(Uri uri) {

		return null;
	}
	/**
	 * @Name insert
	 * @Description TODO
	 * @param uri
	 * @param values
	 * @return
	 * @see android.content.ContentProvider#insert(android.net.Uri,
	 *      android.content.ContentValues)
	 * @Date 2013年9月7日 上午9:50:47
	 **/
	@Override
	public Uri insert(Uri uri, ContentValues values) {

		return null;
	}
	/**
	 * @Name onCreate
	 * @Description TODO
	 * @return
	 * @see android.content.ContentProvider#onCreate()
	 * @Date 2013年9月7日 上午9:50:47
	 **/
	@Override
	public boolean onCreate() {

		return false;
	}
	/**
	 * @Name query
	 * @Description TODO
	 * @param uri
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 * @see android.content.ContentProvider#query(android.net.Uri,
	 *      java.lang.String[], java.lang.String, java.lang.String[],
	 *      java.lang.String)
	 * @Date 2013年9月7日 上午9:50:47
	 **/
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		String query = null;
		if (uri.getPathSegments().size() > 1) {
			query = uri.getLastPathSegment().toLowerCase();
		}
		return getSuggestions(query);
	}
	private Cursor getSuggestions(String query) {
		String processedQuery = query == null ? "" : query.toLowerCase();
		List<SearchUtils.Word> words = SearchUtils.getInstance().getMatches(
				processedQuery);
		MatrixCursor cursor = new MatrixCursor(COLUMNS);
		long id = 0;
		for (SearchUtils.Word word : words) {
			cursor.addRow(columnValuesOfWord(id++, word));
		}
		return cursor;
	}

	private Object[] columnValuesOfWord(long id, SearchUtils.Word word) {
		return new Object[] { id, // _id
		word.word, // text1
		word.definition, // text2
		word.word};
	}
	
	private static final String[] COLUMNS = {"_id",
			SearchManager.SUGGEST_COLUMN_TEXT_1,
			SearchManager.SUGGEST_COLUMN_TEXT_2,
			SearchManager.SUGGEST_COLUMN_INTENT_DATA,// 数据传递到intenter中
	};

	/**
	 * @Name update
	 * @Description TODO
	 * @param uri
	 * @param values
	 * @param selection
	 * @param selectionArgs
	 * @return
	 * @see android.content.ContentProvider#update(android.net.Uri,
	 *      android.content.ContentValues, java.lang.String, java.lang.String[])
	 * @Date 2013年9月7日 上午9:50:47
	 **/
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		return 0;
	}
}
