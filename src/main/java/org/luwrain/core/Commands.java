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

package org.luwrain.core;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.popups.*;
import org.luwrain.base.OperatingSystem;
import org.luwrain.script.*;
import org.luwrain.controls.DefaultControlContext;

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
    static Command[] createStandardCommands(Core core, org.luwrain.core.shell.Conversations conversations)
    {
	NullCheck.notNull(core, "core");
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
		    core.mainMenu();
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
		    core.activateAreaSearch();
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
		    core.enqueueEvent(new SystemEvent(SystemEvent.Code.OK));
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
		    core.enqueueEvent(new SystemEvent(SystemEvent.Code.CANCEL));
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
		    core.enqueueEvent(new SystemEvent(SystemEvent.Code.CLOSE));
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
		    core.enqueueEvent(new SystemEvent(SystemEvent.Code.SAVE));
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
		    final File res = conversations.open();
		    if (res != null)
			core.openFiles(new String[]{res.getAbsolutePath()});
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
		    core.announceActiveArea();
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
		    core.enqueueEvent(new SystemEvent(SystemEvent.Code.REFRESH));
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
		    final Area area = core.getValidActiveArea(true);
		    if (area == null)
			return;
		    if (area.onSystemEvent(new SystemEvent(SystemEvent.Code.ANNOUNCE_LINE)))
			return;
		    final int hotPointY = area.getHotPointY();
		    if (hotPointY >= area.getLineCount())
		    {
			core.eventNotProcessedMessage();
			return;
		    }
		    final String line = area.getLine(hotPointY);
		    if (line == null)
		    {
			core.eventNotProcessedMessage();
			return;
		    }
		    if (!line.trim().isEmpty())
			core.speech.speak(line, 0, 0); else
			core.getObjForEnvironment().setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
		    core.needForIntroduction = false;
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
		    final Area area = core.getValidActiveArea(true);
		    if (area == null)
			return;
		    final AtomicReference res = new AtomicReference();
		    final AtomicReference x = new AtomicReference();
		    final AtomicReference y = new AtomicReference();
		    core.unsafeAreaOperation(()->{
			    if (!area.onSystemEvent(new SystemEvent(SystemEvent.Code.REGION_POINT)))
			    {
				res.set(new Boolean(false));
				return;
			    }
			    x.set(new Integer(area.getHotPointX()));
			    y.set(new Integer(area.getHotPointY()));
			    res.set(new Boolean(true));
			});
		    if (res.get() == null || !((Boolean)res.get()).booleanValue())
		    {
			core.eventNotProcessedMessage();
			return;
		    }
		    final EmptyHookObject argObj = new EmptyHookObject(){
			    @Override public Object getMember(String name)
			    {
				NullCheck.notEmpty(name, "name");
				switch(name)
				{
				case "x":
				    return x.get();
				case "y":
				    return y.get();
				default:
				    return super.getMember(name);
				}
			    }
			};
		    if (!core.hookChainWithCustom("luwrain.area.region.point.set", new Object[]{argObj}))
			core.eventNotProcessedMessage();
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
		    final Area area = core.getValidActiveArea(true);
		    if (area == null)
			return;
		    if (area.onSystemEvent(new SystemEvent(SystemEvent.Code.CLIPBOARD_COPY)))
			core.playSound(Sounds.COPIED); else
			core.eventNotProcessedMessage();
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
		    final Area area = core.getValidActiveArea(true);
		    if (area == null)
			return;
		    final AtomicReference res = new AtomicReference();
		    core.unsafeAreaOperation(()->res.set(new Boolean(area.onSystemEvent(new SystemEvent(SystemEvent.Code.CLIPBOARD_COPY_ALL)))));
		    if (res.get() == null || !((Boolean)res.get()).booleanValue())
		    {
			core.eventNotProcessedMessage();
			return;
		    }
		    final EmptyHookObject argObj = new EmptyHookObject(){
			    @Override public Object getMember(String name)
			    {
				NullCheck.notEmpty(name, "name");
				switch(name)
				{
				default:
				    return super.getMember(name);
				}
			    }
			};
		    if (!core.hookChainWithCustom("luwrain.clipboard.copy.all", new Object[]{argObj}))
			core.eventNotProcessedMessage();
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
		    final Area area = core.getValidActiveArea(true);
		    if (area == null)
			return;
		    if (area.onSystemEvent(new SystemEvent(SystemEvent.Code.CLIPBOARD_CUT)))
			core.playSound(Sounds.CUT);else
			core.eventNotProcessedMessage();
		}
	    },

	    //clear-region
	    new Command() {
		@Override public String getName()
		{
		    return "clear-region";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    final Area area = core.getValidActiveArea(true);
		    if (area == null)
			return;
		    if (area.onSystemEvent(new SystemEvent(SystemEvent.Code.CLEAR_REGION)))
			core.playSound(Sounds.DELETED); else
			core.eventNotProcessedMessage();
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
			core.eventNotProcessedMessage();
			return;
		    }
		    final Area area = core.getValidActiveArea(true);
		    if (area == null)
			return;
		    if (area.onSystemEvent(new SystemEvent(SystemEvent.Code.CLIPBOARD_PASTE)))
			core.playSound(Sounds.PASTE); else
			core.eventNotProcessedMessage();
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
		    final Area area = core.getValidActiveArea(true);
		    if (area == null)
			return;
		    final AtomicReference res = new AtomicReference();
		    core.unsafeAreaOperation(()->{
			    res.set(new Boolean(area.onSystemEvent(new SystemEvent(SystemEvent.Code.CLEAR))));
			});
		    if (res.get() == null || !((Boolean)res.get()).booleanValue())
		    {
						core.eventNotProcessedMessage();
						return;
		    }
		    final EmptyHookObject argObj = new EmptyHookObject(){
			    @Override public Object getMember(String name)
			    {
				NullCheck.notEmpty(name, "name");
				switch(name)
				{
				default:
				    return super.getMember(name);
				}
			    }
			};
		    if (!core.hookChainWithCustom("luwrain.area.clear", new Object[]{argObj}))
			core.eventNotProcessedMessage();
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
		    core.enqueueEvent(new SystemEvent(SystemEvent.Code.HELP));
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
		    core.onSwitchNextAppCommand();
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
		    core.onSwitchNextAreaCommand();
		}
	    },

	    //Increase font size
	    new Command() {
		@Override public String getName()
		{
		    return "font-size-inc";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    core.fontSizeInc();
		}
	    },

	    //Decrease font size
	    new Command() {
		@Override public String getName()
		{
		    return "font-size-dec";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    core.fontSizeDec();
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
		    final Application app = new org.luwrain.app.cpanel.ControlPanelApp(core.getControlPanelFactories());
		    core.launchApp(app);
		}
	    },

	    	    //calc
	    new Command() {
		@Override public String getName()
		{
		    return "calc";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    NullCheck.notNull(luwrain, "luwrain");
		    core.launchApp(new org.luwrain.app.calc.App());
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
		    core.launchApp(app);
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
		    core.launchApp(app);
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
		    core.showContextMenu();
		}
	    },

	    //copy-uniref-area
	    new Command() {
		@Override public String getName()
		{
		    return "copy-uniref-area";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    NullCheck.notNull(luwrain, "luwrain");
		    final String res = luwrain.getActiveAreaAttr(Luwrain.AreaAttr.UNIREF);
		    if (res == null || res.isEmpty())
		    {
			core.eventNotProcessedMessage();
			return;
		    }
		    final UniRefInfo uniRefInfo = core.uniRefProcs.getInfo(res);
		    if (uniRefInfo == null)
		    {
			core.eventNotProcessedMessage();
			return;
		    }
		    UniRefUtils.defaultAnnouncement(new DefaultControlContext(luwrain), uniRefInfo, Sounds.OK, null);
		    core.getClipboard().set(res);
		}
	    },

	    //copy-uniref-hot-point
	    new Command() {
		@Override public String getName()
		{
		    return "copy-uniref-hot-point";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    NullCheck.notNull(luwrain, "luwrain");
		    final String res = luwrain.getActiveAreaAttr(Luwrain.AreaAttr.UNIREF_UNDER_HOT_POINT);
		    if (res == null || res.isEmpty())
		    {
			core.eventNotProcessedMessage();
			return;
		    }
		    final UniRefInfo uniRefInfo = core.uniRefProcs.getInfo(res);
		    if (uniRefInfo == null)
		    {
			core.eventNotProcessedMessage();
			return;
		    }
		    UniRefUtils.defaultAnnouncement(new DefaultControlContext(luwrain), uniRefInfo, Sounds.OK, null);
		    core.getClipboard().set(res);
		}
	    },

	    //copy-url-area
	    new Command() {
		@Override public String getName()
		{
		    return "copy-url-area";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    NullCheck.notNull(luwrain, "luwrain");
		    final String res = luwrain.getActiveAreaAttr(Luwrain.AreaAttr.URL);
		    if (res == null || res.isEmpty())
		    {
			core.eventNotProcessedMessage();
			return;
		    }
		    core.message(luwrain.getSpeakableText(res, Luwrain.SpeakableTextType.PROGRAMMING), Luwrain.MessageType.OK);
		    core.getClipboard().set(res);
		}
	    },

	    //copy-url-hot-point
	    new Command() {
		@Override public String getName()
		{
		    return "copy-url-hot-point";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    NullCheck.notNull(luwrain, "luwrain");
		    final String res = luwrain.getActiveAreaAttr(Luwrain.AreaAttr.URL_UNDER_HOT_POINT);
		    if (res == null || res.isEmpty())
		    {
			core.eventNotProcessedMessage();
			return;
		    }
		    core.message(luwrain.getSpeakableText(res, Luwrain.SpeakableTextType.PROGRAMMING), Luwrain.MessageType.OK);
		    core.getClipboard().set(res);
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
		    final Speech speech = core.speech;
		    speech.setPitch(speech.getPitch() + SPEECH_STEP);
		    luwrain.message("Высота речи " + speech.getPitch());//FIXME:
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
		    final Speech speech = core.speech;
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
		    final Speech speech = core.speech;
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
		    final Speech speech = core.speech;
		    speech.setRate(speech.getRate() + SPEECH_STEP);
		    luwrain.message("Скорость речи " + (100 - speech.getRate()));
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
		    //		    core.onReadAreaCommand();
		    core.startAreaListening();
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
			core.message(word, Luwrain.MessageType.REGULAR);
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
		    final Area area = core.getValidActiveArea(true);
		    if (area == null)
			return;
		    if (!area.onSystemEvent(new SystemEvent(SystemEvent.Code.PROPERTIES)))
			core.eventNotProcessedMessage();
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
		    luwrain.speak("Время" + 
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
		    final SimpleEditPopup popup = new SimpleEditPopup(core.getObjForEnvironment(), luwrain.i18n().getStaticStr("RunPopupName"), luwrain.i18n().getStaticStr("RunPopupPrefix"), "", Popups.DEFAULT_POPUP_FLAGS);
		    core.popup(null, popup, Popup.Position.BOTTOM, popup.closing, true, true);
		    if (popup.closing.cancelled() || popup.text().trim().isEmpty())
			return;
		    */
		    final String cmd = Popups.editWithHistory(core.getObjForEnvironment(), luwrain.i18n().getStaticStr("RunPopupName"), luwrain.i18n().getStaticStr("RunPopupPrefix"), "", osCmdHistory);
		    if (cmd == null)
			return;

		    final String dir;
		    final Area area = core.getValidActiveArea(false);
		    if (area != null)
		    {
			final CurrentDirQuery query = new CurrentDirQuery();
			if (AreaQuery.ask(area, query))
			    dir = query.getAnswer(); else
			    dir = "";
		    } else
			dir = "";
		    luwrain.runOsCommand(cmd.trim(), dir, (line)->{}, (exitCode, output)->{
			    luwrain.runUiSafely(()->ObjRegistry.issueResultingMessage(luwrain, exitCode, output));
			});
		}
	    },


	};    
    }
}
