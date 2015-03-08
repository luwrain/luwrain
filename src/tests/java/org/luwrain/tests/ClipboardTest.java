
package org.luwrain.tests; 

import org.junit.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public class ClipboardTest extends Assert
{
    @Test public void navigateMulti()
    {
	TestingControlEnvironment environment = new TestingControlEnvironment();
	NavigateArea area = new TestingNavigateArea(environment);
	area.setHotPoint(5, 1);
	area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY_CUT_POINT));
	area.setHotPoint(3, 3);
	area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY));
	assertTrue(environment.clipboard.length == 3);
	assertTrue(environment.clipboard[0].equals("yuiop"));
	assertTrue(environment.clipboard[1].equals("asdfghjkl"));
	assertTrue(environment.clipboard[2].equals("zxc"));
    }

    @Test public void navigateMultiReversed()
    {
	TestingControlEnvironment environment = new TestingControlEnvironment();
	NavigateArea area = new TestingNavigateArea(environment);
	area.setHotPoint(3, 3);
	area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY_CUT_POINT));
	area.setHotPoint(5, 1);
	area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY));
	assertTrue(environment.clipboard.length == 3);
	assertTrue(environment.clipboard[0].equals("yuiop"));
	assertTrue(environment.clipboard[1].equals("asdfghjkl"));
	assertTrue(environment.clipboard[2].equals("zxc"));
    }

    @Test public void navigateSingle()
    {
	TestingControlEnvironment environment = new TestingControlEnvironment();
	NavigateArea area = new TestingNavigateArea(environment);
	area.setHotPoint(3, 1);
	area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY_CUT_POINT));
	area.setHotPoint(7, 1);
	area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY));
	assertTrue(environment.clipboard.length == 1);
	assertTrue(environment.clipboard[0].equals("rtyu"));
    }

    @Test public void navigateSingleReversed()
    {
	TestingControlEnvironment environment = new TestingControlEnvironment();
	NavigateArea area = new TestingNavigateArea(environment);
	area.setHotPoint(7, 1);
	area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY_CUT_POINT));
	area.setHotPoint(3, 1);
	area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY));
	assertTrue(environment.clipboard.length == 1);
	assertTrue(environment.clipboard[0].equals("rtyu"));
    }

    @Test public void editCutMulti()
    {
	TestingControlEnvironment environment = new TestingControlEnvironment();
	TestingMultilinedEditModel model = new TestingMultilinedEditModel();
	MultilinedEdit edit = new MultilinedEdit(environment, model);
	model.addLine("1234567890");
	model.addLine("qwertyuiop");
	model.addLine("asdfghjkl");
	model.hotPointX = 3;
	model.hotPointY = 0;
	edit.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY_CUT_POINT));
	model.hotPointX = 6;
	model.hotPointY = 2;
	edit.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.CUT));
	assertTrue(model.getLineCount() == 1);
	assertTrue(model.getLine(0).equals("123jkl"));
	assertTrue(environment.clipboard.length == 3);
	assertTrue(environment.clipboard[0].equals("4567890"));
	assertTrue(environment.clipboard[1].equals("qwertyuiop"));
	assertTrue(environment.clipboard[2].equals("asdfgh"));
    }

    @Test public void editCutMultiReversed()
    {
	TestingControlEnvironment environment = new TestingControlEnvironment();
	TestingMultilinedEditModel model = new TestingMultilinedEditModel();
	MultilinedEdit edit = new MultilinedEdit(environment, model);
	model.addLine("1234567890");
	model.addLine("qwertyuiop");
	model.addLine("asdfghjkl");
	model.hotPointX = 6;
	model.hotPointY = 2;
	edit.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY_CUT_POINT));
	model.hotPointX = 3;
	model.hotPointY = 0;
	edit.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.CUT));
	assertTrue(model.getLineCount() == 1);
	assertTrue(model.getLine(0).equals("123jkl"));
	assertTrue(environment.clipboard.length == 3);
	assertTrue(environment.clipboard[0].equals("4567890"));
	assertTrue(environment.clipboard[1].equals("qwertyuiop"));
	assertTrue(environment.clipboard[2].equals("asdfgh"));
    }

    @Test public void editCutSingle()
    {
	TestingControlEnvironment environment = new TestingControlEnvironment();
	TestingMultilinedEditModel model = new TestingMultilinedEditModel();
	MultilinedEdit edit = new MultilinedEdit(environment, model);
	model.addLine("1234567890");
	model.addLine("qwertyuiop");
	model.addLine("asdfghjkl");
	model.hotPointX = 3;
	model.hotPointY = 1;
	edit.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY_CUT_POINT));
	model.hotPointX = 6;
	model.hotPointY = 1;
	edit.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.CUT));
	assertTrue(model.getLineCount() == 3);
	assertTrue(model.getLine(0).equals("1234567890"));
	assertTrue(model.getLine(1).equals("qweuiop"));
	assertTrue(model.getLine(2).equals("asdfghjkl"));
	assertTrue(environment.clipboard.length == 1);
	assertTrue(environment.clipboard[0].equals("rty"));
    }

    @Test public void editCutSingleReversed()
    {
	TestingControlEnvironment environment = new TestingControlEnvironment();
	TestingMultilinedEditModel model = new TestingMultilinedEditModel();
	MultilinedEdit edit = new MultilinedEdit(environment, model);
	model.addLine("1234567890");
	model.addLine("qwertyuiop");
	model.addLine("asdfghjkl");
	model.hotPointX = 6;
	model.hotPointY = 1;
	edit.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY_CUT_POINT));
	model.hotPointX = 3;
	model.hotPointY = 1;
	edit.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.CUT));
	assertTrue(model.getLineCount() == 3);
	assertTrue(model.getLine(0).equals("1234567890"));
	assertTrue(model.getLine(1).equals("qweuiop"));
	assertTrue(model.getLine(2).equals("asdfghjkl"));
	assertTrue(environment.clipboard.length == 1);
	assertTrue(environment.clipboard[0].equals("rty"));
    }

    @Test public void editCopyMulti()
    {
	TestingControlEnvironment environment = new TestingControlEnvironment();
	TestingMultilinedEditModel model = new TestingMultilinedEditModel();
	MultilinedEdit edit = new MultilinedEdit(environment, model);
	model.addLine("1234567890");
	model.addLine("qwertyuiop");
	model.addLine("asdfghjkl");
	model.hotPointX = 3;
	model.hotPointY = 0;
	edit.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY_CUT_POINT));
	model.hotPointX = 6;
	model.hotPointY = 2;
	edit.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY));
	assertTrue(model.getLineCount() == 3);
	assertTrue(model.getLine(0).equals("1234567890"));
	assertTrue(model.getLine(1).equals("qwertyuiop"));
	assertTrue(model.getLine(2).equals("asdfghjkl"));
	assertTrue(environment.clipboard.length == 3);
	assertTrue(environment.clipboard[0].equals("4567890"));
	assertTrue(environment.clipboard[1].equals("qwertyuiop"));
	assertTrue(environment.clipboard[2].equals("asdfgh"));
    }

    @Test public void editCopyMultiReversed()
    {
	TestingControlEnvironment environment = new TestingControlEnvironment();
	TestingMultilinedEditModel model = new TestingMultilinedEditModel();
	MultilinedEdit edit = new MultilinedEdit(environment, model);
	model.addLine("1234567890");
	model.addLine("qwertyuiop");
	model.addLine("asdfghjkl");
	model.hotPointX = 6;
	model.hotPointY = 2;
	edit.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY_CUT_POINT));
	model.hotPointX = 3;
	model.hotPointY = 0;
	edit.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY));
	assertTrue(model.getLineCount() == 3);
	assertTrue(model.getLine(0).equals("1234567890"));
	assertTrue(model.getLine(1).equals("qwertyuiop"));
	assertTrue(model.getLine(2).equals("asdfghjkl"));
	assertTrue(environment.clipboard.length == 3);
	assertTrue(environment.clipboard[0].equals("4567890"));
	assertTrue(environment.clipboard[1].equals("qwertyuiop"));
	assertTrue(environment.clipboard[2].equals("asdfgh"));
    }

    @Test public void editCopySingle()
    {
	TestingControlEnvironment environment = new TestingControlEnvironment();
	TestingMultilinedEditModel model = new TestingMultilinedEditModel();
	MultilinedEdit edit = new MultilinedEdit(environment, model);
	model.addLine("1234567890");
	model.addLine("qwertyuiop");
	model.addLine("asdfghjkl");
	model.hotPointX = 3;
	model.hotPointY = 1;
	edit.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY_CUT_POINT));
	model.hotPointX = 6;
	model.hotPointY = 1;
	edit.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY));
	assertTrue(model.getLineCount() == 3);
	assertTrue(model.getLine(0).equals("1234567890"));
	assertTrue(model.getLine(1).equals("qwertyuiop"));
	assertTrue(model.getLine(2).equals("asdfghjkl"));
	assertTrue(environment.clipboard.length == 1);
	assertTrue(environment.clipboard[0].equals("rty"));
    }

    @Test public void editCopySingleReversed()
    {
	TestingControlEnvironment environment = new TestingControlEnvironment();
	TestingMultilinedEditModel model = new TestingMultilinedEditModel();
	MultilinedEdit edit = new MultilinedEdit(environment, model);
	model.addLine("1234567890");
	model.addLine("qwertyuiop");
	model.addLine("asdfghjkl");
	model.hotPointX = 6;
	model.hotPointY = 1;
	edit.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY_CUT_POINT));
	model.hotPointX = 3;
	model.hotPointY = 1;
	edit.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY));
	assertTrue(model.getLineCount() == 3);
	assertTrue(model.getLine(0).equals("1234567890"));
	assertTrue(model.getLine(1).equals("qwertyuiop"));
		assertTrue(model.getLine(2).equals("asdfghjkl"));
	assertTrue(environment.clipboard.length == 1);
	assertTrue(environment.clipboard[0].equals("rty"));
    }

    @Test public void editInsertMulti()
    {
	TestingControlEnvironment environment = new TestingControlEnvironment();
	TestingMultilinedEditModel model = new TestingMultilinedEditModel();
	MultilinedEdit edit = new MultilinedEdit(environment, model);
	model.addLine("1234567890");
	model.addLine("qwertyuiop");
	model.addLine("asdfghjkl");
	model.hotPointX = 3;
	model.hotPointY = 1;
	edit.onEnvironmentEvent(new InsertEvent(new String[]{"123", "456", "789"}));
	assertTrue(model.getLineCount() == 5);
	assertTrue(model.getLine(0).equals("1234567890"));
	assertTrue(model.getLine(1).equals("qwe123"));
	assertTrue(model.getLine(2).equals("456"));
		assertTrue(model.getLine(3).equals("789rtyuiop"));
		assertTrue(model.getLine(4).equals("asdfghjkl"));
    }

    @Test public void editInsertSingle()
    {
	TestingControlEnvironment environment = new TestingControlEnvironment();
	TestingMultilinedEditModel model = new TestingMultilinedEditModel();
	MultilinedEdit edit = new MultilinedEdit(environment, model);
	model.addLine("1234567890");
	model.addLine("qwertyuiop");
	model.addLine("asdfghjkl");
	model.hotPointX = 3;
	model.hotPointY = 1;
	edit.onEnvironmentEvent(new InsertEvent(new String[]{"123"}));
	assertTrue(model.getLineCount() == 3);
	assertTrue(model.getLine(0).equals("1234567890"));
	assertTrue(model.getLine(1).equals("qwe123rtyuiop"));
		assertTrue(model.getLine(2).equals("asdfghjkl"));
    }
}
