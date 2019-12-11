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

//LWR_API 1.0

package org.luwrain.controls;

import org.luwrain.core.*;
import org.luwrain.script.*;
import org.luwrain.controls.MultilineEdit.ModificationResult;

public final class EditUtils
{
    static public class DefaultMultilineEditAppearance implements MultilineEdit.Appearance
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
	    context.setEventResponse(DefaultEventResponse.hint(Hint.LINE_BOUND));
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
	    context.setEventResponse(DefaultEventResponse.hint(Hint.LINE_BOUND)); 
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
	    NullCheck.notNull(res, "res");
	    if (!res.isPerformed())
		return false;
	    final String line = res.getStringArg();
	    if (line == null || line.isEmpty())
		context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE)); else
		if (line.trim().isEmpty())
		    context.setEventResponse(DefaultEventResponse.hint(Hint.SPACES)); else
		    context.setEventResponse(DefaultEventResponse.text(line));
	    return true;
	}
	@Override public boolean onChar(ModificationResult res)
	{
	    NullCheck.notNull(res, "res");
	    if (!res.isPerformed())
		return false;
	    if (Character.isSpace(res.getCharArg()))
	    {
		final String word = res.getStringArg();
		if (word != null && !word.trim().isEmpty())
		    context.setEventResponse(DefaultEventResponse.text(word)); else
		    context.setEventResponse(DefaultEventResponse.letter(res.getCharArg()));
	    } else
		context.setEventResponse(DefaultEventResponse.letter(res.getCharArg()));
	    return true;
	}
    }

    static public class DefaultEditAreaAppearance extends DefaultMultilineEditAppearance implements EditArea.Appearance
    {
	public DefaultEditAreaAppearance(ControlContext context)
	{
	    super(context);
	}
	    @Override public void announceLine(int index, String line)
    {
	NullCheck.notNull(line, "line");
	NavigationArea.defaultLineAnnouncement(context, index, line);
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
    static abstract public class CorrectorChangeListener implements MultilineEditCorrector
    {
	protected final MultilineEditCorrector corrector;

	public CorrectorChangeListener(MultilineEditCorrector corrector)
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

    static public class ActiveCorrector implements MultilineEditCorrector
    {
	protected MultilineEditCorrector activatedCorrector = null;
	protected MultilineEditCorrector defaultCorrector = null;
	public void setActivatedCorrector(MultilineEditCorrector corrector)
	{
	    NullCheck.notNull(corrector, "corrector");
	    this.activatedCorrector = corrector;
	}
	public void deactivateCorrector()
	{
	    this.activatedCorrector = null;
	}
	public void setDefaultCorrector(MultilineEditCorrector corrector)
	{
	    NullCheck.notNull(corrector, "corrector");
	    this.defaultCorrector = corrector;
	}
	public MultilineEditCorrector getDefaultCorrector()
	{
	    return defaultCorrector;
	}
	@Override public int getLineCount()
	{
	    if (activatedCorrector != null)
		return activatedCorrector.getLineCount();
	    return defaultCorrector.getLineCount();
	}
	@Override public String getLine(int index)
	{
	    if (activatedCorrector != null)
		return activatedCorrector.getLine(index);
	    return defaultCorrector.getLine(index);
	}
	@Override public int getHotPointX()
	{
	    if (activatedCorrector != null)
		return activatedCorrector.getHotPointX();
	    return defaultCorrector.getHotPointX();
	}
	@Override public int getHotPointY()
	{
	    if (activatedCorrector != null)
		return activatedCorrector.getHotPointY();
	    return defaultCorrector.getHotPointY();
	}
	@Override public String getTabSeq()
	{
	    if (activatedCorrector != null)
		return activatedCorrector.getTabSeq();
	    return defaultCorrector.getTabSeq();
	}
	@Override public ModificationResult deleteChar(int pos, int lineIndex)
	{
	    if (activatedCorrector != null)
		return activatedCorrector.deleteChar(pos, lineIndex);
	    return defaultCorrector.deleteChar(pos, lineIndex);
	}
	@Override public ModificationResult deleteRegion(int fromX, int fromY, int toX, int toY)
	{
	    if (activatedCorrector != null)
		return activatedCorrector.deleteRegion(fromX, fromY, toX, toY);
	    return defaultCorrector.deleteRegion(fromX, fromY, toX, toY);
	}
	@Override public ModificationResult insertRegion(int x, int y, String[] lines)
	{
	    NullCheck.notNullItems(lines, "lines");
	    if (activatedCorrector != null)
		return activatedCorrector.insertRegion(x, y, lines);
	    return defaultCorrector.insertRegion(x, y, lines);
	}
	@Override public ModificationResult putChars(int pos, int lineIndex, String str)
	{
	    NullCheck.notNull(str, "str");
	    if (activatedCorrector != null)
		return activatedCorrector.putChars(pos, lineIndex, str);
	    return defaultCorrector.putChars(pos, lineIndex, str);
	}
	@Override public ModificationResult mergeLines(int firstLineIndex)
	{
	    if (activatedCorrector != null)
		return activatedCorrector.mergeLines(firstLineIndex);
	    return defaultCorrector.mergeLines(firstLineIndex);
	}
	@Override public ModificationResult splitLine(int pos, int lineIndex)
	{
	    if (activatedCorrector != null)
		return activatedCorrector.splitLine(pos, lineIndex);
	    return defaultCorrector.splitLine(pos, lineIndex);
	}
	@Override public ModificationResult doEditAction(TextEditAction action)
	{
	    if (activatedCorrector != null)
		return activatedCorrector.doEditAction(action); else
		return defaultCorrector.doEditAction(action);
	}
    }
}
