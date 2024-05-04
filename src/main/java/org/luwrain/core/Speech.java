/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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
import org.apache.logging.log4j.*;

import org.luwrain.speech.*;

public final class Speech
{
    static private final Logger LOG = LogManager.getLogger();
    static final int PITCH_HINT = -25;
    static final int PITCH_MESSAGE = -25;
    static private final String SPEECH_PREFIX = "--speech=";

    private final CmdLine cmdLine;
    private final Settings.SpeechParams sett;
    private final Map<String, Engine> engines = new HashMap<>();
    private Channel defaultChannel = null;
    private int pitch = 50;
    private int rate = 50;

    Speech(CmdLine cmdLine, Registry registry)
    {
	NullCheck.notNull(cmdLine, "cmdLine");
	NullCheck.notNull(registry, "registry");
	this.cmdLine = cmdLine;
	this.sett = Settings.createSpeechParams(registry);
	this.pitch = sett.getPitch(this.pitch);
	this.rate = sett.getRate(this.rate);
    }

    void init(Engine[] engines)
    {
	NullCheck.notNullItems(engines, "engines");
	for(Engine e: engines)
	{
	    final String name = e.getExtObjName();
	    if (name == null || name.isEmpty())
	    {
		LOG.warn("The speech engine with empty name found, skipping it");
		continue;
	    }
	    if (this.engines.containsKey(name))
	    {
		LOG.warn("Two speech engine with the same name \'" + name + "\'");
		continue;
	    }
	    this.engines.put(name, e);
	}
	final String engineName;
	final Map<String, String> params = new HashMap<>();
	final String speechArg = cmdLine.getFirstArg(SPEECH_PREFIX);
	if (speechArg != null && !speechArg.isEmpty())
	{
	    engineName = parseChannelLine(speechArg, params);
	    if (engineName == null)
	    {
		LOG.error("Unable to parse speech channel loading line: \'" + speechArg + "\'");
		defaultChannel = null;
		return;
	    }
	} else
	{
	    engineName = sett.getMainEngineName("");
	    if (engineName.isEmpty())
	    {
		LOG.error("No engine name in the registry for the main speech channel");
		defaultChannel = null;
		return;
	    }
	    final String paramsLine = sett.getMainEngineParams("");
	    if (!parseParams(paramsLine, params))
	    {
		LOG.error("Unable to parse the params line for the engine \'" + engineName + "\':" + paramsLine);
		defaultChannel = null;
		return;
	    }
	}
	this.defaultChannel = loadChannel(engineName, params);
	if (defaultChannel != null)
	    LOG.debug("Main speech engine is \'" + engineName + "\'"); else
	    LOG.error("Unable to load the default channel of the engine \'" + engineName + "\'");
    }

        public Channel loadChannel(String engineName, String paramsLine)
    {
	NullCheck.notEmpty(engineName, "engineName");
	NullCheck.notNull(paramsLine, "paramsLine");
	final Map<String, String> params = new HashMap<>();
	if (!parseParams(paramsLine, params))
	    return null;
	return loadChannel(engineName, params);
    }

    private Channel loadChannel(String engineName, Map<String, String> params)
    {
	NullCheck.notEmpty(engineName, "engineName");
	NullCheck.notNull(params, "params");
	if (!engines.containsKey(engineName))
	{
	    LOG.error("No such speech engine: \'" + engineName + "\'");
	    return null;
	}
	return engines.get(engineName).newChannel(params);
    }

    //Always cancels any previous text to speak
    public void speak(String text, int relPitch, int relRate)
    {
	NullCheck.notNull(text, "text");
	if (defaultChannel == null || text.isEmpty())
	    return;
	defaultChannel.speak(text, null, makePitch(relPitch), makeRate(relRate), true);
    }

    //Always cancels any previous text to speak
    public void speakEventResponse(String text)
    {
	NullCheck.notNull(text, "text");
	if (defaultChannel == null || text.isEmpty())
	    return;
	defaultChannel.speak(text, null, makePitch(0), makeRate(0), true);
    }

    //Always cancels any previous text to speak
    public void speakLetter(char letter, int relPitch, int relRate)
    {
	if (defaultChannel == null)
	    return;
	defaultChannel.speakLetter(letter, null, makePitch(relPitch), makeRate(relRate), true);
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
	if (value < 0)
	    this.rate = 0; else
	    if (value > 100)
		this.rate = 100; else
		this.rate = value;
	sett.setRate(this.rate);
    }

    int getPitch()
    {
	return pitch;
    }

    void setPitch(int value)
    {
	if (value < 0)
	    this.pitch = 0; else
	    if (value > 100)
		this.pitch = 100; else
		this.pitch = value;
	sett.setPitch(this.pitch);
    }

    private int makePitch(int relPitch)
    {
	final int value = pitch + relPitch - 50;
	if (value < -50)
	    return -50;
	if (value > 50)
	    return 50;
	return value;
    }

    private int makeRate(int relRate)
    {
	final int value = rate + relRate - 50;
	if (value < -50)
	    return -50;
	if (value > 50)
	    return 50;
	return value;
    }

    static private String parseChannelLine(String line, Map<String, String> params)
    {
	NullCheck.notNull(line, "line");
	NullCheck.notNull(params, "params");
	final int pos = line.indexOf(":");
	if (pos <= 0)
	    return line;
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
	final List<String> items = new ArrayList<>();
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
