
package org.luwrain.registry;

import org.luwrain.core.NullCheck;

//Root directory may not contain values;
public final class Path
{
    private boolean absolute = true;
    private final String[] dirItems;
    private final String valueName;

    public Path(boolean absolute,
		String[] dirItems,
		String valueName)
    {
	this.absolute = absolute;
	this.dirItems = dirItems;
	this.valueName = valueName;
	NullCheck.notNullItems(dirItems, "dirItems");
	NullCheck.notNull(valueName, "valueName");
	for(int i = 0;i < dirItems.length;++i)	    
	    if (dirItems[i].isEmpty())
		throw new NullPointerException("dirItems[" + i + "] may not be empty");
    }

    public Path(boolean absolute, String[] dirItems)
    {
	this.absolute = absolute;
	this.dirItems = dirItems;
	if (dirItems == null)
	    throw new NullPointerException("dirItems may not be null");
	for(int i = 0;i < dirItems.length;++i)
	{
	    if (dirItems[i] == null)
		throw new NullPointerException("dirItems[" + i + "] may not be null");
	    if (dirItems[i].isEmpty())
		throw new NullPointerException("dirItems[" + i + "] may not be empty");
	}
	this.valueName = "";
    }

    public boolean isAbsolute()
    {
	return absolute;
    }

    public boolean isDirectory()
    {
	return valueName.isEmpty();
    }

    public boolean isRoot()
    {
	return absolute && dirItems.length < 1 && valueName.isEmpty();
    }


    public String[] dirItems()
    {
	return dirItems;
    }

    public String valueName()
    {
	return valueName;
    }

    public Path getDirectory()
    {
	return new Path(absolute, dirItems);
    }

    public int getDirCount()
    {
	return dirItems.length;
    }

    public Path getParentOfDir()
    {
	if (dirItems.length < 1)
	    return this;
	String[] newItems = new String[dirItems.length - 1];
	for(int i = 0;i < dirItems.length - 1;++i)
	    newItems[i] = dirItems[i];
	return new Path(absolute, newItems);
    }

    public String getLastDirItem()
    {
	if (dirItems.length < 1)
	    return "";
	return dirItems[dirItems.length - 1];
    }

    public @Override String toString()
    {
	String res = absolute?"/":"";
	for(String s: dirItems)
	    res += s + "/";
	res += valueName;
	return res;
    }
}
