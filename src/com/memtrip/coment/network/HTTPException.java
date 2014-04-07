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

/**
 * An exception that also contains an errorCode 
 * @author memtrip
 */
public final class HTTPException extends Exception {
	private static final long serialVersionUID = 1L;
	private String message;
	private String errorCode;
	
	public String getMessage() {
		return message;
	}
	
	public String getErrorCode() {
		return errorCode;
	}
	
	public HTTPException(String message, String errorCode) {
		this.message = message;
		this.errorCode = errorCode;
	}
}