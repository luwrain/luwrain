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

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.util.*;

//Completely skips EnvironmentEvent.CLEAR
public class MultilineEdit2
{
    static public class ModificationResult
    {
	protected final boolean performed;
	protected final String stringArg;
	protected final char charArg;

	public ModificationResult(boolean performed, String stringArg, char charArg)
	{
	    this.performed = performed;
	    this.stringArg = stringArg;
	    this.charArg = charArg;
	}

	public ModificationResult(boolean performed)
	{
	    this(performed, null, '\0');
	}

	public ModificationResult(boolean performed, String stringArg)
	{
	    this(performed, stringArg, '\0');
	}

	public ModificationResult(boolean performed, char charArg)
	{
	    this(performed, null, charArg);
	}

	public final boolean isPerformed()
	{
	    return performed;
	}

	public final String getStringArg()
	{
	    return stringArg;
	}

	public final char getCharArg()
	{
	    return charArg;
	}
    }

    //FIXME:getLineCount() never returns zero
    /**
     * The model for {@link MultilineEdit}. It is supposed that this
     * interface is a front-end for {@link MutableLines} in conjunction with
     * {@link HotPointControl}, but you may use it freely as it is
     * necessary for a particular purpose. See 
     * {@link MultilineEditModelTranslator} for a default implementation.
     * <p>
     * {@code MultilineEdit} guarantees that each user action results exactly in
     * a single call of some method of this interface.  This allows substitution
     * of any method, which makes changes in the model, by any number of
     * other methods in any order, and this will keep all structures
     * consistent.
     * <p>
     * If some operation is addressed at the position outside of the stored
     * text, the result may be undefined. The implementation of this
     * interface should not issue any speech output.
     *
     * @see MultilineEditModelTranslator
     */
    public interface Model extends Lines
    {
	int getHotPointX();
	int getHotPointY();
	String getTabSeq();
	//Processes only chars within line bounds,  neither end of line not end of text not processed
	ModificationResult deleteChar(int pos, int lineIndex);

	//Expects ending point always after starting
	ModificationResult deleteRegion(int fromX, int fromY, int toX, int toY);

	ModificationResult insertRegion(int x, int y, String[] lines);

	//Adds empty line with pos=0 and line=0 if previously there were no lines at all
	ModificationResult putChars(int pos, int lineIndex, String str);

	ModificationResult mergeLines(int firstLineIndex);

	/**
	 * Splits the specified line at the specified position. This method
	 * removes on the line all the content after the specified position and puts
	 * the deleted fragment on new line which is inserted just after
	 * modified. If the position is given outside of the stored text, the
	 * behaviour of this method is undefined.
	 *
	 * @param pos The 0-based position to split line at
	 * @param lineIndex The 0-based index of the line to split
	 * @return The fragment moved onto newly inserted line
	 */
	ModificationResult splitLine(int pos, int lineIndex);
    }

    public interface Appearance
    {
	boolean onBackspaceDeleteChar(ModificationResult result);
	boolean onBackspaceMergeLines(ModificationResult result);
	boolean onBackspaceTextBegin();
	boolean onChar(ModificationResult result);
	boolean onDeleteChar(ModificationResult result);
	boolean onDeleteCharMergeLines(ModificationResult result);
	boolean onDeleteCharTextEnd();
	boolean onSplitLines(ModificationResult result);
	boolean onTab(ModificationResult result);
    }

    static public final class Params
    {
	public ControlContext context = null;
	public Model model = null;
	public Appearance appearance = null;
	public AbstractRegionPoint regionPoint = null;
    }

    protected final ControlContext context;
    protected final Model model;
    protected final Appearance appearance;
    protected final AbstractRegionPoint regionPoint;
    protected final ClipboardTranslator clipboardTranslator;
    protected final RegionTextQueryTranslator regionTextQueryTranslator;

