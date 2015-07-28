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

public class MlTagStrip
{
    private String text = "";
    private int pos;
    private String result = "";

    private LinkedList<String> openedTagStack = new LinkedList<String>();

    public MlTagStrip(String text)
    {
	this.text = text;
	if (text == null)
	    throw new NullPointerException("text may not be null");
    }

    public void strip()
    {
	pos = 0;
	while (pos < text.length())
	{

	    final char c = text.charAt(pos);
	    int newPos = checkAtPos(pos, "<![cdata[");
	    if (newPos > pos)
	    {
		pos = newPos;
		handleCdata();
		continue;
	    }

	    newPos = checkAtPos(pos, "<!--");
	    if (newPos > pos)
	    {
		pos = newPos;
		//		System.out.println("reader:pos before comments:" + pos);
		handleComments();
		//		System.out.println("reader:pos after comments:" + pos);
		//		System.out.println("reader:debug:comments:" + text.substring(newPos, pos));
		continue;
	    }

	    if (text.charAt(pos) == '<')
	    {
		if (checkOpeningTag())
		    continue;
		if (checkClosingTag())
		    continue;
	    }

	    if (c == '&')
	    {
		++pos;
		handleEntity();
		continue;
	    }

	    handleText();
	}
    }

    private boolean  checkOpeningTag()
    {
	if (pos >= text.length())
	    return false;
	int p = skipBlank(pos + 1);
	final int nameStart = p;
	while (p < text.length() &&
	       text.charAt(p) != '>' &&
	       text.charAt(p) != '/' &&
	       !blankChar(text.charAt(p)))
	    ++p;
	final String name = text.substring(nameStart, p);
	if (!admissibleTag(name))
	    return false;
	onOpeningTag(name);
	while (p < text.length() && text.charAt(p) != '>')
	    ++p;
	if (p < text.length())
	    ++p;
	if (tagMustBeClosed(name) && text.charAt(p - 2) != '/')
	    openedTagStack.add(name);
	pos = p;
	return true;
    }

    private boolean checkClosingTag()
    {
	if (pos > text.length())
	    return false;
	final String closingTag = constructClosingTag();
	final int newPos = checkAtPos(pos, closingTag);
	if (newPos <= pos)
	    return false;
	onClosingTag(openedTagStack.pollLast());
	pos = newPos;
	return true;
    }

    private void handleCdata()
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
		    onCdata(value);
		    pos = newPos;
		    return;
		}
	    }
	    value += text.charAt(pos++);
	}
	onCdata(value);
    }

    private void handleComments()
    {
	if (pos >= text.length())
	    return;
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

    private void handleEntity()
    {
	if (pos >= text.length())
	    return;
	String name = "";
	while (pos < text.length() && text.charAt(pos) != ';')
	    name += text.charAt(pos++);
	if (pos < text.length())
	    ++pos;
	onEntity(name.trim());
    }

    private void handleText()
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
	onText(text.substring(oldPos, pos));
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
	if (substr.isEmpty())
	    throw new NullPointerException("substr may not be empty");
	int posInText = posFrom;
	for(int i = 0;i < substr.length();++i)
	{
	    final char c = substr.charAt(i);
	    //Skipping all spaces if there are any
	    while (posInText < text.length() && blankChar(text.charAt(posInText)))
		++posInText;
	    if (posInText >= text.length())
		return posFrom;
	    if (Character.toLowerCase(text.charAt(posInText)) != Character.toLowerCase(c))
		return posFrom;
	    ++posInText;
	}
	return posInText;
    }

    private int skipBlank(int pos)
    {
	int i = pos;
	while (i < text.length() && blankChar(text.charAt(i)))
	    ++i;
	return i;
    }

    private boolean blankChar(char c)
    {
	return c == ' ' || c == '\t' || c == '\r' || c == '\n' || Character.isSpace(c);
    }

    protected void onOpeningTag(String name)
    {
    }

    protected void onClosingTag(String name)
    {
	if (name == null || name.isEmpty())
	    return;
	if (name.toLowerCase().equals("p"))//FIXME:case;
	    result += '\n';
    }

    protected void onEntity(String name)
    {
	if (name == null || name.trim().isEmpty())
	    return;
	onText(translateEntity(name));
    }

    protected void onText(String str)
    {
	if (str == null || str.isEmpty())
	    return;
	for(int i = 0;i < str.length();++i)
	{
	    final char c = str.charAt(i);
	    if (blankChar(c))
	    {
		if (!result.isEmpty() && !blankChar(result.charAt(result.length() - 1)))
		    result += ' ';
	    } else
		result += c;
	}
    }

    protected void onCdata(String value)
    {
	if (value == null || value.isEmpty())
	    return;
	onText(value);
    }

    public String result()
    {
	return result != null?result:"";
    }

    public static String run(String input)
    {
	if (input == null)
	    throw new NullPointerException("input may not be null");
	    if (input.trim().isEmpty())
		return "";
	MlTagStrip ml = new MlTagStrip(input);
	//	System.out.println(input + "\"");
	    ml.strip();
	    //	    System.out.println("Result \"" + ml.result() + "\"");
	    return ml.result();
    }

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
	if (name.equals("quot"))
	    return 34;
	if (name.equals("amp"))
	    return 38;
	if (name.equals("apos"))
	    return 39;
	if (name.equals("lt"))
	    return 60;
	if (name.equals("gt"))
	    return 62;
	if (name.equals("nbsp"))
	    if (name.equals("HTML"))
		if (name.equals("iexcl"))
		    return 161;
	if (name.equals("cent"))
	    return 162;
	if (name.equals("pound"))
	    return 163;
	if (name.equals("curren"))
	    return 164;
	if (name.equals("yen"))
	    return 165;
	if (name.equals("brvbar"))
	    return 166;
	if (name.equals("sect"))
	    return 167;
	if (name.equals("uml"))
	    return 168;
	if (name.equals("copy"))
	    return 169;
	if (name.equals("ordf"))
	    return 170;
	if (name.equals("laquo"))
	    return 171;
	if (name.equals("not"))
	    return 172;
	if (name.equals("reg"))
	    return 174;
	if (name.equals("macr"))
	    return 175;
	if (name.equals("deg"))
	    return 176;
	if (name.equals("plusmn"))
	    return 177;
	if (name.equals("sup2"))
	    return 178;
	if (name.equals("sup3"))
	    return 179;
	if (name.equals("acute"))
	    return 180;
	if (name.equals("micro"))
	    return 181;
	if (name.equals("para"))
	    return 182;
	if (name.equals("middot"))
	    return 183;
	if (name.equals("cedil"))
	    return 184;
	if (name.equals("sup1"))
	    return 185;
	if (name.equals("ordm"))
	    return 186;
	if (name.equals("raquo"))
	    return 187;
	if (name.equals("frac14"))
