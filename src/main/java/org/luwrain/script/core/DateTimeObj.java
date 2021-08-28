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

package org.luwrain.script.core;

import java.time.*;
import java.time.temporal.*;

final class DateTimeObj extends MapScriptObject
{
    DateTimeObj()
    {
	final LocalDateTime d = LocalDateTime.now();
	this.members.put("year", new Integer(d.get(ChronoField.YEAR)));
	this.members.put("month", new Integer(d.get(ChronoField.MONTH_OF_YEAR)));
	this.members.put("dayOfMonth", new Integer(d.get(ChronoField.DAY_OF_MONTH)));
	this.members.put("dayOfWeek", new Integer(d.get(ChronoField.DAY_OF_WEEK)));
	this.members.put("hour", new Integer(d.get(ChronoField.HOUR_OF_DAY)));
	this.members.put("min", new Integer(d.get(ChronoField.MINUTE_OF_HOUR)));
	this.members.put("sec", new Integer(d.get(ChronoField.SECOND_OF_MINUTE)));
    }
}
