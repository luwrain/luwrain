/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.script2;

import java.util.*;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.script.core.*;

public final class ScriptUtils
{
    static public boolean notNull(Value[] values)
    {
	if (values == null)
	    return false;
	for(int i = 0;i < values.length;i++)
	    if (values[i] == null || values[i].isNull())
		return false;
	return true;
    }

    static public boolean notNullAndLen(Value[] values, int len)
    {
	if (!notNull(values))
	    return false;
	return values.length == len;
    }

    static public List getArrayItems(Object o)
    {
	if (o == null || !(o instanceof Value))
	    return null;
	final Value value = (Value)o;
	if (!value.hasArrayElements())
	    return null;
	final List<Value> res = new ArrayList<>();
	for(long i = 0;i < value.getArraySize();i++)
	    res.add(value.getArrayElement(i));
	return res;
    }

    static public Object getMember(Object obj, String name)
    {
	NullCheck.notEmpty(name, "name");
	if (obj == null || !(obj instanceof Value))
	    return null;
	final Value value = (Value)obj;
	if (value.isNull())
	    return null;
	return value.getMember(name);
    }

    static public String asString(Object obj)
    {
	if (obj == null || !(obj instanceof Value))
	    return null;
	final Value value = (Value)obj;
	if (value.isNull() || !value.isString())
	    return null;
	return value.asString();
	    }

        static public Object[] asArray(Object o)
    {
	if (o == null || !(o instanceof Value))
	    return null;
	final Value v = (Value)o;
		if (v.isNull() || !v.hasArrayElements())
	    return null;
	final Object[] res = new Object[(int)v.getArraySize()];
	for(int i = 0;i < res.length;i++)
	    res[i] = v.getArrayElement(i);
	return res;
    }

        static public String[] asStringArray(Object o)
    {
		if (o == null || !(o instanceof Value))
	    return null;
	final Value v = (Value)o;
	if (v.isNull() || !v.hasArrayElements())
	    return null;
	final String[] res = new String[(int)v.getArraySize()];
	for(int i = 0;i < res.length;i++)
	    res[i] = v.getArrayElement(i).asString();
	return res;
    }

    static public int asInt(Object obj)
    {
	if (obj == null || !(obj instanceof Value))
	    return 0;
	final Value value = (Value)obj;
	if (value.isNull() || !value.isString())
	    return 0;
	return value.asInt();
    }

    static public Object getArray(Object[] items)
    {
	NullCheck.notNullItems(items, "items");
	return ProxyArray.fromArray((Object[])items);
    }

        static public <E> Object getArray(List<E> items)
    {
	NullCheck.notNull(items, "items");
	return ProxyArray.fromArray((Object[])items.toArray(new Object[items.size()]));
    }

    static public Object createInputEvent(InputEvent event)
    {

		NullCheck.notNull(event, "event");
		final Map<String, Object> values = new HashMap<>();
		values.put("special", event.isSpecial()?event.getSpecial().toString():null);
		values.put("ch", event.isSpecial()?null:new String(new StringBuilder().append(event.getChar())));
		values.put("withAlt", new Boolean(event.withAlt()));
		values.put("withAltOnly", new Boolean(event.withAltOnly()));
		values.put("withControl", new Boolean(event.withControl()));
		values.put("withControlOnly", new Boolean(event.withControlOnly()));
		values.put("withShift", new Boolean(event.withShift()));
		values.put("withShiftOnly", new Boolean(event.withShiftOnly()));
		values.put("modified", new Boolean(event.isModified()));
		return new MapScriptObject(values);
    }

	    static public Object createSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	final Map<String, Object> values = new HashMap<>();
	values.put("code", event.getCode().toString());
	values.put("type", event.getType().toString());
	return new MapScriptObject(values);
    }

}
