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

package org.luwrain.core;

import java.util.*;

import org.luwrain.speech.*;

public final class Speech2
{
    static private final String LOG_COMPONENT = Base.LOG_COMPONENT;

    static final int PITCH_HIGH = 25;
    static final int PITCH_NORMAL = 0;
    static final int PITCH_LOW = -25;
    static final int PITCH_HINT = -25;
    static final int PITCH_MESSAGE = -25;

    static private final String SPEECH_PREFIX = "--speech=";
    static private final String ADD_SPEECH_PREFIX = "--add-speech=";

    private final CmdLine cmdLine;
    private final Registry registry;
    private final Settings.SpeechParams settings;

    private final Map<String, Factory2> factories = new HashMap();
    private Channel2 defaultChannel = null;

    private int pitch = 50;
    private int rate = 50;

    Speech2(CmdLine cmdLine, Registry registry)
    {
	NullCheck.notNull(cmdLine, "cmdLine");
	NullCheck.notNull(registry, "registry");
	this.cmdLine = cmdLine;
	this.registry = registry;
	this.settings = Settings.createSpeechParams(registry);
    }

    //Always cancels any previous text to speak
    void speak(String text, int relPitch, int relRate)
    {
	if (text == null || text.isEmpty())
	    return;
	if (defaultChannel == null)
	    return;
	defaultChannel.speak(text, null, relPitch, relRate, true);
    }

    //Always cancels any previous text to speak
    void speakEventResponse(String text)
    {
	if (text == null || text.isEmpty())
	    return;
	if (defaultChannel == null)
	    return;
	defaultChannel.speak(text, null, 0, 0, true);
    }

    //Always cancels any previous text to speak
    void speakLetter(char letter, int relPitch, int relRate)
    {
	if (defaultChannel == null)
	    return;
	defaultChannel.speakLetter(letter, null, relPitch, relRate, true);
    }

    void silence()
    {
	if (defaultChannel == null)
	    return;
	defaultChannel.silence();
    }

    int getRate()
    {
	return rate;
    }

    void setRate(int value)
    {
    }

    int getPitch()
    {
	return pitch;
    }

    void setPitch(int value)
    {
    }


    //Never returns default channe
    Channel2[] getChannelsByCond(Set<Channel.Features> cond)
    {
	NullCheck.notNull(cond, "cond");
	final List<Channel> res = new LinkedList();
	return null;
	/*	
	for(Map.Entry<String, Channel> e: channels.entrySet())
	    if (e.getValue() != defaultChannel && e.getValue().getFeatures().containsAll(cond))
		res.add(e.getValue());
	return res.toArray(new Channel[res.size()]);
	*/
    }

    Channel getAnyChannelByCond(Set<Channel.Features> cond)
    {
	return null;
	/*
	final Channel[] res = getChannelsByCond(cond);
	if (res.length < 1)
	    return null;
	return res[0];
	*/
    }

    boolean init(Factory2[] factories)
    {
	NullCheck.notNullItems(factories, "factories");
	for(Factory2 f: factories)
	{
	    final String name = f.getExtObjName();
	    if (name == null || name.isEmpty())
	    {
		Log.warning(LOG_COMPONENT, "the speech factory with empty name found, skipping it");
		continue;
	    }
	    if (this.factories.containsKey(name))
	    {
		Log.warning(LOG_COMPONENT, "two speech factories with the same name \'" + name + "\'");
		continue;
	    }
	    this.factories.put(name, f);
	}
	final String factoryName;
	final Map<String, String> params = new HashMap();
	final String speechArg = cmdLine.getFirstArg(SPEECH_PREFIX);
	if (speechArg != null && !speechArg.isEmpty())
	{
	    factoryName = parseChannelLine(speechArg, params);
	    if (factoryName == null)
	    {
		Log.error(LOG_COMPONENT, "unable to parse speech channel loading line: \'" + speechArg + "\'");
		return false;
	    }
	} else
	factoryName = "rhvoice";//Take from registry
	this.defaultChannel = loadChannel(factoryName, params);
	if (defaultChannel == null)
	{
	    Log.error(LOG_COMPONENT, "unable to load the default channel of the factory \'" + factoryName + "\'");
	    return false;
	}
	return true;
    }

    private Channel2 loadChannel(String factoryName, Map<String, String> params)
    {
	NullCheck.notEmpty(factoryName, "factoryName");
	NullCheck.notNull(params, "params");
	if (!factories.containsKey(factoryName))
	{
	    Log.error(LOG_COMPONENT, "no such speech factory: \'" + factoryName + "\'");
	    return null;
	}
	return factories.get(factoryName).newChannel(params);
    }


    static private Factory findFactory(Factory[] factories, String name)
    {
	NullCheck.notNullItems(factories, "factories");
	NullCheck.notEmpty(name, "name");
	for(Factory f: factories)
	    if (f.getExtObjName().equals(name))
		return f;
	return null;
    }

    static private boolean hasFactory(Factory[] factories, String name)
    {
	NullCheck.notNullItems(factories, "factories");
	NullCheck.notEmpty(name, "name");
	return findFactory(factories, name) != null;
    }

    static private String parseChannelLine(String line, Map<String, String> params)
    {
	NullCheck.notNull(line, "line");
	NullCheck.notNull(params, "params");
	final int pos = line.indexOf(line);
	if (pos <= 0)
	    return null;
	if (!parseParams(line.substring(pos + 1), params))
	    return null;
	return line.substring(0, pos);
    }

    static private boolean parseParams(String line, Map<String, String> params)
    {
	NullCheck.notNull(line, "line");
	NullCheck.notNull(params, "params");
	if (line.isEmpty())
	    return true;
	final List<String> items = new LinkedList();
	String item = "";
	for(int i = 0;i < line.length();++i)
	{
	    final char c = line.charAt(i);
	    switch(c)
	    {
	    case ',':
		if (i == 0 || line.charAt(i - 1) != '\\')
		{
		    items.add(item);
		    item = "";
		    continue;
		}
				item += c;
				break;
	    default:
		item += c;
	    }
	}
	items.add(item);
	for(String s: items)
	{
	    final int pos = s.indexOf("=");
	    if (pos <= 0)
		return false;
	    params.put(s.substring(0, pos), s.substring(pos + 1));
	}
	return true;
    }
}
