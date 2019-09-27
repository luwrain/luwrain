
package org.luwrain.app.console;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.controls.ConsoleArea.InputHandler;

class Utils
{
    static String firstWord(String text)
    {
	NullCheck.notNull(text, "text");
	final int pos = text.indexOf(" ");
	if (pos < 0)
	    return text.trim();
	return text.substring(0, pos).trim();
    }
    
}
