
package org.luwrain.script2;

import java.util.*;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;

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
	final List<Value> res = new ArrayList();
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
	return ProxyArray.fromArray(items);
    }
}
