/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.util;

import java.util.*;

public class MlReader
{
    private MlReaderConfig config;
    private MlReaderListener listener;

    private String text = "";
    private int pos;

    private LinkedList<String> openedTagStack = new LinkedList<String>();

    public MlReader(MlReaderConfig config,
		   MlReaderListener listener,
		   String text)
    {
	this.config = config;
	this.listener = listener;
	this.text = text;

	if (config == null)
	    throw new NullPointerException("config may not be null");
	if (listener == null)
	    throw new NullPointerException("listener may not be null");
	if (text == null)
	    throw new NullPointerException("text may not be null");
    }

    public void read()
    {
	pos = 0;
	while (pos < text.length())
	{
	    //	    System.out.println("+step");

	    final char c = text.charAt(pos);
	    int newPos = checkAtPos(pos, "<![cdata[");
	    if (newPos > pos)
	    {
		pos = newPos;
		onCdata();
		continue;
	    }

	    newPos = checkAtPos(pos, "<!doctype");
	    if (newPos > pos)
	    {
		pos = newPos;
		onDoctype();
		continue;
	    }

	    newPos = checkAtPos(pos, "<!--");
	    if (newPos > pos)
	    {
		pos = newPos;
		onComments();
		continue;
	    }

	    if (text.charAt(pos) == '<')
	    {
		if (onOpenTag())
		    continue;
		if (!openedTagStack.isEmpty() && onClosingTag())
		    continue;
	    }

	    if (c == '&')
	    {
		++pos;
		onEntity();
		continue;
	    }

	    onText();
	} //while();
    }

    public boolean isTagOpened(String tag)
    {
	final String adjusted = tag.toLowerCase().trim();
	for(String s: openedTagStack)
	    if (s.equals(adjusted))
		return true;
	return false;
    }

    private boolean  onOpenTag()
    {
	try {
	StringIterator it = new StringIterator(text, pos);
	it.moveNext();
	it.skipBlank();
	final String tagName = it.getUntilBlankOr(">/").toLowerCase();
	if (!config.mlAdmissibleTag(tagName))
	    return false;
	it.skipBlank();
	    if (it.currentChar() == '/' || it.currentChar() == '>')
	{
	    //No attributes;
	    if (it.currentChar() == '>')
	    {
		if (config.mlTagMustBeClosed(tagName))
	    openedTagStack.add(tagName);
	    listener.onMlTagOpen(tagName, null);
		    pos = it.pos() + 1;
		return true;
	    }
	    if (it.isStringHere("/>"))
	    {
	    listener.onMlTagOpen(tagName, null);
		    pos = it.pos() + 2;
		return true;
	    }
	    return false;
	} //No attributes;
	    TreeMap<String, String> attr = new TreeMap<String, String>();
	    while(it.currentChar() != '>' && it.currentChar() != '/')
	    {
		if (!onOpenTagAttr(it, attr))
		    return false;
		it.skipBlank();
	    } //No attributes;
	    if (it.currentChar() == '>')
	    {
		if (config.mlTagMustBeClosed(tagName))
	    openedTagStack.add(tagName);
	    listener.onMlTagOpen(tagName, attr);
		    pos = it.pos() + 1;
		return true;
	    }
	    if (it.isStringHere("/>"))
	    {
	    listener.onMlTagOpen(tagName, attr);
		    pos = it.pos() + 2;
		return true;
	    }
	    return false;
	}
	catch(StringIterator.OutOfBoundsException e)
	{
	    return false;
	}
    }

	private boolean onOpenTagAttr(StringIterator it, TreeMap<String, String> attr) throws StringIterator.OutOfBoundsException
	{
	    final String attrName = it.getUntilBlankOr("=>/");
	    it.skipBlank();
	    if (it.currentChar() != '=')
	    {
		attr.put(attrName, "");
		return true;
	    }
	    //	    System.out.println("+attrName=" + attrName);
	    it.moveNext();
	    it.skipBlank();
	    String value = "";
	    while (!it.isCurrentBlank() &&
		   it.currentChar() != '/' && it.currentChar() != '>')
		   {
		       //		       System.out.println("+char=" + it.currentChar());
		       if (it.currentChar() == '\'')
		       {
			   it.moveNext();
			   value += it.getUntil("\'");
			   it.moveNext();
			   continue;
		       }

		       		       if (it.currentChar() == '\"')
		       {
			   it.moveNext();
			   value += it.getUntil("\"");
			   //			   System.out.println("+value=" + value);
			   it.moveNext();
			   continue;
		       }
				       value += it.getUntilBlankOr(">/\'\"");
		   }
	    //FIXME:entity processing;
	    attr.put(attrName, value);
	    return true;
	}

