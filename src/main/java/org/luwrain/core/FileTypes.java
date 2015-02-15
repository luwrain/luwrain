/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core;

public class FileTypes
{
    private ShortcutManager shortcuts;

    public FileTypes(ShortcutManager shortcuts)
    {
	this.shortcuts = shortcuts;
    }

    public void openFileNames(String[] fileNames)
    {
	//FIXME:
	if (fileNames == null || fileNames.length!= 1 || fileNames[0] == null )
	{
	    Log.error("file-types", "files could not be properly processed due to incomplete implementation");
	    return;
	}
	String ext = extension(fileNames[0]);
	if (ext.equals("txt") || ext.equals("TXT"))
	{
	    /*
	    if (!shortcuts.launch("notepad", fileNames))
		Log.warning("file-types", "could not launch the application by wrapper with name \'notepad\'");
	    */
	    return;
	}

	if (ext.equals("doc") || ext.equals("DOC"))
	{
	    /*
	    if (!shortcuts.launch("preview", fileNames))
		Log.warning("file-types", "could not launch the application by wrapper with name \'preview\'");
	    */
	    return;
	}

	if (ext.equals("avi") || ext.equals("AVI"))
	{
	    run("/usr/bin/mplayer -fs -slave -quiet \'" + fileNames[0] + "\'");
	    return;
	}

	if (ext.equals("pdf") || ext.equals("PDF"))
	{
	    run("/usr/bin/xpdf -fullscreen \'" + fileNames[0] + "\' &> /tmp/output");
	    return;
	}


    }

    private static String extension(String path)
    {
	if (path == null || path.trim().isEmpty())
	    return "";
	int pos = -1;
	for(int i = 0;i < path.length();i++)
	    switch(path.charAt(i))
	    {
	    case '/'://FIXME:UNIX style!
		pos = -1;
		break;
	    case '.':
		pos = i;
		break;
	    }
	if (pos < 0 || pos + 1 >= path.length())
	    return "";
	return path.substring(pos + 1).trim();
    }

    private static void run(String cmd)
    {
	if (cmd == null || cmd.trim().isEmpty())
	    return;
	Log.debug("file-types", "executing:" + cmd);
	String[] args = new String[3];
	args[0] = "/bin/sh";
	args[1] = "-c";
	args[2] = cmd;
	try {
	    Process process = Runtime.getRuntime().exec(args);
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    Log.error("file-types", e.getMessage());
	}
    }
}
