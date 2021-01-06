
package org.luwrain.script2;

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
}
