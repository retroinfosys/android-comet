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

package com.memtrip.comet.asynctask;

import java.util.HashMap;

import com.memtrip.comet.asynctask.base.BaseResponse;

/**
 * The response of the coment connection
 * @author samkirton
 */
public class CometResponse extends BaseResponse {
	private int mHttpStatusCode;
	private byte[] mData;
	private HashMap<String,String> mHeaders;
	
	public int getHttpStatusCode() {
		return mHttpStatusCode;
	}
	
	public void setHttpStatusCode(int newVal) {
		mHttpStatusCode = newVal;
	}
	
	public byte[] getData() {
		return mData;
	}
	
	public void setData(byte[] newVal) {
		mData = newVal;
	}
	
	public HashMap<String,String> getHeaders() {
		return mHeaders;
	}
	
	public void setHeaders(HashMap<String,String> newVal) {
		mHeaders = newVal;
	}
}
