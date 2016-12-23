
package org.luwrain.settings;

import java.util.*;
import java.io.IOException;
import java.nio.file.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

class SoundSchemes extends ListArea implements SectionArea
{
    static private final String SCHEMES_DIR = "sounds/schemes";

    static private class Item 
    {
	Path path;
	String title;

	Item(Path path, String title)
	{
	    this.path = path;
	    this.title = title;
	}

	@Override public String toString()
	{
	    return title;
	}
    }

    static private class ClickHandler implements ListClickHandler
    {
	private Luwrain luwrain;

	ClickHandler(Luwrain luwrain)
	{
	    this.luwrain = luwrain;
	}

	@Override public boolean onListClick(ListArea area, int index, Object obj)
	{
	    if (obj == null || !(obj instanceof Item))
		return false;
	    final Settings.SoundScheme scheme = Settings.createCurrentSoundScheme(luwrain.getRegistry());
	    final Item item = (Item)obj;
	    Path path = item.path;
	    if (path.startsWith(luwrain.getPathProperty("luwrain.dir.data")))
		path = luwrain.getPathProperty("luwrain.dir.data").relativize(path);
	    try {
		scheme.setEventNotProcessed(path.resolve("event-not-processed.wav").toString());
		scheme.setError(path.resolve("error.wav").toString());
		scheme.setDone(path.resolve("done.wav").toString());
		scheme.setBlocked(path.resolve("blocked.wav").toString());
		scheme.setOk(path.resolve("ok.wav").toString());
		scheme.setNoApplications(path.resolve("no-applications.wav").toString());
		scheme.setStartup(path.resolve("startup.wav").toString());
		scheme.setShutdown(path.resolve("shutdown.wav").toString());
		scheme.setMainMenu(path.resolve("main-menu.wav").toString());
		scheme.setMainMenuEmptyLine(path.resolve("main-menu-empty-line.wav").toString());
		scheme.setNoItemsAbove(path.resolve("no-items-above.wav").toString());
		scheme.setNoItemsBelow(path.resolve("no-items-below.wav").toString());
		scheme.setNoLinesAbove(path.resolve("no-lines-above.wav").toString());
		scheme.setNoLinesBelow(path.resolve("no-lines-below.wav").toString());
		scheme.setListItem(path.resolve("list-item.wav").toString());
		scheme.setIntroRegular(path.resolve("intro-regular.wav").toString());
		scheme.setIntroPopup(path.resolve("intro-popup.wav").toString());
		scheme.setIntroApp(path.resolve("intro-app.wav").toString());
		scheme.setCommanderLocation(path.resolve("commander-location.wav").toString());
		scheme.setGeneralTime(path.resolve("general-time.wav").toString());
		scheme.setTermBell(path.resolve("term-bell.wav").toString());
	    }
	    catch(Exception e)
	    {
		e.printStackTrace();
		luwrain.message("Во время внесения изменений в реестр произошла неожиданная ошибка", Luwrain.MESSAGE_ERROR); 
	    }
	    luwrain.reloadComponent(Luwrain.ReloadComponents.ENVIRONMENT_SOUNDS);
	    luwrain.message("Новые настройки сохранены", Luwrain.MESSAGE_OK);
	    return true;
	}
    };

    private ControlPanel controlPanel;
    private final ListUtils.FixedModel model = new ListUtils.FixedModel();

    SoundSchemes(ControlPanel controlPanel, ListArea.Params params)
    {
	super(params);
	NullCheck.notNull(controlPanel, "controlPanel");
	this.controlPanel = controlPanel;
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onKeyboardEvent(event))
	    return true;
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onEnvironmentEvent(event))
	    return true;
	return super.onEnvironmentEvent(event);
    }

    @Override public boolean saveSectionData()
    {
	return true;
    }

    static private Item[] loadItems(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final LinkedList<Item> items = new LinkedList<Item>();
	final LinkedList<Path> dirs = new LinkedList<Path>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(luwrain.getPathProperty("luwrain.dir.data").resolve(SCHEMES_DIR))) {
		for (Path p : directoryStream) 
		    if (Files.isDirectory(p))
			dirs.add(p);
	    } 
	catch (IOException e) 
	{
	    luwrain.crash(e);
	    return new Item[0];
	}
	for(Path p: dirs)
	{
	    String title = null;
	    try {
		final List<String> lines = Files.readAllLines(p.resolve("TITLE." + luwrain.getProperty("luwrain.lang") + ".txt"));
		if (!lines.isEmpty())
		    title = lines.get(0);
	    }
	    catch(Exception e)
	    {
		Log.warning("control-panel", "unable to read title of the sound scheme in " + p.toString());
		e.printStackTrace();
		continue;
	    }
	    if (title != null && !title.trim().isEmpty())
		items.add(new Item(p, title));
	} //for(dirs)
return items.toArray(new Item[items.size()]);
    }

    static SoundSchemes create(ControlPanel controlPanel)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	final Luwrain luwrain = controlPanel.getCoreInterface();
	final ListArea.Params params = new ListArea.Params();
	params.environment = new DefaultControlEnvironment(luwrain);
	params.appearance = new ListUtils.DefaultAppearance(params.environment);
	params.name = "Звуковые схемы";
	params.model = new ListUtils.FixedModel(loadItems(luwrain));
	return new SoundSchemes(controlPanel, params);
    }
}
