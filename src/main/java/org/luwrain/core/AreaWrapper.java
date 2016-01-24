/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

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

/**
 * The interface to mark area wrapping classes. This interface is empty,
 * there are no any methods. It is used for easy checking that particular
 * area object is an area wrapper. (not a natural area).  Natural areas
 * are provided by applications, but usually they are wrapped by one or
 * more wrapping areas. One of them is used due to security reasons,
 * other may be used, for instance, for text search in the area.
 *
 * @see SecurityAreaWrapper SearchAreaWrapper
 */
interface AreaWrapper
{
}
