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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Handle HTTPS connection
 * TODO: Implement SSL integrity checks
 * @author	memtrip
 */
public class HTTPSConnection {
	private HTTPRequest httpRequest;
	private HTTPResponse httpResponse;
	
	private HttpURLConnection mUrlConnection;
	private InputStream mInputStream;
	private OutputStream mOutputStream;
	private int mConnectionState;
	
	public static final int CONNECTION_ERROR = 0;
	public static final int CONNECTION_SUCCESS = 1;
	
	public static final int RESPONSE_OK = 200;
	public static final int RESPONSE_ERROR = 400;
	
	public HTTPResponse getHTTPResponse() {
		return httpResponse;
	}
	
	public HTTPSConnection(HTTPRequest httpRequest) {
		this.httpRequest = httpRequest;
	}
	
	public int getConnectionState() {
		return mConnectionState;
	}
	
	/**
	 * Close the persistent connection
	 */
	public void close() {		
		closeInputStream();
		closeOutputStream();

		if (mUrlConnection != null)
			mUrlConnection.disconnect();
	}
	
	/**
	 * Open the connection and build a request 
	 * @throws	MalformedURLException	
	 * @throws	IOException	
	 */
	public void open() throws MalformedURLException, IOException {
		try {
			URL url = new URL(httpRequest.getUrl());
			mUrlConnection = (HttpURLConnection)url.openConnection();
			mUrlConnection.setRequestProperty("Connection", "keep-alive");
			mUrlConnection.setRequestMethod(httpRequest.getHttpMethod());
			
			buildRequestHeaders(httpRequest.getHeaders());
			writeRequestData(httpRequest.getRequestData());
			
			httpResponse = new HTTPResponse();
			httpResponse.setHttpResponseCode(mUrlConnection.getResponseCode());
			httpResponse.setResponseData(getResponseData());
			httpResponse.setHeaders(getResponseHeaders());
			mConnectionState = CONNECTION_SUCCESS;
		} catch (Exception e) {
			mConnectionState = CONNECTION_ERROR;
		} finally {
			close();
		}
	}
	
	/**
	 * Add a collection of headers to the HTTP request
	 * @param	httpHeaders	The HTTP headers to add to the request
	 */
	private void buildRequestHeaders(HashMap<String,String> httpHeaders) {
		if (httpHeaders != null && httpHeaders.size() > 0) {
			for (String key : httpHeaders.keySet()) {
				String value = httpHeaders.get(key);
				mUrlConnection.setRequestProperty(key, value);
				System.out.println("Header data : " + key + " - " + value);
			}
		}
	}
	
	/**
	 * Write data to the HTTP request
	 * @param	requestData	The data to the write to the http request
	 * @throws IOException
	 */
	private void writeRequestData(byte[] requestData) throws IOException {
		if (httpRequest.getHttpMethod() != HTTPRequest.HTTP_GET && requestData != null && requestData.length > 0) {
			System.out.println("Request data: " + new String(requestData));
			mUrlConnection.setDoOutput(true);
			mOutputStream = mUrlConnection.getOutputStream();
			mOutputStream.write(requestData);
			mOutputStream.flush();
		}
	}
	
	/**
	 * Retrieve the response data from the request
	 * @return	The response data
	 * @throws IOException
	 */
	private byte[] getResponseData() throws IOException {
		// Get the response stream
		if (mUrlConnection.getResponseCode() == RESPONSE_OK) {
			mInputStream = new BufferedInputStream(mUrlConnection.getInputStream());
		} else if (mUrlConnection.getResponseCode() == RESPONSE_ERROR) {
			mInputStream = new BufferedInputStream(mUrlConnection.getErrorStream());
		}
		
		byte[] responseData = null;
		if (mInputStream != null) {
			final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			byte[] buff = new byte[1024]; // buffer size
			
			while (true) {
				int read = mInputStream.read(buff);
				if (read < 0)
					break;
				
				byteArrayOutputStream.write(buff, 0, read);
			}
			
			responseData = byteArrayOutputStream.toByteArray();
		}
		
		return responseData;
	}
	
	/**
	 * Retrieve the response headers from the request
	 * @return	The response headers
	 */
	private HashMap<String,String> getResponseHeaders() {
		HashMap<String,String> repsonseHeaders = new HashMap<String,String>();
		int responseHeaderCount = 0;
		
		while (true) {
			String key = mUrlConnection.getHeaderFieldKey(responseHeaderCount);
			String value = mUrlConnection.getHeaderField(responseHeaderCount); 
			
			if (key != null) {
				repsonseHeaders.put(key, value);
			} else {
				if (responseHeaderCount > 0)
					break;
			}
			
			responseHeaderCount++;
		}
		
		return repsonseHeaders;
	}
	
	/**
	 * Close the request input stream
	 */
	private void closeInputStream() {
		if (mInputStream != null) {
			try {
				mInputStream.close();
			} catch (IOException e) {}
			mInputStream = null;
		}
	}
	
	/**
	 * Close the response output stream
	 */
	private void closeOutputStream() {
		if (mOutputStream != null) {
			try {
				mOutputStream.close();
			} catch (IOException e) {}
			mOutputStream = null;
		}
	}
}