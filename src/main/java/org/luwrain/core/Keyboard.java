/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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

import org.luwrain.core.events.*;

class Keyboard
{
    static KeyboardEvent translate(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	KeyboardEvent e = event;
e = translateControl(e);
e = translateAlternative(e);
return e;
    }

    static KeyboardEvent translateAlternative(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (!event.isSpecial() || !event.withControlOnly())
	    return event;
	switch (event.getSpecial())
	{
	case ARROW_UP:
	    return new KeyboardEvent(true, KeyboardEvent.Special.ALTERNATIVE_ARROW_UP, ' ');
	case ARROW_DOWN:
	    return new KeyboardEvent(true, KeyboardEvent.Special.ALTERNATIVE_ARROW_DOWN, ' ');
	case ARROW_LEFT:
	    return new KeyboardEvent(true, KeyboardEvent.Special.ALTERNATIVE_ARROW_LEFT, ' ');
	case ARROW_RIGHT:
	    return new KeyboardEvent(true, KeyboardEvent.Special.ALTERNATIVE_ARROW_RIGHT, ' ');
	case PAGE_DOWN:
	    return new KeyboardEvent(true, KeyboardEvent.Special.ALTERNATIVE_PAGE_DOWN, ' ');
	case PAGE_UP:
	    return new KeyboardEvent(true, KeyboardEvent.Special.ALTERNATIVE_PAGE_UP, ' ');
	case HOME:
	    return new KeyboardEvent(true, KeyboardEvent.Special.ALTERNATIVE_HOME, ' ');
	case END:
	    return new KeyboardEvent(true, KeyboardEvent.Special.ALTERNATIVE_END, ' ');
	case DELETE:
	    return new KeyboardEvent(true, KeyboardEvent.Special.ALTERNATIVE_DELETE, ' ');
	default:
	    return event;
	}
    }

	static private KeyboardEvent translateControl(KeyboardEvent event)
	{
	    if (event.isSpecial())
		return event;
	    return new KeyboardEvent(translateControlChar(event.getChar()), event.withShift(), event.withControl(), event.withAlt());
	}

    static private char translateControlChar(char c)
    {
	switch(c)
	{
	case '':
	    return 'a';
	case '':
	    return 'b';
	case '':
	    return 'c';
	case '':
	    return 'd';
	case '':
	    return 'e';
	case '':
	    return 'f';
	case '':
	    return 'g';
	    //FIXME:h
	    //FIXME:i
	case '':
	    return 'k';
	case '':
	    return 'l';
	    //FIXME:m
	case '':
	    return 'n';
	case '':
	    return 'o';
	case '':
	    return 'p';
	case '':
	    return 'q';
	case '':
	    return 'r';
	case '':
	    return 's';
	case '':
	    return 't';
	case '':
	    return 'u';
	case '':
	    return 'v';
	case '':
	    return 'w';
	case '':
	    return 'x';
	case '':
	    return 'y';
	case '':
	    return 'z';
	default:
	    return c;
	}
    }
    }
