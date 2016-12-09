
package org.luwrain.settings;

import org.luwrain.core.*;
import org.luwrain.cpanel.*;

public class StandardFactory implements Factory
{
    static private final String ELEMENT_PREFIX = "org.luwrain.settings.StandardFactory:";

    static private final Element personalInfo = new SimpleElement(StandardElements.ROOT, ELEMENT_PREFIX + "PersonalInfo");
    static private final Element uiGeneral = new SimpleElement(StandardElements.UI, ELEMENT_PREFIX + "UIGeneral");
    static private final Element hotKeys = new SimpleElement(StandardElements.UI, ELEMENT_PREFIX + "HotKeys");
    static private final Element fileTypes = new SimpleElement(StandardElements.UI, ELEMENT_PREFIX + "FileTypes");
    static private final Element mainMenu = new SimpleElement(StandardElements.UI, ELEMENT_PREFIX + "MainMenu");
    static private final Element sysInfo = new SimpleElement(StandardElements.ROOT, ELEMENT_PREFIX + "SysInfo");
    static private final Element speechParams = new SimpleElement(StandardElements.SPEECH, ELEMENT_PREFIX + "SpeechParams");
    //    static private final Element speechChannels = new SimpleElement(StandardElements.SPEECH, ELEMENT_PREFIX + "SpeechChannels");
    static private final Element speechCurrent = new SimpleElement(StandardElements.SPEECH, ELEMENT_PREFIX + "SpeechCurrent");

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
	    StandardElements.ROOT,
	    personalInfo,
	    StandardElements.UI,
	    uiGeneral, 
	    hotKeys,
	    mainMenu,
	    soundSchemes,
	    soundsList,
	    fileTypes,
	    StandardElements.APPLICATIONS,
	    StandardElements.INPUT_OUTPUT,
	    StandardElements.SPEECH,
	    StandardElements.BRAILLE,
	    StandardElements.SOUND,
	    StandardElements.KEYBOARD,
	    StandardElements.NETWORK,
	    sysInfo,
	    StandardElements.HARDWARE,
	    StandardElements.EXTENSIONS,
	    StandardElements.WORKERS,
	    speechParams,
	    speechCurrent,
	    //	    speechChannels,
	};
    }

    @Override public Element[] getOnDemandElements(Element parent)
    {
	return new Element[0];
    }

    @Override public org.luwrain.cpanel.Section createSection(Element el)
    {
	NullCheck.notNull(el, "el");
	if (el.equals(StandardElements.ROOT))
	    return new SimpleSection(StandardElements.ROOT, "Панель управления");
	if (el.equals(StandardElements.APPLICATIONS))
	    return new SimpleSection(StandardElements.APPLICATIONS, "Приложения");
		if (el.equals(StandardElements.INPUT_OUTPUT))
		    return new SimpleSection(StandardElements.INPUT_OUTPUT, "Ввод/вывод");
		if (el.equals(StandardElements.KEYBOARD))
		    return new SimpleSection(StandardElements.KEYBOARD, "Клавиатура");
		if (el.equals(StandardElements.SOUND))
		    return new SimpleSection(StandardElements.SOUND, "Звук");
		if (el.equals(StandardElements.BRAILLE))
		    return new SimpleSection(StandardElements.BRAILLE, "Браиль", (controlPanel)->Braille.create(controlPanel));
		if (el.equals(StandardElements.SPEECH))
		    return new SimpleSection(StandardElements.SPEECH, "Речь");
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
		    return new SimpleSection(uiGeneral, luwrain.i18n().getStaticStr("CpUserInterfaceGeneral"), (controlPanel)->UserInterface.create(controlPanel));
		if (el.equals(hotKeys))
		    return new SimpleSection(hotKeys, "Горячие клавиши", (controlPanel)->HotKeys.create(controlPanel));
		if (el.equals(personalInfo))
		    return new SimpleSection(personalInfo, luwrain.i18n().getStaticStr("CpPersonalInfoSection"), (controlPanel)->{return new PersonalInfo(controlPanel);});
		if (el.equals(fileTypes))
		    return new SimpleSection(fileTypes, "Типы файлов", (controlPanel)->FileTypes.create(controlPanel));
		if (el.equals(mainMenu))
		    return new SimpleSection(mainMenu, "Главное меню");
		if (el.equals(sysInfo))
		    return new SimpleSection(sysInfo, "Информация о системе", (controlPanel)->SysInfo.create(controlPanel));
		if (el.equals(speechParams))
		    return new SimpleSection(speechParams, "Основные параметры", (controlPanel)->SpeechParams.create(controlPanel));
		if (el.equals(speechCurrent))
		    return new SimpleSection(speechCurrent, "Загруженные каналы", (controlPanel)->SpeechCurrent.create(controlPanel));
		/*
		if (el.equals(speechChannels))
		    return new SimpleSection(speechChannels, "Настройка каналов");
		*/
		if (el.equals(soundsList))
		    return new SimpleSection(soundsList, "Звуки системных событий", (controlPanel)->SoundsList.create(controlPanel));
		if (el.equals(soundSchemes))
		    return new SimpleSection(soundSchemes, "Звуковые схемы", (controlPanel)->SoundSchemes.create(controlPanel));
		return null;
    }
}
