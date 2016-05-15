
package org.luwrain.settings;

import org.luwrain.core.NullCheck;
import org.luwrain.cpanel.*;

public class StandardFactory implements Factory
{
static private class Section extends EmptySection
{
    private Element element;
    private String title;

    Section(Element element, String title)
    {
	NullCheck.notNull(element, "element");
	NullCheck.notNull(title, "title");
	this.element = element;
	this.title = title;
    }

    @Override public Element getElement()
    {
	return element;
    }

    @Override public String toString()
    {
	return title;
    }
}

    @Override public Element[] getElements()
    {
	//This method should never be called
	return null;
    }

    @Override public Section createSection(Element el)
    {
	NullCheck.notNull(el, "el");
	if (el.equals(StandardElements.ROOT))
	    return new Section(StandardElements.ROOT, "Панель управления");
	if (el.equals(StandardElements.APPLICATIONS))
	    return new Section(StandardElements.APPLICATIONS, "Приложения");
	if (el.equals(StandardElements.KEYBOARD))
	    return new Section(StandardElements.KEYBOARD, "Клавиатура");
	if (el.equals(StandardElements.SOUNDS))
	    return new Section(StandardElements.SOUNDS, "Звук");
	if (el.equals(StandardElements.SPEECH))
	    return new Section(StandardElements.SPEECH, "Речь");
	if (el.equals(StandardElements.NETWORK))
	    return new Section(StandardElements.NETWORK, "Сеть");
	if (el.equals(StandardElements.HARDWARE))
	    return new Section(StandardElements.HARDWARE, "Оборудование");
	if (el.equals(StandardElements.UI))
	    return new Section(StandardElements.UI, "Интерфейс пользователя");
	if (el.equals(StandardElements.EXTENSIONS))
	    return new Section(StandardElements.EXTENSIONS, "Расширения");
	if (el.equals(StandardElements.WORKERS))
	    return new Section(StandardElements.WORKERS, "Фоновые задачи");
	return null;
    }
}
