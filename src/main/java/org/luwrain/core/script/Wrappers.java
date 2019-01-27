
package org.luwrain.core.script;

import java.util.*;
import javax.script.*;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import java.util.function.*;

import org.luwrain.base.*;
import org.luwrain.core.*;

final class Wrappers
{
    static final class Output extends AbstractJSObject
    {
	private final Context.Output output;
        Output(Context.Output output)
	{
	    NullCheck.notNull(output, "out");
	    this.output = output;
	}
	@Override public Object getMember(String name)
	{
	    NullCheck.notNull(name, "name");
	    switch(name)
	    {
	    case "print":
		return (Consumer)this::print;
	    default:
		return null;
	    }
	}
	private void print(Object b)
	{
	    if (b != null && b.toString() != null)
		output.onOutputLine(b.toString());
	}
    }
}
