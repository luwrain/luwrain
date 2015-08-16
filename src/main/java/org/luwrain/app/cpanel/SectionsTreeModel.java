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
import org.luwrain.hardware.*;
import org.luwrain.app.cpanel.sects.*;

class SectionsTreeModel implements TreeModel
{
    private Luwrain luwrain;
    private EnvironmentImpl environment;
    private Section[] extensionsSections;

    private BasicSection appsSection;
    private BasicSection keyboardSection;
    private BasicSection soundsSection;
    private BasicSection speechSection;
    private BasicSection networkSection;
    private BasicSection hardwareSection;
    private BasicSection storageDevicesSection;
    private BasicSection uiSection;
    private BasicSection extensionsSection;
    private BasicSection workersSection ;
    private PersonalInfo personalInfoSection;

    private Device[] devices;
    private StorageDevice[] storageDevices;

    private BasicSection root;

    public SectionsTreeModel(Luwrain luwrain,
			     EnvironmentImpl environment,
			     Section[] extensionsSections)
    {
	this.luwrain = luwrain;
	this.environment = environment;
	this.extensionsSections = extensionsSections;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (environment == null)
	    throw new NullPointerException("environment may not be null");
	if (extensionsSections == null)
	    throw new NullPointerException("extensionsSections may not be null");
	final org.luwrain.hardware.Hardware hardware = luwrain.getHardware();
	if (hardware != null)
	{
	    devices = hardware.getDevices();
	    storageDevices = hardware.getStorageDevices();
	}

	if (devices != null)
	{
	    hardwareSection = new BasicSection("Оборудование");
	    for(int i = 0;i < devices.length;++i)
		hardwareSection.addSubsection(new BasicSection(devices[i].vendor + " " + devices[i].model));
	} else
	    hardwareSection = null;

	if (storageDevices != null)
	{
	    storageDevicesSection = new BasicSection("Устройства хранения");
	    for(int i = 0;i < storageDevices.length;++i)
		storageDevicesSection.addSubsection(new BasicSection(storageDevices[i].model));
	} else
	    storageDevicesSection = null;

	appsSection = new BasicSection("Приложения");
	speechSection = new BasicSection("Речь");
	soundsSection = new BasicSection("Звуки");
	keyboardSection = new BasicSection("Клавиатура");
	uiSection = new BasicSection("Интерфейс");
	extensionsSection = new BasicSection("Расширения");
	networkSection = new BasicSection("Сеть");
	workersSection = new BasicSection("Фоновые задачи");
	personalInfoSection = new PersonalInfo(environment);

	for(Section s: extensionsSections)
	{
	    if (!s.isSectionEnabled())
		continue;
	    switch(s.getDesiredRoot())
	    {
		//	    case BasicSections.ROOT:
	    case BasicSections.APPLICATIONS:
		appsSection.addSubsection(s);
		break;
	    case BasicSections.KEYBOARD:
		keyboardSection.addSubsection(s);
		break;
	    case BasicSections.SOUNDS:
		soundsSection.addSubsection(s);
		break;
	    case BasicSections.SPEECH:
		speechSection.addSubsection(s);
		break;
	    case BasicSections.NETWORK:
		networkSection.addSubsection(s);
		break;
	    case BasicSections.HARDWARE:
		hardwareSection.addSubsection(s);
		break;
	    case BasicSections.UI:
		uiSection.addSubsection(s);
	    case BasicSections.EXTENSIONS:
		extensionsSection.addSubsection(s);
		break;
	    case BasicSections.WORKERS:
		workersSection.addSubsection(s);
	    }
	}

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
	final Section[] subsections = sect.getChildSections();
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
	final Section[] subsections = sect.getChildSections();
	return subsections != null?subsections.length:0;
    }

    @Override public Object getChild(Object node, int index)
    {
	if (node == null || !(node instanceof Section))
	    return 0;
	final Section sect = (Section)node;
	final Section[] subsections = sect.getChildSections();
	if (subsections == null)
	    return null;
	return index < subsections.length?subsections[index]:null;
    }

    @Override public void endChildEnumeration(Object obj)
    {
    }

    private BasicSection constructTree()
    {
	final BasicSection res = new BasicSection("Панель управления");

	res.addSubsection(appsSection);
	res.addSubsection(personalInfoSection);
	res.addSubsection(uiSection);
	res.addSubsection(keyboardSection);
	res.addSubsection(speechSection);
	res.addSubsection(soundsSection);
	res.addSubsection(networkSection);
	res.addSubsection(extensionsSection);
	if (hardwareSection != null)
	res.addSubsection(hardwareSection);
	if (storageDevicesSection != null)
	res.addSubsection(storageDevicesSection);
	res.addSubsection(workersSection);
	return res;
    }
}
