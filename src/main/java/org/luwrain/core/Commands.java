/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.popups.*;
import org.luwrain.base.OperatingSystem;

/**
 * The set of standard commands. The commands provided by this class are
 * an essential part of LUWRAIN core and are always easily accessible to
 * users.
 */
class Commands
{
    static private final int SPEECH_STEP = 5;
    static private final int VOLUME_STEP = 5;

    static private final Set<String> osCmdHistory = new HashSet<String>();

    /** Creates the set of standard commands.
     *
     * @param env The environment object to process commands on
     * @return The vector of created commands
     */
    static Command[] createStandardCommands(Environment env, org.luwrain.shell.Conversations conversations)
    {
	NullCheck.notNull(env, "env");
	NullCheck.notNull(conversations, "conversations");
	return new Command[]{

	    //Main menu
	    new Command() {
		@Override public String getName()
		{
		    return "main-menu";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.mainMenu();
		}
	    },

	    //Quit
	    new Command() {
		@Override public String getName()
		{
		    return "quit";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.quit();
		}
	    },

	    //search
	    new Command() {
		@Override public String getName()
		{
		    return "search";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.activateAreaSearch();
		}
	    },

	    //ok
	    new Command() {
		@Override public String getName()
		{
		    return "ok";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.Code.OK));
		}
	    },

