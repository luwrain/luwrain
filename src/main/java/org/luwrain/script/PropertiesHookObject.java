
package org.luwrain.script;

import java.util.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.core.*;

public class PropertiesHookObject extends EmptyHookObject
{
    private final Properties props;
    private final String propName;

    public PropertiesHookObject(Properties props, String propName)
    {
	NullCheck.notNull(props, "props");
	NullCheck.notNull(propName, "propName");
	this.props = props;
	this.propName = propName;
    }

    public PropertiesHookObject(Properties props)
    {
	this(props, "");
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	if (name.isEmpty())
	    return super.getMember(name);
	if (propName.isEmpty())
	    return new PropertiesHookObject(props, name);
	return new PropertiesHookObject(props, propName + "." + name);
    }

    @Override public void setMember(String name, Object value)
    {
	NullCheck.notNull(name, "name");
	if (name.isEmpty() || value == null)
	    return;
	final String text = value.toString();
	if (text == null)
	    return;
	if (!propName.isEmpty())
	props.setProperty(propName + "." + name, text); else
	    	props.setProperty(name, text);
    }

    @Override public Object getDefaultValue(Class hint)
    {
	final String res = props.getProperty(propName);
	return res != null?res:"";
    }

    @Override public String toString()
    {
	return getDefaultValue(String.class).toString();
    }

    public Properties getProperties()
    {
	return props;
    }
}
