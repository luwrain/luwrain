
package org.luwrain.controls;

import org.luwrain.core.*;

/**
 * Implements a listener of all changes in 
 * {@link MultilineEditModel}. This class contains the abstract method 
 * {@code onMultilineEditChange} called each time when any changes occurred in
 * the state of the model.  This allows users to implement any necessary
 * actions, which should have effect if and only if something was changed
 * in the model and this class guarantees that {@code
 * onMultilineEditChange} is called strictly after changes in the model.
 *
 * @see MultilineEdit
 */
abstract public class MultilineEditModelChangeListener implements MultilineEditModel
{
    private final MultilineEditModel model;

    public MultilineEditModelChangeListener(MultilineEditModel model)
    {
	NullCheck.notNull(model, "model");
	this.model = model;
    }

    /** Called if the model gets some changes. There is a guarantee that this method
     * is invoked strictly after the changes in the model.
     */
    abstract public void onMultilineEditChange();

    @Override public int getLineCount()
    {
	return model.getLineCount();
    }

    @Override public String getLine(int index)
    {
	return model.getLine(index);
    }

    @Override public int getHotPointX()
    {
	return model.getHotPointX();
    }

    @Override public int getHotPointY()
    {
	return model.getHotPointY();
    }

    @Override public String getTabSeq()
    {
	return model.getTabSeq();
    }

    @Override public char deleteChar(int pos, int lineIndex)
    {
final char res = model.deleteChar(pos, lineIndex);
if (res != '\0')
    onMultilineEditChange();
return res;
    }

    @Override public boolean deleteRegion(int fromX, int fromY, int toX, int toY)
    {
	final boolean res = model.deleteRegion(fromX, fromY, toX, toY);
if (res)
    onMultilineEditChange();
return res;
    }

    @Override public boolean insertRegion(int x, int y, String[] lines)
    {
final boolean res = model.insertRegion(x, y, lines);
if (res)
    onMultilineEditChange();
return res;
    }

    @Override public void insertChars(int pos, int lineIndex, String str)
    {
	model.insertChars(pos, lineIndex, str);
	onMultilineEditChange();
    }

    @Override public void mergeLines(int firstLineIndex)
    {
	model.mergeLines(firstLineIndex);
	onMultilineEditChange();
    }

    @Override public String splitLines(int pos, int lineIndex)
    {
final String res = model.splitLines(pos, lineIndex);
onMultilineEditChange();
return res;
    }
}
