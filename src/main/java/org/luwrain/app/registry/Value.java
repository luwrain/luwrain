
package org.luwrain.app.registry;

import org.luwrain.core.Registry;

class Value implements Comparable
{
    public int type = Registry.INTEGER;
    public String parentDir;
    public String name;
    public String strValue;
    public int intValue;
    public boolean boolValue;

    @Override public int compareTo(Object o)
    {
	if (o == null || !(o instanceof Value))
	    return 0;
	final Value v = (Value)o;
	return name.compareTo(v.name);
    }
}
