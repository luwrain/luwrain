
package org.luwrain.registry.mem;

import java.io.*;
import java.nio.file.*;
import java.util.regex.*;
import java.util.*;

import org.luwrain.core.NullCheck;

class ValueLineParser
{
    private Pattern pat = Pattern.compile("^\\s*(\"[^\"]*\"(\"[^\"]*\")*)\\s*=\\s*\"(.*)\"\\s*$", Pattern.CASE_INSENSITIVE);

    String key = "";
    String value = "";

    boolean parse(String line)
    {
	NullCheck.notNull(line, "line");
	if (line.trim().isEmpty() || line.trim().charAt(0) == '#')
	{
	    key = "";
	    value = "";
	    return true;
	}
	final Matcher matcher = pat.matcher(line);
	if (!matcher.find())
	    return false;
	key = matcher.group(1);
	value = matcher.group(3);
	key = key.substring(1, key.length() - 1).replaceAll("\"\"", "\"");
	//	value = value.substring(1, key.length() - 1).replaceAll("\"\"", "\"");
	value = value.replaceAll("\"\"", "\"");
	value = value.replaceAll("\\\\n", "\n");
	    return true;
    }
}
