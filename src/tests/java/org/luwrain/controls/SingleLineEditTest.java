/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.controls;

import org.junit.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class SingleLineEditTest extends Assert
{
    @Test public void basics()
    {
	final TestingSingleLineEditModel model = new TestingSingleLineEditModel();
	final SingleLineEdit edit = new SingleLineEdit(new TestingControlEnvironment(), model);
	model.text = "0123456789";
	model.hotPoint = 5;
	//typing 'a' on '5'
	assertTrue(edit.onKeyboardEvent(new KeyboardEvent('a')));
	assertTrue(model.text.equals("01234a56789"));
	assertTrue(model.hotPoint == 6);
	//backspace
	assertTrue(edit.onKeyboardEvent(new KeyboardEvent(KeyboardEvent.Special.BACKSPACE)));
	assertTrue(model.text.equals("0123456789"));
	assertTrue(model.hotPoint == 5);
	//delete
	assertTrue(edit.onKeyboardEvent(new KeyboardEvent(KeyboardEvent.Special.DELETE)));
	assertTrue(model.text.equals("012346789"));
	assertTrue(model.hotPoint == 5);
	//home
	assertTrue(edit.onKeyboardEvent(new KeyboardEvent(KeyboardEvent.Special.HOME)));
	assertTrue(model.text.equals("012346789"));
	assertTrue(model.hotPoint == 0);
	//Once again backspace, nothing must change
	assertTrue(edit.onKeyboardEvent(new KeyboardEvent(KeyboardEvent.Special.BACKSPACE)));
	assertTrue(model.text.equals("012346789"));
	assertTrue(model.hotPoint == 0);
	//Delete at the end, nothing must change
	model.hotPoint = 9;
	assertTrue(edit.onKeyboardEvent(new KeyboardEvent(KeyboardEvent.Special.DELETE)));
	assertTrue(model.text.equals("012346789"));
	assertTrue(model.hotPoint == 9);
    }

    @Test public void loopTyping()
    {
	final StringBuilder b = new StringBuilder();
	for(int i = 0;i < 100;++i)
	    b.append("" + i);
	final String initialText = new String(b);
	final TestingSingleLineEditModel model = new TestingSingleLineEditModel();
	final SingleLineEdit edit = new SingleLineEdit(new TestingControlEnvironment(), model);
	for(int i = 0;i < initialText.length();++i)
	{
	    model.text = initialText;
	    model.hotPoint = i;
	    assertTrue(edit.onKeyboardEvent(new KeyboardEvent('a')));
	    assertTrue(model.hotPoint == i + 1);
	    final String newText = initialText.substring(0, i) + "a" + initialText.substring(i);
	    assertTrue(model.text.equals(newText));
	}
    }

    @Test public void loopDeleting()
    {
	final StringBuilder b = new StringBuilder();
	for(int i = 0;i < 100;++i)
	    b.append("" + i);
	final String initialText = new String(b);
	final TestingSingleLineEditModel model = new TestingSingleLineEditModel();
	final SingleLineEdit edit = new SingleLineEdit(new TestingControlEnvironment(), model);
	for(int i = 0;i < initialText.length();++i)
	{
	    model.text = initialText;
	    model.hotPoint = i;
	    assertTrue(edit.onKeyboardEvent(new KeyboardEvent(KeyboardEvent.Special.DELETE)));
	    assertTrue(model.hotPoint == i);
	    final String newText;
	    if (i < initialText.length())
		newText = initialText.substring(0, i) + initialText.substring(i + 1); else
		newText = initialText;
	    assertTrue(model.text.equals(newText));
	}
    }

        @Test public void loopBackspace()
    {
	final StringBuilder b = new StringBuilder();
	for(int i = 0;i < 100;++i)
	    b.append("" + i);
	final String initialText = new String(b);
	final TestingSingleLineEditModel model = new TestingSingleLineEditModel();
	final SingleLineEdit edit = new SingleLineEdit(new TestingControlEnvironment(), model);
	for(int i = 0;i < initialText.length();++i)
	{
	    model.text = initialText;
	    model.hotPoint = i;
	    assertTrue(edit.onKeyboardEvent(new KeyboardEvent(KeyboardEvent.Special.BACKSPACE)));
	    if (i == 0)
			    assertTrue(model.hotPoint == 0); else
			    assertTrue(model.hotPoint == i - 1);
	    final String newText;
	    if (i > 0)
		newText = initialText.substring(0, i - 1) + initialText.substring(i); else
		newText = initialText;
	    assertTrue(model.text.equals(newText));
	}
    }




    //FIXME:clipboard tests 
}
