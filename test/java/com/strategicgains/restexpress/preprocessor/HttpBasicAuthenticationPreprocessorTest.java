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

import static org.junit.Assert.*;

import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.junit.Test;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.exception.UnauthorizedException;
import com.strategicgains.restexpress.pipeline.Preprocessor;

/**
 * @author toddf
 * @since Feb 28, 2013
 */
public class HttpBasicAuthenticationPreprocessorTest
{
	private Preprocessor p = new HttpBasicAuthenticationPreprocessor();
	Request r = new Request(new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/"), null);

	@Test(expected=UnauthorizedException.class)
	public void shouldThrowUnauthorizedExceptionOnNullHeader()
	{
		p.process(r);
	}
	
	@Test
	public void shouldSetHeadersOnSuccess()
	{
		r.addHeader(HttpHeaders.Names.AUTHORIZATION, "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==");
		p.process(r);
		assertEquals("Aladdin", r.getRawHeader(HttpBasicAuthenticationPreprocessor.X_AUTHENTICATED_USER));
		assertEquals("open sesame", r.getRawHeader(HttpBasicAuthenticationPreprocessor.X_AUTHENTICATED_PASSWORD));
	}
}
