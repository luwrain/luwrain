
package org.luwrain.core.script2;

import org.graalvm.polyglot.*;

import org.luwrain.core.*;

public final class Module implements AutoCloseable
{
    private final Luwrain luwrain;
    private Context context = null;
    final LuwrainObj luwrainObj;

    Module(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	this.luwrainObj = new LuwrainObj(luwrain);
    }

    public void run(String text)
    {
	close();
	this.context = Context.newBuilder()
	.allowExperimentalOptions(true)
	//.option("js.nashorn-compat", "true"
	.build();
	context.getBindings("js").putMember("Luwrain", this.luwrainObj);
context.eval("js", text);
    }

    @Override public void close()
    {
	if (context == null)
	    return;
	context.close();
	context = null;
    }

}
