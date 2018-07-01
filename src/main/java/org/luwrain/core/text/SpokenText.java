

package org.luwrain.core.text;

import org.luwrain.core.*;
import org.luwrain.core.Luwrain.SpokenTextType;

public final class SpokenText
{
    private final I18n i18n;

    public SpokenText(I18n i18n)
    {
	NullCheck.notNull(i18n, "i18n");
	this.i18n = i18n;
    }

    public String translate(String text, SpokenTextType type)
    {
	NullCheck.notNull(text, "text");
	final StringBuilder b = new StringBuilder();
	for(int i = 0;i < text.length();++i)
	{
	    final char c = text.charAt(i);
	    if (Character.isSpaceChar(c))
	    {
		b.append(" ");
		continue;
	    }
	    if (Character.isLetter(c) || Character.isDigit(c))
	    {
		b.append(c);
		continue;
	    }
	    final String special = i18n.hasSpecialNameOfChar(c);
	    if (special != null && !special.isEmpty())
		b.append(" " + special + " "); else
		b.append("" + c);
	}
	return new String(b);
    }
    
}
