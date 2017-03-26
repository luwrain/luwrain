
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
name, prefix, getPathWithTrailingSlash(path), popupFlags);
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

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != EnvironmentEvent.Type.REGULAR)
	    return super.onEnvironmentEvent(event);
	switch(event.getCode())
	{
	case PROPERTIES:
	    return openCommanderPopup();
	default:
	    return super.onEnvironmentEvent(event);
	}
    }

    @Override public boolean onOk()
    {
	if (result() == null)
	    return false;
	return acceptance != null?acceptance.pathAcceptable(result()):true;
    }

    protected boolean openCommanderPopup()
    {
	Path path = result();
	if (path == null)
	    return false;
	if (!Files.isDirectory(path))
	    path = path.getParent();
	if (path == null || !Files.isDirectory(path))
	    return false;
	final Path res = Popups.commanderSingle(luwrain, getAreaName(), path, popupFlags);
	if (res != null)
	    setText(res.toString(), "");
	return true;
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

    static protected class Model extends EditListPopupUtils.DynamicModel
    {
	protected final Path defPath;
	protected final boolean skipHidden;

	Model(Path defPath, boolean skipHidden)
	{
	    NullCheck.notNull(defPath, "defPath");
	    this.defPath = defPath;
	    if (!defPath.isAbsolute())
		throw new IllegalArgumentException("defPath must be absolute");
	    this.skipHidden = skipHidden;
	}

	@Override protected EditListPopup.Item[] getItems(String context)
	{
	    NullCheck.notNull(context, "context");
	    if (context.isEmpty())
		return readDirectory(defPath, defPath);
	    final Path contextPath = Paths.get(context);
	    NullCheck.notNull(contextPath, "contextPath");
	    final Path base;
	    Path path;
	    if (contextPath.isAbsolute())
	    {
		base = null;
		path = contextPath;
	    } else
	    {
		base = defPath;
		path = defPath.resolve(contextPath);
	    }
	    if (!context.endsWith(getSeparator()) && path.getParent() != null)
		path = path.getParent();
	    if (!Files.exists(path) || !Files.isDirectory(path))
		return new Item[0];
	    return readDirectory(path, base);
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
		path = defPath.resolve(path);
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

	//Just adds ending slash, if necessary
	@Override public String getCompletion(String beginning)
	{
	    final String res = super.getCompletion(beginning);
	    NullCheck.notNull(res, "res");
	    final String path = beginning + res;
					     //We already have the slash, doing nothing
	    if (!path.isEmpty() && path.endsWith(getSeparator()))
		return res;
	    Path pp = Paths.get(path);
	    if (!pp.isAbsolute())
		pp = defPath.resolve(pp);
					     final boolean withSlash;
	    if (!Files.exists(pp) || !Files.isDirectory(pp))
		withSlash = false; else
		withSlash = true;
	    if (withSlash && !hasWithSameBeginningNearby(pp))
		return res + getSeparator();
	    return res;
	}

	protected Item[] readDirectory(Path path, Path base)
	{
	    NullCheck.notNull(path, "path");
	    final LinkedList<Item> items = new LinkedList<Item>();
	    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
		    for (Path pp : directoryStream) 
			if (!skipHidden || !Files.isHidden(pp))
			{
			    if (base != null)
				items.add(new Item(base.relativize(pp).toString(), pp.getFileName().toString())); else
				items.add(new Item(pp.toString(), pp.getFileName().toString()));
			}
		    final Item[] res = items.toArray(new Item[items.size()]);
		    Arrays.sort(res);
		    return res;
		}
	    catch (IOException e) 
	    {
		Log.error("core", "unable to read content of " + path.toString() + ":" + e.getClass().getName() + ":" + e.getMessage());
		e.printStackTrace();
		return new Item[0];
	    }
	}

	protected boolean hasWithSameBeginningNearby(Path path)
	{
	    NullCheck.notNull(path, "path");
	    final Path parent = path.getParent();
	    if (parent == null)
		return false;
	    final String fileName = path.getFileName().toString();
	    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(parent)) {
		    for (Path pp : directoryStream) 
			if (!skipHidden || !Files.isHidden(pp))
			{
			    final Path f = pp.getFileName();
			    if (f == null)
				continue;
			    final String name = f.toString();
			    if (name.length() > fileName.length() && name.startsWith(fileName))
				return true;
			}
		    return false;
	}
	    catch (IOException e) 
	    {
		Log.error("core", "unable to read content of " + path.toString() + ":" + e.getClass().getName() + ":" + e.getMessage());
		e.printStackTrace();
		return false;
	    }
	}
    }
}
