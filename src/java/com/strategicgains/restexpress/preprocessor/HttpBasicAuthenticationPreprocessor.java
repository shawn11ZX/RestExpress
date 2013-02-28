/*
    Copyright 2013, Strategic Gains, Inc.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package com.strategicgains.restexpress.preprocessor;

import javax.xml.bind.DatatypeConverter;

import org.jboss.netty.handler.codec.http.HttpHeaders;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.exception.UnauthorizedException;
import com.strategicgains.restexpress.pipeline.Preprocessor;

/**
 * This preprocessor implements HTTP Basic authentication.  To use it, simply add it to your server as follows:
 * <code>
 * server.addPreprocessor(new HttpBasicAuthenticationPreprocessor("my realm"));
 * </code>
 * <p/>
 * Once this preprocessor completes successfully, it places the username and password in the request as headers,
 * X-AuthenticatedUser and X-AuthenticatedPassword, respectively.
 * <p/>
 * If the preprocessor fails, it throws UnauthorizedException, which results in an HTTP status of 401 to the caller.
 * <p/>
 * Use of this preprocessor assumes you'll implement an authorization preprocessor that validations the username and 
 * password extracted from the Authorization header in the request.
 * 
 * @author toddf
 * @since Feb 28, 2013
 */
public class HttpBasicAuthenticationPreprocessor
implements Preprocessor
{
	public static final String X_AUTHENTICATED_USER = "X-AuthenticatedUser";
	public static final String X_AUTHENTICATED_PASSWORD = "X-AuthenticatedPassword";

	private String realm;

	/**
	 * Utilize HTTP Basic Authentication with the given realm returned on an unauthenticated request.
	 * 
	 * @param realm any value to identify the secure area and may used by HTTP clients to manage passwords.
	 */
	public HttpBasicAuthenticationPreprocessor(String realm)
	{
		super();
		this.realm = realm;
	}

	@Override
    public void process(Request request)
    {
		String authorization = request.getRawHeader(HttpHeaders.Names.AUTHORIZATION);
		
		if (authorization == null || !authorization.startsWith("Basic "))
		{
			UnauthorizedException e = new UnauthorizedException("Authenticationrequired");
			e.setHeader(HttpHeaders.Names.WWW_AUTHENTICATE, "Basic realm=\"" + realm + "\"");
			throw e;
		}

		String[] pieces = authorization.split(" ");
		byte[] bytes = DatatypeConverter.parseBase64Binary(pieces[1]);
		String credentials = new String(bytes);
		String[] parts = credentials.split(":");
		
		if (parts.length < 2)
		{
			throw new UnauthorizedException("Authenticationrequired");
		}

		request.addHeader(X_AUTHENTICATED_USER, parts[0]);
		request.addHeader(X_AUTHENTICATED_PASSWORD, parts[1]);
    }
}
