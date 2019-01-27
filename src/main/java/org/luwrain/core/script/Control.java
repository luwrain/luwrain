
// https://docs.oracle.com/javase/8/docs/jdk/api/nashorn/jdk/nashorn/api/scripting/ScriptObjectMirror.html

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

final class Control extends AbstractJSObject
{
    private final Luwrain luwrain;
    private final File dataDir;
    final List<CommandLineTool> cmdLineTools = new LinkedList();
    final List<Shortcut> shortcuts = new LinkedList();
    final List<Command> commands = new LinkedList();
    final List<TextEditingExtension> textEdits = new LinkedList();

    Control(Luwrain luwrain, File dataDir)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(dataDir, "dataDir");
	this.luwrain = luwrain;
	this.dataDir = dataDir;
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	switch(name)
	{
	case "prop":
	    return new Prop(luwrain, "");
	case "message":
	    return (Consumer)this::message;
	case "addCommandLineTool":
	    return (BiPredicate)this::addCommandLineTool;
	case "addApp":
	case "addShortcut":
	    return (BiPredicate)this::addShortcut;
	case "addCommand":
	    return (BiPredicate)this::addCommand;
	    	    	case "addTextEditing":
	    return (BiPredicate)this::addTextEditing;
	    	case "getActiveAreaText":
	    return (Function)this::getActiveAreaText;

	    
	case "launchApp":
	    return (BiPredicate)this::launchApp;
	    	case "runBkg":
		    return (Predicate)this::runBkg;
	default:
	    return null;
	}
    }

    private void message(Object b)
    {
	if (b != null && !b.toString().trim().isEmpty())
	    luwrain.message(b.toString());
    }

    private boolean addCommandLineTool(Object name, Object obj)
    {
	if (name == null || obj == null)
	    return false;
	if (!(obj instanceof jdk.nashorn.api.scripting.ScriptObjectMirror))
	    return false;
	final JSObject cons = (JSObject)obj;
	final Object newObj = cons.newObject();
	final ScriptObjectMirror newJsObj = (ScriptObjectMirror)newObj;
	luwrain.message(newJsObj.get("name").toString());
	//	luwrain.message(o2.getClass().getName());
	return true;
    }

    private boolean addShortcut(Object name, Object obj)
    {
	if (name == null || obj == null)
	    return false;
	if (!(obj instanceof jdk.nashorn.api.scripting.ScriptObjectMirror))
	    return false;
	for(Shortcut s: shortcuts)
	    if (s.getExtObjName().equals(name.toString()))
		return false;
	final JSObject cons = (JSObject)obj;
	shortcuts.add(new ShortcutAdapter(name.toString(), dataDir, cons));
	return true;
    }

        private boolean addCommand(Object name, Object obj)
    {
	if (name == null || obj == null)
	    return false;
	if (!(obj instanceof jdk.nashorn.api.scripting.ScriptObjectMirror))
	    return false;
	for(Command c: commands)
	    if (c.getName().equals(name.toString()))
		return false;
	final JSObject func = (JSObject)obj;
	commands.add(new CommandAdapter(name.toString(), func));
	return true;
    }

            private boolean addTextEditing(Object name, Object obj)
    {
	if (name == null || obj == null)
	    return false;
	if (!(obj instanceof jdk.nashorn.api.scripting.ScriptObjectMirror))
	    return false;
	for(TextEditingExtension t: textEdits)
	    if (t.getExtObjName().equals(name.toString()))
		return false;
	final JSObject func = (JSObject)obj;
	//textEdits.add(new TextEditingAdapter(name.toString(), func));
	return true;
    }

    private boolean runBkg(Object obj)
    {
	if (obj == null)
	    return false;
	if (!(obj instanceof jdk.nashorn.api.scripting.ScriptObjectMirror))
	    return false;
	final JSObject func = (JSObject)obj;
	luwrain.executeBkg(new FutureTask(()->{
		    func.call(null, new Object[0]);
	}, null));
	return true;
    }

    private String getActiveAreaText(Object type)
    {
	if (type == null)
	    return null;
	final String typeStr = type.toString();
	if (typeStr == null)
	    return null;
	final Luwrain.AreaTextType typeValue;
	switch(typeStr.toLowerCase().trim())
	{
	case "region":
	    typeValue = Luwrain.AreaTextType.REGION;
	    break;
	case "word":
	    typeValue = Luwrain.AreaTextType.WORD;
	    break;
	case "line":
	    typeValue = Luwrain.AreaTextType.LINE;
	    break;
	case "sentence":
	    typeValue = Luwrain.AreaTextType.SENTENCE;
	    break;
	case "url":
	    typeValue = Luwrain.AreaTextType.URL;
	default:
	    return null;
	}
	return luwrain.getActiveAreaText(typeValue, false);
    }

    private boolean launchApp(Object name, Object args)
    {
	if (name == null || args == null || !(args instanceof JSObject))
	    return false;

	final JSObject jsArgs = (JSObject)args;
	if (!jsArgs.isArray())
	    return false;
	final List<String> argsList = Utils.getStringArray(jsArgs);
	if (argsList == null)
	    return false;
	luwrain.launchApp(name.toString(), argsList.toArray(new String[argsList.size()]));
//FIXME:proper return value
return true;
    }
}
