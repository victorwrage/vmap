/** 
 * @Filename SearchUtils.java 
 * @Description TODO 
 * @Version 1.0
 * @Author xiaoyl
 * @Creation 2013-9-7 上午10:41:09   
 * @Copyright Copyright © 2009 - 2013 Victor.All Rights Reserved.
**/
package com.victor.vmap.utils;

/** 
 * @ClassName SearchUtils 
 * @Description TODO 
 * @Version 1.0
 * @Creation 2013-9-7 上午10:41:09 
 * @Mender xiaoyl
 * @Modification 2013-9-7 上午10:41:09 
 **/

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public class SearchUtils {
	public static class Word {
		public final String word;
		public final String definition;

		public Word(String word, String definition) {
			this.word = word;
			this.definition = definition;
		}
	}

	private static final SearchUtils sInstance = new SearchUtils();

	private final Map<String, List<Word>> mDict = new ConcurrentHashMap<String, List<Word>>();

	public static SearchUtils getInstance() {
		return sInstance;
	}

	private SearchUtils() {
	}

	private boolean mLoaded = false;
	
	 public synchronized void ensureLoaded() {
	        if (mLoaded) return;

	        new Thread(new Runnable() {
	            public void run() {
	            	//插入数据
	            	addWord("a", "aaa");
	            	addWord("aa", "aaa");
	            	addWord("aaa", "aaa");
	            }
	        }).start();
	    }
	 
	 
	  public List<Word> getMatches(String query) {
	        List<Word> list = mDict.get(query);
	        return list == null ? Collections.EMPTY_LIST : list;
	    }

	    private void addWord(String word, String definition) {
	        final Word theWord = new Word(word, definition);

	        final int len = word.length();
	        for (int i = 0; i < len; i++) {
	            final String prefix = word.substring(0, len - i);
	            addMatch(prefix, theWord);
	        }
	    }

	    private void addMatch(String query, Word word) {
	        List<Word> matches = mDict.get(query);
	        if (matches == null) {
	            matches = new ArrayList<Word>();
	            mDict.put(query, matches);
	        }
	        matches.add(word);
	    }

}
