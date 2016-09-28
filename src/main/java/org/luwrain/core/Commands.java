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

import java.nio.file.*;
import java.util.*;

import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.popups.*;
import org.luwrain.os.OperatingSystem;

/**
 * The set of standard commands. The commands provided by this class are
 * an essential part of LUWRAIN core and are always easily accessible to
 * users.
 */
class Commands
{
    static private final int SPEECH_STEP = 5;
    static private final int VOLUME_STEP = 5;

    /** Creates the set of standard commands.
     *
     * @param env The environment object to process commands on
     * @return The vector of created commands
     */
    static Command[] createStandardCommands(Environment env)
    {
	NullCheck.notNull(env, "env");
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
		    final Luwrain luwrain = env.getObjForEnvironment();
		    final Path current = Paths.get(luwrain.currentAreaDir());
		    final FilePopup popup = new FilePopup(luwrain, 
							  luwrain.i18n().getStaticStr("OpenPopupName"), luwrain.i18n().getStaticStr("OpenPopupPrefix"), 
							  null, current, current, 
							  env.uiSettings.getFilePopupSkipHidden(false)?EnumSet.of(FilePopup.Flags.SKIP_HIDDEN):EnumSet.noneOf(FilePopup.Flags.class),
							  EnumSet.noneOf(Popup.Flags.class));
		    env.popup(null, popup, Popup.BOTTOM, popup.closing, true, true);
		    if (popup.closing.cancelled())
			return;
		    final Path res = popup.result();
		    final Area area = env.getValidActiveArea(false);
		    if (area == null || !area.onEnvironmentEvent(new OpenEvent(res.toString())))
			env.openFiles(new String[]{res.toString()});
		}
	    },

	    //announcee
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
			env.getObjForEnvironment().hint(Hints.EMPTY_LINE);
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
		    {
			env.message(luwrain.i18n().getStaticStr("RegionPointSet"), Luwrain.MESSAGE_REGULAR); 
			env.playSound(Sounds.REGION_POINT);
		    }else
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
		    final RegionQuery query = new RegionQuery();
		    if (!area.onAreaQuery(query) || !query.hasAnswer())
		    {
			env.eventNotProcessedMessage();
			return;
		    }
		    final RegionContent res = query.getAnswer();
		    if (res == null)
		    {
			env.eventNotProcessedMessage();
			return;
		    }
		    env.setClipboard(res);
		    env.message(luwrain.i18n().getStaticStr("LinesCopied") + " " + res.getLineCount(), Sounds.COPIED);
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
		    final CutQuery query = new CutQuery();
		    if (!area.onAreaQuery(query) || !query.hasAnswer())
		    {
			env.eventNotProcessedMessage();
			return;
		    }
		    final RegionContent res = query.getAnswer();
		    if (res == null)
		    {
			env.eventNotProcessedMessage();
			return;
		    }
		    env.setClipboard(res);
		    env.message(luwrain.i18n().getStaticStr("LinesCut") + " " + res.getLineCount(), Sounds.CUT);
		}
	    },

	    //delete
	    new Command() {
		@Override public String getName()
		{
		    return "delete";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    final Area area = env.getValidActiveArea(true);
		    if (area == null)
			return;
		    if (area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.Code.DELETE)))
		    {
			env.message(luwrain.i18n().getStaticStr("LinesDeleted"), Sounds.DELETED);
		    } else
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
		    if (env.getClipboard().isEmpty())
		    {
			env.message(luwrain.i18n().getStaticStr("NoClipboardContent"), Luwrain.MESSAGE_ERROR);
			return;
		    }
		    final Area area = env.getValidActiveArea(true);
		    if (area == null)
			return;
		    final InsertEvent event = new InsertEvent(env.getClipboard ());
		    if (area.onEnvironmentEvent(event))
		    {
			env.message(luwrain.i18n().getStaticStr("LinesInserted") + " " + env.getClipboard().getLineCount(), Sounds.PASTE);
		} else
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
		    final Application app = new org.luwrain.app.cpanel.ControlPanelApp(env.getControlPanelFactories());
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
		    final Application app = new org.luwrain.app.console.ConsoleApp();
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
		    if (!area.onAreaQuery(query) || !query.hasAnswer())
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
		    final UniRefInfo uniRefInfo = env.getUniRefInfoIface(uniRef);
		    if (uniRefInfo == null)
		    {
			env.eventNotProcessedMessage();
			return;
		    }
		    env.message(uniRefInfo.toString(), Luwrain.MESSAGE_REGULAR);
		    env.setClipboard(new RegionContent(new String[]{uniRef}));
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
		    final OperatingSystem os = env.os();
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
		    final OperatingSystem os = env.os();
		    os.getHardware().getAudioMixer().setMasterVolume(os.getHardware().getAudioMixer().getMasterVolume() - VOLUME_STEP);
		    luwrain.message("Громкость " + os.getHardware().getAudioMixer().getMasterVolume());
		}
	    },

	    //read-area
	    new Command() {
		@Override public String getName()
		{
		    return "read-area";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.onReadAreaCommand();
		}
	    },

	    //shutdown
	    new Command() {
		@Override public String getName()
		{
		    return "shutdown";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.os().shutdown();
		}
	    },

	    //reboot
	    new Command() {
		@Override public String getName()
		{
		    return "reboot";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.os().reboot();
		}
	    },

	    //suspend
	    new Command() {
		@Override public String getName()
		{
		    return "suspend";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.os().suspend(false);
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
		    env.onSayCurrentWordCommand();
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
		    final SimpleEditPopup popup = new SimpleEditPopup(env.getObjForEnvironment(), luwrain.i18n().getStaticStr("RunPopupName"), luwrain.i18n().getStaticStr("RunPopupPrefix"), "", Popups.DEFAULT_POPUP_FLAGS);
		    env.popup(null, popup, Popup.BOTTOM, popup.closing, true, true);
		    if (popup.closing.cancelled() || popup.text().trim().isEmpty())
			return;
		    luwrain.runOsCommand(popup.text().trim(), (line)->{}, (exitCode, output)->{
			    if (output.length >= 1)
			    {
			    			    final StringBuilder b = new StringBuilder();
						    b.append(output[0]);
						    for(int i = 1;i < output.length;++i)
							b.append(" " + output[i]);
						    luwrain.runInMainThread(()->luwrain.message(new String(b), exitCode == 0?Luwrain.MESSAGE_DONE:Luwrain.MESSAGE_ERROR));
			    }
			});
		}
	    },


	};    
    }
}
