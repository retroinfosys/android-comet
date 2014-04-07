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
 * The data that composes the HTTP connection request
 * @author memtrip
 */
public final class HTTPRequest {
	public static final String HTTP_GET = "GET";
	public static final String HTTP_POST = "POST";
	public static final String HTTP_PUT = "PUT";
	public static final String HTTP_DELETE = "DELETE";
	
	private String mHttpMethod;
	private String mUrl;
	private HashMap<String,String> mHeaders;
	private byte[] mRequestData;
	
	public String getHttpMethod() {
		return mHttpMethod;
	}
	
	public void setHttpMethod(String newVal) {
		mHttpMethod = newVal;
	}
	
	public String getUrl() {
		return mUrl;
	}
	
	public void setUrl(String newVal) {
		mUrl = newVal;
	}
	
	public HashMap<String,String> getHeaders() {
		return mHeaders;
	}
	
	public void setHeaders(HashMap<String,String> newVal) {
		mHeaders = newVal;
	}
	
	public byte[] getRequestData() {
		return mRequestData;
	}
	
	public void setRequestData(byte[] newVal) {
		mRequestData = newVal;
	}
}