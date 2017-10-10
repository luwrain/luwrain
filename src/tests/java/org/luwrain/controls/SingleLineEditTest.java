
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
}
