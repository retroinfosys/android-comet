/**
 * Copyright 2010-present memtrip LTD.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.memtrip.comet.asynctask.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

/**
 * @author samkirton
 */
public abstract class BaseCometTask extends AsyncTask<IParam, Void, BaseResponse>  {
	private Context mContext;
	private CometAsyncCallback mCometAsyncCallback;
	private BaseResponse mResult;
	private boolean mCancelled;
	
	public interface CometAsyncCallback {
		public void onCometAsyncResponse(BaseResponse response, boolean cancelled);
	}
	
	/**
	 * Run the result of the AsyncTask on the GUI thread
	 */
	private Runnable doUpdateGUI = new Runnable() {
		public void run() { 
			mCometAsyncCallback.onCometAsyncResponse(mResult, mCancelled);
		} 
	};
	
	public void setCometAsyncCallback(CometAsyncCallback newVal) {
		mCometAsyncCallback = newVal;
	}
	
	public Context getContext() {
		return mContext;
	}
	
	/**
	 * Constructor
	 * @param	applicationContext	The application context
	 */
	public BaseCometTask(Context context) {
		if (!(context instanceof Activity) && !(context instanceof Service)) {
			throw new IllegalArgumentException("The AsyncTask context must be able to cast into Activity or Service");
		}
		
		mContext = context;
	}
	
	/**
	 * An override of the AsyncTask execute method that takes a single param
	 * @param	param	The param to provide the task with
	 */
	@SuppressLint("NewApi")
	public void execute(IParam param) {
		IParam[] params = new IParam[1];
		params[0] = param;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		    this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,params);
		} else {
		    this.execute(params);
		}
	}
	
	@Override
	protected BaseResponse doInBackground(IParam... params) {
		IParam param = null;
		if (params !=  null && params.length > 0) {
			param = params[0];
		}
		
		return run(param);
	}
	
	@Override
	protected void onPostExecute(BaseResponse result) {
		mResult = result;
		
		if (mContext instanceof Activity) {
			((Activity)mContext).runOnUiThread(doUpdateGUI);
		} else if (mContext instanceof Service) {
			mCometAsyncCallback.onCometAsyncResponse(mResult,mCancelled);
		}
	}
	
	@Override
	protected void onPreExecute() { }
	
	@Override
	protected void onProgressUpdate(Void... values) { }
	
	@Override
	protected void onCancelled() { 
		mCancelled = true;
	}
	
	/**
	 * Run the async logic based on the context provided
	 * @param	baseParam	The param for the logic
	 * @return	Return a response
	 */
	protected abstract BaseResponse run(IParam baseParam);
}
