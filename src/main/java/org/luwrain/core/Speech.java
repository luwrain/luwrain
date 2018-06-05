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

public final class Speech
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

    private final Map<String, Channel> channels = new HashMap();
    private Channel defaultChannel = null;

    private int pitch = 50;
    private int rate = 50;

    Speech(CmdLine cmdLine, Registry registry)
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
	if (value < 0)
	    rate = 0; else 
	    if (value > 100)
		rate = 100; else
		rate = value;
	defaultChannel.setDefaultRate(rate);
	settings.setRate(rate);
    }

    int getPitch()
    {
	return pitch;
    }

    void setPitch(int value)
    {
	if (value < 0)
	    pitch = 0; else 
	    if (value > 100)
		pitch = 100; else
		pitch = value;
	defaultChannel.setDefaultPitch(pitch);
	settings.setPitch(pitch);
    }

    Channel getReadingChannel()
    {
	return getAnyChannelByCond(EnumSet.of(Channel.Features.CAN_SYNTH_TO_SPEAKERS, Channel.Features.CAN_NOTIFY_WHEN_FINISHED));
    }

    boolean hasReadingChannel()
    {
	return false;
    }

    //Never returns default channe
    Channel[] getChannelsByCond(Set<Channel.Features> cond)
    {
	NullCheck.notNull(cond, "cond");
	final List<Channel> res = new LinkedList();
	for(Map.Entry<String, Channel> e: channels.entrySet())
	    if (e.getValue() != defaultChannel && e.getValue().getFeatures().containsAll(cond))
		res.add(e.getValue());
	return res.toArray(new Channel[res.size()]);
    }

    Channel getAnyChannelByCond(Set<Channel.Features> cond)
    {
	final Channel[] res = getChannelsByCond(cond);
	if (res.length < 1)
	    return null;
	return res[0];
    }

    public Channel[] getAllChannels()
    {
	final List<Channel> res = new LinkedList<Channel>();
	for(Map.Entry<String, Channel> e: channels.entrySet())
	    res.add(e.getValue());
	return res.toArray(new Channel[res.size()]);
    }

    public boolean isDefaultChannel(Channel channel)
    {
	NullCheck.notNull(channel, "channel");
	return channel == defaultChannel;
    }

    boolean init(Factory[] factories)
    {
	NullCheck.notNullItems(factories, "factories");
	final String speechArg = cmdLine.getFirstArg(SPEECH_PREFIX);
	if (speechArg != null && !speechArg.isEmpty())
	{
	    final Channel main = loadChannelByStr(speechArg, factories);
	    if (main == null)
	    {
		Log.error(LOG_COMPONENT, "unable to initialize default speech channel with arguments line \'" + speechArg + "\'");
		return false;
	    }
	    channels.put(main.getChannelName(), main);
	    final String[] additional = cmdLine.getArgs(ADD_SPEECH_PREFIX);
	    final List<Channel> res = new LinkedList();
	    for(String s: additional)
	    {
		final Channel c = loadChannelByStr(s, factories);
		if (c != null)
		{
		    final String name = c.getChannelName();
		    if (channels.containsKey(name))
		    {
			Log.error(LOG_COMPONENT, "speech channel name \'" + name + "\' used more than ones");
			return false;
		    }
		    channels.put(name, c);
		}
	    }
	    defaultChannel = main;
	} else
	{
	    //from registry
	    loadRegistryChannels(factories);
	    if (!chooseDefaultChannel())
	    {
		Log.error(LOG_COMPONENT, "unable to choose the default speech channel");
		return false;
	    }
	}
	Log.debug(LOG_COMPONENT, "default speech channel is \'" + defaultChannel.getChannelName() + "\'");
	pitch = settings.getPitch(50);
	rate = settings.getRate(50);
	if (pitch < 0)
	    pitch = 0;
	if (pitch > 100)
	    pitch = 100;
	if (rate < 0)
	    rate = 0;
	if (rate > 100)
	    rate = 100;
	defaultChannel.setDefaultRate(rate);
	defaultChannel.setDefaultPitch(pitch);
	defaultChannel.setCurrentPuncMode(Channel.PuncMode.NONE);
	return true;
    }

    private void loadRegistryChannels(Factory[] factories)
    {
	NullCheck.notNullItems(factories, "factories");
	final String path = Settings.SPEECH_CHANNELS_PATH;
	final String[] dirs = registry.getDirectories(path);
	for(String s: dirs)
	{
	    final String dir = Registry.join(path, s);
	    final Settings.SpeechChannelBase channelBase = Settings.createSpeechChannelBase(registry, dir);
	    final String type = channelBase.getType("");
	    if (type.isEmpty())
	    {
		Log.error(LOG_COMPONENT, "no type information in " + dir);
		continue;
	    }
	    if (!hasFactory(factories, type))
	    {
		Log.error(LOG_COMPONENT, "no speech factory which is able to servc speech channels of type \'" + type + "\'");
		continue;
	    }
	    final Factory factory = findFactory(factories, type);
	    final Channel channel = factory.newChannel();
	    if (channel == null)
	    {
		Log.error(LOG_COMPONENT, "speech factory of type \'" + type + "\' is unable to create a channel instance");
		continue;
	    }
	    if (!channel.initByRegistry(registry, dir))
	    {
		Log.error(LOG_COMPONENT, "speech channel " + channel.getClass().getName() + " unable to initialize by registry data in " + dir);
		continue;
	    }
	    final String name = channel.getChannelName();
	    if (name.isEmpty())
	    {
		Log.error(LOG_COMPONENT, "no speech channel name in " + dir);
		continue;
	    }
	    if (channels.containsKey(name))
	    {
		Log.error(LOG_COMPONENT, "speech channels name \'" + name + " used more than ones, using only the first");
		continue;
	    }
	    channels.put(channel.getChannelName(), channel);
	    Log.debug(LOG_COMPONENT, "the registry speech channel " + name + "(" + dir + ") successfully loaded");
	}
    }

    private Channel loadChannelByStr(String arg, Factory[] factories)
    {
	NullCheck.notNullItems(factories, "factories");
	if (arg.isEmpty())
	{
	    Log.warning(LOG_COMPONENT, "an empty value of speech channel arguments in command line option, skipping");
	    return null;
	}
	final String[] params = arg.split(":", -1);
	final String type = params[0];
	if (!hasFactory(factories, type))
	{
	    Log.error(LOG_COMPONENT, "no speech factory to serve channel type \'" + type + "\'");
	}
	final Factory factory = findFactory(factories, type);
	final Channel res = factory.newChannel();
	if (res == null)
	{
	    Log.error(LOG_COMPONENT, "the factory is unable to load new speech channel of type \'" + type + "\'");
	    return null;
	}
	if (!res.initByArgs(params.length <= 1?new String[0]:Arrays.copyOfRange(params, 1, params.length)))
	{
	    Log.error(LOG_COMPONENT, "newly created channel of type \'" + type + "\' refuses to initialize, complete arguments line is \'" + arg + "\'");
	    return null;
	}
	if (res.getChannelName().isEmpty())
	{
	    Log.error(LOG_COMPONENT, "newly created channel of type \'" + type + "\' has an empty name");
	    return null;
	}
	return res;
    }

    private boolean chooseDefaultChannel()
    {
	Channel any = null;
	for(Map.Entry<String, Channel> e: channels.entrySet())
	{
	    final Channel c = e.getValue();
	    if (!c.getFeatures().contains(Channel.Features.CAN_SYNTH_TO_SPEAKERS))
		continue;
	    any = c;
	    if (c.isDefault())
	    {
		defaultChannel = c;
		return true;
	    }
	}
	if (any == null)
	{
	    Log.error(LOG_COMPONENT, "unable to select a default speech channel capable of synthesizing to speakers");
	    return false;
	}
	defaultChannel = any;
	return true;
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
}
