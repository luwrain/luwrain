/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core.properties;

import java.io.*;
import java.util.*;

import org.luwrain.base.*;
import org.luwrain.core.*;
import org.luwrain.speech.*;

public final class Speech implements PropertiesProvider
{
    private final org.luwrain.core.Speech speech;
    private PropertiesProvider.Listener listener = null;

    public Speech(org.luwrain.core.Speech speech)
    {
	NullCheck.notNull(speech, "speech");
	this.speech = speech;
    }

    @Override public String getExtObjName()
    {
	return this.getClass().getName();
    }

    @Override public String[] getPropertiesRegex()
    {
	return new String[]{"^luwrain \\.speech\\."};
    }

    @Override public Set<PropertiesProvider.Flags> getPropertyFlags(String propName)
    {
	return EnumSet.of(PropertiesProvider.Flags.PUBLIC);
    }

    @Override public String getProperty(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
	if (!propName.startsWith("luwrain.speech.channel."))
	    return null;
	final String arg = propName.substring("luwrain.speech.channel.".length());
	final String[] args = arg.split("\\.", -1);
	if (args.length != 2 ||
	    args[0].isEmpty() ||
	    args[1].isEmpty())
	    return null;
	final int n;
	try {
	    n = Integer.parseInt(args[0]);
	}
	catch(NumberFormatException e)
	{
	    return null;
	}
	/*
	final Channel[] channels = speech.getAllChannels();
	if (n < 0 || n >= channels.length)
	    return null;
	final Channel channel = channels[n];
	switch(args[1])
	{
	case "name":
	    return channel.getChannelName();
	case "class":
	    return channel.getClass().getName();
	case "default":
	    return speech.isDefaultChannel(channel)?"1":"0";
	case "cansynthtospeakers":
	    return channel.getFeatures().contains(Channel.Features.CAN_SYNTH_TO_SPEAKERS)?"1":"0";
	case "cansynthtostream":
	    return channel.getFeatures().contains(Channel.Features.CAN_SYNTH_TO_STREAM)?"1":"0";
	case "cannotifywhenfinished":
	    return channel.getFeatures().contains(Channel.Features.CAN_NOTIFY_WHEN_FINISHED)?"1":"0";
	default:
	    return null;
	}
	*/
	return null;
    }

    @Override public File getFileProperty(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
	return null;
    }

    @Override public boolean setProperty(String propName, String value)
    {
	NullCheck.notEmpty(propName, "propName");
	NullCheck.notNull(value, "value");
	return false;
    }

    @Override public boolean setFileProperty(String propName, File value)
    {
	NullCheck.notEmpty(propName, "propName");
	NullCheck.notNull(value, "value");
	return false;
    }

    @Override public void setListener(PropertiesProvider.Listener listener)
    {
	this.listener = listener;
    }
}
