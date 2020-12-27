/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.controls;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import java.util.*;

public class WizardArea extends FormArea
{
public interface WizardItem
{
}

    public final class WizardText implements WizardItem
    {
	private final String text;
	public WizardText(String text)
	{
	    NullCheck.notEmpty(text, "text");
	    this.text = text;
	}
	public String getText()
	{
	    return text;
	}
    }

    public class WizardFrame
    {
	private final List<WizardItem> items = new ArrayList();
	protected final void addText(String text)
	{
	    NullCheck.notEmpty(text, "text");
	    items.add(new WizardText(text));
	}
	WizardItem[] getItems()
	{
	    return items.toArray(new WizardItem[items.size()]);
	}
    }

    public WizardArea(ControlContext context)
    {
	super(context);
    }

    void fillForm(WizardFrame frame)
    {
	NullCheck.notNull(frame, "frame");
	clear();
	for(WizardItem i: frame.getItems())
	{
	    if (i instanceof WizardText)
	    {
		final WizardText t = (WizardText)i;
		addStatic(t.getText());
	    }
	}
    }
}
