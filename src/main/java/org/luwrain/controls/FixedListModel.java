
package org.luwrain.controls;

import java.util.*;

import org.luwrain.core.*;

public class FixedListModel extends Vector implements ListArea.Model
{
    public FixedListModel()
    {
    }

    public FixedListModel(Object[] items)
    {
	NullCheck.notNullItems(items, "items");
	setItems(items);
    }

    public void setItems(Object[] items)
    {
	NullCheck.notNullItems(items, "items");
	setSize(items.length);
	for(int i = 0;i < items.length;++i)
	    set(i, items[i]);
    }

    public Object[] getItems()
    {
	return toArray(new Object[size()]);
    }

    @Override public int getItemCount()
    {
	return size();
    }

    @Override public Object getItem(int index)
    {
	return get(index);
    }

    @Override public void refresh()
    {
    }

    @Override public boolean toggleMark(int index)
    {
	return false;
    }
}