	    //Cancel
	    new Command() {
		@Override public String getName()
		{
		    return "cancel";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.Code.CANCEL));
		}
	    },

	    //Close
	    new Command() {
		@Override public String getName()
		{
		    return "close";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.Code.CLOSE));
		}
	    },

	    //gc
	    new Command() {
		@Override public String getName()
		{
		    return "gc";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    System.gc();
		}
	    },

	    //Save
	    new Command() {
		@Override public String getName()
		{
		    return "save";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.Code.SAVE));
		}
	    },

	    //open
	    new Command() {
		@Override public String getName()
		{
		    return "open";
		}
		@Override public void onCommand(Luwrain luwrainArg)
		{
		    final File res = conversations.openPopup();
		    if (res == null)
			return;
		    final Area area = env.getValidActiveArea(false);
		    if (area == null || !area.onEnvironmentEvent(new OpenEvent(res.getAbsolutePath())))
			env.openFiles(new String[]{res.getAbsolutePath()});
		}
	    },

	    //announce
	    new Command() {
		@Override public String getName()
		{
		    return "announce";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.introduceActiveArea();
		}
	    },

	    //Refresh
	    new Command() {
		@Override public String getName()
		{
		    return "refresh";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.Code.REFRESH));
		}
	    },

	    //announce-line
	    new Command() {
		@Override public String getName()
		{
		    return "announce-line";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    final Area area = env.getValidActiveArea(true);
		    if (area == null)
			return;
		    if (area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.Code.ANNOUNCE_LINE)))
			return;
		    final int hotPointY = area.getHotPointY();
		    if (hotPointY >= area.getLineCount())
		    {
			env.eventNotProcessedMessage();
			return;
		    }
		    final String line = area.getLine(hotPointY);
		    if (line == null)
		    {
			env.eventNotProcessedMessage();
			return;
		    }
		    if (!line.trim().isEmpty())
			env.getSpeech().speak(line, 0, 0); else
			env.getObjForEnvironment().setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
		    env.needForIntroduction = false;
		}
	    },

	    //region-point
	    new Command() {
		@Override public String getName()
		{
		    return "region-point";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    final Area area = env.getValidActiveArea(true);
		    if (area == null)
			return;
		    if (area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.Code.REGION_POINT)))
			env.playSound(Sounds.REGION_POINT); else
			env.eventNotProcessedMessage();
		}
	    },

	    //copy
	    new Command() {
		@Override public String getName()
		{
		    return "copy";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    final Area area = env.getValidActiveArea(true);
		    if (area == null)
			return;
		    if (area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.Code.CLIPBOARD_COPY)))
			env.playSound(Sounds.COPIED); else
			env.eventNotProcessedMessage();
		}
	    },

	    //copy-all
	    new Command() {
		@Override public String getName()
		{
		    return "copy-all";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    final Area area = env.getValidActiveArea(true);
		    if (area == null)
			return;
		    if (area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.Code.CLIPBOARD_COPY_ALL)))
			env.playSound(Sounds.COPIED); else
			env.eventNotProcessedMessage();
		}
	    },

	    //cut
	    new Command() {
		@Override public String getName()
		{
		    return "cut";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    final Area area = env.getValidActiveArea(true);
		    if (area == null)
			return;
		    if (area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.Code.CLIPBOARD_CUT)))
			env.playSound(Sounds.CUT);else
			env.eventNotProcessedMessage();
		}
	    },

	    //delete-region
	    new Command() {
		@Override public String getName()
		{
		    return "delete-region";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    final Area area = env.getValidActiveArea(true);
		    if (area == null)
			return;
		    if (area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.Code.CLEAR_REGION)))
			env.playSound(Sounds.DELETED); else
			env.eventNotProcessedMessage();
		}
	    },

	    //paste
	    new Command() {
		@Override public String getName()
		{
		    return "paste";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    if (luwrain.getClipboard().isEmpty())
		    {
			env.eventNotProcessedMessage();
			return;
		    }
		    final Area area = env.getValidActiveArea(true);
		    if (area == null)
			return;
		    if (area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.Code.CLIPBOARD_PASTE)))
			env.playSound(Sounds.PASTE); else
			env.eventNotProcessedMessage();
		}
	    },

	    //clear
	    new Command() {
		@Override public String getName()
		{
		    return "clear";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    final Area area = env.getValidActiveArea(true);
		    if (area == null)
			return;
		    if (area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.Code.CLEAR)))
			env.playSound(Sounds.DELETED); else
			env.eventNotProcessedMessage();
		}
	    },

	    //Help
	    new Command() {
		@Override public String getName()
		{
		    return "help";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.Code.HELP));
		}
	    },

	    //Switch to next App
	    new Command() {
		@Override public String getName()
		{
		    return "switch-next-app";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.onSwitchNextAppCommand();
		}
	    },

	    //Switch to next area
	    new Command() {
		@Override public String getName()
		{
		    return "switch-next-area";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.onSwitchNextAreaCommand();
		}
	    },

	    //Increase font size
	    new Command() {
		@Override public String getName()
		{
		    return "increase-font-size";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.onIncreaseFontSizeCommand();
		}
	    },

	    //Decrease font size
	    new Command() {
		@Override public String getName()
		{
		    return "decrease-font-size";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.onDecreaseFontSizeCommand();
		}
	    },

	    //control panel
	    new Command() {
		@Override public String getName()
		{
		    return "control-panel";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    final Application app = new org.luwrain.app.cpanel.ControlPanelApp(env.getControlPanelFactories(), env.os.getHardware());
		    env.launchApp(app);
		}
	    },

	    //console
	    new Command() {
		@Override public String getName()
		{
		    return "console";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    final Application app = new org.luwrain.app.console.App();
		    env.launchApp(app);
		}
	    },


	    //registry
	    new Command() {
		@Override public String getName()
		{
		    return "registry";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    Application app = new org.luwrain.app.registry.RegistryApp();
		    env.launchApp(app);
		}
	    },

	    //context-menu
	    new Command() {
		@Override public String getName()
		{
		    return "context-menu";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.onContextMenuCommand();
		}
	    },

	    //copy-object-uniref
	    new Command() {
		@Override public String getName()
		{
		    return "copy-object-uniref";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    final Area area = env.getValidActiveArea(true);
		    if (area == null)
			return;
		    final ObjectUniRefQuery query = new ObjectUniRefQuery();
		    if (!AreaQuery.ask(area, query))
		    {
			env.eventNotProcessedMessage();
			return;
		    }
		    final String uniRef = query.getAnswer();
		    if (uniRef == null || uniRef.trim().isEmpty())
		    {
			env.eventNotProcessedMessage();
			return;
		    }
		    final UniRefInfo uniRefInfo = env.uniRefProcs.getInfo(uniRef);
		    if (uniRefInfo == null)
		    {
			env.eventNotProcessedMessage();
			return;
		    }
		    env.message(uniRefInfo.toString(), Luwrain.MESSAGE_OK);
		    env.getClipboard().set(uniRef);
		}
	    },

	    //speech-pitch-inc
	    new Command() {
		@Override public String getName()
		{
		    return "speech-pitch-inc";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    final Speech speech = env.getSpeech();
		    speech.setPitch(speech.getPitch() + SPEECH_STEP);
		    luwrain.message("Высота речи " + speech.getPitch());
		}
	    },

	    //speech-pitch-dec
	    new Command() {
		@Override public String getName()
		{
		    return "speech-pitch-dec";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    final Speech speech = env.getSpeech();
		    speech.setPitch(speech.getPitch() - SPEECH_STEP);
		    luwrain.message("Высота речи " + speech.getPitch());
		}
	    },

	    //speech-speed-inc
	    new Command() {
		@Override public String getName()
		{
		    return "speech-speed-inc";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    final Speech speech = env.getSpeech();
		    speech.setRate(speech.getRate() - SPEECH_STEP);
		    luwrain.message("Скорость речи " + (100 - speech.getRate()));
		}
	    },

	    //speech-speed-dec
	    new Command() {
		@Override public String getName()
		{
		    return "speech-speed-dec";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    final Speech speech = env.getSpeech();
		    speech.setRate(speech.getRate() + SPEECH_STEP);
		    luwrain.message("Скорость речи " + (100 - speech.getRate()));
		}
	    },

	    //volume-inc
	    new Command() {
		@Override public String getName()
		{
		    return "volume-inc";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    final OperatingSystem os = env.os;
		    os.getHardware().getAudioMixer().setMasterVolume(os.getHardware().getAudioMixer().getMasterVolume() + VOLUME_STEP);
		    luwrain.message("Громкость " + os.getHardware().getAudioMixer().getMasterVolume());
		}
	    },

	    //volume-dec
	    new Command() {
		@Override public String getName()
		{
		    return "volume-dec";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    final OperatingSystem os = env.os;
		    os.getHardware().getAudioMixer().setMasterVolume(os.getHardware().getAudioMixer().getMasterVolume() - VOLUME_STEP);
		    luwrain.message("Громкость " + os.getHardware().getAudioMixer().getMasterVolume());
		}
	    },

	    //listen
	    new Command() {
		@Override public String getName()
		{
		    return "listen";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    //		    env.onReadAreaCommand();
		    env.startAreaListening();
		}
	    },

	    //say-current-word
	    new Command() {
		@Override public String getName()
		{
		    return "say-current-word";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    NullCheck.notNull(luwrain, "luwrain");
		    final String word = luwrain.getActiveAreaText(Luwrain.AreaTextType.WORD, true);
		    if (word != null && !word.trim().isEmpty())
			env.message(word, Luwrain.MESSAGE_REGULAR);
		}
	    },

	    //properties
	    new Command() {
		@Override public String getName()
		{
		    return "properties";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    final Area area = env.getValidActiveArea(true);
		    if (area == null)
			return;
		    if (!area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.Code.PROPERTIES)))
			env.eventNotProcessedMessage();
		}
	    },

	    //hot-info
	    new Command() {
		@Override public String getName()
		{
		    return "hot-info";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    final Date date = new Date();
		    final Calendar cal = GregorianCalendar.getInstance();
		    cal.setTime(date);
		    luwrain.silence();
		    luwrain.say("Время" + 
				luwrain.i18n().getNumberStr(cal.get(Calendar.HOUR_OF_DAY), "hours") + " " + 
				luwrain.i18n().getNumberStr(cal.get(Calendar.MINUTE), "minutes"));
				luwrain.playSound(Sounds.GENERAL_TIME);
		}
	    },

	    //run
	    new Command() {
		@Override public String getName()
		{
		    return "run";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    /*
		    final SimpleEditPopup popup = new SimpleEditPopup(env.getObjForEnvironment(), luwrain.i18n().getStaticStr("RunPopupName"), luwrain.i18n().getStaticStr("RunPopupPrefix"), "", Popups.DEFAULT_POPUP_FLAGS);
		    env.popup(null, popup, Popup.Position.BOTTOM, popup.closing, true, true);
		    if (popup.closing.cancelled() || popup.text().trim().isEmpty())
			return;
		    */
		    final String cmd = Popups.editWithHistory(env.getObjForEnvironment(), luwrain.i18n().getStaticStr("RunPopupName"), luwrain.i18n().getStaticStr("RunPopupPrefix"), "", osCmdHistory);
		    if (cmd == null)
			return;

		    final String dir;
		    final Area area = env.getValidActiveArea(false);
		    if (area != null)
		    {
			final CurrentDirQuery query = new CurrentDirQuery();
			if (AreaQuery.ask(area, query))
			    dir = query.getAnswer(); else
			    dir = "";
		    } else
			dir = "";
		    luwrain.runOsCommand(cmd.trim(), dir, (line)->{}, (exitCode, output)->{
			    luwrain.runInMainThread(()->ObjRegistry.issueResultingMessage(luwrain, exitCode, output));
			});
		}
	    },


	};    
    }
}