    public MultilineEdit2(Params params)
    {
	NullCheck.notNull(params, "params");
	NullCheck.notNull(params.model, "params.model");
			  NullCheck.notNull(params.appearance, "params.appearance");
	NullCheck.notNull(params.regionPoint, "params.regionPoint");
	this.context = params.context;
	this.regionPoint = params.regionPoint;
	this.model = params.model;
	this.appearance = params.appearance;
	this.clipboardTranslator = new ClipboardTranslator(new LinesClipboardProvider(model, ()->context.getClipboard()){
		@Override public boolean onClipboardCopy(int fromX, int fromY, int toX, int toY, boolean withDeleting)
		{
		    if (!super.onClipboardCopy(fromX, fromY, toX, toY, false))
			return false;
		    if (!withDeleting)
			return true;
		    final ModificationResult res = model.deleteRegion(fromX, fromY, toX, toY);
		    return res != null?res.isPerformed():false;
		}
		@Override public boolean onDeleteRegion(int fromX, int fromY, int toX, int toY)
		{
		    final ModificationResult res = model.deleteRegion(fromX, fromY, toX, toY);
		    return res != null?res.isPerformed():null;
		}
	    }, regionPoint, EnumSet.noneOf(ClipboardTranslator.Flags.class));
	this.regionTextQueryTranslator = new RegionTextQueryTranslator(new LinesRegionTextQueryProvider(model), regionPoint, EnumSet.noneOf(RegionTextQueryTranslator.Flags.class));
    }

    public Model getMultilineEditModel()
    {
	return model;
    }

    public Appearance getMultilineEditAppearance()
    {
	return appearance;
    }

    public boolean onInputEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (!event.isSpecial())//&&
	    return onChar(event);
	if (event.isModified())
	    return false;
	switch(event.getSpecial())
	{
	case BACKSPACE:
	    return onBackspace(event);
	case DELETE:
	    return onDelete(event);
	case TAB:
	    return onTab(event);
	case ENTER:
	    return onEnter(event);
	default:
	    return false;
	}
    }

    public boolean onSystemEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != EnvironmentEvent.Type.REGULAR)
	    return false;
	switch(event.getCode())
	{
	case CLEAR:
	    return false;
	case CLIPBOARD_PASTE:
	    return onClipboardPaste();
	default:
	    if (clipboardTranslator.onSystemEvent(event, model.getHotPointX(), model.getHotPointY()))
		return true;
	    return regionTextQueryTranslator.onSystemEvent(event, model.getHotPointX(), model.getHotPointY());
	}
    }

    public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	return regionTextQueryTranslator.onAreaQuery(query, model.getHotPointX(), model.getHotPointY());
    }

    protected boolean onBackspace(KeyboardEvent event)
    {
	if (model.getHotPointY() >= model.getLineCount())
	    return false;
	if (model.getHotPointX() <= 0 && model.getHotPointY() <= 0)
	    return appearance.onBackspaceTextBegin();
	if (model.getHotPointX() <= 0)
	{
	    final ModificationResult res = model.mergeLines(model.getHotPointY() - 1);
	    return appearance.onBackspaceMergeLines(res);
	}
	final ModificationResult res = model.deleteChar(model.getHotPointX() - 1, model.getHotPointY());
	return appearance.onBackspaceDeleteChar(res);
    }

    protected boolean onDelete(KeyboardEvent event)
    {
	if (model.getHotPointY() >= model.getLineCount())
	    return false;
	final String line = model.getLine(model.getHotPointY());
	if (line == null)
	    return false;
	if (model.getHotPointX() < line.length())
	{
	    final ModificationResult res = model.deleteChar(model.getHotPointX(), model.getHotPointY());
	    return appearance.onDeleteChar(res);
	}
	if (model.getHotPointY() + 1 >= model.getLineCount())
	    return appearance.onDeleteCharTextEnd();
	final ModificationResult res = model.mergeLines(model.getHotPointY());
	return appearance.onDeleteCharMergeLines(res);
    }

    protected boolean onTab(KeyboardEvent event)
    {
	final String tabSeq = model.getTabSeq();
	if (tabSeq == null)
	    return false;
	final ModificationResult res = model.putChars(model.getHotPointX(), model.getHotPointY(), tabSeq);
	return appearance.onTab(res);
    }

    protected boolean onEnter(KeyboardEvent event)
    {
	final ModificationResult res = model.splitLine(model.getHotPointX(), model.getHotPointY());
	return appearance.onSplitLines(res);
    }

    protected boolean onChar(KeyboardEvent event)
    {
	final char c = event.getChar();
	final String line = model.getLine(model.getHotPointY());
	NullCheck.notNull(line, "line");
	final ModificationResult res = model.putChars(model.getHotPointX(), model.getHotPointY(), "" + c);
	return appearance.onChar(res);
    }

    protected boolean onClipboardPaste()
    {
	if (context.getClipboard().isEmpty())
	    return false;
	final ModificationResult res = model.insertRegion(model.getHotPointX(), model.getHotPointY(), context.getClipboard().getStrings());
	return res != null?res.isPerformed():false;
    }
}
