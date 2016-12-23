
package org.luwrain.settings;

import java.io.*;
import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

class SoundsList extends ListArea implements SectionArea
{
static private class Item 
{
    Settings.SoundScheme scheme;
    String title;
    String path;

    Item(Settings.SoundScheme scheme, String title,
	 String path, Object saver)
    {
	this.scheme = scheme;
	this.title = title;
	this.path = path;
    }

    @Override public String toString()
    {
	return title + ": " + path;
    }
}

    static private class ClickHandler implements ListClickHandler
    {
	private Luwrain luwrain;

	ClickHandler(Luwrain luwrain)
	{
	    this.luwrain = luwrain;
	}

	@Override public boolean onListClick(ListArea area, int index, Object obj)
	{
	    return false;
	}
    };

    private ControlPanel controlPanel;

    SoundsList(ControlPanel controlPanel, ListArea.Params params)
    {
	super(params);
	NullCheck.notNull(controlPanel, "controlPanel");
	this.controlPanel = controlPanel;
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onKeyboardEvent(event))
	    return true;
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onEnvironmentEvent(event))
	    return true;
	return super.onEnvironmentEvent(event);
    }

    @Override public boolean saveSectionData()
    {
	return true;
    }

    static private Item[] loadItems(Registry registry)
    {
	final Settings.SoundScheme scheme = Settings.createCurrentSoundScheme(registry);
	final LinkedList<Item> items = new LinkedList<Item>();
		items.add(new Item(scheme, "Событие не обработано", scheme.getEventNotProcessed(""), null));
	items.add(new Item(scheme, "Общая ошибка", scheme.getError(""), null));
	items.add(new Item(scheme, "Сообщение о завершённой работе", scheme.getDone(""), null));
	items.add(new Item(scheme, "Сообщение о неприменимости действия", scheme.getBlocked(""), null));
	items.add(new Item(scheme, "Сообщение об успешной операции", scheme.getOk(""), null));
	items.add(new Item(scheme, "Нет запущенных приложений", scheme.getNoApplications(""), null));
	items.add(new Item(scheme, "Загрузка системы", scheme.getStartup(""), null));
	items.add(new Item(scheme, "Завершение работы", scheme.getShutdown(""), null));
	items.add(new Item(scheme, "Открытие главного меню", scheme.getMainMenu(""), null));
	items.add(new Item(scheme, "Пустая строка в главном меню", scheme.getMainMenuEmptyLine(""), null));
	items.add(new Item(scheme, "Элементы выше отсутствуют", scheme.getNoItemsAbove(""), null));
	items.add(new Item(scheme, "Элементы ниже отсутствуют", scheme.getNoItemsBelow(""), null));
	items.add(new Item(scheme, "Строки выше отсутствуют", scheme.getNoLinesAbove(""), null));
	items.add(new Item(scheme, "Строки ниже отсутствуют", scheme.getNoLinesBelow(""), null));
	items.add(new Item(scheme, "Элемент списка", scheme.getListItem(""), null));
	items.add(new Item(scheme, "Переход к новой области", scheme.getIntroRegular(""), null));
	items.add(new Item(scheme, "Переход к всплывающей области", scheme.getIntroPopup(""), null));
	items.add(new Item(scheme, "Переход к другому приложению", scheme.getIntroApp(""), null));
	items.add(new Item(scheme, "Новая папка в обзоре файлов", scheme.getCommanderLocation(""), null));
	items.add(new Item(scheme, "Время", scheme.getGeneralTime(""), null));
	items.add(new Item(scheme, "Сигнал в терминале", scheme.getTermBell(""), null));
	return items.toArray(new Item[items.size()]);
    }

    static SoundsList create(ControlPanel controlPanel)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	final Luwrain luwrain = controlPanel.getCoreInterface();
	final ListArea.Params params = new ListArea.Params();
	params.environment = new DefaultControlEnvironment(luwrain);
	params.appearance = new DefaultListItemAppearance(params.environment);
	params.name = "Звуки системных событий";
	params.model = new ListUtils.FixedModel(loadItems(luwrain.getRegistry()));
	return new SoundsList(controlPanel, params);
    }
}
