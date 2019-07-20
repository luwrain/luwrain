
package org.luwrain.popups;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.script.*;

public class DisksVolumePopup extends ListPopupBase
{
    static private final String LIST_HOOK = "luwrain.popups.disks.list";
        static private final String CLICK_HOOK = "luwrain.popups.disks.click";

protected File result = null;

    public DisksVolumePopup(Luwrain luwrain, String name, Set<Popup.Flags> popupFlags)
    {
	super(luwrain, constructParams(luwrain, name), popupFlags);
    }

    public File result()
    {
	return this.result;
    }

    @Override public boolean onInputEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() || !event.isModified())
	    switch(event.getSpecial())
	    {
	    case ENTER:
		return closing.doOk();
		/*
	    case INSERT:
	    case DELETE:
		*/
	    }
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() == EnvironmentEvent.Type.BROADCAST)
	    switch(event.getCode())
	    {
	    case REFRESH:
		if (event.getBroadcastFilterUniRef().startsWith("disksvolumes:"))
		    refresh();
		return true;
	    default:
		return super.onSystemEvent(event);
	    }
	return super.onSystemEvent(event);
    }

    @Override public boolean onOk()
    {
	return true;
    }

    static private Object[] prepareContent(Luwrain luwrain)
    {
	final Object obj;
	try {
	    obj = new org.luwrain.script.hooks.ProviderHook(luwrain).run(LIST_HOOK, new Object[0]);
	}
	catch(RuntimeException e)
	{
	    luwrain.message(luwrain.i18n().getExceptionDescr(e), Luwrain.MessageType.ERROR);
	    return new Object[0];
	}
	if (obj == null)
	    return new Object[0];
	final List items = ScriptUtils.getArray(obj);
	if (items == null)
	    return new Object[0];
	final List<Item> res = new LinkedList();
	for(Object o: items)
	{
	    if (o == null)
		continue;
	    final Object nameObj = ScriptUtils.getMember(o, "name");
	    if (nameObj == null)
		continue;
	    final String name = ScriptUtils.getStringValue(nameObj);
	    if (name == null || name.trim().isEmpty())
		continue;
	    res.add(new Item(name, o));
	}
	return res.toArray(new Item[res.size()]);
    }
    
    static private ListArea.Params constructParams(Luwrain luwrain, String name)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlContext(luwrain);
	params.name = name;
	params.model = new ListUtils.FixedModel(prepareContent(luwrain)){
		@Override public void refresh()
		{
		    setItems(prepareContent(luwrain));
		}};
	params.appearance = new ListUtils.DefaultAppearance(new DefaultControlContext(luwrain));
	return params;
    }

    static public final class Item
    {
private final String name;
private final Object obj;
	public Item(String name, Object obj)
	{
	    NullCheck.notEmpty(name, "name");
	    NullCheck.notNull(obj, "obj");
	    this.name = name;
	    this.obj = obj;
	}
	@Override public String toString()
	{
	    return name;
	}
	public String getName()
	{
	    return name;
	}
	    public Object getObj()
	{
	    return obj;
	}
    }

}
