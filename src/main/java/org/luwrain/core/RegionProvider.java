/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import org.luwrain.core.HeldData;

/**
 * Implementation of copy-cut-paste operations in real objects. By
 * implementing this interface objects declare their support of clipboard
 * operations (possibly, not all of them). Operations to copy content
 * must be implemented anyway, while operations to delete or insert
 * may be empty. Operations are cleaned from corresponding events
 * processing and coordinates checking ({@code Region} class takes care
 * of this).
 *
 * @see Region
 */
public interface RegionProvider
{
    /**
     * Returnes a whole object content.
     *
     * @return A whole object content
     */
    HeldData getWholeRegion();

    /**
     * Returns an object content between two points of its text representation.
     *
     * @param fromX A horizontal value of the point to copy from
     * @param fromY A vertical value of the point to copy from
     * @param toX A horizontal value of the point to copy to
     * @param toY A vertical value of the point to copy to
     * @return An object content between two points
     */
    HeldData getRegion(int fromX, int fromY,
		       int toX, int toY);

    boolean deleteWholeRegion();

    /**
     * Changes the object content by removing its part between  two points.
     *
     * @param fromX A horizontal value of the point to delete from
     * @param fromY A vertical value of the point to delete from
     * @param toX A horizontal value of the point to delete to
     * @param toY A vertical value of the point to delete to
     * @return True if the object supports this operation and the content removed, false otherwise
     */
    boolean deleteRegion(int fromX, int fromY,
			 int toX, int toY);

    /**
     * Changes the object content inserting some data at the specified position.
     *
     * @param x The horizontal point to put the data at
     * @param x The vertical point to put the data at
     * @return True if the object supports the operation and the data has been inserted, false otherwise
     */
    boolean insertRegion(int x, int y,
			 HeldData heldData);
}
