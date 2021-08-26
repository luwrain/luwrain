
package org.luwrain.core.script2;

import java.io.*;
import java.util.*;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;

public class MapScriptObject implements ProxyObject
{
    protected final Map<String, Object> members;
    protected ProxyArray membersCache = null;

    public MapScriptObject(Map<String, Object> members)
    {
	NullCheck.notNull(members, "members");
	this.members = members;
    }

    public MapScriptObject()
    {
	this(new HashMap());
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notEmpty(name, "name");
	final Object obj = members.get(name);
	return obj;
	    }

    @Override public boolean hasMember(String name)
    {
	NullCheck.notEmpty(name, "name");
	return members.containsKey(name);
	    }

    @Override public Object getMemberKeys()
    {
	if (membersCache != null)
	    return membersCache;
	final List<String> m = new ArrayList();
	for(Map.Entry<String, Object> e: members.entrySet())
	    m.add(e.getKey());
	membersCache = ProxyArray.fromArray((Object[])m.toArray(new String[m.size()]));
	return membersCache;
}

@Override public void putMember(String name, Value value)
{
    NullCheck.notEmpty(name, "name");
    if (value != null)
	members.put(name, value); else
	members.remove(value);
    membersCache = null;
}

    public ProxyArray array(Object[] a)
    {
	if (a == null)
	    return null;
	return ProxyArray.fromArray(a);
    }

    public void updateMembersCache()
    {
	membersCache = null;
    }
}
