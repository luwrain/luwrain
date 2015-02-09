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

package org.luwrain.mainmenu;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.mainmenu.StringConstructor;
import org.luwrain.sounds.EnvironmentSounds;

public class StandardBuilderPart implements BuilderPart
{
    private Luwrain luwrain;
    private String[] content;
    private StringConstructor stringConstructor;

    public StandardBuilderPart(StringConstructor stringConstructor, String[] content)
    {
	this.stringConstructor = stringConstructor;
	this.content = content;
    }

    public void setContent(String[] content)
    {
	this.content = content;
    }

    @Override public Item[] buildItems()
    { 
	final StringConstructor s = stringConstructor;
	Vector<Item> res = new Vector<Item>();
	res.add(new EmptyItem());
	res.add(new DateTimeItem(stringConstructor));
	res.add(new Separator(){
		private StringConstructor stringConstructor = s;
		@Override public boolean isDefaultSeparator()
		{
		    return true;
		}
		@Override public String getSeparatorText()
		{
		    return stringConstructor.mainMenuStandardPart();
		}
		@Override public String getText()
		{
		    return "";
		}
		@Override public void introduce()
		{
		    luwrain.silence();
		    EnvironmentSounds.play(Sounds.MAIN_MENU_EMPTY_LINE);
		}
		@Override public boolean isAction()
		{
		    return false;
		}
		@Override public void doAction()
		{
		}
	    });
	for(String ss: content)
	    res.add(constructItem(ss));
	return res.toArray(new Item[res.size()]);
    }

    private Item constructItem(String name)
    {
	if (name == null || name.trim().isEmpty())
	    return new EmptyItem();
	final String title = stringConstructor.actionTitle(name);
	if (title.trim().isEmpty())
	    return new EmptyItem();
	return new ActionItem(name, title);
    }
}
