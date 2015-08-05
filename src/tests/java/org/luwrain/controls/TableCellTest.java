
package org.luwrain.controls;

import org.junit.*;

public class TableCellTest extends Assert
{
    @Test public void moveNext()
    {
	TableCell cell = new TableCell(0, 0, 3, "123456");
	assertTrue(cell.pos == 0);
	assertTrue(cell.shift == 0);
	assertTrue(cell.moveNext());
	//2;
	assertTrue(cell.pos == 1);
	assertTrue(cell.shift == 0);
	assertTrue(cell.moveNext());
	//3;
	assertTrue(cell.pos == 2);
	assertTrue(cell.shift == 0);
	assertTrue(cell.moveNext());
	//4;
	assertTrue(cell.pos == 2);
	assertTrue(cell.shift == 1);
	assertTrue(cell.moveNext());
	//5;
	assertTrue(cell.pos == 2);
	assertTrue(cell.shift == 2);
	assertTrue(cell.moveNext());
	//6;
	assertTrue(cell.pos == 2);
	assertTrue(cell.shift == 3);
	assertTrue(cell.moveNext());
	//end;
	assertTrue(cell.pos == 3);
	assertTrue(cell.shift == 3);
	assertFalse(cell.moveNext());
    }
}