    private boolean onClosingTag()
    {
	final String closingTag = constructClosingTag();
	final int newPos = checkAtPos(pos, closingTag);
	if (newPos <= pos)
	    return false;
	listener.onMlTagClose(openedTagStack.pollLast());
	//	System.out.println("+" + closingTag);
	//	System.out.println("+" + pos);
	//	System.out.println("+" + newPos);
	pos = newPos;
	return true;
    }

    private void onCdata()
    {
	if (pos >= text.length())
	    return;
	String value = "";
	while (pos < text.length())
	{
	    if (text.charAt(pos) == ']')
	    {
		final int newPos = checkAtPos(pos, "]]>");
		if (newPos > pos)
		{
		    onCdata();
		    pos = newPos;
		    return;
		}
	    }
	    value += text.charAt(pos++);
	}
	listener.onMlText(value, openedTagStack);
    }

    private void onComments()
    {
	while (pos < text.length())
	{
	    if (text.charAt(pos) == '-')
	    {
		final int newPos = checkAtPos(pos, "-->");
		if (newPos > pos)
		{
		    pos = newPos;
		    return;
		}
	    }
	    ++pos;
	}
    }

    private void onDoctype()
    {
	while (pos < text.length() && text.charAt(pos) != '>')
	    ++pos;
	if (pos + 1 < text.length())
	    ++pos;
    }

    private void onEntity()
    {
	if (pos >= text.length())
	    return;
	String name = "";
	while (pos < text.length() && text.charAt(pos) != ';')
	    name += text.charAt(pos++);
	if (pos < text.length())
	    ++pos;
	//	onEntity(name.trim());
    }

    private void onText()
    {
	final int oldPos = pos;
	++pos;
	String res = "";
	while (pos < text.length())
	{
	    final char current = text.charAt(pos);
	    if (current == '<')
		break;
	    if (current == '&')
		break;
	    ++pos;
	}
	listener.onMlText(text.substring(oldPos, pos), openedTagStack);
    }

    /**
     * Checks if a substring presents at the specified position.
     *
     * @param posFrom The position to start checking from
     * @param substr A substring to check
     * @return The position immediately after the encountered substring
     */
    private int checkAtPos(int posFrom, String substr)
    {
	//	System.out.println("check substr: " + pos + ", " + substr);
	if (substr.isEmpty())
	    throw new NullPointerException("substr may not be empty");
	int posInText = posFrom;
	for(int i = 0;i < substr.length();++i)
	{
	    final char c = substr.charAt(i);
	    //Skipping all spaces if there are any
	    while (posInText < text.length() && StringIterator.blankChar(text.charAt(posInText)))
		++posInText;
	    if (posInText >= text.length())
		return posFrom;
	    if (Character.toLowerCase(text.charAt(posInText)) != Character.toLowerCase(c))
		return posFrom;
	    ++posInText;
	}
	return posInText;
    }

    /*
    private int skipBlank(int pos)
    {
	int i = pos;
	while (i < text.length() && blankChar(text.charAt(i)))
	    ++i;
	return i;
    }
    */

    /*
    private String getCurrentTag()
    {
	if (openedTagStack == null || openedTagStack.isEmpty())
	    return "";
	return openedTagStack.getLast();
    }
    */

    private String constructClosingTag()
    {
	if (openedTagStack.isEmpty())
	    return "";
	return "</" + openedTagStack.getLast() + ">";
    }

    /*
    private boolean outOfBounds()
    {
	return pos >= text.length();
    }
    */


    public static String translateEntity(String entity)
    {
	final String name = entity.trim().toLowerCase();
	if (name.charAt(0) == '#')
	{
	    if (name.length() < 2)
		return "";
	    if (name.charAt(1) != 'x')//Decimal;
	    {
		int value;
		try {
		    value = Integer.parseInt(name.substring(1));
		}
		catch(NumberFormatException ee)
		{
		    return "";
		}
		return "" + (char)value;
	    } 
	    //Hex;
	    final String str = name.substring(2).trim();
	    if (str.isEmpty())
		    return "";
	    //fixme:
	    return "";
	} //By code;
	return "" + (char)getCodeOfEntity(name.toLowerCase().trim());
    }

    public static int getCodeOfEntity(String name)
    {
	//FIXME:
	return 32;
    }

    /*
    protected int currentLine()
    {
	int count = 1;
	for(int i = 0;i < pos;++i)
	    if (text.charAt(i) == '\n')
		++count;
	return count;
    }
    */
}
