/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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

//LWR_API 1.0

package org.luwrain.script;

import java.util.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public final class ScriptUtils
{
    static public boolean isValid(Object obj)
    {
	return obj != null && !ScriptObjectMirror.isUndefined(obj);
    }

        static public JSObject toValidJsObject(Object obj)
    {
	if (obj == null)
	    return null;
	if (!(obj instanceof JSObject))
	    return null;
	final JSObject jsObj = (JSObject)obj;
	if (ScriptObjectMirror.isUndefined(jsObj))
	    return null;
	return jsObj;
    }

    static public Object getMember(Object obj, String memberName)
    {
	NullCheck.notNull(obj, "obj");
	NullCheck.notEmpty(memberName, "memberName");
		if (ScriptObjectMirror.isUndefined(obj) || !(obj instanceof JSObject))
	    return null;
	final JSObject jsObj = (JSObject)obj;
	return jsObj.getMember(memberName);
    }

    static public String getStringValue(Object obj)
    {
	if (!isValid(obj))
	    return null;
	return obj.toString();
    }

    static public Integer getIntegerValue(Object obj)
    {
	if (!isValid(obj))
	    return null;
	if (obj instanceof Number)
	    return new Integer(((Number)obj).intValue());
	if (obj instanceof String)
	    try {
		return new Integer(Integer.parseInt(obj.toString()));
	    }
	    catch(NumberFormatException e)
	    {
		return null;
	    }
	return null;
    }

             static public Number getNumberValue(Object obj)
    {
	if (!isValid(obj))
	    return null;
	if (!(obj instanceof Number))
	    return null;
	    return (Number)obj;
    }


             static public Boolean getBooleanValue(Object obj)
    {
	if (!isValid(obj))
	    return null;
	if (!(obj instanceof Boolean))
	    return null;
	    return (Boolean)obj;
    }



        static public List getArray(Object obj)
    {
	NullCheck.notNull(obj, "obj");
	if (ScriptObjectMirror.isUndefined(obj))
	    return null;
	if (!(obj instanceof JSObject))
	    return null;
	final JSObject jsObj = (JSObject)obj;
	if (!jsObj.isArray())
	    return null;
		final List res = new LinkedList();
	int index = 0;
	while (jsObj.hasSlot(index))
	{
	    final Object o = jsObj.getSlot(index);
	    if (o == null)
		break;
	    res.add(o);
	    ++index;
	}
	return res;
    }

    
    //Returns null if the provided object isn't an array
    static public List<String> getStringArray(Object obj)
    {
	NullCheck.notNull(obj, "obj");
	if (ScriptObjectMirror.isUndefined(obj))
	    return null;
	if (!(obj instanceof JSObject))
	    return null;
	final JSObject jsObj = (JSObject)obj;
	final List<String> res = new LinkedList();
	if (!jsObj.isArray())
	    return null;
	int index = 0;
	while (jsObj.hasSlot(index))
	{
	    final Object o = jsObj.getSlot(index);
	    if (o == null)
		break;
	    res.add(o.toString());
	    ++index;
	}
	return res;
    }

    static public HookObject createArray(Object[] items)
    {
	NullCheck.notNullItems(items, "items");
	//FIXME:check the type of given objects
	return new EmptyHookObject()
	    {
		@Override public Object getMember(String name)
		{
		    NullCheck.notNull(name, "name");
		    switch(name)
		    {
		    case "items":
			return items.clone();
		    case "length":
			return new Integer(items.length);
		    default:
			return null;//FIXME:undefined
		    }
		}
		@Override public Object getSlot(int index)
		{
		    if (index < 0 || index >= items.length)
			return null;//FIXME:undefined
		    return items[index];
		}
		@Override public boolean hasSlot(int index)
		{
		    return index >= 0 && index < items.length;
		}
		@Override public void setSlot(int index, Object obj)
		{
		    if (obj == null)
			return;
		    if (index >= 0 && index < items.length)
			items[index] = obj;
		}
		@Override public boolean isArray()
		{
		    return true;
		}
	};
    }

    static public HookObject createReadOnlyArray(Object[] items)
    {
	NullCheck.notNullItems(items, "items");
	//FIXME:check the type of given objects
	return new EmptyHookObject()
	    {
		@Override public Object getMember(String name)
		{
		    NullCheck.notNull(name, "name");
		    switch(name)
		    {
		    case "items":
			return items.clone();
		    case "length":
			return new Integer(items.length);
		    default:
			return null;//FIXME:undefined
		    }
		}
		@Override public Object getSlot(int index)
		{
		    if (index < 0 || index >= items.length)
			return null;//FIXME:undefined
		    return items[index];
		}
		@Override public boolean hasSlot(int index)
		{
		    return index >= 0 && index < items.length;
		}
		@Override public boolean isArray()
		{
		    return true;
		}
	};
    }

    static public HookObject createInputEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	return new EmptyHookObject(){
	    @Override public Object getMember(String name)
	    {
		NullCheck.notNull(name, "name");
		switch(name)
		{
		case "special":
		    return event.isSpecial()?event.getSpecial().toString().toLowerCase():null;
		case "ch":
		    return event.isSpecial()?null:new String(new StringBuilder().append(event.getChar()));
		case "withAlt":
		    return new Boolean(event.withAlt());
		case "withAltOnly":
		    return new Boolean(event.withAltOnly());
		case "withControl":
		    return new Boolean(event.withControl());
		case "withControlOnly":
		    return new Boolean(event.withControlOnly());
		case "withShift":
		    return new Boolean(event.withShift());
		case "withShiftOnly":
		    return new Boolean(event.withShiftOnly());
		case "modified":
		    return new Boolean(event.isModified());
		default:
		    return super.getMember(name);
		}
	    }
	};
    }

	    static public HookObject createSystemEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	return new EmptyHookObject(){
	    @Override public Object getMember(String name)
	    {
		NullCheck.notNull(name, "name");
		switch(name)
		{
		case "code":
		    return event.getCode().toString().toLowerCase();
		case "type":
		    return event.getType().toString().toLowerCase();
		default:
		    return super.getMember(name);
		}
	    }
	};
    }


    static public KeyboardEvent getInputEvent(Object obj)
    {
	if (!isValid(obj))
	    return null;
	final Object chObj = getMember(obj, "ch");
	final Object specialObj = getMember(obj, "special");
	final Object withAltObj = getMember(obj, "withAlt");
	final Object withShiftObj = getMember(obj, "withShift");
	final Object withControlObj = getMember(obj, "withControl");
	final String ch;
	if (chObj != null)
	{
	    final String value = getStringValue(chObj);
	    ch = value != null?value:"";
	} else
	    ch = "";
	final String special;
	if (specialObj != null)
	{
	    final String value = getStringValue(specialObj);
	    special = value != null?value:"";
	} else
	    special = "";
	final KeyboardEvent.Special sp;
	if (!special.isEmpty())
	{
	    KeyboardEvent.Special s = null;
	    final EnumSet<KeyboardEvent.Special> allSpecials = EnumSet.allOf(KeyboardEvent.Special.class);
	    for(KeyboardEvent.Special ss: allSpecials)
		if (ss.toString().toUpperCase().equals(special))
		    s = ss;
	    if (s == null)
		return null;
	    sp = s;
	} else
	    sp = null;
	if (ch.isEmpty() && sp == null)
	    return null;
	final Boolean withAlt;
	final Boolean withControl;
	final Boolean withShift;
	if (withAltObj != null)
	{
	    final Boolean value = getBooleanValue(withAltObj);
	    withAlt = value != null?value:new Boolean(false);
	} else
	    withAlt = new Boolean(false);
	if (withShiftObj != null)
	{
	    final Boolean value = getBooleanValue(withShiftObj);
	    withShift = value != null?value:new Boolean(false);
	} else
	    withShift = new Boolean(false);
	if (withControlObj != null)
	{
	    final Boolean value = getBooleanValue(withControlObj);
	    withControl = value != null?value:new Boolean(false);
	} else
	    withControl = new Boolean(false);
	if (sp != null)
	    return new KeyboardEvent(sp, withShift.booleanValue(), withControl.booleanValue(), withAlt.booleanValue());
	return new KeyboardEvent(ch.charAt(0), withShift.booleanValue(), withControl.booleanValue(), withAlt.booleanValue());
    }

    static public Action getAction(Object obj)
    {
	if (!isValid(obj))
	    return null;
	final Object nameObj = getMember(obj, "name");
	final Object titleObj = getMember(obj, "title");
	final Object inputEventObj = getMember(obj, "event");
	if (nameObj == null || titleObj == null)
	    return null;
	final String name = getStringValue(nameObj);
	final String title = getStringValue(titleObj);
	if (name == null || name.isEmpty() ||
	    title == null || title.isEmpty())
	    return null;
	final KeyboardEvent inputEvent;
	if (inputEventObj != null)
	    inputEvent = getInputEvent(inputEventObj); else
	    inputEvent = null;
	if (inputEvent != null)
	    return new Action(name, title, inputEvent);
	return new Action(name, title);
    }

    static public Object createEnumSet(Set s)
    {
	NullCheck.notNull(s, "s");
	final List<String> res = new LinkedList();
	for(Object o: s)
	    res.add(o.toString().toLowerCase());
	return createReadOnlyArray(res.toArray(new String[res.size()]));
    }

    static public Object getEnumItemByStr(Class enumClass, String itemName)
    {
	NullCheck.notNull(enumClass, "enumClass");
	NullCheck.notEmpty(itemName, "itemName");
	final EnumSet allItems = EnumSet.allOf(enumClass);
	for(Object s: allItems)
	    if (s.toString().equals(itemName))
		return s;
	return null;
    }

    static public Set getEnumByArrayObj(Class enumClass, Object arrayObj)
    {
	NullCheck.notNull(enumClass, "enumClass");
	NullCheck.notNull(arrayObj, "arrayObj");
	final List items = getArray(arrayObj);
	if (items == null)
	    return null;
	final Set res = EnumSet.noneOf(enumClass);
	for(Object o: items)
	{
	    if (o == null)
		continue;
	    final String str = getStringValue(o);
	    if (str == null)
		continue;
	    final Object enumItem = getEnumItemByStr(enumClass, str.toUpperCase());
	    if (enumItem != null)
		res.add(enumItem);
	}
	return res;
    }

    static public Object createTextEditHookObject(NavigationArea area, MutableLines lines, HotPointControl hotPoint, AbstractRegionPoint regionPoint)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNull(lines, "lines");
	NullCheck.notNull(hotPoint, "hotPoint");
	NullCheck.notNull(regionPoint, "regionPoint");
	final HookObject regionObj = createRegionHookObject(hotPoint, regionPoint);
	return new EmptyHookObject(){
	    @Override public Object getMember(String name)
	    {
		NullCheck.notNull(name, "name");
		switch(name)
		{
		case "lines":
		    return new MutableLinesHookObject(lines);
		case "hotPoint":
		    return new HotPointControlHookObject(hotPoint);
		case "regionPoint":
		    return new RegionPointHookObject(regionPoint);
		case "region":
		    return regionObj;
		default:
		    return super.getMember(name);
		}
	    }
	};
    }

    static public HookObject createRegionHookObject(HotPoint p1, HotPoint p2)
    {
	final int fromX;
	final int fromY;
	final int toX;
	final int toY;
	if (p1.getHotPointX() < 0 || p1.getHotPointY() < 0 ||
	    p2.getHotPointX() < 0 || p2.getHotPointY() < 0)
	{
	    fromX = -1;
	    fromY = -1;
	    toX = -1;
	    toY = -1;
	} else
	    if (p1.getHotPointY() < p2.getHotPointY())
	    {
		fromX = p1.getHotPointX();
		fromY = p1.getHotPointY();
		toX = p2.getHotPointX();
		toY = p2.getHotPointY();
	    } else
	    	if (p2.getHotPointY() < p1.getHotPointY())
		{
		    fromX = p2.getHotPointX();
		    fromY = p2.getHotPointY();
		    toX = p1.getHotPointX();
		    toY = p1.getHotPointY();
		} else
		{
		    //p1.y == p2.y
		    fromY = p1.getHotPointY();
		    toY = p1.getHotPointY();
		    fromX = Math.min(p1.getHotPointX(), p2.getHotPointX());
		    toX = Math.max(p1.getHotPointX(), p2.getHotPointX());
		}
	return new EmptyHookObject(){
	    @Override public Object getMember(String name)
	    {
		NullCheck.notNull(name, "name");
		switch(name)
		{
		case "fromX":
		    return new Integer(fromX);
		case "fromY":
		    return new Integer(fromY);
		case "toX":
		    return new Integer(toX);
		case "toY":
		    return new Integer(toY);
		default:
		    return super.getMember(name);
		}
	    }
	};
    }
}
