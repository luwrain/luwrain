
package org.luwrain.core;

import org.luwrain.controls.*;
import org.luwrain.popups.*;

class ContextMenu extends ListPopup
{
    static private class Model implements ListArea.Model
    {
	private Action[] actions;

	Model(Action[] actions)
	{
	    this.actions = actions;
	    NullCheck.notNull(actions, "actions");
	}

	@Override public int getItemCount()
	{
	    return actions.length;
	}

	@Override public Object getItem(int index)
	{
	    return actions[index];
	}

	@Override public boolean toggleMark(int index)
    {
	return false;
    }

	@Override public void refresh()
	{
	}
    }

    static private class Appearance implements ListItemAppearance
    {
	private Luwrain luwrain;

	Appearance(Luwrain luwrain)
	{
	    this.luwrain = luwrain;
	    NullCheck.notNull(luwrain, "luwrain");
	}

	@Override public void introduceItem(Object item, int flags)
	{
	    if (item == null || !(item instanceof Action))
		return;
	    final Action act = (Action)item;
	    luwrain.playSound(Sounds.NEW_LIST_ITEM);
	    luwrain.say(act.getActionTitle());
    }

	@Override public String getScreenAppearance(Object item, int flags)
	{
	    if (item == null || !(item instanceof Action))
		return "";
	    final Action act = (Action)item;
return act.getActionTitle();
	}

	@Override public int getObservableLeftBound(Object item)
	{
	    return 0;
	}

	@Override public int getObservableRightBound(Object item)
	{
	    return getScreenAppearance(item, 0).length();
	}
    }

    ContextMenu(Luwrain luwrain, Action[] actions)
    {
	super(luwrain, "Контексное меню",
	      new Model(actions), new Appearance(luwrain), 0);
    }
}
