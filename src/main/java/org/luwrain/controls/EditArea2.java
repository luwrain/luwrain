
//LWR_API 1.0

package org.luwrain.controls;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class EditArea2 extends NavigationArea
{
    public interface ChangeListener
    {
	void onEditChange();
    }

    public interface MultilineEditFactory
    {
	MultilineEdit newMultilineEdit(MultilineEdit.Model model, MutableLines lines, HotPointControl hotPoint);
    }

    static public final class Params
    {
	public ControlContext context = null;
	public String name = "";
	public MutableLines content = null;
	public ChangeListener changeListener = null;
	public MultilineEditFactory multilineEditFactory = null;
    }

    protected final MutableLines content;
    protected String areaName = "";
    protected final ChangeListener changeListener;
    protected final MultilineEdit edit;

    public EditArea2(Params params)
    {
	super(params.context);
	NullCheck.notNull(params, "params");
	NullCheck.notNull(params.name, "params.name");
	this.areaName = params.name;
	this.content = params.content != null?params.content:new MutableLinesImpl();
	this.changeListener = params.changeListener;
	/*
	final MultilineEdit.Model createBasicModel
	MultilineEdit e = null;
	if (params.multilineEditFactory != null)
	{
	}
	edit = new MultilineEdit(context, createMultilineEditModel(params.correctorFactory), regionPoint);
	*/
	edit = null;
    }

    protected MultilineEdit.Model createBasicModel()
    {
	MultilineEditCorrector corrector = new MultilineEditModelTranslator(content, this);
	return new MultilineEditModelChangeListener(corrector){
	    @Override public void onMultilineEditChange()
	    {
		if (changeListener != null)
		    changeListener.onEditChange();
	    }};
    }

    @Override public int getLineCount()
    {
	final int value = content.getLineCount();
	return value > 0?value:1;
    }

    @Override public String getLine(int index)
    {
	if (index < 0)
	    throw new IllegalArgumentException("index (" + index + ") may not be negative");
	if (index >= content.getLineCount())
	    return "";
	final String line = content.getLine(index);
	return line != null?line:"";
    }

    public void setLine(int index, String line)
    {
	NullCheck.notNull(line, "line");
	if (index < 0)
	    throw new IllegalArgumentException("index (" + index + ") may not be negative");
	content.setLine(index, line);
	context.onAreaNewContent(this);
    }

    @Override public String getAreaName()
    {
	return areaName;
    }

    public void setAreaName(String areaName)
    {
	NullCheck.notNull(areaName, "areaName");
	this.areaName = areaName;
	context.onAreaNewName(this);
    }

    public String[] getLines()
    {
	return content.getLines();
    }

    public void setLines(String[] lines)
    {
	NullCheck.notNullItems(lines, "lines");
	content.setLines(lines);
	context.onAreaNewContent(this);
	setHotPoint(getHotPointX(), getHotPointY());
    }

    public void clear()
    {
	content.clear();
	context.onAreaNewContent(this);
	setHotPoint(0, 0);
    }

    public MutableLines getContent()
    {
	return content;
    }

    @Override public boolean onInputEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (edit.onInputEvent(event))
	    return true;
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (edit.onSystemEvent(event))
	    return true;
	return super.onSystemEvent(event);
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	if (edit.onAreaQuery(query))
	    return true;
	return super.onAreaQuery(query);
    }

    protected String getTabSeq()
    {
	return "\t";
    }
}
