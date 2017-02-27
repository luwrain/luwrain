
package org.luwrain.io;

import java.nio.file.*;

import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.controls.NgCommanderArea.EntryType;

public class CommanderUtilsVfs
{
    static private final String LOG_COMPONENT = "commander-vfs";

    static public class Model implements NgCommanderArea.Model<FileObject>
    {
	protected final FileSystemManager manager;

	public Model(FileSystemManager manager)
	{
	    NullCheck.notNull(manager, "manager");
	    this.manager = manager;
	}

	public FileSystemManager getFileSystemManager()
	{
	    return manager;
	}

	@Override public EntryType getEntryType(FileObject entry)
	{
	    NullCheck.notNull(entry, "entry");
	    if (entry instanceof org.apache.commons.vfs2.provider.local.LocalFile)
	    {
		final Path path = Paths.get(entry.getName().getPath());
		if (Files.isSymbolicLink(path))
		    return Files.isDirectory(path)?EntryType.SYMLINK_DIR:EntryType.SYMLINK;
		if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS))
		    return EntryType.DIR;
		if (Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
		    return EntryType.REGULAR;
		return EntryType.SPECIAL;
	    }
	    //	    entry.isDirectory();
	    return EntryType.REGULAR;
	}

	@Override public FileObject[] getEntryChildren(FileObject entry)
	{
	    NullCheck.notNull(entry, "entry");
	    try {
	    return entry.getChildren();
	    }
	    catch(org.apache.commons.vfs2.FileSystemException e)
	    {
		Log.error(LOG_COMPONENT, "unable to get children of " + entry + ":" + e.getClass().getName() + ":" + e.getMessage());
		return null;
	    }
	}

	@Override public FileObject getEntryParent(FileObject entry)
	{
	    NullCheck.notNull(entry, "entry");
	    try {
	    return entry.getParent();
	    }
	    catch(org.apache.commons.vfs2.FileSystemException e)
	    {
		Log.error(LOG_COMPONENT, "unable to get parent of " + entry + ":" + e.getClass().getName() + ":" + e.getMessage());
		return null;
	    }
	}
    }

    static public class Appearance implements NgCommanderArea.Appearance<FileObject>
    {
	protected final ControlEnvironment environment;
	protected final FileSystemManager manager;

	public Appearance(ControlEnvironment environment, FileSystemManager manager)
	{
	    NullCheck.notNull(environment, "environment");
	    NullCheck.notNull(manager, "manager");
	    this.environment = environment;
	    this.manager = manager;
	}

	@Override public String getCommanderName(FileObject entry)
	{
	    return "";
	}

	@Override public void announceLocation(FileObject entry)
	{
	}

	@Override public String getEntryTextAppearance(FileObject entry)
	{
	    NullCheck.notNull(entry, "entry");
	    return entry.getName().getBaseName();
	}

	@Override public void announceEntry(FileObject entry, NgCommanderArea.EntryType type, boolean marked)
	{
	    NullCheck.notNull(entry, "entry");
	    NullCheck.notNull(type, "type");

	    final String name = entry.getName().getBaseName();

		if (name.trim().isEmpty())
		{
		    environment.hint(Hints.EMPTY_LINE);
		    return;
							    }


	    final StringBuilder b = new StringBuilder();

	    if (marked)
		b.append(environment.getStaticStr("CommanderSelected") + " ");
	    b.append(name);
	    switch(type)
	    {
	    case PARENT:
		environment.hint(environment.getStaticStr("CommanderParentDirectory"));
		return;
	    case DIR:
		b.append(environment.getStaticStr("CommanderDirectory"));
		break;
	case SYMLINK:
	    case SYMLINK_DIR:
	    b.append(environment.getStaticStr("CommanderSymlink"));
	    break;
	case SPECIAL:
	    b.append(environment.getStaticStr("CommanderSpecial"));
	break;
	    }
	    environment.playSound(Sounds.LIST_ITEM);
	    environment.say(new String(b));
	}
    }

    static public class ByNameComparator implements java.util.Comparator
    {
	@Override public int compare(Object o1, Object o2)
	{
	    if (!(o1 instanceof NgCommanderArea.Wrapper) || !(o2 instanceof NgCommanderArea.Wrapper))
		return 0;
	    final NgCommanderArea.Wrapper w1 = (NgCommanderArea.Wrapper)o1;
	    final NgCommanderArea.Wrapper w2 = (NgCommanderArea.Wrapper)o2;
	    if (w1.type == EntryType.PARENT)
		return w2.type == EntryType.PARENT?0:-1;
	    if (w2.type == EntryType.PARENT)
		return w1.type == EntryType.PARENT?0:1;
	    final String name1 = ((FileObject)w1.obj).getName().getBaseName().toLowerCase();
	    final String name2 = ((FileObject)w2.obj).getName().getBaseName().toLowerCase();
	    if (w1.isDirectory() && w2.isDirectory())
		return name1.compareTo(name2);
	    if (w1.isDirectory())
		return -1;
	    if (w2.isDirectory())
		return 1;
	    return name1.compareTo(name2);
	}
    }

    static public class AllEntriesFilter implements NgCommanderArea.Filter<FileObject>
    {
@Override public boolean commanderEntrySuits(FileObject entry)
{
    return true;
}
    }

    static public NgCommanderArea.Params<FileObject> createParams(ControlEnvironment environment) throws org.apache.commons.vfs2.FileSystemException
    {
	NullCheck.notNull(environment, "environment");
	final NgCommanderArea.Params<FileObject> params = new NgCommanderArea.Params<FileObject>();
	final FileSystemManager manager = VFS.getManager();
	params.environment = environment;
	params.model = new Model(manager);
	params.appearance = new Appearance(environment, manager);
	params.filter = new AllEntriesFilter();
	params.comparator = new ByNameComparator();
	return params;
    }

    static public FileObject prepareLocation(NgCommanderArea.Params<FileObject> params, String path) throws org.apache.commons.vfs2.FileSystemException
    {
	NullCheck.notNull(params, "params");
	NullCheck.notEmpty(path, "path");
	if (params.model == null || !(params.model instanceof Model))
	    return null;
	final Model model = (Model)params.model;

                               FileSystemOptions opts = new FileSystemOptions();                                                                              
                              FtpFileSystemConfigBuilder.getInstance().setPassiveMode(opts, true); 
            FtpFileSystemConfigBuilder.getInstance( ).setUserDirIsRoot(opts,true);                                                                            



	    return model.getFileSystemManager().resolveFile(path, opts);
    }
}
