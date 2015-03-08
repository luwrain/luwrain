/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.util;

public class CopyCutInfo
{
    private CopyCutRequest request;
    private int fromX = -1;
    private int fromY  = -1;

    public CopyCutInfo(CopyCutRequest request)
    {
	this.request = request;
	if (request == null)
	    throw new NullPointerException("request may not be null");
    }

    public boolean copyCutPoint(int hotPointX, int hotPointY)
    {
	fromX = hotPointX;
	fromY = hotPointY;
	return true;
    }

    public boolean copy(int hotPointX, int hotPointY)
    {
	if (fromX < 0 || fromY < 0)
	    return request.onCopyAll();
	if (fromY < hotPointY)
	    return request.onCopy(fromX, fromY, hotPointX, hotPointY);
	if (fromY > hotPointY)
	    return request.onCopy(hotPointX, hotPointY, fromX, fromY);
	if (fromX < hotPointX)
	    return request.onCopy(fromX, fromY, hotPointX, hotPointY);
	if (fromX > hotPointX)
	    return request.onCopy(hotPointX, hotPointY, fromX, fromY);
	return request.onCopyAll();
    }

    public boolean cut(int hotPointX, int hotPointY)
    {
	if (fromX < 0 || fromY < 0)
	    return false;
	if (fromY < hotPointY)
	    return request.onCut(fromX, fromY, hotPointX, hotPointY);
	if (fromY > hotPointY)
	    return request.onCut(hotPointX, hotPointY, fromX, fromY);
	if (fromX < hotPointX)
	    return request.onCut(fromX, fromY, hotPointX, hotPointY);
	if (fromX > hotPointX)
	    return request.onCut(hotPointX, hotPointY, fromX, fromY);
	return false;
    }
}
