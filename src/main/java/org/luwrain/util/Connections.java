/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

//LWR_API 1.0

package org.luwrain.util;

import java.net.*;
import java.io.*;

import org.luwrain.core.*;

public final class Connections
{
    static public final String DEFAULT_USER_AGENT = "Mozilla/5.0";
    static private final int MAX_REDIRECT_COUNT = 16;
    static private final int TIMEOUT = 15000;

    static public final class InvalidHttpResponseCodeException extends IOException
    {
	private final int code;
	private final String url;
	public InvalidHttpResponseCodeException(int code, String url)
	{
	    super("" + code + " for " + url);
	    this.code = code;
	    this.url = url;
	}
	public int getHttpCode()
	{
	    return this.code;
	}
	public String getHttpUrl()
	{
	    return this.url;
	}
    }

    public static URLConnection connect(URI uri, long startFrom) throws IOException
    {
	NullCheck.notNull(uri, "uri");
	URL urlToTry = uri.toURL();
	for(int i = 0;i < MAX_REDIRECT_COUNT;++i)
	{
	    final URLConnection con = urlToTry.openConnection();
	    if (!(con instanceof HttpURLConnection))
	    {
		con.connect();
		return con;
	    }
	    final HttpURLConnection httpCon = (HttpURLConnection)con;
	    httpCon.setRequestProperty("User-Agent", DEFAULT_USER_AGENT);
	    httpCon.setConnectTimeout(TIMEOUT);
	    httpCon.setReadTimeout(TIMEOUT);
	    if (startFrom > 0)
		httpCon.setRequestProperty("Range", "bytes=" + startFrom + "-");
	    httpCon.setInstanceFollowRedirects(false);
	    httpCon.connect();
	    if (httpCon.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM ||
		httpCon.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP)
	    {
		final String location = httpCon.getHeaderField("location");
		if (location == null || location.isEmpty())
		    throw new IOException("The response has the redirect code but the location is empty (" + urlToTry.toString() + ")");
		String decodedLocation = null;
		try {
		    //We can see a little mess around using of non-ASCII characters in HTTP headers
		    decodedLocation = new String(location.getBytes("ISO8859-1"), "UTF-8");
		}
		catch(Exception e)
		{
		}
		if (decodedLocation == null)
		    decodedLocation = location;
		urlToTry = new URL(urlToTry, decodedLocation);
		continue; 
	    }
	    if ((startFrom == 0 && httpCon.getResponseCode() != HttpURLConnection.HTTP_OK) ||
		(startFrom > 0 && httpCon.getResponseCode() != HttpURLConnection.HTTP_PARTIAL))
		throw new InvalidHttpResponseCodeException(httpCon.getResponseCode(), urlToTry.toString());
	    return httpCon;
	}
	throw new IOException("Too many redirects (" + MAX_REDIRECT_COUNT + ")");
    }
}
