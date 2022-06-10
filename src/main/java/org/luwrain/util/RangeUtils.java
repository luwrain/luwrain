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

//LWR_API 1.0

package org.luwrain.util;

import java.util.*;
import static java.lang.Math.min;
import static java.lang.Math.max;

public final class RangeUtils
{
    static public boolean between(int pos, int from, int to)
    {
	return pos >= from && pos < to;
    }

    static public boolean intersects(int start1, int len1, int start2, int len2)
    {
	if (start1 < start2)
	    return between(start2, start1, start1 + len1);
	return between(start1, start2, start2 + len2);
    }

			   static public int[] commonRange(int start1, int len1, int start2, int len2)
							      {
								  if (!intersects(start1, len1, start2, len2))
								      return null;
								  if (start1 < start2)
								      return new int[]{start2, min(start1 + len1, start2 + len2)};
								  								      return new int[]{start1, min(start1 + len1, start2 + len2)};
							      }

    static public int[] commonRangeByBounds(int from1, int  to1, int from2, int to2)
    {
	return commonRange(from1, to1 - from1, from2, to2 - from2);
    }
}
