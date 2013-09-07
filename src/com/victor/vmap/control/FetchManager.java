package com.victor.vmap.control;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.victor.vmap.utils.MapConstant;
import com.yachi.library_yachi.VLog;
import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;

/**
 * @firm 长沙江泓信息技术有限公司
 * 
 * @author xiaoyl
 * @date 2013-07-13
 * 
 * @file 事物执行管理类
 * 
 */
public class FetchManager {
	private static FetchManager instance;

	private AddTaskThread handlerThread;
	private Handler loadTaskHandler;

	/** 动作任务线程池 */
	private ExecutorService iExecutor;

	private FetchManager(Context context) {

		iExecutor = Executors.newFixedThreadPool(1);
		handlerThread = new AddTaskThread();
		handlerThread.start();
		loadTaskHandler = new Handler(handlerThread.getLooper(), handlerThread);
	}

	public static FetchManager getInstance(Context context) {
		if (instance == null) {
			instance = new FetchManager(context);
		}
		return instance;
	}

	/**
	 * 开始以堆栈形式执行Action
	 * 
	 * @param item
	 */
	public void startFetchData() {
		for (BranchRequestModel item : MapConstant.getRequest_list()) {
			loadTaskHandler.sendMessage(loadTaskHandler.obtainMessage(0, item));
		}
	}

	/**
	 * 堆栈线程
	 * 
	 * @author xiaoyl
	 */
	private class AddTaskThread extends HandlerThread implements Callback {
		public AddTaskThread() {
			super("AddTaskThread");
		}

		@Override
		public boolean handleMessage(Message msg) {
			BranchRequestModel item = (BranchRequestModel) msg.obj;
			startAction(item);
			return true;
		}
	}

	/**
	 * 开始执行单条Action
	 * 
	 * @param item
	 */
	private void startAction(BranchRequestModel item) {
		FetchThread thread = new FetchThread(item);
		VLog.v("startAction---" + item.getRequest_url());
		iExecutor.submit(thread);
	}

}
