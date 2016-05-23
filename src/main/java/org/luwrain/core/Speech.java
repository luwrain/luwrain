
package org.luwrain.core;

import java.util.*;

import org.luwrain.speech.*;
import org.luwrain.os.*;
import org.luwrain.util.RegistryPath;

class Speech
{
    static private final String SPEECH_PREFIX = "--speech=";
    static private final String ADD_SPEECH_PREFIX = "--add-speech=";

    private final Vector<Channel> channels = new Vector<Channel>();
    private Channel defaultChannel = null;
    private OperatingSystem os;
    private CmdLineUtils cmdLine;
    private Registry registry;
    private Settings.SpeechParams settings;
    private int pitch = 50;
    private int rate = 50;

    Speech(OperatingSystem os,
	   CmdLineUtils cmdLine, Registry registry)
    {

	NullCheck.notNull(os, "os");
	NullCheck.notNull(cmdLine, "cmdLine");
	NullCheck.notNull(registry, "registry");
	this.os = os;
	this.cmdLine = cmdLine;
	this.registry = registry;
	settings = Settings.createSpeechParams(registry);
    }

    boolean init()
    {
	final String speechArg = cmdLine.getFirstArg(SPEECH_PREFIX);
	if (speechArg != null)
	{
	    Log.debug("core", "trying to initialize speech channel for main speech output with arguments line \'" + speechArg + "\', skipping all channels in the registry");
	    final Channel main = loadChannelByStr(speechArg);
	    if (main == null)
	    {
		Log.error("core", "unable to initialize main speech channel with arguments line \'" + speechArg + "\'");
		return false;
	    }
	    final String[] additional = cmdLine.getArgs(ADD_SPEECH_PREFIX);
	    final LinkedList<Channel> res = new LinkedList<Channel>();
	    channels.add(main);
	    for(String s: additional)
	    {
		Log.debug("core", "initializing addition speech channel with arguments line \'" + s + "\'");
		final Channel c = loadChannelByStr(s);
		if (c != null)
channels.add(c);
	    }
	    defaultChannel = main;
	} else
	{
	loadRegistryChannels();
	if (!chooseDefaultChannel())
	{
	    Log.error("core", "unable to choose the default channel");
	    return false;
	}
	}
	Log.debug("core", "default speech channel is \'" + defaultChannel.getChannelName() + "\'");
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

    private void loadRegistryChannels()
    {
	Log.debug("core", "loading registry speech channels");
	final String path = "/org/luwrain/speech/channels";
	final String[] dirs = registry.getDirectories(path);
	for(String s: dirs)
	{
	    final String dir = RegistryPath.join(path, s);
	    Log.debug("core", "trying the channel from " + dir);
	    final Settings.SpeechChannelBase channelBase = Settings.createSpeechChannelBase(registry, dir);
	    final String type = channelBase.getType("");
	    Log.debug("core", "channel\'s type is \'" + type + "\'");
	    if (type.isEmpty())
	    {
		Log.warning("core", "no type information in speech channel registry directory at " + dir);
		continue;
	    }
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
	    channels.add(channel);
	    Log.info("core", "registry speech channel " + channel.getChannelName() + "(" + dir + ") successfully loaded");
	}
    }

    private Channel loadChannelByStr(String arg)
    {
	Log.debug("core", "trying to prepare new speech channel with complete arguments line \'" + arg + "\'");
	if (arg.isEmpty())
	{
	    Log.warning("core", "an empty value of speech channel parameters in command line option, skipping");
	    return null;
	}
	final String[] params = arg.split(":", -1);
	final String type = params[0];
	Log.debug("core", "asking the operating system to prepare new speech channel of type \'" + type + "\'");
	final Channel res = os.loadSpeechChannel(params[0]);
	if (res == null)
	{
	    Log.error("core", "the operating system is unable to load new speech channel of type \'" + type + "\'");
	    return null;
	}
	Log.debug("core", "initializing the newly created channel");
	if (!res.initByArgs(params.length <= 1?new String[0]:Arrays.copyOfRange(params, 1, params.length)))
	{
	    Log.error("core", "newly created channel of type \'" + type + "\' refuses to initialize, complete arguments line is \'" + arg + "\'");
	    return null;
	}
	    return res;
    }

    private boolean chooseDefaultChannel()
    {
	Channel any = null;
	for(Channel c: channels)
	{
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

    void speak(String text, int relPitch, int relRate)
    {
	defaultChannel.silence();
	if (text != null)
	    defaultChannel.speak(text, null, relPitch, relRate);
    }

    void speakLetter(char letter, int relPitch, int relRate)
    {
	defaultChannel.silence();
	defaultChannel.speakLetter(letter, null, relPitch, relRate);
    }

    void silence()
    {
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

    //Never returns default channe;
    Channel[] getChannelsByCond(Set<Channel.Features> cond)
    {
	NullCheck.notNull(cond, "cond");
	final LinkedList<Channel> res = new LinkedList<Channel>();
	for(Channel c: channels)
	    if (c != defaultChannel && c.getFeatures().containsAll(cond))
		res.add(c);
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
	return channels.toArray(new Channel[channels.size()]);
    }

    boolean isDefaultChannel(Channel channel)
    {
	NullCheck.notNull(channel, "channel");
	return channel == defaultChannel;
    }
}
