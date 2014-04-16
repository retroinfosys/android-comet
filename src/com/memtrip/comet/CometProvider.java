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

package com.memtrip.comet;

import java.util.HashMap;

import android.content.Context;

import com.memtrip.comet.asynctask.CometParam;
import com.memtrip.comet.asynctask.CometResponse;
import com.memtrip.comet.asynctask.ConnectionAsyncTask;
import com.memtrip.comet.asynctask.base.BaseCometTask.CometAsyncCallback;
import com.memtrip.comet.asynctask.base.BaseResponse;

/**
 * The CometProvider opens a HTTP connection at the specified URL with the request
 * data and headers provided. It assumes that the URL points to a long polling (COMET) 
 * server implementation that keeps a connection open until a response is available. 
 * When the server returns a response it is sent to the CometCallback and the connection is
 * re-opened to wait for the next available response.
 * 
 * The connection can be refreshed to use a new endpoint or different request data / headers.
 * The existing long poll is cancelled and a new one is started in this case.
 * @author samkirton
 */
public class CometProvider implements CometAsyncCallback {
	private String mUrl;
	private byte[] mRequestData;
	private HashMap<String,String> mHeaders;
	private boolean mShouldRefresh;
	private Context mContext;
	
	private ConnectionAsyncTask mConnectionAsyncTask;
	private CometCallback mCometCallback;
	
	private static final String REASON_CANCEL = "CANCELLED";
	
	/**
	 * Receive the response of comet connections
	 */
	public interface CometCallback {
		/**
		 * The response from a comet connections, after this callback is returned
		 * the comet connection continues to wait for more responses
		 * @param	data	The response data
		 * @param	statusCode	The HTTP status code
		 */
		public void onCometResponse(int statusCode, byte[] data, HashMap<String,String> headers);
		
		/**
		 * The comet connection has been stopped
		 */
		public void onCometStopped(String reason);
	}
	
	public void setCometCallback(CometCallback cometCallback) {
		mCometCallback = cometCallback;
	}
	
	/**
	 * Create a comet provider object to make http connections
	 * @param	url	The URL to begin the comet connection on
	 * @param	requestData	The requestData of the initial comet connection
	 * @param	headers	The request headers of the initial comet connections
	 * @param	context	An android context that can be cast into an activity
	 */
	public CometProvider(String url, byte[] requestData, HashMap<String,String> headers, Context context) {
		mUrl = url;
		mRequestData = requestData;
		mHeaders = headers;
		mContext = context;
	}
	
	/**
	 * Create a comet provider object to make http connections
	 * @param	url	The URL to begin the comet connection on
	 * @param	requestData	The requestData of the initial comet connection
	 * @param	context	An android context that can be cast into an activity
	 */
	public CometProvider(String url, byte[] requestData, Context context) {
		this(url,requestData,null,context);
	}
	
	/**
	 * Create a comet provider object to make http connections
	 * @param	url	The URL to begin the comet connection on
	 * @param	context	An android context that can be cast into an activity
	 */
	public CometProvider(String url, Context context) {
		this(url,null,null,context);
	}
	
	/**
	 * Start the comet request, the connection to the specified URL will keep
	 * re-opening the connection until stop() is called
	 * @param	timestamp	The timestamp of the last successful long poll
	 */
	public void start(String timestamp) {
		CometParam param = new CometParam();
		param.setData(mRequestData);
		param.setUrl(mUrl);
		
		// add the timestamp header if it exists
		if (timestamp != null) 
			mHeaders.put("X-Timestamp", timestamp);
			
		param.setHeaders(mHeaders);
		
		mConnectionAsyncTask = new ConnectionAsyncTask(mContext);
		mConnectionAsyncTask.setCometAsyncCallback(this);
		mConnectionAsyncTask.execute(param);
	}
	
	/**
	 * An override of start() that assumes a timestamp is not available.
	 * Start the comet request, the connection to the specified URL will keep
	 * re-opening the connection until stop() is called
	 */
	public void start() {
		start(null);
	}
	
	/**
	 * Cancel the current connection and start a new one with the new arguments
	 * @param	url	The URL to begin the comet connection on
	 * @param	requestData	The requestData of the initial comet connection
	 * @param	headers	The request headers of the initial comet connections
	 */
	public void refreshConnection(String url, byte[] requestData, HashMap<String,String> headers) {
		mUrl = url;
		mRequestData = requestData;
		mHeaders = headers;
		
		mShouldRefresh = true;
		mConnectionAsyncTask.cancel(true);
	}
	
	/**
	 * Stop the comet request and destroy the network connection
	 */
	public void stop() {
		mConnectionAsyncTask.cancel(true);
	}
	
	@Override
	public void onCometAsyncResponse(BaseResponse response, boolean cancelled) {
		CometResponse cometResponse = (CometResponse)response;
		
		if (cancelled && mShouldRefresh) {
			mShouldRefresh = false;
			start();
		} else if (cancelled) {
			mCometCallback.onCometStopped(REASON_CANCEL);
		} else if (cometResponse.getHttpStatusCode() < 200 || cometResponse.getHttpStatusCode() > 299) {
			String reason = null;
			if (cometResponse.getData() != null) 
				reason = new String(cometResponse.getData());
			
			mCometCallback.onCometStopped(reason);
		} else {
			mCometCallback.onCometResponse(
				cometResponse.getHttpStatusCode(),
				cometResponse.getData(),
				cometResponse.getHeaders()
			);
			
			// start the connection again
			start(cometResponse.getHeaders().get("X-Timestamp"));
		}
	}
}
