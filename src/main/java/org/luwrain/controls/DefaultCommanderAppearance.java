
package org.luwrain.controls;

import java.nio.file.*;

import org.luwrain.core.*;

class DefaultCommanderAppearance implements CommanderArea.Appearance
{
    private ControlEnvironment environment;

    public DefaultCommanderAppearance(ControlEnvironment environment)
    {
	this.environment = environment;
	NullCheck.notNull(environment, "environment");
    }

    @Override public void introduceEntry(CommanderArea.Entry entry, boolean brief)
    {
	NullCheck.notNull(entry, "entry");
	environment.playSound(Sounds.NEW_LIST_ITEM);
	if (brief)
	    briefIntroduction(entry); else
	    fullIntroduction(entry);
    }

    private void briefIntroduction(CommanderArea.Entry entry)
    {
	final String name = entry.baseName();
	if (entry.parent())
	    environment.hint(environment.staticStr(LangStatic.COMMANDER_PARENT_DIRECTORY)); else
	    if (name.trim().isEmpty())
		environment.hint(Hints.EMPTY_LINE); else
		environment.say(entry.baseName());
    }

    private void fullIntroduction(CommanderArea.Entry entry)
    {
	/*
	final boolean selected = entry.selected();
	final boolean dir = entry.type() == Entry.Type.DIR;
	final String name = entry.file().getName();
	if (name.equals(PARENT_DIR))
	{
	    environment.hint(environment.staticStr(LangStatic.COMMANDER_PARENT_DIRECTORY));
	    return;
	}
	if (selected && dir)
	    environment.say(environment.staticStr(LangStatic.COMMANDER_SELECTED_DIRECTORY) + " " + name); else
	    if (selected)
		environment.say(environment.staticStr(LangStatic.COMMANDER_SELECTED) + " " + name); else
		if (dir)
		    environment.say(name + " " + environment.staticStr(LangStatic.COMMANDER_DIRECTORY)); else
		{
		    if (name.trim().isEmpty())
			environment.hint(Hints.EMPTY_LINE); else
			environment.say(name);
		}
	*/
    }

    @Override public  void introduceLocation(Path path)
    {
	/*
	if (file == null)
	    return;
	environment.playSound(Sounds.COMMANDER_NEW_LOCATION);
	for(Partition p: mountedPartitions)
	    if (p.file().equals(file))
	    {
		environment.say(strings.partitionTitle(p));
		return;
	    }
	environment.say(file.getName());
	*/
    }


    @Override public String getScreenLine(CommanderArea.Entry entry)
    {
    /*
	if (entry == null)
	    throw new NullPointerException("entry may not be null");
	final boolean selected = entry.selected();
	final boolean dir = entry.type() == Entry.DIRECTORY;
	if (selected && dir)
	    return "*[" + entry.file().getName() + "]";
	if (selected)
	    return "* " + entry.file().getName();
	if (dir)
	    return " [" + entry.file().getName() + "]";
	return "  " + entry.file().getName();
	*/
	return "fixme";
    }

    @Override public String getCommanderName(Path path)
    {
	return "коммандер";
	/*
	if (current == null)
	    return "-";
	for(Partition p: mountedPartitions)
	    if (p.file().equals(current))
		return strings.partitionTitle(p);
	return current.getAbsolutePath();
    }
    */
    }
}


