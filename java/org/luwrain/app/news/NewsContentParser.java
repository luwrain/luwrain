
package org.luwrain.app.news;

import java.util.*;
import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;

class NewsContentParser extends DefaultHandler 
{
    public ArrayList<String> lines = new ArrayList<String>();
    public String line = "";

	public void startElement(String uri,
				 String localName,
				 String rawName,
				 Attributes amap) throws SAXException 
    {
	if (localName.equals("p"))
	{
	    if (line != null && !line.trim().isEmpty())
		lines.add(line);
	    line = "";
	    lines.add("");
	}
    }

    public void endElement(String uri, String local_name, String raw_name) throws SAXException 
    {
    }

    public void characters(char ch[], int start, int length) 
    {
	if (ch == null)
	    return;
	for(int i = start;i < start + length;++i)
	{
	    if (i >= ch.length)
		continue;
	    if (ch[i] == ' ' && line.length() > 40)
	    {
		lines.add(line.trim());
		line = "";
		continue;
	    }
	    line += ch[i];
	}
    }

    public void flush()
    {
	if (line != null && !line.trim().isEmpty())
	    lines.add(line.trim());
    }

    public static String[] parse(String text)
    {
	NewsContentParser contentParser = new NewsContentParser();
	String preparedText = "<?xml version=\"1.0\"?><tmp>" + text + "</tmp>";
	try {
	    SAXParserFactory spf = SAXParserFactory.newInstance();
	    spf.setNamespaceAware(true);
	    SAXParser saxParser = spf.newSAXParser();
	    XMLReader xmlReader = saxParser.getXMLReader();
	    xmlReader.setContentHandler(contentParser);
	    xmlReader.parse(new InputSource(new StringBufferInputStream(preparedText)));
	    contentParser.flush();
	    return contentParser.lines.toArray(new String[contentParser.lines.size()]);
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	    contentParser.flush();
	    return contentParser.lines.toArray(new String[contentParser.lines.size()]);
	}
	catch (SAXException e)
	{
	    e.printStackTrace();
	    contentParser.flush();
	    return contentParser.lines.toArray(new String[contentParser.lines.size()]);
	}
	catch (ParserConfigurationException e)
	{
	    e.printStackTrace();
	    contentParser.flush();
	    return contentParser.lines.toArray(new String[contentParser.lines.size()]);
	}
    }
}
