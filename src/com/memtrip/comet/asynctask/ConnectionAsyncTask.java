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

import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Context;

import com.memtrip.coment.network.HTTPRequest;
import com.memtrip.coment.network.HTTPResponse;
import com.memtrip.coment.network.HTTPSConnection;
import com.memtrip.comet.asynctask.base.BaseCometTask;
import com.memtrip.comet.asynctask.base.BaseResponse;
import com.memtrip.comet.asynctask.base.IParam;

/**
 * Start a http connection and wait for a response from the server, comet requests require a 
 * server application that keeps the connection alive until a response is available.
 * @author samkirton
 */
public class ConnectionAsyncTask extends BaseCometTask {

	public ConnectionAsyncTask(Context context) { super(context); }

	@Override
	protected BaseResponse run(IParam baseParam) {
		CometParam param = (CometParam)baseParam;
		CometResponse cometResponse = new CometResponse();

		boolean isValid = false;
		
		HTTPRequest request = new HTTPRequest();
		request.setUrl(param.getUrl());
		request.setHttpMethod(HTTPRequest.HTTP_GET);
		request.setRequestData(param.getData());
		request.setHeaders(param.getHeaders());
		
		HTTPResponse response =  new HTTPResponse();
		
		HTTPSConnection connection = new HTTPSConnection(request);
		
		try {
			// When a connection is made to a long polling server application the thread will
			// block on open. This block will not be released until the server returns a response
			// or ComentProvider.stop() is called.
			connection.open();
			
			// The response is returned when the server has data available
			response = connection.getHTTPResponse();
			isValid = true;
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		}
		
		cometResponse.setIsValid(isValid);
		cometResponse.setHttpStatusCode(response.getHttpResponseCode());
		cometResponse.setData(response.getResponseData());
		cometResponse.setHeaders(response.getHeaders());
		
		return cometResponse;
	}
}
