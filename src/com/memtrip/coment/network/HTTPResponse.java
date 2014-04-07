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

package com.memtrip.coment.network;

import java.util.HashMap;

/**
 * Data that composes the response received from a HTTP connection
 * @author memtrip
 */
public final class HTTPResponse {
	private int mHttpResponseCode;
	private byte[] mResponseData;
	private HashMap<String,String> mHeaders;
	
	public int getHttpResponseCode() {
		return mHttpResponseCode;
	}
	
	public void setHttpResponseCode(int newVal) {
		mHttpResponseCode = newVal;
	}
	
	public byte[] getResponseData() {
		return mResponseData;
	}
	
	public void setResponseData(byte[] newVal) {
		mResponseData = newVal;
	}
	
	public HashMap<String,String> getHeaders() {
		return mHeaders;
	}
	
	public void setHeaders(HashMap<String,String> newVal) {
		mHeaders = newVal;
	}
}