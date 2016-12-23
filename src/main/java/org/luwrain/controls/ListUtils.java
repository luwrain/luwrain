
package org.luwrain.controls;

import java.util.*;

import org.luwrain.core.*;

public class ListUtils
{
    static public class DefaultTransition implements ListArea.Transition
    {
	@Override public State transition(Type type, State fromState, int itemCount,
					  boolean hasEmptyLineTop, boolean hasEmptyLineBottom)
	{
	    NullCheck.notNull(type, "type");
	    NullCheck.notNull(fromState, "fromState");
	    if (itemCount == 0)
		throw new IllegalArgumentException("itemCount must be positive and non-zero (itemCount=" + itemCount + ")");
	    Log.debug("list", "type=" + type + ",state=" + fromState.type);
	    Log.debug("list", "index=" + fromState.itemIndex + ",count=" + itemCount);
	    switch(type)
	    {
	    case SINGLE_DOWN:
		if (fromState.type == State.Type.EMPTY_LINE_TOP)
		    return new State(0);
		if (fromState.type == State.Type.EMPTY_LINE_BOTTOM)
		    return new State(State.Type.NO_TRANSITION);
		if (fromState.type != State.Type.ITEM_INDEX)
		    return new State(State.Type.NO_TRANSITION);
		if (fromState.itemIndex + 1 < itemCount)
		    return new State(fromState.itemIndex + 1);
		return new State(hasEmptyLineBottom?State.Type.EMPTY_LINE_BOTTOM:State.Type.NO_TRANSITION);
	    case SINGLE_UP:
		if (fromState.type == State.Type.EMPTY_LINE_BOTTOM)
		    return new State(itemCount - 1);
		if (fromState.type == State.Type.EMPTY_LINE_TOP)
		    return new State(State.Type.NO_TRANSITION);
		if (fromState.type != State.Type.ITEM_INDEX)
		    return new State(State.Type.NO_TRANSITION);
		if (fromState.itemIndex > 0)
		    return new State(fromState.itemIndex - 1);
		return new State(hasEmptyLineTop?State.Type.EMPTY_LINE_TOP:State.Type.NO_TRANSITION);
	    case PAGE_DOWN:
	    case PAGE_UP:
	    case HOME:
		return new State(0);
	    case END:
		if (hasEmptyLineBottom)
		    return new State(State.Type.EMPTY_LINE_BOTTOM);
		if (fromState.type != State.Type.ITEM_INDEX)
		    return new State(State.Type.NO_TRANSITION);
		return new State(fromState.itemIndex - 1);
	    default:
		return new State(State.Type.NO_TRANSITION);
	    }
	}
    }

    static public class FixedModel extends Vector implements ListArea.Model
    {
	public FixedModel()
	{
	}

	public FixedModel(Object[] items)
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
}
