
package org.luwrain.core;

import java.util.*;

import org.luwrain.speech.*;
import org.luwrain.os.*;
import org.luwrain.util.RegistryPath;

class Speech
{
    static private final String NO_REGISTRY_CHANNELS = "--speech-no-registry";
    static private final String LOAD_CHANNEL = "--speech-load-channel=";

    private final Vector<Channel> channels = new Vector<Channel>();
    private Channel defaultChannel = null;
    private OperatingSystem os;
    private String[] cmdLine;
    private Registry registry;
    private CmdLineUtils cmdLineUtils;

    Speech(OperatingSystem os,
	   String[] cmdLine, Registry registry)
    {
	this.os = os;
	this.cmdLine = cmdLine;
	this.registry = registry;
	NullCheck.notNull(os, "os");
	NullCheck.notNullItems(cmdLine, "cmdLine");
	NullCheck.notNull(registry, "registry");
	cmdLineUtils = new CmdLineUtils(cmdLine);
    }

    boolean init()
    {
	if (!cmdLineUtils.used(NO_REGISTRY_CHANNELS))
	    loadRegistryChannels();
	loadFromCmdLine();
	return chooseDefaultChannel();
    }

    private void loadFromCmdLine()
    {
	final String[] classes = cmdLineUtils.getArgs(LOAD_CHANNEL);
	for(String c: classes)
	{
	    Log.debug("core", "loading speech channel " + c + " by the command line option");
	    Object o;
	    try {
		o = Class.forName(c).newInstance();
	    }
	    catch(Exception e)
	    {
		Log.error("core", "unable to load speech channel " + c + " by a command line option:" + e.getMessage());
		e.printStackTrace();
		continue;
	    }
	    if (!(o instanceof Channel))
	    {
		Log.error("core", "the instance of " + c + " is not an instance of org.luwrain.speech.Channel");
		continue;
	    }
	    final Channel channel = (Channel)o;
	    if (!channel.init(cmdLine, null, null))
	    {
		Log.error("core", "speech channel " + c + " refuced to initialize");
		continue;
	    }
	    channels.add(channel);
	}
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
	    final Channel channel = os.loadSpeechChannel(cmdLine, registry, dir);
	    if (channel == null)
	    {
		Log.error("core", "OS unable to load a speech channel from the registry directory " + dir);
		continue;
	    }
	    if (!channel.init(cmdLine, registry, dir))
	    {
		Log.error("core", "speech channel " + channel.getClass().getName() + " loaded from the registry directory " + s + " refused to initialize");
		continue;
	    }
	    channels.add(channel);
	    Log.info("core", "registry speech channel " + channel.getChannelName() + "(" + dir + ") successfully loaded");
	}
    }

    private boolean chooseDefaultChannel()
    {
	Channel any = null;
	for(Channel c: channels)
	{
	    /*
	    if (!c.getFeatures().contains(Channel.Features.CAN_SYNTH_TO_SPEAKERS))
		continue;
	    */
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

    Channel getDefaultStreamingChannel()
    {
	//FIXME:
	return channels.get(0);
    }
}
