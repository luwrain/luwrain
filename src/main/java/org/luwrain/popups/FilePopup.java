
package org.luwrain.popups;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class FilePopup extends EditListPopup
{
    public enum Flags { SKIP_HIDDEN };

    public interface Acceptance 
    {
	boolean pathAcceptable(Path path);
    }

    private final Path defPath;
    private final Acceptance acceptance;

    public FilePopup(Luwrain luwrain, String name,
		     String prefix, Acceptance acceptance,
		     Path path, Path defPath,
		     Set<Flags> flags, Set<Popup.Flags> popupFlags)
    {
	super(luwrain, new Model(defPath, flags .contains(Flags.SKIP_HIDDEN)), 
name, prefix, Model.getPathWithTrailingSlash(path), popupFlags);
	this.defPath = defPath;
	this.acceptance = acceptance;
    }

    public Path result()
    {
	final Path res = Paths.get(text());
	if (res.isAbsolute())
	    return res;
	return defPath.resolve(res);
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && event.withAltOnly())
	    switch(event.getSpecial())
	    {
	    case 	    ENTER:
		return openCommanderPopup();
	    }
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onOk()
    {
	if (result() == null)
	    return false;
	return acceptance != null?acceptance.pathAcceptable(result()):true;
    }

    private boolean openCommanderPopup()
    {
	Path path = result();
	if (path == null)
	    return false;
	if (!Files.isDirectory(path))
	    path = path.getParent();
	if (path == null || !Files.isDirectory(path))
	    return false;
	final Path res = Popups.commanderSingle(luwrain, getAreaName() + ": ", path, popupFlags);
	if (res != null)
	    setText(res.toString(), "");
	return true;
    }

    static protected class Model extends EditListPopupUtils.DynamicModel
    {
	protected final Path defPath;
	protected final boolean skipHidden;

	Model(Path defPath, boolean skipHidden)
	{
	    NullCheck.notNull(defPath, "defPath");
	    this.defPath = defPath;
	    this.skipHidden = skipHidden;
	}

	@Override protected EditListPopup.Item[] getItems(String context)
	{
	    Path path = null;
	    Path base = null;
	    final String from = context != null?context:"";
	    final Path fromPath = Paths.get(from);
	    final boolean hadTrailingSlash = from.endsWith(getSeparator());
	    if (!from.isEmpty() && fromPath.isAbsolute())
	    {
		base = null;
		path = fromPath;
	    } else
		if (from.isEmpty())
		{
		    base = defPath;
		    path = defPath;
		} else
		{
		    base = defPath;
		    path = defPath.resolve(fromPath);
		}
	    if (!from.isEmpty() && !hadTrailingSlash)
		path = path.getParent();
	    if (!Files.exists(path) || !Files.isDirectory(path))
		return new Item[0];
	    final LinkedList<Item> items = new LinkedList<Item>();
	    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
		    for (Path pp : directoryStream) 
			if (!skipHidden || !Files.isHidden(pp))
			{
			if (base != null)
			    items.add(new Item(base.relativize(pp).toString(), pp.getFileName().toString())); else
			    items.add(new Item(pp.toString(), pp.getFileName().toString()));
			}
		} 
	    catch (IOException e) 
	    {
		e.printStackTrace();
		return new Item[0];
	    }
	    final EditListPopup.Item[] res = items.toArray(new EditListPopup.Item[items.size()]);
	    Arrays.sort(res);
	    return res;
	}

	@Override protected EditListPopup.Item getEmptyItem(String context)
	{
	    NullCheck.notNull(context, "context");
	    if (context.isEmpty())
		return new EditListPopup.Item();
	    Path base = null;
	    Path path = Paths.get(context);
	    if (!path.isAbsolute())
	    {
		base = defPath;
		path = base.resolve(path);
	    }
	    if (context.endsWith(getSeparator()) && Files.exists(path) && Files.isDirectory(path))
		return new EditListPopup.Item(context);
	    path = path.getParent();
	    if (path != null)
	    {
		String suffix = "";
		//We don't want double slash in root designation and at the top of relative enumeration
		if (Files.exists(path) && Files.isDirectory(path) && 
		    !path.equals(path.getRoot()) &&
		    (base == null || !base.equals(path)))
		    suffix = getSeparator();
		if (base != null)
		    return new EditListPopup.Item(base.relativize(path).toString() + suffix);
		return new EditListPopup.Item(path.toString() + suffix);
	    }
	    return new EditListPopup.Item(context);
	}

	@Override public String getCompletion(String beginning)
	{
	    final String res = super.getCompletion(beginning);
	    final String path = beginning + (res != null?res:"");
	    if (!path.isEmpty() && path.endsWith(getSeparator()))
		return res;
	    Path pp = Paths.get(path);
	    if (!pp.isAbsolute())
		pp = defPath.resolve(pp);
	    if (Files.exists(pp) && Files.isDirectory(pp))
		return res + getSeparator();
	    return res;
	}

	static String getPathWithTrailingSlash(Path p)
	{
	    NullCheck.notNull(p, "p");
	    final String str = p.toString();
	    //Checking if there is nothing to do
	    if (str.endsWith(getSeparator()))
		return str;
	    if (Files.exists(p) && Files.isDirectory(p))
		return str + getSeparator();
	    return str;
	}

	static protected String getSeparator()
	{
	    return FileSystems.getDefault().getSeparator();
	}
    }
}
