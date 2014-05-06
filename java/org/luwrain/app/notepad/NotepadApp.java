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
import org.luwrain.core.events.*;
import org.luwrain.controls.EditArea;
import org.luwrain.core.registry.Registry;
import org.luwrain.popups.*;

public class NotepadApp implements Application, Actions
{
    static private final Charset ENCODING = StandardCharsets.UTF_8;

    private StringConstructor stringConstructor;
    private Object instance;
    private EditArea area;
    private String fileName = "";
    private boolean modified = false; 

    public NotepadApp()
    {
    }

    public NotepadApp(String arg)
    {
	this.fileName = arg;
    }

    public boolean onLaunch(Object instance)
    {
	Object o = Langs.requestStringConstructor("notepad");
	if (o == null)
	    return false;
	stringConstructor = (StringConstructor)o;
	createArea();
	if (fileName != null && !fileName.isEmpty())
	{
	    readByFileName(fileName);
	    File f = new File(fileName);
	    area.setName(f.getName());
	} else
	    area.setName(stringConstructor.newFileName());
	this.instance = instance;
	return true;
    }

    public boolean save()
    {
	if (fileName == null || fileName.isEmpty())
	{
	    Registry registry = Luwrain.getRegistry();
	    File dir = new File(registry.getTypeOf(CoreRegistryValues.INSTANCE_USER_HOME_DIR) == Registry.STRING?registry.getString(CoreRegistryValues.INSTANCE_USER_HOME_DIR):"/");//FIXME:System dependent slash;
	    File chosenFile = Luwrain.openPopup(instance,  stringConstructor.savePopupName(), stringConstructor.savePopupPrefix(),
						new File(dir, stringConstructor.newFileName()));
	    if (chosenFile == null)
		return false;
	    //FIXME:Is a valid file;
	    fileName = chosenFile.getAbsolutePath();
	}
	try {
	    if (area.getContent() != null)
		writeTextFile(fileName, area.getContent());
	    modified = false;
	    Luwrain.message(stringConstructor.fileIsSaved());
	}
	catch(IOException e)
	{
	    Log.error("notepad", fileName + ":" + e.getMessage());
	    e.printStackTrace();
	    Luwrain.message(stringConstructor.errorSavingFile());
	    return false;
	}
	return true;
    }

    public void open()
    {
	if (!checkIfUnsaved())
	    return;
	File dir = null;
	if (fileName == null || fileName.isEmpty())
	{
	    Registry registry = Luwrain.getRegistry();
	    dir = new File(registry.getTypeOf(CoreRegistryValues.INSTANCE_USER_HOME_DIR) == Registry.STRING?registry.getString(CoreRegistryValues.INSTANCE_USER_HOME_DIR):"/");//FIXME:System dependent slash;
	} else
	{
	    File f = new File(fileName);
	    dir = f.getParentFile();
	}
	File chosenFile = Luwrain.openPopup(instance, null, null, dir);
	if (chosenFile == null)
	    return;
	if (!readByFileName(chosenFile.getAbsolutePath()))
	    return;
	    fileName = chosenFile.getAbsolutePath();
	    area.setName(chosenFile.getName());
    }

    public void markAsModified()
    {
	modified = true;
    }

    private boolean readByFileName(String pathToRead)
    {
	if (pathToRead == null || pathToRead.isEmpty())
	    return false;
	try {
		area.setContent(readTextFile(pathToRead));
	    }
	    catch (IOException e)
	    {
		Log.error("notepad", fileName + ":" + e.getMessage());
		e.printStackTrace();
		return false;
	    }
	return true;
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
		if (i + 1 < lines.length)
		    writer.newLine();
	    }
	}
    }

    private void createArea()
    {
	final Actions a = this;
	area = new EditArea(fileName){
		private Actions actions = a;
		public void onChange()
		{
		    actions.markAsModified();
		}
		public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    switch(event.getCode())
		    {
		    case EnvironmentEvent.CLOSE:
			actions.close();
			return true;
		    case EnvironmentEvent.INTRODUCE:
			Speech.say(stringConstructor.introduction() + " " + getName()); 
			return true;
		    case EnvironmentEvent.SAVE:
			actions.save();
			return true;
		    case EnvironmentEvent.OPEN:
			actions.open();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
	    };
    }

    public AreaLayout getAreasToShow()
    {
	return new AreaLayout(area);
    }

    public void close()
    {
	if (!checkIfUnsaved())
	    return;
	Luwrain.closeApp(instance);
    }

    private boolean checkIfUnsaved()
    {
	if (!modified)
	    return true;
	YesNoPopup popup = new YesNoPopup(instance, stringConstructor.saveChangesPopupName(), stringConstructor.saveChangesPopupQuestion(), false);
	Luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return false;
	if ( popup.getResult() && !save())
	    return false;
	modified = false;
	return true;
    }
}
