
package org.luwrain.popups;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.io.*;

public class CommanderPopup extends NgCommanderArea<File> implements NgCommanderArea.ClickHandler<File>, Popup, PopupClosingRequest
{
    public final PopupClosingTranslator closing = new PopupClosingTranslator(this);
    protected final Luwrain luwrain;
    protected final String name;
    protected final FilePopup.Acceptance acceptance;
    protected final Set<Popup.Flags> popupFlags;
    protected Path result;

    public CommanderPopup(Luwrain luwrain, String name,
			  Path path, FilePopup.Acceptance acceptance,
			  NgCommanderArea.ClickHandler<File> clickHandler, Set<Popup.Flags> popupFlags)
    {
	super(constructParams(luwrain));
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(popupFlags, "popupFlags");
	this.luwrain = luwrain;
	this.name = name;
	this.popupFlags = popupFlags;
	this.acceptance = acceptance;
	setClickHandler(clickHandler != null?clickHandler:this);
	open(path.toFile());
    }

    @Override public NgCommanderArea.ClickHandler.Result onCommanderClick(NgCommanderArea area, File file, boolean dir)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNull(file, "file");
	if (dir)
	    return ClickHandler.Result.OPEN_DIR;
	result = file.toPath();
	closing.doOk();
	return ClickHandler.Result.OK;
    }

    public Path result()
    {
	return result;
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (closing.onKeyboardEvent(event))
	    return true;
	if (!event.isSpecial() && !event.isModified())
	    switch(event.getChar())
	    {
	    case '=':
		//		setCommanderFilter(new CommanderUtils.AllFilesFilter());
		refresh();
		return true;
	    case '-':
		//		setCommanderFilter(new CommanderUtils.NoHiddenFilter());
		refresh();
		return true;
	    }
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != EnvironmentEvent.Type.REGULAR)
	    return super.onEnvironmentEvent(event);
	switch(event.getCode())
	{
	case PROPERTIES:
	    openMountedPartitions();
	    return true;
	case OK:
	    if (opened() == null)
		result = opened().toPath();
	    closing.doOk();
	    return true;
	default:
	    if (closing.onEnvironmentEvent(event))
		return true;
	    return super.onEnvironmentEvent(event);
	}
    }

    @Override public String getAreaName()
    {
	return name + super.getAreaName();
    }

    @Override public boolean onOk()
    {
	if (result() == null)
	    return false;
	return acceptance != null?acceptance.pathAcceptable(result()):true;
    }

    @Override public boolean onCancel()
    {
	return true;
    }

    @Override public Luwrain getLuwrainObject()
    {
	return luwrain;
    }

    @Override public EventLoopStopCondition getStopCondition()
    {
	return closing;
    }

    @Override public Set<Popup.Flags> getPopupFlags()
    {
	return popupFlags;
    }

    protected void openMountedPartitions()
    {
	final org.luwrain.base.Partition part = Popups.mountedPartitions(luwrain, popupFlags);
	if (part == null)
	    return;
	open(part.file(), null);
    }

    static private NgCommanderArea.Params<File> constructParams(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final NgCommanderArea.Params<File> params = CommanderUtilsFile.createParams(new DefaultControlEnvironment(luwrain));
	params.filter = new CommanderUtilsFile.AllEntriesFilter();
	params.comparator = new CommanderUtilsFile.ByNameComparator();
	return params;
    }
}
