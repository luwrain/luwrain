
package org.luwrain.controls;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.luwrain.core.*;

import org.luwrain.controls.CommanderArea.Entry.Type;

public class CommanderUtils
{
    static public class AllFilesFilter implements CommanderArea.Filter
    {
	@Override public boolean commanderEntrySuits(CommanderArea.Entry entry)
	{
	    return true;
	}
    }

    static public class NoHiddenFilter implements CommanderArea.Filter
    {
	@Override public boolean commanderEntrySuits(CommanderArea.Entry entry)
	{
	    NullCheck.notNull(entry, "entry");
	    if (entry.type == Type.PARENT)
		return true;
	    try {
		return !Files.isHidden(entry.path);
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

    static public class DefaultAppearance implements CommanderArea.CommanderAppearance
    {
	protected ControlEnvironment environment;

	public DefaultAppearance(ControlEnvironment environment)
	{
	    NullCheck.notNull(environment, "environment");
	    this.environment = environment;
	}

	@Override public void announceEntry(CommanderArea.Entry entry, boolean brief)
	{
	    NullCheck.notNull(entry, "entry");
	    environment.playSound(Sounds.LIST_ITEM);
	    if (brief)
		briefIntroduction(entry); else
		fullIntroduction(entry);
	}

	private void briefIntroduction(CommanderArea.Entry entry)
	{
	    final String name = entry.baseName();
	    if (entry.type == Type.PARENT)
		environment.hint(environment.getStaticStr("CommanderParentDirectory")); else
		if (name.trim().isEmpty())
		    environment.hint(Hints.EMPTY_LINE); else
		    environment.say(entry.baseName());
	}

	private void fullIntroduction(CommanderArea.Entry entry)
	{
	    if (entry.type == Type.PARENT)
	    {
		environment.hint(environment.getStaticStr("CommanderParentDirectory"));
		return;
	    }
	    final String name = entry.baseName();
	    if (name.trim().isEmpty() && !entry.marked() && 
		entry.type == Type.REGULAR)
	    {
		environment.hint(Hints.EMPTY_LINE);
		return;
	    }
	    String res = name;
	    switch(entry.type)
	    {
	    case DIR:
		res += (" " + environment.getStaticStr("CommanderDirectory"));
		break;
	    case SYMLINK:
	    case SYMLINK_DIR:
		res += (" " + environment.getStaticStr("CommanderSymlink"));
		break;
	    case SPECIAL:
		res += (" " + environment.getStaticStr("CommanderSpecial"));
		break;
	    }
	    if (entry.marked())
		res = environment.getStaticStr("CommanderSelected") + " " + res;
	    environment.say(res);
	}

	@Override public  void announceLocation(Path path)
	{
	    NullCheck.notNull(path, "path");
	    environment.playSound(Sounds.COMMANDER_LOCATION);
	    if (path.getFileName() != null)
	    environment.say(path.getFileName().toString()); else
		environment.say("Корневой каталог");//FIXME:
	}

	@Override public String getScreenLine(CommanderArea.Entry entry)
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
	    if (!(o1 instanceof CommanderArea.Entry) || !(o2 instanceof CommanderArea.Entry))
		return 0;
	    final CommanderArea.Entry i1 = (CommanderArea.Entry)o1;
	    final CommanderArea.Entry i2 = (CommanderArea.Entry)o2;
	    if (i1.type == Type.PARENT)
		return i2.type == Type.PARENT?0:-1;
	    if (i2.type == Type.PARENT)
		return i2.type == Type.PARENT?0:1;
	    if (Files.isDirectory(i1.path) && Files.isDirectory(i2.path))//We don't use Entry.type() because it  returns symlink even on a directory
		return i1.baseName().compareTo(i2.baseName());
	    if (Files.isDirectory(i1.path))
		return -1;
	    if (Files.isDirectory(i2.path))
		return 1;
	    return i1.baseName().compareTo(i2.baseName());
	}
    }
}
