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

package org.luwrain.player.backends;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import javafx.application.Application;                                       
import javafx.scene.media.Media;                                             
import javafx.scene.media.MediaPlayer;                                       
import javafx.stage.Stage;                                                   

import org.luwrain.core.NullCheck;

public class JavaFx implements org.luwrain.player.BackEnd
{                                                                                                
    //    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private MediaPlayer player = null;

    @Override public boolean play(String uri)
    {
	NullCheck.notNull(uri, "uri");
	final Media media = new Media(uri);
	player = new MediaPlayer(media);
	player.currentTimeProperty().addListener((observable, oldValue, newValue)->{System.out.println(newValue.toString());});
	player.play();
	return true;
    }                                                                                            

    @Override public void stop()
    {
	if (player == null)
	    return;
	player.stop();
    }
}

