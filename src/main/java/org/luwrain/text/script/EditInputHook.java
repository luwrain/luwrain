
package org.luwrain.text.script;

import java.util.concurrent.atomic.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.script.*;
import org.luwrain.script2.hooks.*;

public class EditInputHook
{
    static private final String LOG_COMPONENT = "text";

    boolean runInputEventHook(HookContainer hookContainer, EditArea editArea, MultilineEdit edit, AbstractRegionPoint regionPoint, InputEvent event)
    {
	NullCheck.notNull(hookContainer, "hookContainer");
	NullCheck.notNull(event, "event");
	final MultilineEdit.Model model = edit.getMultilineEditModel();
	if (model == null || !(model instanceof MultilineEditCorrector))
	    return false;
	final MultilineEditCorrector corrector = (MultilineEditCorrector)model;
	final AtomicReference res = new AtomicReference();
	corrector.doEditAction((lines, hotPoint)->{
		try {
		    res.set(new Boolean(ChainOfResponsibilityHook.run(hookContainer, "luwrain.edit.multiline.input", new Object[]{
				    ScriptUtils.createInputEvent(event),
				    TextScriptUtils.createTextEditHookObject(editArea, lines, hotPoint, regionPoint)
				})));
		}
		catch(Throwable e)
		{
		    Log.error(LOG_COMPONENT, "the luwrain.edit.multiline.input hook failed:" + e.getClass().getName() + ":" + e.getMessage());
		}
	    });
	if (res.get() == null)
	    return false;
	return ((Boolean)res.get()).booleanValue();
    }
}
