/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.util;

import java.util.*;
import java.util.function.*;

import org.luwrain.core.*;

public final class TextFragmentUtils
{
    private final Lines lines;
    public TextFragmentUtils(Lines lines)
    {
	NullCheck.notNull(lines, "lines");
	this.lines = lines;
    }

    String getWord(int pos, int lineIndex, Predicate<Character> wordCharCond)
    {
	NullCheck.notNull(wordCharCond, "wordCharCond");
	if (pos < 0)
	    throw new IllegalArgumentException("pos can't be negative");
	if (lineIndex < 0)
	    throw new IllegalArgumentException("lineIndex can't be negative");
	if (lineIndex >= lines.getLineCount())
	    return null;
	final String line = lines.getLine(lineIndex);
if (line == null)
return null;
if (pos >= line.length())
return null;
if (!wordCharCond.test(Character.valueOf(line.charAt(pos))))
return null;
int posFrom = pos, posTo = pos;
while(posFrom > 0 && wordCharCond.test(Character.valueOf(line.charAt(posFrom - 1))))
posFrom--;
while (posTo + 1 < line.length() && wordCharCond.test(Character.valueOf(line.charAt(posTo + 1))))
posTo++;
return line.substring(posFrom, posTo + 1);
    }

public String getWord(int pos, int lineIndex)
{
    return getWord(pos, lineIndex, (ch)->(Character.isLetter(ch) || ch.charValue() == '-'));
}

    String replaceWord(int pos, int lineIndex, String replaceWith, Predicate<Character> wordCharCond)
    {
	NullCheck.notNull(wordCharCond, "wordCharCond");
	NullCheck.notNull(replaceWith, "replaceWith");
	if (pos < 0)
	    throw new IllegalArgumentException("pos can't be negative");
	if (lineIndex < 0)
	    throw new IllegalArgumentException("lineIndex can't be negative");
	if (lineIndex >= lines.getLineCount())
	    return null;
	final String line = lines.getLine(lineIndex);
if (line == null)
return null;
if (pos >= line.length())
return null;
if (!wordCharCond.test(Character.valueOf(line.charAt(pos))))
return null;
int posFrom = pos, posTo = pos;
while(posFrom > 0 && wordCharCond.test(Character.valueOf(line.charAt(posFrom - 1))))
posFrom--;
while (posTo + 1 < line.length() && wordCharCond.test(Character.valueOf(line.charAt(posTo + 1))))
posTo++;
return line.substring(0, posFrom) + replaceWith + line.substring(posTo + 1);
    }

    public String replaceWord(int pos, int lineIndex, String replaceWith)
{
    NullCheck.notNull(replaceWith, "replaceWith");
    return replaceWord(pos, lineIndex, replaceWith, (ch)->(Character.isLetter(ch) || ch.charValue() == '-'));
}

    
}
