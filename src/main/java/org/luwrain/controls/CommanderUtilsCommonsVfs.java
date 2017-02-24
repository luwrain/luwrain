
package org.luwrain.controls;

import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;

import org.luwrain.core.*;

public class CommanderUtilsCommonsVfs
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

	@Override public FileObject[] getEntryChildren(FileObject entry)
	{
	    NullCheck.notNull(entry, "entry");
	    try {
	    return entry.getChildren();
	    }
	    catch(FileSystemException e)
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
	    catch(FileSystemException e)
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
	    environment.playSound(Sounds.LIST_ITEM);
	    environment.say(entry.getName().getBaseName());
	}
    }

    static public class ByNameComparator implements java.util.Comparator
    {
	@Override public int compare(Object o1, Object o2)
	{
	    /*
	    if (!(o1 instanceof CommanderArea.Entry) || !(o2 instanceof CommanderArea.Entry))
		return 0;
	    final CommanderArea.Entry i1 = (CommanderArea.Entry)o1;
	    final CommanderArea.Entry i2 = (CommanderArea.Entry)o2;
	    if (i1.type == Type.PARENT)
		return i2.type == Type.PARENT?0:-1;
	    if (i2.type == Type.PARENT)
		return i2.type == Type.PARENT?0:1;
	    if (Files.isDirectory(i1.path) && Files.isDirectory(i2.path))//We don't use Entry.type() because it  returns symlink even on a directory
		return i1.getBaseName().compareTo(i2.getBaseName());
	    if (Files.isDirectory(i1.path))
		return -1;
	    if (Files.isDirectory(i2.path))
		return 1;
	    return i1.getBaseName().compareTo(i2.getBaseName());
	    */
	    return 0;
	}
    }


    static public class AllEntriesFilter implements NgCommanderArea.Filter<FileObject>
    {
@Override public boolean commanderEntrySuits(FileObject entry)
{
    return true;
}
    }

    static public NgCommanderArea.Params<FileObject> createParams(ControlEnvironment environment) throws FileSystemException
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

    static public FileObject prepareInitialLocation(NgCommanderArea.Params<FileObject> params, String path) throws FileSystemException
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
