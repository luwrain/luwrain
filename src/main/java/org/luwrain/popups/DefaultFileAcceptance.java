
package org.luwrain.popups;

import java.nio.file.*;

import org.luwrain.core.NullCheck;
import org.luwrain.core.FileTypes;

public class DefaultFileAcceptance implements FilePopup.Acceptance
{
    public enum Type {ANY, EXISTING, NOT_EXISTING, DIRECTORY, NOT_DIRECTORY};

    private Type type;
    private String[] fileExtensions;

    public DefaultFileAcceptance(Type type)
    {
	this.type = type;
	this.fileExtensions = new String[0];
    }

    public DefaultFileAcceptance(Type type, String[] fileExtensions)
    {
	this.type = type;
	this.fileExtensions = fileExtensions;
	NullCheck.notNullItems(fileExtensions, "fileExtensions");
    }

	@Override public boolean pathAcceptable(Path path)
    {
	NullCheck.notNull(path, "path");
	if (!path.isAbsolute())
	    return false;
	if (!Files.exists(path))
	    return type == Type.ANY || type == Type.NOT_EXISTING;
	if (type == Type.NOT_EXISTING)
	    return false;
	if (Files.isDirectory(path))
	    return type == Type.ANY || type == Type.EXISTING || type == Type.DIRECTORY;
	//Regular files
	if (type == Type.NOT_DIRECTORY)
	    return false;
	if (fileExtensions == null || fileExtensions.length < 1)
	    return true;
	final String ext = FileTypes.getExtension(path.toString());
	for(String s: fileExtensions)
	    if (s.toLowerCase().equals(ext.toLowerCase()))
		return true;
	return false;
    }
}
