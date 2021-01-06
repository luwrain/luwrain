
package org.luwrain.core.script2;

import java.util.*;
import java.io.*;

import org.graalvm.polyglot.*;

import org.luwrain.core.*;
import org.luwrain.util.*;

public final class ScriptCore implements HookContainer, AutoCloseable
{
    static private final String LOG_COMPONENT = "script2";
    
    private final Luwrain luwrain;
    private final List<Module> modules = new ArrayList();

        ScriptCore(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    @Override public void close()
    {
	for(Module m: modules)
	    m.close();
    }

    public void load (File file) throws IOException
    {
	NullCheck.notNull(file, "file");
	final Module m = new Module(luwrain);
	m.run(FileUtils.readTextFileSingleString(file, "UTF-8"));
	modules.add(m);
	    }

    @Override public boolean runHooks(String hookName, Luwrain.HookRunner runner)
    {
	NullCheck.notEmpty(hookName, "hookName");
	try {
	    for(Module m: modules)
		for(Value v: m.luwrainObj.hooks.get(hookName))
		{
		    final Luwrain.HookResult res = runner.runHook((args)->v.execute(args));
		    if (res == null)
			return false;
		    if (res == Luwrain.HookResult.BREAK)
			return false;
		}
	}
	catch(Throwable e)
	{
	    Log.error(LOG_COMPONENT, "running of the hook '" + hookName + "' failed: " + e.getClass().getName() + ": " + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
	return true;
    }
}
