/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

class Speech
{
    static private final String SPEECH_PREFIX = "--speech=";
    static private final String ADD_SPEECH_PREFIX = "--add-speech=";

    private final HashMap<String, Factory> factories = new HashMap<String, Factory>();
    private final HashMap<String, Channel> channels = new HashMap<String, Channel>();
    private Channel defaultChannel = null;
    private CmdLine cmdLine;
    private Registry registry;
    private Settings.SpeechParams settings;
    private int pitch = 50;
    private int rate = 50;

    Speech(CmdLine cmdLine, Registry registry)
    {
	NullCheck.notNull(cmdLine, "cmdLine");
	NullCheck.notNull(registry, "registry");
	this.cmdLine = cmdLine;
	this.registry = registry;
	settings = Settings.createSpeechParams(registry);
    }

    boolean init()
    {
	final String speechArg = cmdLine.getFirstArg(SPEECH_PREFIX);
	if (speechArg != null && !speechArg.isEmpty())
	{
	    Log.debug("core", "trying to initialize speech channel for main speech output with arguments line \'" + speechArg + "\', skipping all channels in the registry");
	    final Channel main = loadChannelByStr(speechArg);
	    if (main == null)
	    {
		Log.error("core", "unable to initialize default speech channel with arguments line \'" + speechArg + "\'");
		return false;
	    }
	    channels.put(main.getChannelName(), main);
	    final String[] additional = cmdLine.getArgs(ADD_SPEECH_PREFIX);
	    final LinkedList<Channel> res = new LinkedList<Channel>();
	    for(String s: additional)
	    {
		Log.debug("core", "initializing addition speech channel with arguments line \'" + s + "\'");
		final Channel c = loadChannelByStr(s);
		if (c != null)
		{
		    final String name = c.getChannelName();
		    if (channels.containsKey(name))
		    {
			Log.error("core", "speech channel name \'" + name + "\' used more than ones");
			return false;
		    }
		    channels.put(name, c);
		}
	    }
	    defaultChannel = main;
	} else
	{
	    //from registry
	    loadRegistryChannels();
	    if (!chooseDefaultChannel())
	    {
		Log.error("core", "unable to choose the default speech channel");
		return false;
	    }
	}
	Log.info("core", "default speech channel is \'" + defaultChannel.getChannelName() + "\'");
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
	defaultChannel.setDefaultPitch(50);
	return true;
    }

    //Always cancels any previous text to speak
    void speak(String text, int relPitch, int relRate)
    {
	NullCheck.notNull(text, "text");
	if (defaultChannel == null)
	    return;
	defaultChannel.speak(text, null, relPitch, relRate, true);
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
	final LinkedList<Channel> res = new LinkedList<Channel>();
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

    Channel[] getAllChannels()
    {
	final LinkedList<Channel> res = new LinkedList<Channel>();
	for(Map.Entry<String, Channel> e: channels.entrySet())
	    res.add(e.getValue());
	return res.toArray(new Channel[res.size()]);
    }

    boolean isDefaultChannel(Channel channel)
    {
	NullCheck.notNull(channel, "channel");
	return channel == defaultChannel;
    }

    boolean addFactory(Factory factory)
    {
	NullCheck.notNull(factory, "factory");
	factories.put(factory.getServedChannelType(), factory);
	return true;
    }

    boolean hasFactoryForType(String type)
    {
	NullCheck.notNull(type, "type");
	return factories.containsKey(type);
    }

    org.luwrain.cpanel.Section getSettingsSection(String type,
						  org.luwrain.cpanel.Element el, String path)
    {
	NullCheck.notNull(type, "type");
	NullCheck.notNull(el, "el");
	NullCheck.notNull(path, "path");
	if (!factories.containsKey(type))
	    return null;
	return factories.get(type).newSettingsSection(el, path);
    }

    private void loadRegistryChannels()
    {
	Log.debug("core", "loading registry speech channels");
	final String path = Settings.SPEECH_CHANNELS_PATH;
	final String[] dirs = registry.getDirectories(path);
	for(String s: dirs)
	{
	    final String dir = Registry.join(path, s);
	    Log.debug("core", "trying the channel from " + dir);
	    final Settings.SpeechChannelBase channelBase = Settings.createSpeechChannelBase(registry, dir);
	    final String type = channelBase.getType("");
	    Log.debug("core", "channel\'s type is \'" + type + "\'");
	    if (type.isEmpty())
	    {
		Log.error("core", "no type information in " + dir);
		continue;
	    }
	    /*
	    final Channel channel = os.loadSpeechChannel(type);
	    if (channel == null)
	    {
		Log.error("core", "the OS unable to load a speech channel of type \'" + type + "\'");
		continue;
	    }
	    if (!channel.initByRegistry(registry, dir))
	    {
		Log.error("core", "speech channel " + channel.getClass().getName() + " loaded from the registry directory " + s + " refused to be initialized");
		continue;
	    }
	    */

	    if (!factories.containsKey(type))
	    {
		Log.error("core", "no speech factory which is able to servc speech channels of type \'" + type + "\'");
		continue;
	    }
	    final Factory factory = factories.get(type);

	    final Channel channel = factory.newChannel();
	    if (channel == null)
	    {
		Log.error("core", "speech factory of type \'" + type + "\' is unable to create a channel instance");
		continue;
	    }
	    if (!channel.initByRegistry(registry, dir))
	    {
		Log.error("core", "speech channel " + channel.getClass().getName() + " unable to initialize by registry data in " + dir);
		continue;
	    }

	    final String name = channel.getChannelName();
	    if (name.isEmpty())
	    {
		Log.error("core", "no speech channel name in " + dir);
		continue;
	    }
	    if (channels.containsKey(name))
	    {
		Log.error("core", "speech channels name \'" + name + " used more than ones, using only the first");
		continue;
	    }
	    channels.put(channel.getChannelName(), channel);
	    Log.info("core", "registry speech channel " + name + "(" + dir + ") successfully loaded");
	}
    }

    private Channel loadChannelByStr(String arg)
    {
	Log.debug("core", "trying to prepare new speech channel with complete arguments line \'" + arg + "\'");
	if (arg.isEmpty())
	{
	    Log.warning("core", "an empty value of speech channel arguments in command line option, skipping");
	    return null;
	}
	final String[] params = arg.split(":", -1);
	final String type = params[0];
	if (!factories.containsKey(type))
	{
	    Log.error("core", "no speech factory to serve channel type \'" + type + "\'");
	}
	final Factory factory = factories.get(type);
	final Channel res = factory.newChannel();
	if (res == null)
	{
	    Log.error("core", "the factory is unable to load new speech channel of type \'" + type + "\'");
	    return null;
	}
	Log.debug("core", "initializing the newly created channel");
	if (!res.initByArgs(params.length <= 1?new String[0]:Arrays.copyOfRange(params, 1, params.length)))
	{
	    Log.error("core", "newly created channel of type \'" + type + "\' refuses to initialize, complete arguments line is \'" + arg + "\'");
	    return null;
	}
	if (res.getChannelName().isEmpty())
	{
	    Log.error("core", "newly created channel of type \'" + type + "\' has an empty name");
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
	    Log.error("core", "unable to select a default speech channel capable of synthesizing to speakers");
	    return false;
	}
	defaultChannel = any;
	return true;
    }
}
