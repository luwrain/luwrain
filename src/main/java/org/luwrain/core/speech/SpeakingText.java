/*
   Copyright 2012-2019 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core.speech;

import org.luwrain.core.*;

public class SpeakingText
{
    private final org.luwrain.core.extensions.Manager extensions;
    
    public SpeakingText(org.luwrain.core.extensions.Manager extensions)
    {
	NullCheck.notNull(extensions, "extensions");
	this.extensions = extensions;
    }

        public String  processRegular(String text)
    {
	NullCheck.notNull(text, "text");
	final SpeakingHook hook = new SpeakingHook(text);
	extensions.runHooks("luwrain.speech.text.regular", hook);
	return hook.getText().replaceAll("\\h", " ");
    }

        public String  processEventResponse(String text)
    {
	NullCheck.notNull(text, "text");
	return processRegular(text);
    }
}
