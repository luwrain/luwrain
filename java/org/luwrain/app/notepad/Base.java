/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.app.notepad;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;
import org.luwrain.core.*;

class Base
{
    public String[] read(String fileName, Charset encoding)
    {
	if (fileName == null || fileName.isEmpty())
	    return null;
	try {
	    return readTextFile(fileName, encoding);
	    }
	    catch (IOException e)
	    {
		Log.error("notepad", fileName + ":" + e.getMessage());
		e.printStackTrace();
		return null;
	    }
    }

    public boolean save(String fileName, 
			String[] lines,
			Charset encoding)
    {
	if (fileName == null || fileName.isEmpty())
	    return false;
	try {
	    saveTextFile(fileName, lines, encoding);
	    return true;
	    }
	    catch (IOException e)
	    {
		Log.error("notepad", fileName + ":" + e.getMessage());
		e.printStackTrace();
		return false;
	    }
    }

    private String[] readTextFile(String fileName, Charset encoding) throws IOException
    {
	ArrayList<String> a = new ArrayList<String>();
	Path path = Paths.get(fileName);
	try (Scanner scanner =  new Scanner(path, encoding.name()))
	{
	    while (scanner.hasNextLine())
		a.add(scanner.nextLine());
	    }
	return a.toArray(new String[a.size()]);
    }

    private void saveTextFile(String fileName,
			       String[] lines,
			       Charset encoding) throws IOException
    {
	Path path = Paths.get(fileName);
	try (BufferedWriter writer = Files.newBufferedWriter(path, encoding))
	{
	    for(int i = 0;i < lines.length;i++)
	    {
		writer.write(lines[i]);
		if (i + 1 < lines.length)
		    writer.newLine();
	    }
	}
    }
}
