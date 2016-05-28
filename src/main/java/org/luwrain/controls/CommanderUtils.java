
package org.luwrain.controls;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.luwrain.core.*;

public class CommanderUtils
{
    static public class AllFilesFilter implements CommanderArea2.Filter
    {
	@Override public boolean commanderEntrySuits(CommanderArea2.Entry entry)
	{
	    return true;
	}
    }

    static public class NoHiddenFilter implements CommanderArea2.Filter
    {
	@Override public boolean commanderEntrySuits(CommanderArea2.Entry entry)
	{
	    NullCheck.notNull(entry, "entry");
	    if (entry.type() == CommanderArea2.Entry.Type.PARENT)
		return true;
	    try {
		return !Files.isHidden(entry.path());
	    }
	    catch(IOException e)
	    {
		e.printStackTrace();
		return true;
	    }
	}
    }

    public interface CommanderClickHandler
    {
	/**
	 * Handles the click in the commander area. {@code cursorAt} argument may
	 * be {@code null}, what means that the cursor is at the bottom empty
	 * line or at the parent directory entry. {@code selected} argument is
	 * never {@code null}, but may be empty. 
	 *
	 * @param cursorAt The path entry under the cursor (may be {@code null})
	 * @param selected The entries with user marks or the current entry, if there are no marks (reference to the parent never included)
	 * @return {@code true} if the click is handled
	 */
	boolean onCommanderClick(Path cursorAt, Path[] selected);
    }

    static public class DefaultAppearance implements CommanderArea2.CommanderAppearance
    {
	protected ControlEnvironment environment;

	public DefaultAppearance(ControlEnvironment environment)
	{
	    NullCheck.notNull(environment, "environment");
	    this.environment = environment;
	}

	@Override public void introduceEntry(CommanderArea2.Entry entry, boolean brief)
	{
	    NullCheck.notNull(entry, "entry");
	    environment.playSound(Sounds.NEW_LIST_ITEM);
	    if (brief)
		briefIntroduction(entry); else
		fullIntroduction(entry);
	}

	private void briefIntroduction(CommanderArea2.Entry entry)
	{
	    final String name = entry.baseName();
	    if (entry.type() == CommanderArea2.Entry.Type.PARENT)
		environment.hint(environment.staticStr(LangStatic.COMMANDER_PARENT_DIRECTORY)); else
		if (name.trim().isEmpty())
		    environment.hint(Hints.EMPTY_LINE); else
		    environment.say(entry.baseName());
	}

	private void fullIntroduction(CommanderArea2.Entry entry)
	{
	    if (entry.type() == CommanderArea2.Entry.Type.PARENT)
	    {
		environment.hint(environment.staticStr(LangStatic.COMMANDER_PARENT_DIRECTORY));
		return;
	    }
	    final String name = entry.baseName();
	    if (name.trim().isEmpty() && !entry.selected() && 
		entry.type() == CommanderArea2.Entry.Type.REGULAR)
	    {
		environment.hint(Hints.EMPTY_LINE);
		return;
	    }
	    String res = name;
	    switch(entry.type())
	    {
	    case DIR:
		res += (" " + environment.staticStr(LangStatic.COMMANDER_DIRECTORY));
		break;
	    case SYMLINK:
		res += (" " + environment.staticStr(LangStatic.COMMANDER_SYMLINK));
		break;
	    case SPECIAL:
		res += (" " + environment.staticStr(LangStatic.COMMANDER_SPECIAL));
		break;
	    }
	    if (entry.selected())
		res = environment.staticStr(LangStatic.COMMANDER_SELECTED) + " " + res;
	    environment.say(res);
	}

	@Override public  void introduceLocation(Path path)
	{
	    NullCheck.notNull(path, "path");
	    environment.playSound(Sounds.COMMANDER_NEW_LOCATION);
	    environment.say(path.toString());
	}

	@Override public String getScreenLine(CommanderArea2.Entry entry)
	{
	    NullCheck.notNull(entry, "entry");
	    return entry.baseName();
	}

	@Override public String getCommanderName(Path path)
	{
	    return path.toString();
	}
    }

static public class ByNameComparator implements Comparator
{
    @Override public int compare(Object o1, Object o2)
    {
	if (!(o1 instanceof CommanderArea2.Entry) || !(o2 instanceof CommanderArea2.Entry))
	    return 0;
	final CommanderArea2.Entry i1 = (CommanderArea2.Entry)o1;
	final CommanderArea2.Entry i2 = (CommanderArea2.Entry)o2;
	if (i1.type() == CommanderArea2.Entry.Type.PARENT)
	    return i2.type() == CommanderArea2.Entry.Type.PARENT?0:-1;
	if (i2.type() == CommanderArea2.Entry.Type.PARENT)
	    return i2.type() == CommanderArea2.Entry.Type.PARENT?0:1;
	if (Files.isDirectory(i1.path()) && Files.isDirectory(i2.path()))//We don't use Entry.type() because it  returns symlink even on a directory
	    return i1.baseName().compareTo(i2.baseName());
	    if (Files.isDirectory(i1.path()))
	    return -1;
	    if (Files.isDirectory(i2.path()))
	    return 1;
		return i1.baseName().compareTo(i2.baseName());
    }
}



}
