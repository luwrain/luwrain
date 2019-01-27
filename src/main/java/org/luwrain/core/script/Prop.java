
package org.luwrain.core.script;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.concurrent.*;
import javax.script.*;
import jdk.nashorn.api.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.base.*;
import org.luwrain.core.*;

final class Prop extends AbstractJSObject
{
    private final Luwrain luwrain;
    private final String propName;

    Prop(Luwrain luwrain, String propName)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(propName, "propName");
	this.luwrain = luwrain;
	this.propName = propName;
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	if (name.isEmpty())
	    return null;
	if (propName.isEmpty())
	    return new Prop(luwrain, name);
	return new Prop(luwrain, propName + "." + name);
    }

    @Override public String toString()
    {
	final String res = luwrain.getProperty(propName);
	return res != null?res:"";
    }
}
