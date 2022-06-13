/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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
	this.members.put("year", Integer.valueOf(d.get(ChronoField.YEAR)));
	this.members.put("month", Integer.valueOf(d.get(ChronoField.MONTH_OF_YEAR)));
	this.members.put("dayOfMonth", Integer.valueOf(d.get(ChronoField.DAY_OF_MONTH)));
	this.members.put("dayOfWeek", Integer.valueOf(d.get(ChronoField.DAY_OF_WEEK)));
	this.members.put("hour", Integer.valueOf(d.get(ChronoField.HOUR_OF_DAY)));
	this.members.put("min", Integer.valueOf(d.get(ChronoField.MINUTE_OF_HOUR)));
	this.members.put("sec", Integer.valueOf(d.get(ChronoField.SECOND_OF_MINUTE)));
    }
}
