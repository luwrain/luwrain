/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.app.cpanel;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

class SectionsTreeModel implements TreeModel
{



    private BasicSection appsSection;
    private BasicSection keyboardSection;
    private BasicSection soundsSection;
    private BasicSection speechSection;
    private BasicSection hardwareSection;
    private BasicSection uiSection;
    private BasicSection extensionsSection;

    private BasicSection root;

    public SectionsTreeModel()
    {
	appsSection = new BasicSection("Приложения", new Section[0]);
	speechSection = new BasicSection("Речь", new Section[0]);
	soundsSection = new BasicSection("Звуки", new Section[0]);
	keyboardSection = new BasicSection("Клавиатура", new Section[0]);
	hardwareSection = new BasicSection("Оборудование", new Section[0]);
	uiSection = new BasicSection("Интерфейс", new Section[0]);
	extensionsSection = new BasicSection("Расширения", new Section[0]);


	root = constructTree();
    }

    @Override public Object getRoot()
    {
	return root;
    }

    @Override public boolean isLeaf(Object node)
    {
	if (node == null || !(node instanceof Section))
	    return true;
	final Section sect = (Section)node;
	final Section[] subsections = sect.getControlPanelSubsections();
	return subsections == null || subsections.length < 1;
    }

    @Override public void beginChildEnumeration(Object obj)
    {
    }

    @Override public int getChildCount(Object node)
    {
	if (node == null || !(node instanceof Section))
	    return 0;
	final Section sect = (Section)node;
	final Section[] subsections = sect.getControlPanelSubsections();
	return subsections != null?subsections.length:0;
    }

    @Override public Object getChild(Object node, int index)
    {
	if (node == null || !(node instanceof Section))
	    return 0;
	final Section sect = (Section)node;
	final Section[] subsections = sect.getControlPanelSubsections();
	if (subsections == null)
	    return null;
	return index < subsections.length?subsections[index]:null;
    }

    @Override public void endChildEnumeration(Object obj)
    {
    }

    private BasicSection constructTree()
    {
	Vector<Section> sections = new Vector<Section>();
	sections.add(appsSection);
	sections.add(uiSection);
	sections.add(keyboardSection);
	sections.add(speechSection);
	sections.add(soundsSection);
	sections.add(extensionsSection);
	sections.add(hardwareSection);
	return new BasicSection("Панель управления", sections.toArray(new Section[sections.size()]));
    }
}
