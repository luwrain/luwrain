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
	if (!(obj instanceof Integer))
	    return null;
	    return (Integer)obj;
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
}
