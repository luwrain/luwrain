/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.app.cpanel.sects;

import java.util.*;

import org.luwrain.core.Luwrain;
import org.luwrain.core.NullCheck;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;
import org.luwrain.hardware.*;
import org.luwrain.app.cpanel.*;

public class Tree
{
    private EnvironmentImpl environment;
    private org.luwrain.app.cpanel.Strings strings;
    private Section[] extensionsSections;

    private BasicSection root;
    private BasicSection appsSection;
    private BasicSection keyboardSection;
    private BasicSection soundsSection;
    private BasicSection speechSection;
    private BasicSection networkSection;
    private BasicSection hardwareSection;
    private BasicSection uiSection;
    private BasicSection extensionsSection;
    private BasicSection workersSection ;
    private PersonalInfo personalInfoSection;

public Tree(EnvironmentImpl environment, 
     org.luwrain.app.cpanel.Strings strings,
     Section[] extensionsSections)
    {
	this.environment = environment;
	this.strings = strings;
	this.extensionsSections = extensionsSections;
	NullCheck.notNull(environment, "environment");
	NullCheck.notNull(strings, "strings");
	NullCheck.notNull(extensionsSections, "extensionsSections");
    }

    public void init()
    {
	appsSection = new BasicSection(strings.sectName(Strings.APPS));
	speechSection = new BasicSection(strings.sectName(Strings.SPEECH));
	soundsSection = new BasicSection(strings.sectName(Strings.SOUNDS));
	keyboardSection = new BasicSection(strings.sectName(Strings.KEYBOARD));
	uiSection = new BasicSection(strings.sectName(Strings.UI));
	extensionsSection = new BasicSection(strings.sectName(Strings.EXTENSIONS));
	networkSection = new BasicSection(strings.sectName(Strings.NETWORK));
	workersSection = new BasicSection(strings.sectName(Strings.WORKERS));
	personalInfoSection = new PersonalInfo(environment);
	hardwareSection = makeHardwareSection();
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
	root = makeRoot();
    }

    private BasicSection makeHardwareSection()
    {
	Device[] sysDevices = null;
	StorageDevice[] storageDevices = null;
	final BasicSection res = new BasicSection(strings.sectName(Strings.HARDWARE));
	final org.luwrain.hardware.Hardware hardware = environment.getLuwrain().getHardware();
	if (hardware != null)
	{
	    sysDevices = hardware.getDevices();
	    storageDevices = hardware.getStorageDevices();
	}
	if (sysDevices != null)
	{
	    final BasicSection sect = new BasicSection(strings.sectName(Strings.SYS_DEVICES));
	    for(int i = 0;i < sysDevices.length;++i)
		sect.addSubsection(new BasicSection(sysDevices[i].vendor + " " + sysDevices[i].model));
	    res.addSubsection(sect);
	}
	if (storageDevices != null)
	{
	    final BasicSection sect = new BasicSection(strings.sectName(Strings.STORAGE_DEVICES));
	    for(int i = 0;i < storageDevices.length;++i)
		sect.addSubsection(new BasicSection(storageDevices[i].model));
	    res.addSubsection(sect);
	}
	return res;
    }

    private BasicSection makeRoot()
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
	res.addSubsection(hardwareSection);
	res.addSubsection(workersSection);
	return res;
    }

    public BasicSection getRoot()
    {
	return root;
    }
}
