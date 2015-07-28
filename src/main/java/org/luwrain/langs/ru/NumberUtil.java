/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.langs.ru;

class NumberUtil
{
    public static String chooseNumberDependentForm(int num,
						   String form1,
						   String form2,
String form3)
    {
	int k = num;
	if (k < 0)
	    k *= -1;
	k = k % 100;
	if (k >= 10 && k <= 20)
	    return form3;
	int kk = k % 10;
	if (kk == 1)
	    return form1;
	if (kk >= 2 && kk <= 4)
	    return form2;
	return form3;
    }
}
