/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.app.preview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

public class FilterPoi implements Filter
{
    private String[] lines;
    private String fileName;

    public void open(String fileName) throws Exception
    {
	File docFile=new File(fileName);
	FileInputStream finStream = new FileInputStream(docFile.getAbsolutePath());
	HWPFDocument doc = new HWPFDocument(finStream);
	WordExtractor wordExtract = new WordExtractor(doc);
lines = wordExtract.getParagraphText();
	finStream.close(); //closing fileinputstream
	this.fileName = fileName;
}

    public String getFileName()
    {
	return fileName;
    }

    public int getLineCount()
    {
	return lines != null?lines.length:0;
    }

    public String getLine(int index)
    {
	if (lines == null ||
index >= lines.length ||
lines[index] == null)
	    return "";
	return lines[index];
    }
}
