/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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

import org.luwrain.core.*;
import org.luwrain.controls.MultilineEdit2.ModificationResult;

public final class EditUtils2
{
    static public class DefaultMultilineEditAppearance implements MultilineEdit2.Appearance
    {
	protected final ControlContext context;

	public DefaultMultilineEditAppearance(ControlContext context)
	{
	    NullCheck.notNull(context, "context");
	    this.context = context;
	}

	@Override public boolean onBackspaceTextBegin()
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.BEGIN_OF_TEXT));
	    return true;
	}

	@Override public boolean onBackspaceMergeLines(ModificationResult res)
	{
	    NullCheck.notNull(res, "res");
	    if (!res.isPerformed())
		return false;
	    context.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE));
	    return true;
	}

	@Override public boolean onBackspaceDeleteChar(ModificationResult res)
	{
	    NullCheck.notNull(res, "res");
	    if (!res.isPerformed() || res.getCharArg() == '\0')
		return false;
	    context.setEventResponse(DefaultEventResponse.letter(res.getCharArg()));
	    return true;
	}

	@Override public boolean onDeleteChar(ModificationResult res)
	{
	    NullCheck.notNull(res, "res");
	    if (!res.isPerformed() || res.getCharArg() == '\0')
		return false;
	    context.setEventResponse(DefaultEventResponse.letter(res.getCharArg()));
	    return true;
	}

	@Override public boolean onDeleteCharTextEnd()
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_TEXT));
	    return true;
	}

	@Override public boolean onDeleteCharMergeLines(ModificationResult res)
	{
	    NullCheck.notNull(res, "res");
	    if (!res.isPerformed())
		return false;
	    context.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE)); 
	    return true;
	}

	@Override public boolean onTab(ModificationResult res)
	{
	    NullCheck.notNull(res, "res");
	    if (!res.isPerformed())
		return false;
	    context.setEventResponse(DefaultEventResponse.hint(Hint.TAB));
	    return true;
	}

	@Override public boolean onSplitLines(ModificationResult res)
	{
	    /*
	      if (line == null)
	    return false;
	if (line.isEmpty())
	    environment.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE)); else
	    environment.say(line);
    */
	    return true;
	}


	@Override public boolean onChar(ModificationResult res)
	{
	    /*
	if (!done)
	    return false;
	if (Character.isSpace(c))
	{
	    final String newLine = model.getLine(model.getHotPointY());
	    final int pos = Math.min(model.getHotPointX(), newLine.length());
	    final String lastWord = TextUtils.getLastWord(newLine, pos);
	    NullCheck.notNull(lastWord, "lastWord");
		if (!lastWord.isEmpty())
		    environment.say(lastWord); else
		    environment.setEventResponse(DefaultEventResponse.hint(Hint.SPACE));
	} else
		environment.sayLetter(c);
	    return true;
	    */
	    return true;
	}
    }

    /**
     * Implements a listener of all changes in 
     * {@link MultilineEdit.Model}. This class contains the abstract method 
     * {@code onMultilineEditChange} called each time when any changes occurred in
     * the state of the model.  This allows users to implement any necessary
     * actions, which should have effect if and only if something was changed
     * in the model and this class guarantees that {@code
     * onMultilineEditChange} is called strictly after changes in the model.
     *
     * @see MultilineEdit
     */
    static abstract public class CorrectorChangeListener implements MultilineEditCorrector2
    {
	protected final MultilineEditCorrector2 corrector;

	public CorrectorChangeListener(MultilineEditCorrector2 corrector)
	{
	    NullCheck.notNull(corrector, "corrector");
	    this.corrector = corrector;
	}

	/** Called if the model gets some changes. There is a guarantee that this method
	 * is invoked strictly after the changes in the model.
	 */
	abstract public void onMultilineEditChange();

	@Override public int getLineCount()
	{
	    return corrector.getLineCount();
	}

	@Override public String getLine(int index)
	{
	    return corrector.getLine(index);
	}

	@Override public int getHotPointX()
	{
	    return corrector.getHotPointX();
	}

	@Override public int getHotPointY()
	{
	    return corrector.getHotPointY();
	}

	@Override public String getTabSeq()
	{
	    return corrector.getTabSeq();
	}

	@Override public ModificationResult deleteChar(int pos, int lineIndex)
	{
	    final ModificationResult res = corrector.deleteChar(pos, lineIndex);
	    if (res.isPerformed())
		onMultilineEditChange();
	    return res;
	}

	@Override public ModificationResult deleteRegion(int fromX, int fromY, int toX, int toY)
	{
	    final ModificationResult res = corrector.deleteRegion(fromX, fromY, toX, toY);
	    if (res.isPerformed())
		onMultilineEditChange();
	    return res;
	}

	@Override public ModificationResult insertRegion(int x, int y, String[] lines)
	{
	    final ModificationResult res = corrector.insertRegion(x, y, lines);
	    if (res.isPerformed())
		onMultilineEditChange();
	    return res;
	}

	@Override public ModificationResult putChars(int pos, int lineIndex, String str)
	{
	    final ModificationResult res = corrector.putChars(pos, lineIndex, str);
	    if (res.isPerformed())
		onMultilineEditChange();
	    return res;
	}

	@Override public ModificationResult mergeLines(int firstLineIndex)
	{
	    final ModificationResult res = corrector.mergeLines(firstLineIndex);
	    if (res.isPerformed())
		onMultilineEditChange();
	    return  res;
	}

	@Override public ModificationResult splitLine(int pos, int lineIndex)
	{
	    final ModificationResult res = corrector.splitLine(pos, lineIndex);
	    if (res.isPerformed())
		onMultilineEditChange();
	    return res;
	}

        @Override public ModificationResult doEditAction(TextEditAction action)
	{
	    final ModificationResult res = corrector.doEditAction(action);
	    if (res.isPerformed())
		onMultilineEditChange();
	    return res;
	}
    }
}
