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

package org.luwrain.settings;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

final class Speech extends FormArea implements SectionArea
{
    private final ControlPanel controlPanel;
    private final Luwrain luwrain;
    private final Settings.SpeechParams sett;

    Speech(ControlPanel controlPanel, String name)
    {
	super(new DefaultControlContext(controlPanel.getCoreInterface()), name);
	this.controlPanel = controlPanel;
	this.luwrain = controlPanel.getCoreInterface();
	this.sett = Settings.createSpeechParams(luwrain.getRegistry());
	addEdit("main-engine-name", luwrain.i18n().getStaticStr("CpSpeechMainEngineName"), sett.getMainEngineName(""));
	addEdit("main-engine-params", luwrain.i18n().getStaticStr("CpSpeechMainEngineParams"), sett.getMainEngineParams(""));
	addEdit("listening-engine-name", luwrain.i18n().getStaticStr("CpSpeechListeningEngineName"), sett.getListeningEngineName(""));
	addEdit("listening-engine-params", luwrain.i18n().getStaticStr("CpSpeechListeningEngineParams"), sett.getListeningEngineParams(""));
	addEdit("listening-pitch", luwrain.i18n().getStaticStr("CpSpeechListeningPitch"), "" + sett.getListeningPitch(50));
	addEdit("listening-rate", luwrain.i18n().getStaticStr("CpSpeechListeningRate"), "" + sett.getListeningRate(50));
    }

    @Override public boolean saveSectionData()
    {
	sett.setMainEngineName(getEnteredText("main-engine-name"));
	sett.setMainEngineParams(getEnteredText("main-engine-params"));
	sett.setListeningEngineName(getEnteredText("listening-engine-name"));
	sett.setListeningEngineParams(getEnteredText("listening-engine-params"));
	final int listeningPitch;
	try {
	    listeningPitch = Integer.parseInt(getEnteredText("listening-pitch"));
	}
	catch(NumberFormatException e)
	{
	    luwrain.message(luwrain.i18n().getStaticStr("CpSpeechInvalidListeningPitch"), Luwrain.MessageType.ERROR);
	    return false;
	}
	if (listeningPitch < 0 || listeningPitch > 100)
	{
	    luwrain.message(luwrain.i18n().getStaticStr("CpSpeechInvalidListeningPitch"), Luwrain.MessageType.ERROR);
	    return false;
	}
	sett.setListeningPitch(listeningPitch);
	final int listeningRate;
	try {
	    listeningRate = Integer.parseInt(getEnteredText("listening-rate"));
	}
	catch(NumberFormatException e)
	{
	    luwrain.message(luwrain.i18n().getStaticStr("CpSpeechInvalidListeningRate"), Luwrain.MessageType.ERROR);
	    return false;
	}
	if (listeningRate < 0 || listeningRate > 100)
	{
	    luwrain.message(luwrain.i18n().getStaticStr("CpSpeechInvalidListeningRate"), Luwrain.MessageType.ERROR);
	    return false;
	}
	sett.setListeningRate(listeningRate);
	return true;
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	if (controlPanel.onInputEvent(event))
	    return true;
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	if (controlPanel.onSystemEvent(event))
	    return true;
	return super.onSystemEvent(event);
    }

    static Speech create(ControlPanel controlPanel)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	final Luwrain luwrain = controlPanel.getCoreInterface();
	return new Speech(controlPanel, luwrain.i18n().getStaticStr("CpSpeechGeneral"));
    }
}
