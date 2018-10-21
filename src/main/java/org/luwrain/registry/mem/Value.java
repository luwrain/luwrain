
package org.luwrain.registry.mem;

import org.luwrain.core.*;

final class Value
{
    int type = Registry.INVALID;
    String strValue = "";
    int intValue = 0;
    boolean boolValue = false;

    Value(String value)
    {
	NullCheck.notNull(value, "value");
	this.type = Registry.STRING;
	this.strValue = value;
    }

    Value(int value)
    {
	type = Registry.INTEGER;
	intValue = value;
    }

    Value(boolean value)
    {
	type = Registry.BOOLEAN;
	boolValue = value;
    }
}
