/*
   Copyright 2012-2013 Michael Pozhidaev <msp@altlinux.org>

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

import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.registry.Registry;

public class NotepadApp implements Application, NotepadActions
{
    static private final Charset ENCODING = StandardCharsets.UTF_8;

    private NotepadStringConstructor stringConstructor;
    private Object instance;
    private NotepadArea area;
    private String arg;

    public NotepadApp()
    {
    }

    public NotepadApp(String arg)
    {
	this.arg = arg;
    }

    public boolean onLaunch(Object instance)
    {
	Object o = Langs.requestStringConstructor("notepad");
	if (o == null)
	    return false;
	stringConstructor = (NotepadStringConstructor)o;
	area = new NotepadArea(this, stringConstructor, stringConstructor.newFileName());
	if (arg != null && !arg.trim().isEmpty())
	{
	    try {
		String[] text = readTextFile(arg);
		area.setContent(text);
		area.setFileName(arg);
	    }
	    catch (IOException e)
	    {
		e.printStackTrace();
		Log.error("notepad", arg + ":" + e.getMessage());
		Dispatcher.message(stringConstructor.errorOpeningFile());
		return false;
	    }
	} else
	{
	    Registry registry = Dispatcher.getRegistry();
	    if (registry.getTypeOf(CoreRegistryValues.INSTANCE_USER_HOME_DIR) == Registry.STRING)
	    {
		File dir = new File(registry.getString(CoreRegistryValues.INSTANCE_USER_HOME_DIR));
		File f = new File(dir, stringConstructor.newFileName());
		area.setFileName(f.getAbsolutePath());
	    } else
		area.setFileName(stringConstructor.newFileName());
	}
	this.instance = instance;
	return true;
    }

    public void save()
    {
	//FIXME:Proper file name choosing;
	String fileName = area.getFileName();
	if (fileName == null || fileName.trim().isEmpty())
	{
	    Log.warning("notepad", "edit area has no associated file name");
	    return;
	}
	try {
	    if (area.getContent() != null)
		writeTextFile(fileName, area.getContent());
	    Dispatcher.message(stringConstructor.fileIsSaved());
	}
	catch(IOException e)
	{
	    e.printStackTrace();
	    Log.error("notepad", fileName + ":" + e.getMessage());
	    Dispatcher.message(stringConstructor.errorSavingFile());
	}
    }

    public AreaLayout getAreasToShow()
    {
	return new AreaLayout(area);
    }

    public void closeNotepad()
    {
	Dispatcher.closeApplication(instance);
    }

    private String[] readTextFile(String fileName) throws IOException
    {
	ArrayList<String> a = new ArrayList<String>();
	Path path = Paths.get(fileName);
	try (Scanner scanner =  new Scanner(path, ENCODING.name()))
	{
	    while (scanner.hasNextLine())
		a.add(scanner.nextLine());
	    }
	return a.toArray(new String[a.size()]);
    }

    private void writeTextFile(String fileName, String[] lines) throws IOException
    {
	Path path = Paths.get(fileName);
	try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING))
	{
	    for(int i = 0;i < lines.length;i++)
	    {
		writer.write(lines[i]);
		writer.newLine();
	    }
	}
    }
}
