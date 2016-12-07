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

import org.luwrain.core.events.*;
import org.luwrain.cpanel.*;
import org.luwrain.popups.Popups;

class SpeechControlPanelFactory implements Factory
{
    static private final Element channelsElement = new SimpleElement(StandardElements.SPEECH, SpeechControlPanelFactory.class.getName());

    private Luwrain luwrain;
    private Speech speech;

    SpeechControlPanelFactory(Luwrain luwrain, Speech speech)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(speech, "speech");
	this.luwrain = luwrain;
	this.speech = speech;
    }

    @Override public Element[] getElements()
    {
	final LinkedList<Element> res = new LinkedList<Element>();
	res.add(channelsElement);
	final Element[] channels = readChannelsData(channelsElement, luwrain.getRegistry(), speech);
	for(Element e: channels)
	    res.add(e);
	return res.toArray(new Element[res.size()]);
    }

    @Override public Element[] getOnDemandElements(Element parent)
    {
	return new Element[0];
    }

    @Override public Section createSection(Element el)
    {
	NullCheck.notNull(el, "el");
	if (el.equals(channelsElement))
	    return new SimpleSection(channelsElement, luwrain.i18n().getStaticStr("CpSpeechChannels"), null,
				     new Action[]{new Action("add-speech-channel", luwrain.i18n().getStaticStr("CpAddNewSpeechChannel"))}, (controlPanel, event)->onActionEvent(controlPanel, event));
	if (!(el instanceof ChannelElement))
	return null;
	final ChannelElement c = (ChannelElement)el;
	return speech.getSettingsSection(c.type(), el, c.path());
    }

    private boolean onActionEvent(ControlPanel controlPanel, EnvironmentEvent event)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	NullCheck.notNull(event, "event");
	if (!ActionEvent.isAction(event, "add-speech-channel"))
	return false;
	final Luwrain luwrain = controlPanel.getCoreInterface();
	final String[] types = "voiceman:command:emacspeak".split(":", -1);
	Arrays.sort(types);
	    final Object res = Popups.fixedList(luwrain, luwrain.i18n().getStaticStr("CpAddNewSpeechChannelPopupName"), types);
	    if (res == null)
		return true;
	    final RegistryKeys keys = new RegistryKeys();
	    final Registry registry = luwrain.getRegistry();
	    final int num = Registry.nextFreeNum(registry, Settings.SPEECH_CHANNELS_PATH);
	    final String path = Registry.join(Settings.SPEECH_CHANNELS_PATH, "" + num); 
	    registry.addDirectory(path);
	    final Settings.SpeechChannelBase settings = Settings.createSpeechChannelBase(registry, path);
	    settings.setType(res.toString());
	    settings.setName(luwrain.i18n().getStaticStr("CpNewSpeechChannelName") + " " + num);
	    return true;
    }

    static private Element[] readChannelsData(Element parent,
					      Registry registry, Speech speech)
    {
	final LinkedList<Element> res = new LinkedList<Element>();
	final String path = Settings.SPEECH_CHANNELS_PATH;
	final String[] dirs = registry.getDirectories(path);
	for(String s: dirs)
	{
	    final String dir = Registry.join(path, s);
	    final Settings.SpeechChannelBase channelBase = Settings.createSpeechChannelBase(registry, dir);
	    final String type = channelBase.getType("");
	    if (type.isEmpty() || !speech.hasFactoryForType(type))
		continue;
	    res.add(new ChannelElement(parent, type, dir));
	}
	return res.toArray(new Element[res.size()]);
    }

    static private class ChannelElement implements Element
    {
	private Element parent;
	private String type, path;

	ChannelElement(Element parent,
		       String type, String path)
	{
	    NullCheck.notNull(parent, "parent");
	    NullCheck.notNull(type, "type");
	    NullCheck.notNull(path, "path");
	    this.parent = parent;
	    this.type = type;
	    this.path = path;
	}

	@Override public Element getParentElement()
	{
	    return parent;
	}

	@Override public String toString()
	{
	    return this.getClass().getName() +":" + type + ":" + path;
	}

	@Override public boolean equals(Object o)
	{
	    if (o == null || !(o instanceof ChannelElement))
		return false;
	    return type == ((ChannelElement)o).type &&
	    path == ((ChannelElement)o).path;
	}

	@Override public int hashCode()
	{
	    return toString().hashCode();
	}

	String type() {return type;}
	String path() {return path;}
    }
}
