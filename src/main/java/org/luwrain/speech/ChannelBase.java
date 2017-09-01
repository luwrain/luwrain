/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.speech;

import org.luwrain.core.*;

public abstract class ChannelBase implements Channel
{
    protected int defaultPitch = 50;
    protected int defaultRate = 50;
    protected String channelName = "";
    protected PuncMode puncMode = PuncMode.ALL;
    protected boolean defaultChannel = false;

    @Override public String getChannelName()
    {
	return channelName;
    }

    @Override public boolean isDefault()
    {
	return defaultChannel;
    }

    @Override public void setDefaultPitch(int value)
    {
	defaultPitch=properRange(value);
    }

    @Override public void setDefaultRate(int value)
    {
	defaultRate=properRange(value);
    }

    @Override public int getDefaultRate()
    {
	return defaultRate;
    }

    @Override public int getDefaultPitch()
    {
	return defaultPitch;
    }

    @Override public void setCurrentPuncMode(PuncMode puncMode)
    {
	NullCheck.notNull(puncMode, "puncMode");
	this.puncMode = puncMode;
    }

    @Override public PuncMode getCurrentPuncMode()
    {
	return puncMode;
    }

    static protected int properRange(int value)
    {
	if(value < 0)
	    return 0;
	if(value > 100)
	    return 100;
	return value;
    }
}
