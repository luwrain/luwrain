/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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
import org.luwrain.i18n.*;

public final class EventResponseSpeech implements EventResponse.Speech
{
    private final Speech speech;
    private final I18n i18n;
    private final SpeakingText speakableText;

    public EventResponseSpeech(Speech speech, I18n i18n, SpeakingText speakableText)
    {
	NullCheck.notNull(speech, "speech");
	NullCheck.notNull(i18n, "i18n");
	NullCheck.notNull(speakableText, "speakableText");
	this.speech = speech;
	this.i18n = i18n;
	this.speakableText = speakableText;
    }

    @Override public void speak(String[] parts)
    {
	NullCheck.notNullItems(parts, "parts");
	if (parts.length == 0)
	    return;
	if (parts.length == 1)
	{
	    speech.speakEventResponse(speakableText.processEventResponse(parts[0]));
	    return;
	}
	final StringBuilder b = new StringBuilder();
	b.append(parts[0]);
	for(int i = 1;i < parts.length;++i)
	    b.append(", ").append(parts[i]);
	speech.speakEventResponse(speakableText.processEventResponse(new String(b)));
    }

    @Override public void speakLetter(char letter)
    {
	final String value = i18n.hasSpecialNameOfChar(letter);
	if (value == null)
	    speech.speakLetter(letter, 0, 0); else
	    speech.speak(value, 0, 0);
    }
}
