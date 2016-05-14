
package org.luwrain.settings;

import java.io.*;
import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

class SoundsListSection extends SimpleListSection
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

    SoundsListSection()
    {
	super("Звуки событий", BasicSections.NONE, (luwrain, params)->{
		params.clickHandler = new ClickHandler(luwrain);
		params.model = new FixedListModel();
		params.appearance = new DefaultListItemAppearance(params.environment);
		fillModel(luwrain, (FixedListModel)params.model);
	    });
}

    static private void fillModel(Luwrain luwrain, FixedListModel model)
    {
	final Settings.SoundScheme scheme = Settings.createCurrentSoundScheme(luwrain.getRegistry());
	final LinkedList<Item> items = new LinkedList<Item>();
		items.add(new Item(scheme, "Событие не обработано", scheme.getEventNotProcessed(""), null));
	items.add(new Item(scheme, "Общая ошибка", scheme.getGeneralError(""), null));
	items.add(new Item(scheme, "Сообщение о завершённой работе", scheme.getMessageDone(""), null));
	items.add(new Item(scheme, "Сообщение о неприменимости действия", scheme.getMessageNotReady(""), null));
	items.add(new Item(scheme, "Сообщение об успешной операции", scheme.getMessageOk(""), null));
	items.add(new Item(scheme, "Нет запущенных приложений", scheme.getNoApplications(""), null));
	items.add(new Item(scheme, "Загрузка системы", scheme.getStartup(""), null));
	items.add(new Item(scheme, "Завершение работы", scheme.getShutdown(""), null));
	items.add(new Item(scheme, "Открытие главного меню", scheme.getMainMenu(""), null));
	items.add(new Item(scheme, "Пустая строка в главном меню", scheme.getMainMenuEmptyLine(""), null));
	items.add(new Item(scheme, "Элементы выше отсутствуют", scheme.getNoItemsAbove(""), null));
	items.add(new Item(scheme, "Элементы ниже отсутствуют", scheme.getNoItemsBelow(""), null));
	items.add(new Item(scheme, "Строки выше отсутствуют", scheme.getNoLinesAbove(""), null));
	items.add(new Item(scheme, "Строки ниже отсутствуют", scheme.getNoLinesBelow(""), null));
	items.add(new Item(scheme, "Элемент списка", scheme.getNewListItem(""), null));
	items.add(new Item(scheme, "Переход к новой области", scheme.getIntroRegular(""), null));
	items.add(new Item(scheme, "Переход к всплывающей области", scheme.getIntroPopup(""), null));
	items.add(new Item(scheme, "Переход к другому приложению", scheme.getIntroApp(""), null));
	items.add(new Item(scheme, "Новая папка в обзоре файлов", scheme.getCommanderNewLocation(""), null));
	items.add(new Item(scheme, "Время", scheme.getGeneralTime(""), null));
	items.add(new Item(scheme, "Сигнал в терминале", scheme.getTermBell(""), null));
	model.setItems(items.toArray(new Item[items.size()]));
    }
}
