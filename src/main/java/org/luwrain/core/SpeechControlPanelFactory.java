
package org.luwrain.core;

import java.util.*;

import org.luwrain.cpanel.*;
import org.luwrain.util.*;

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
	    return new SimpleSection(channelsElement, "Речевые каналы");
	if (!(el instanceof ChannelElement))
	return null;
	final ChannelElement c = (ChannelElement)el;
	return speech.getSettingsSection(c.type(), el, c.path());
    }

    static private Element[] readChannelsData(Element parent,
					      Registry registry, Speech speech)
    {
	final LinkedList<Element> res = new LinkedList<Element>();
	final String path = new RegistryKeys().speechChannels();
	final String[] dirs = registry.getDirectories(path);
	for(String s: dirs)
	{
	    final String dir = RegistryPath.join(path, s);
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
