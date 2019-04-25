/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core;

import java.util.*;

public final class  EventResponses
{
    static public class Hint implements EventResponse
    {
	protected final org.luwrain.core.Hint hint;
	protected final String text;
	public Hint(org.luwrain.core.Hint hint, String text)
	{
	    NullCheck.notNull(hint, "hint");
	    this.hint = hint;
	    this.text = text;
	}
	public Hint(org.luwrain.core.Hint hint)
	{
	    this(hint, null);
	}
	@Override public void announce(Luwrain luwrain, Speech speech)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    NullCheck.notNull(speech, "speech");
	    final String hintText;
	    if (this.text == null)
		hintText = getTextForHint(luwrain, hint); else
		hintText = this.text;
	    final Sounds sound = getSoundForHint(hint);
	    if (sound != null)
		luwrain.playSound(sound);
	    if (hintText != null && !hintText.trim().isEmpty())
	    luwrain.speak(hintText, org.luwrain.core.Speech.PITCH_HINT);
	}
    }

    static public class Text implements EventResponse
    {
	protected final Sounds sound;
	protected final String text;
	public Text(Sounds sound, String text)
	{
	    NullCheck.notNull(text, "text");
	    this.sound = sound;
	    this.text = text;
	}
	@Override public void announce(Luwrain luwrain, Speech speech)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    NullCheck.notNull(text, "text");
	    NullCheck.notNull(speech, "speech");
	    if (sound != null)
		luwrain.playSound(sound);
	    if (!text.trim().isEmpty())
		speech.speak(new String[]{text});
	}
    }

    static public class Letter implements EventResponse
    {
	protected final char letter;
	public Letter(char letter)
	{
	    this.letter = letter;
	}
	@Override public void announce(Luwrain luwrain, Speech speech)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    NullCheck.notNull(speech, "speech");
	    speech.speakLetter(letter);
	}
    }

    static public class ListItem implements EventResponse
    {
	protected final Sounds sound;
	protected final String text;
	protected final Suggestions suggestion;
	public ListItem(Sounds sound, String text, Suggestions suggestion)
	{
	    NullCheck.notNull(text, "text");
	    this.sound = sound;
	    this.text = text;
	    this.suggestion = suggestion;
	}
	@Override public void announce(Luwrain luwrain, Speech speech)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    NullCheck.notNull(speech, "speech");
	    if (sound != null)
		luwrain.playSound(sound); else
		luwrain.playSound(Sounds.LIST_ITEM);
	    if (suggestion == null)
	    {
		speech.speak(new String[]{text});
		return;
	    }
	    final String suggestionText = getSuggestionText(suggestion, luwrain.i18n());
	    if (suggestionText != null)
		speech.speak(new String[]{text, suggestionText}); else
		speech.speak(new String[]{text});
	}
    }

    static public class TreeItem implements EventResponse
    {
	public enum Type {LEAF, EXPANDED, COLLAPSED};
	protected final Type type;
	protected final Sounds sound;
	protected final String text;
	protected final int level;
	protected final Suggestions suggestion;
	public TreeItem(Type type, Sounds sound, String text, int level, Suggestions suggestion)
	{
	    NullCheck.notNull(type, "type");
	    NullCheck.notNull(text, "text");
	    this.type = type;
	    this.sound = sound;
	    this.text = text;
	    this.level = level;
	    if (level < 1)
		throw new IllegalArgumentException("level (" + level + ") may not be less than one");
	    this.suggestion = suggestion;
	}
		public TreeItem(Type type, String text, int level, Suggestions suggestion)
	{
	    this(type, null, text, level, suggestion);
	}
			public TreeItem(Type type, String text, int level)
	{
	    this(type, null, text, level, null);
	}
	@Override public void announce(Luwrain luwrain, Speech speech)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    NullCheck.notNull(speech, "speech");
	    if (sound != null)
		luwrain.playSound(sound); else
		switch(type)
		{
		case COLLAPSED:
		case EXPANDED:
		luwrain.playSound(Sounds.DOC_SECTION);
		break;
		case LEAF:
				luwrain.playSound(Sounds.LIST_ITEM);
				break;
		}
	    final List<String> parts = new LinkedList();
	    parts.add(text);
	    switch(type)
	    {
	    case EXPANDED:
		parts.add(luwrain.i18n().getStaticStr("TreeExpanded"));
		break;
	    case COLLAPSED:
		parts.add(luwrain.i18n().getStaticStr("TreeCollapsed"));
		break;
	    }
	    if (level > 1)
		parts.add(luwrain.i18n().getStaticStr("TreeLevel") + String.valueOf(level));
	    if (suggestion != null)
	    {
		final String suggestionText = getSuggestionText(suggestion, luwrain.i18n());
		if (suggestionText != null)
		    parts.add(suggestionText);
	    }
	    speech.speak(parts.toArray(new String[parts.size()]));
	}
    }

    //May return null
    static private String getSuggestionText(Suggestions suggestion, I18n i18n)
    {
	NullCheck.notNull(suggestion, "suggestion");
	NullCheck.notNull(i18n, "i18n");
	switch(suggestion)
	{
	case CLICKABLE_LIST_ITEM:
	    return i18n.getStaticStr("SuggestionClickableListItem");
	case LIST_ITEM:
	    return i18n.getStaticStr("SuggestionListItem");
	case POPUP_LIST_ITEM:
	    return i18n.getStaticStr("SuggestionPopupListItem");
	default:
	    return null;
	}
    }

    static private String getTextForHint(Luwrain luwrain, org.luwrain.core.Hint hint)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(hint, "hint");
	switch(hint)
	{
	case TREE_BRANCH_EXPANDED:
	    return "Раскрыто";//FIXME:
	case TREE_BRANCH_COLLAPSED:
	    return "свёрнуто";
	default:
	    return luwrain.i18n().staticStr(hintToStaticStrMap(hint));
	}
    }

    static LangStatic hintToStaticStrMap(org.luwrain.core.Hint hint)
    {
	switch (hint)
	{
	case SPACE:
	    return LangStatic.SPACE;
	case TAB:
	    return LangStatic.TAB;
	case EMPTY_LINE:
	    return LangStatic.EMPTY_LINE;
	case NO_CONTENT:
	    return LangStatic.NO_CONTENT;
	case BEGIN_OF_LINE:
	    return LangStatic.BEGIN_OF_LINE;
	case END_OF_LINE:
	    return LangStatic.END_OF_LINE;
	case BEGIN_OF_TEXT:
	    return LangStatic.BEGIN_OF_TEXT;
	case END_OF_TEXT:
	    return LangStatic.END_OF_TEXT;
	case NO_LINES_ABOVE:
	    return LangStatic.NO_LINES_ABOVE;
	case NO_LINES_BELOW:
	    return LangStatic.NO_LINES_BELOW;
	case NO_ITEMS_ABOVE:
	    return LangStatic.NO_ITEMS_ABOVE;
	case NO_ITEMS_BELOW:
	    return LangStatic.NO_ITEMS_BELOW;
	case TREE_BEGIN:
	    return LangStatic.BEGIN_OF_TREE;
	case TREE_END:
	    return LangStatic.END_OF_TREE;
	case TABLE_NO_ROWS_ABOVE:
	    return LangStatic.TABLE_NO_ROWS_ABOVE;
	case TABLE_NO_ROWS_BELOW:
	    return LangStatic.TABLE_NO_ROWS_BELOW;
	case TABLE_END_OF_COL:
	    return LangStatic.TABLE_END_OF_COL;
	case TABLE_BEGIN_OF_ROW:
	    return LangStatic.TABLE_BEGIN_OF_ROW;
	case TABLE_END_OF_ROW:
	    return LangStatic.TABLE_END_OF_ROW;
	default:
	    return null;
	}
    }

    static private Sounds getSoundForHint(org.luwrain.core.Hint hint)
    {
	switch (hint)
	{
	case NO_ITEMS_ABOVE:
	case TREE_BEGIN:
	case TABLE_NO_ROWS_ABOVE:
	    return Sounds.NO_ITEMS_ABOVE;
	case NO_ITEMS_BELOW:
	case TREE_END:
	case TABLE_NO_ROWS_BELOW:
	    return Sounds.NO_ITEMS_BELOW;
	case NO_LINES_ABOVE:
	    return Sounds.NO_LINES_ABOVE;
	case BEGIN_OF_LINE:
	case BEGIN_OF_TEXT:
	case END_OF_TEXT:
	case END_OF_LINE:
	    return Sounds.END_OF_LINE;
	case NO_LINES_BELOW:
	    return Sounds.NO_LINES_BELOW;
	case NO_CONTENT:
	    return Sounds.NO_CONTENT;
	case EMPTY_LINE:
	    return Sounds.EMPTY_LINE;
	case TREE_BRANCH_COLLAPSED:
	    return Sounds.COLLAPSED;
	case TREE_BRANCH_EXPANDED:
	    return Sounds.EXPANDED;
	default:
	    return null;
	}
    }
}
