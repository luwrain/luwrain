

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
}
