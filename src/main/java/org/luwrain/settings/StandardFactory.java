/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

//LWR_API 1.0

package org.luwrain.settings;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.cpanel.*;

public final class StandardFactory implements Factory
{
    static private final String ELEMENT_PREFIX = "org.luwrain.settings.StandardFactory:";
    static private final Element personalInfo = new SimpleElement(StandardElements.ROOT, ELEMENT_PREFIX + "PersonalInfo");
    static private final Element uiGeneral = new SimpleElement(StandardElements.UI, ELEMENT_PREFIX + "UIGeneral");
    static private final Element hotKeys = new SimpleElement(StandardElements.UI, ELEMENT_PREFIX + "HotKeys");
    static private final Element fileTypes = new SimpleElement(StandardElements.UI, ELEMENT_PREFIX + "FileTypes");
    static private final Element mainMenu = new SimpleElement(StandardElements.UI, ELEMENT_PREFIX + "MainMenu");
    static private final Element hardwareCpuMem = new SimpleElement(StandardElements.HARDWARE, ELEMENT_PREFIX + "HardwareCpuMem");
    static private final Element hardwareSysDevices = new SimpleElement(StandardElements.HARDWARE, ELEMENT_PREFIX + "HardwareSysDevices");
    static private final Element version = new SimpleElement(StandardElements.ROOT, ELEMENT_PREFIX + "Version");
    static private final Element dateTime = new SimpleElement(StandardElements.ROOT, ELEMENT_PREFIX + "DateTime");
    static private final Element speechGeneral = new SimpleElement(StandardElements.SPEECH, ELEMENT_PREFIX + "SpeechCurrent");
    static private final Element soundsList = new SimpleElement(StandardElements.UI, ELEMENT_PREFIX + "SoundsList");
    static private final Element soundSchemes = new SimpleElement(StandardElements.UI, ELEMENT_PREFIX + "SoundSchemes");

    private final Luwrain luwrain;

    public StandardFactory(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    @Override public Element[] getElements()
    {
	return new Element[]{
	    	    	    version,
	    StandardElements.ROOT,
	    personalInfo,
	    StandardElements.APPLICATIONS,
	    StandardElements.UI,
	    uiGeneral, 
	    hotKeys,
	    mainMenu,
			    //soundSchemes,
	    soundsList,
	    fileTypes,
	    StandardElements.INPUT_OUTPUT,
	    StandardElements.SPEECH,
	    StandardElements.BRAILLE,
	    StandardElements.SOUND,
	    StandardElements.KEYBOARD,
			    	    StandardElements.HARDWARE,
	    hardwareCpuMem,
	    hardwareSysDevices,
	    StandardElements.NETWORK,
	    dateTime,
	    StandardElements.EXTENSIONS,
	    StandardElements.WORKERS,
			    speechGeneral,
	};
    }

    @Override public Element[] getOnDemandElements(Element parent)
    {
	NullCheck.notNull(parent, "parent");
	return new Element[0];
    }

    @Override public org.luwrain.cpanel.Section createSection(Element el)
    {
	NullCheck.notNull(el, "el");
	if (el.equals(hardwareSysDevices))
	    return new SimpleSection(hardwareSysDevices, "Системные устройства");
	if (el.equals(StandardElements.ROOT))
	    return new SimpleSection(StandardElements.ROOT, "Панель управления");
	if (el.equals(StandardElements.APPLICATIONS))
	    return new SimpleSection(StandardElements.APPLICATIONS, "Приложения");
	if (el.equals(dateTime))
	    return new SimpleSection(dateTime, "Дата и время", (controlPanel)->{return new DateTime(controlPanel);});
	if (el.equals(StandardElements.INPUT_OUTPUT))
	    return new SimpleSection(StandardElements.INPUT_OUTPUT, "Ввод/вывод");
	if (el.equals(StandardElements.KEYBOARD))
	    return new SimpleSection(StandardElements.KEYBOARD, "Клавиатура");
	if (el.equals(StandardElements.SOUND))
	    return new SimpleSection(StandardElements.SOUND, "Звук");
	if (el.equals(StandardElements.BRAILLE))
	    return new SimpleSection(StandardElements.BRAILLE, "Браиль", (controlPanel)->Braille.create(controlPanel));
	if (el.equals(StandardElements.SPEECH))
	    return new SimpleSection(StandardElements.SPEECH, luwrain.i18n().getStaticStr("CpSpeech"));
	if (el.equals(StandardElements.NETWORK))
	    return new SimpleSection(StandardElements.NETWORK, "Сеть");
	if (el.equals(StandardElements.HARDWARE))
	    return new SimpleSection(StandardElements.HARDWARE, "Оборудование");
	if (el.equals(StandardElements.UI))
	    return new SimpleSection(StandardElements.UI, "Интерфейс пользователя");
	if (el.equals(StandardElements.EXTENSIONS))
	    return new SimpleSection(StandardElements.EXTENSIONS, "Расширения");
	if (el.equals(StandardElements.WORKERS))
	    return new SimpleSection(StandardElements.WORKERS, "Фоновые задачи");
	if (el.equals(uiGeneral))
	    return new SimpleSection(uiGeneral, luwrain.i18n().getStaticStr("CpUiGeneral"), (controlPanel)->UserInterface.create(controlPanel));
	if (el.equals(hotKeys))
	    return new SimpleSection(hotKeys, "Горячие клавиши", (controlPanel)->HotKeys.create(controlPanel));
	if (el.equals(personalInfo))
	    return new SimpleSection(personalInfo, luwrain.i18n().getStaticStr("CpPersonalInfoSection"), (controlPanel)->{return new PersonalInfo(controlPanel);});
	if (el.equals(fileTypes))
	    return new SimpleSection(fileTypes, "Типы файлов", (controlPanel)->FileTypes.create(controlPanel));
	if (el.equals(hardwareCpuMem))
	    return new SimpleSection(hardwareCpuMem, "Процессор и память", (controlPanel)->HardwareCpuMem.create(controlPanel));
	if (el.equals(version))
	    return new SimpleSection(version, luwrain.i18n().getStaticStr("CpVersion"), (controlPanel)->Version.create(controlPanel));
	if (el.equals(speechGeneral))
	    return new SimpleSection(speechGeneral, luwrain.i18n().getStaticStr("CpSpeechGeneral"), (controlPanel)->Speech.create(controlPanel));
	if (el.equals(soundsList))
	    return new SimpleSection(soundsList, luwrain.i18n().getStaticStr("CpSoundsList"), (controlPanel)->SoundsList.create(controlPanel));
	if (el.equals(mainMenu))
	    return new SimpleSection(mainMenu, luwrain.i18n().getStaticStr("CpMainMenu"), (controlPanel)->MainMenu.create(controlPanel));
	if (el.equals(soundSchemes))
	    return new SimpleSection(soundSchemes, "Звуковые схемы", (controlPanel)->SoundSchemes.create(controlPanel));
	return null;
    }
}