return 188;
	if (name.equals("frac12"))
	    return 189;
	if (name.equals("frac34"))
	    return 190;
	if (name.equals("iquest"))
	    return 191;
	if (name.equals("Agrave"))
	    return 192;
	if (name.equals("Aacute"))
	    return 193;
	if (name.equals("Acirc"))
	    return 194;
	if (name.equals("Atilde"))
	    return 195;
	if (name.equals("Auml"))
	    return 196;
	if (name.equals("Aring"))
	    return 197;
	if (name.equals("AElig"))
	    return 198;
	if (name.equals("Ccedil"))
	    return 199;
	if (name.equals("Egrave"))
	    return 200;
	if (name.equals("Eacute"))
	    return 201;
	if (name.equals("Ecirc"))
	    return 202;
	if (name.equals("Euml"))
	    return 203;
	if (name.equals("Igrave"))
	    return 204;
	if (name.equals("Iacute"))
	    return 205;
	if (name.equals("Icirc"))
	    return 206;
	if (name.equals("Iuml"))
	    return 207;
	if (name.equals("ETH"))
	    return 208;
	if (name.equals("Ntilde"))
	    return 209;
	if (name.equals("Ograve"))
	    return 210;
	if (name.equals("Oacute"))
	    return 211;
	if (name.equals("Ocirc"))
	    return 212;
	if (name.equals("Otilde"))
	    return 213;
	if (name.equals("Ouml"))
	    return 214;
	if (name.equals("times"))
	    return 215;
	if (name.equals("Oslash"))
	    return 216;
	if (name.equals("Ugrave"))
	    return 217;
	if (name.equals("Uacute"))
	    return 218;
	if (name.equals("Ucirc"))
	    return 219;
	if (name.equals("Uuml"))
	    return 220;
	if (name.equals("Yacute"))
	    return 221;
	if (name.equals("THORN"))
	    return 222;
	if (name.equals("szlig"))
	    return 223;
	if (name.equals("agrave"))
	    return 224;
	if (name.equals("aacute"))
	    return 225;
	if (name.equals("acirc"))
	    return 226;
	if (name.equals("atilde"))
	    return 227;
	if (name.equals("auml"))
	    return 228;
	if (name.equals("aring"))
	    return 229;
	if (name.equals("aelig"))
	    return 230;
	if (name.equals("ccedil"))
	    return 231;
	if (name.equals("egrave"))
	    return 232;
	if (name.equals("eacute"))
	    return 233;
	if (name.equals("ecirc"))
	    return 234;
	if (name.equals("euml"))
	    return 235;
	if (name.equals("igrave"))
	    return 236;
	if (name.equals("iacute"))
	    return 237;
	if (name.equals("icirc"))
	    return 238;
	if (name.equals("iuml"))
	    return 239;
	if (name.equals("eth"))
	    return 240;
	if (name.equals("ntilde"))
	    return 241;
	if (name.equals("ograve"))
	    return 242;
	if (name.equals("oacute"))
	    return 243;
	if (name.equals("ocirc"))
	    return 244;
	if (name.equals("otilde"))
	    return 245;
	if (name.equals("ouml"))
	    return 246;
	if (name.equals("divide"))
	    return 247;
	if (name.equals("oslash"))
	    return 248;
	if (name.equals("ugrave"))
	    return 249;
	if (name.equals("uacute"))
	    return 250;
	if (name.equals("ucirc"))
	    return 251;
	if (name.equals("uuml"))
	    return 252;
	if (name.equals("yacute"))
	    return 253;
	if (name.equals("thorn"))
	    return 254;
	if (name.equals("yuml"))
	    return 255;
	if (name.equals("OElig"))
	    return 338;
	if (name.equals("oelig"))
	    return 339;
	if (name.equals("Scaron"))
	    return 352;
	if (name.equals("scaron"))
	    return 353;
	if (name.equals("Yuml"))
	    return 376;
	if (name.equals("fnof"))
	    return 402;
	if (name.equals("circ"))
	    return 710;
	if (name.equals("tilde"))
	    return 732;
	if (name.equals("Alpha"))
	    return 913;
	if (name.equals("Beta"))
	    return 914;
	if (name.equals("Gamma"))
	    return 915;
	if (name.equals("Delta"))
	    return 916;
	if (name.equals("Epsilon"))
	    return 917;
	if (name.equals("Zeta"))
	    return 918;
	if (name.equals("Eta"))
	    return 919;
	if (name.equals("Theta"))
	    return 920;
	if (name.equals("Iota"))
	    return 921;
	if (name.equals("Kappa"))
	    return 922;
	if (name.equals("Lambda"))
	    return 923;
	if (name.equals("Mu"))
	    return 924;
	if (name.equals("Nu"))
	    return 925;
	if (name.equals("Xi"))
	    return 926;
	if (name.equals("Omicron"))
	    return 927;
	if (name.equals("Pi"))
	    return 928;
	if (name.equals("Rho"))
	    return 929;
	if (name.equals("Sigma"))
	    return 931;
	if (name.equals("Tau"))
	    return 932;
	if (name.equals("Upsilon"))
	    return 933;
	if (name.equals("Phi"))
	    return 934;
	if (name.equals("Chi"))
	    return 935;
	if (name.equals("Psi"))
	    return 936;
	if (name.equals("Omega"))
	    return 937;
	if (name.equals("alpha"))
	    return 945;
	if (name.equals("beta"))
	    return 946;
	if (name.equals("gamma"))
	    return 947;
	if (name.equals("delta"))
	    return 948;
	if (name.equals("epsilon"))
	    return 949;
	if (name.equals("zeta"))
	    return 950;
	if (name.equals("eta"))
	    return 951;
	if (name.equals("theta"))
	    return 952;
	if (name.equals("iota"))
	    return 953;
	if (name.equals("kappa"))
	    return 954;
	if (name.equals("lambda"))
	    return 955;
	if (name.equals("mu"))
	    return 956;
	if (name.equals("nu"))
	    return 957;
	if (name.equals("xi"))
	    return 958;
	if (name.equals("omicron"))
	    return 959;
	if (name.equals("pi"))
	    return 960;
	if (name.equals("rho"))
	    return 961;
	if (name.equals("sigmaf"))
	    return 962;
	if (name.equals("sigma"))
	    return 963;
	if (name.equals("tau"))
	    return 964;
	if (name.equals("upsilon"))
	    return 965;
	if (name.equals("phi"))
	    return 966;
	if (name.equals("chi"))
	    return 967;
	if (name.equals("psi"))
	    return 968;
	if (name.equals("omega"))
	    return 969;
	if (name.equals("thetasym"))
	    return 977;
	if (name.equals("upsih"))
	    return 978;
	if (name.equals("piv"))
	    return 982;
	if (name.equals("ensp"))
	    return 8194;
	if (name.equals("emsp"))
	    return 8195;
	if (name.equals("thinsp"))
	    return 8201;
	if (name.equals("mdash"))
	    return 8212;
	if (name.equals("lsquo"))
	    return 8216;
	if (name.equals("rsquo"))
	    return 8217;
	if (name.equals("sbquo"))
	    return 8218;
	if (name.equals("ldquo"))
	    return 8220;
	if (name.equals("rdquo"))
	    return 8221;
	if (name.equals("bdquo"))
	    return 8222;
	if (name.equals("dagger"))
	    return 8224;
	if (name.equals("Dagger"))
	    return 8225;
	if (name.equals("bull"))
	    return 8226;
	if (name.equals("hellip"))
	    return 8230;
	if (name.equals("permil"))
	    return 8240;
	if (name.equals("prime"))
	    return 8242;
	if (name.equals("Prime"))
	    return 8243;
	if (name.equals("lsaquo"))
	    return 8249;
	if (name.equals("rsaquo"))
	    return 8250;
	if (name.equals("oline"))
	    return 8254;
	if (name.equals("frasl"))
	    return 8260;
	if (name.equals("euro"))
	    return 8364;
	if (name.equals("image"))
	    return 8465;
	if (name.equals("weierp"))
	    return 8472;
	if (name.equals("real"))
	    return 8476;
	if (name.equals("trade"))
	    return 8482;
	if (name.equals("alefsym"))
	    return 8501;
	if (name.equals("larr"))
	    return 8592;
	if (name.equals("uarr"))
	    return 8593;
	if (name.equals("rarr"))
	    return 8594;
	if (name.equals("darr"))
	    return 8595;
	if (name.equals("harr"))
	    return 8596;
	if (name.equals("crarr"))
	    return 8629;
	if (name.equals("lArr"))
	    return 8656;
	if (name.equals("uArr"))
	    return 8657;
	if (name.equals("rArr"))
	    return 8658;
	if (name.equals("dArr"))
	    return 8659;
	if (name.equals("hArr"))
	    return 8660;
	if (name.equals("forall"))
	    return 8704;
	if (name.equals("part"))
	    return 8706;
	if (name.equals("exist"))
	    return 8707;
	if (name.equals("empty"))
	    return 8709;
	if (name.equals("nabla"))
	    return 8711;
	if (name.equals("isin"))
	    return 8712;
	if (name.equals("notin"))
	    return 8713;
	if (name.equals("ni"))
	    return 8715;
	if (name.equals("prod"))
	    return 8719;
	if (name.equals("sum"))
	    return 8721;
	if (name.equals("minus"))
	    return 8722;
	if (name.equals("lowast"))
	    return 8727;
	if (name.equals("radic"))
	    return 8730;
	if (name.equals("prop"))
	    return 8733;
	if (name.equals("infin"))
	    return 8734;
	if (name.equals("ang"))
	    return 8736;
	if (name.equals("and"))
	    return 8743;
	if (name.equals("or"))
	    return 8744;
	if (name.equals("cap"))
	    return 8745;
	if (name.equals("cup"))
	    return 8746;
	if (name.equals("int"))
	    return 8747;
	if (name.equals("there4"))
	    return 8756;
	if (name.equals("sim"))
	    return 8764;
	if (name.equals("cong"))
	    return 8773;
	if (name.equals("asymp"))
	    return 8776;
	if (name.equals("ne"))
	    return 8800;
	if (name.equals("equiv"))
	    return 8801;
	if (name.equals("le"))
	    return 8804;
	if (name.equals("ge"))
	    return 8805;
	if (name.equals("sub"))
	    return 8834;
	if (name.equals("sup"))
	    return 8835;
	if (name.equals("nsub"))
	    return 8836;
	if (name.equals("sube"))
	    return 8838;
	if (name.equals("supe"))
	    return 8839;
	if (name.equals("oplus"))
	    return 8853;
	if (name.equals("otimes"))
	    return 8855;
	if (name.equals("perp"))
	    return 8869;
	if (name.equals("sdot"))
	    return 8901;
	if (name.equals("lceil"))
	    return 8968;
	if (name.equals("rceil"))
	    return 8969;
	if (name.equals("lfloor"))
	    return 8970;
	if (name.equals("rfloor"))
	    return 8971;
	if (name.equals("lang"))
	    return 9001;
	if (name.equals("rang"))
	    return 9002;
	if (name.equals("loz"))
	    return 9674;
	if (name.equals("spades"))
	    return 9824;
	if (name.equals("clubs"))
	    return 9827;
	if (name.equals("hearts"))
	    return 9829;
	if (name.equals("diams"))
	    return 9830;
	return 32;
    }

    protected int currentLine()
    {
	int count = 1;
	for(int i = 0;i < pos;++i)
	    if (text.charAt(i) == '\n')
		++count;
	return count;
    }

    protected boolean admissibleTag(String tag)
    {
	return tag != null && !tag.trim().isEmpty();
    }

    protected boolean tagMustBeClosed(String tag)
    {
	return true;
    }

    protected String getCurrentTag()
    {
	if (openedTagStack == null || openedTagStack.isEmpty())
	    return "";
	return openedTagStack.getLast();
    }

    protected boolean isTagOpened(String tag)
    {
	final String adjusted = tag.toLowerCase().trim();
	for(String s: openedTagStack)
	    if (s.equals(adjusted))
		return true;
	return false;
    }




    private String constructClosingTag()
    {
	if (openedTagStack.isEmpty())
	    return "";
	return "</" + openedTagStack.getLast() + ">";
    }
}
