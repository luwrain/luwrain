/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.controls.block;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.browser.*;

final class ViewBuilder
{
    private final Block[] blocks;

    ViewBuilder(Block[] blocks)
    {
	NullCheck.notNullItems(blocks, "blocks");
	this.blocks = blocks;
    }

    View build(BlockArea.Appearance appearance, int width)
    {
	NullCheck.notNull(appearance, "appearance");
		if (width < 10)
	    throw new IllegalArgumentException("width (" + width + ") may not be less than 10");
		//calcTextXAndWidth(width);
		//calcTextY();
	for(Block c: blocks)
	    c.textY *= 2;
	for(int i = 0;i < blocks.length;++i)
	    for(int j = 0;j < blocks.length;++j)
		if (i != j)
		    if (blocks[i].intersectsText(blocks[j]))
		{
		    //		    Log.warning(LOG_COMPONENT, "intersecting containers with numbers " + i + " and " + j);
		    //		    Log.warning(LOG_COMPONENT, "container #" + i + ":" + blocks[i].toString());
		    //		    		    Log.warning(LOG_COMPONENT, "container #" + j + ":" + blocks[j].toString());
		}
	final List<Block> viewBlocks = new LinkedList();
	for(Block c: blocks)
	{
	    final BlockRowsBuilder b = new BlockRowsBuilder(c.textWidth);
	    /*
	    for(BlockObject i: c.getObjs())
		processObject(b, i);
	    */
	    b.commitRow();
	    //FIXME:c.setRows(b.rows.toArray(new ContainerRow[b.rows.size()]));
	    if (c.getRowCount() > 0)
		viewBlocks.add(c);

	}
	final Block[] res = viewBlocks.toArray(new Block[viewBlocks.size()]);
			for(int i = 0;i < res.length;++i)
	    for(int j = 0;j < res.length;++j)
		if (i != j)
	    {
		final Block ci = res[i];
		final Block cj = res[j];
		if (cj.textY <= ci.textY)
		    continue;
		if (Block.intersects(ci.textX, ci.textWidth, cj.textX, cj.textWidth))
		    cj.vertDepOn.add(ci);
	    }
	for(Block c: res)
	    c.actualTextY = false;
		for(Block c: res)
		    c.calcActualTextY();
		return new View(appearance, res);
    }


    /*
    private void calcTextXAndWidth(int width)
    {
	int graphicalWidth = 0;
	for(Block c: blocks)
	    graphicalWidth = Math.max(graphicalWidth, c.x + c.width);
	Log.debug(LOG_COMPONENT, "graphical width is " + graphicalWidth);
	final float ratio = (float)graphicalWidth / width;
	Log.debug(LOG_COMPONENT, "ratio is " + String.format("%.2f", ratio));
	for(Container c: containers)
	{
	    final float textX = (float)c.x / ratio;
	    c.textX = new Float(textX).intValue();
	    final float textWidth = (float)c.width / ratio;
	    c.textWidth = new Float(textWidth).intValue();
	}
    }
    */

    /*
    private void calcTextY()
    {
	int topLevel = 0;
	int nextTextY = 0;
	while(true)
	{
	    int baseContIndex = -1;
	    for(int i = 0;i < blocks.length;++i)
	    {
		final Block c = blocks[i];
		//Checking if the container already has the text Y
		if (c.textY >= 0)
		    continue;
		if (c.y < topLevel)
		    continue;
		if (baseContIndex < 0)
		    baseContIndex = i;
		if (c.y < containers[baseContIndex].y)
		{
		    baseContIndex = i;
		    continue;
		}
	    }
	    //Checking if all containers were processed
	    if (baseContIndex < 0)
		return;
	    containers[baseContIndex].textY = nextTextY;
	    final Container chosenContainer = containers[baseContIndex];
	    Log.debug("building", "chosen for " + nextTextY + " is " + chosenContainer.toString());
	    nextTextY++;
	    //Checking if there are some more non-overlapping containers located vertically closely
	    final List<Container> closeContList = new LinkedList();
	    for(int k = 0;k < containers.length;++k)
	    {
		if (k == baseContIndex)
		    continue;
		final Container c2 = containers[k];
		if (c2.textY >= 0)//already has textY
		    continue;
		if (chosenContainer.intersectsGraphically(c2))
		    continue;
		final int diff = chosenContainer.y - c2.y;
		if (diff > -16 && diff < 16)
		    closeContList.add(c2);
	    }
	    if (closeContList.isEmpty())
		continue;
	    final Container[] closeCont = closeContList.toArray(new Container[closeContList.size()]);
	    Arrays.sort(closeCont, (o1, o2)->{
		    final Container c1 = (Container)o1;
		    		    final Container c2 = (Container)o2;
				    if (c1.y < c2.y)
					return -1;
				    if (c1.y < c2.y)
					return 1;
				    final int sq1 = c1.getGraphicalSquare();
				    				    final int sq2 = c2.getGraphicalSquare();
				    if (sq1 == 0 && sq2 != 0)
					return -1;
				    if (sq1 != 0 && sq2 == 0)
					return 1;
				    return 0;
		});
	    closeCont[0].textY = chosenContainer.textY;
	    for(int k = 1;k < closeCont.length;++k)
	    {
		if (closeCont[k].textY >= 0)
		    throw new RuntimeException("Considering the previously used container");
		int kk = 0;
		for(kk = 0;kk < k;++kk)
		{
		    //Log.debug("building", "" + closeCont[kk].textY);
		    if (closeCont[kk].textY >= 0 && closeCont[k].intersectsGraphically(closeCont[kk]))
			break;
		}
		//Log.debug("building", "k=" + k + ",kk=" + kk);
		if (kk < k)//We have an intersection with one of the previously use container
		    continue;
		closeCont[k].textY = chosenContainer.textY;
	    }
	} //for(containers)
    }
*/
}
