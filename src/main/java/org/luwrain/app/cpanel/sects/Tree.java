/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
    private Environment environment;
    private org.luwrain.app.cpanel.Strings strings;
    private Section[] extensionsSections;

    private BasicSection root;
    private BasicSection appsSection;
    private BasicSection keyboardSection;
    private BasicSection soundsSection;
    private BasicSection speechSection;
    private BasicSection networkSection;
    private BasicSection hardwareSection;
    private UI uiSection;
    private BasicSection extensionsSection;
    private BasicSection workersSection ;
    private PersonalInfo personalInfoSection;

public Tree(Environment environment, 
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
	speechSection.addSubsection(new SimpleNavigateSection("Параметры речи", BasicSections.NONE, (environment)->{return new SpeechParamsArea(environment, "Параметры речи");}));
	soundsSection = new BasicSection(strings.sectName(Strings.SOUNDS));
	soundsSection.addSubsection(new SoundSchemes());
	soundsSection.addSubsection(new SoundsList());
	keyboardSection = new BasicSection(strings.sectName(Strings.KEYBOARD));
	uiSection = new UI();
	extensionsSection = new BasicSection(strings.sectName(Strings.EXTENSIONS));
	networkSection = new BasicSection(strings.sectName(Strings.NETWORK));
	workersSection = new BasicSection(strings.sectName(Strings.WORKERS));
	personalInfoSection = new PersonalInfo();
	hardwareSection = makeHardwareSection();
	root = new BasicSection("Панель управления");
	fillRoot();
	addExtensionsSections();
    }

    private void reinit()
    {
	appsSection.clear();
	speechSection.clear();
	soundsSection.clear();
	keyboardSection.clear();
	//	uiSection.clear();
	extensionsSection.clear();
	networkSection.clear();
	workersSection.clear();
	hardwareSection = makeHardwareSection();
	root.clear();
	fillRoot();
	addExtensionsSections();
    }

    private void fillRoot()
    {
	root.addSubsection(appsSection);
	root.addSubsection(personalInfoSection);
	root.addSubsection(uiSection);
	root.addSubsection(keyboardSection);
	root.addSubsection(speechSection);
	root.addSubsection(soundsSection);
	root.addSubsection(networkSection);
	root.addSubsection(extensionsSection);
	root.addSubsection(hardwareSection);
	root.addSubsection(workersSection);
    }

    private void addExtensionsSections()
    {
	for(Section s: extensionsSections)
	{
	    if (!s.isSectionEnabled())
		continue;
	    switch(s.getDesiredRoot())
	    {
	    case BasicSections.ROOT:
root.addSubsection(s);
break;
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
		//FIXME:		uiSection.addSubsection(s);
		break;
	    case BasicSections.EXTENSIONS:
		extensionsSection.addSubsection(s);
		break;
	    case BasicSections.WORKERS:
		workersSection.addSubsection(s);
		break;
	    }
	}
    }

    private BasicSection makeHardwareSection()
    {
	SysDevice[] sysDevices = null;
	StorageDevice[] storageDevices = null;
	final BasicSection res = new BasicSection(strings.sectName(Strings.HARDWARE));
	final org.luwrain.hardware.Hardware hardware = environment.getLuwrain().getHardware();
	if (hardware != null)
	{
	    sysDevices = hardware.getSysDevices();
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

    public BasicSection getRoot()
    {
	return root;
    }

    public void refresh()
    {
	for(Section sect: extensionsSections)
	    sect.refreshChildSubsections();
	reinit();
    }
}
