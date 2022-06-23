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

package org.luwrain.core;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.popups.*;
import org.luwrain.script.*;
import org.luwrain.controls.DefaultControlContext;
import org.luwrain.script.core.MapScriptObject;

final class Commands
{
    static private final int
	SPEECH_STEP = 5,
	VOLUME_STEP = 5;

    static private final Set<String> osCmdHistory = new HashSet<String>();

    static Command[] getCommands(Core core, org.luwrain.core.shell.Conversations conversations)
    {
	NullCheck.notNull(core, "core");
	NullCheck.notNull(conversations, "conversations");
	final org.luwrain.script.Hooks hooks = new org.luwrain.script.Hooks();
	return new Command[]{

	    new Cmd(
		    "main-menu",
		    (luwrain)->{
			core.mainMenu();
		    }),

	    new Cmd(
		    "search",
		    (luwrain)->{
			core.activateAreaSearch();
		    }),

	    new Cmd(
		    "ok",
		    (luwrain)->{
			core.enqueueEvent(new SystemEvent(SystemEvent.Code.OK));
		    }),

	    new Cmd(
		    "cancel",
		    (luwrain)->{
			core.enqueueEvent(new SystemEvent(SystemEvent.Code.CANCEL));
		    }),

	    new Cmd(
		    "close",
		    (luwrain)->{
			core.enqueueEvent(new SystemEvent(SystemEvent.Code.CLOSE));
		    }),

	    new Cmd(
		    "gc",
		    (luwrain)->{
			System.gc();
		    }),

	    new Cmd(
		    "save",
		    (luwrain)->{
			core.enqueueEvent(new SystemEvent(SystemEvent.Code.SAVE));
		    }),

	    new Cmd(
		    "open",
		    (luwrain)->{
			final File res = conversations.open();
			if (res != null)
			    core.openFiles(new String[]{res.getAbsolutePath()});
		    }),

	    new Cmd(
		    "announce",
		    (luwrain)->{
			core.announceActiveArea();
		    }),

	    new Cmd(
		    "refresh",
		    (luwrain)->{
			core.enqueueEvent(new SystemEvent(SystemEvent.Code.REFRESH));
		    }),

	    new Cmd(
		    "announce-line",
		    (luwrain)->{
			final Area area = core.getActiveArea(true);
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
			core.announcement = null;
		    }),

	    new Cmd(
		    "region-point",
		    (luwrain)->{
			final Area area = core.getActiveArea(true);
			if (area == null)
			    return;
			if (!area.onSystemEvent(new SystemEvent(SystemEvent.Code.REGION_POINT)))
			{
			    core.eventNotProcessedMessage();
			    return;
			}
			final Map<String, Object> arg = new HashMap<>();
			arg.put("x", area.getHotPointX());
			arg.put("y", area.getHotPointY());
			final MapScriptObject argObj = new MapScriptObject(arg);
			if (!hooks.chainOfResponsibility(core.luwrain, "luwrain.area.region.point.set", new Object[]{argObj}))
			    core.eventNotProcessedMessage();
		    }),

	    new Cmd(
		    "copy",
		    (luwrain)->{
			final Area area = core.getActiveArea(true);
			if (area == null)
			    return;
			if (area.onSystemEvent(new SystemEvent(SystemEvent.Code.CLIPBOARD_COPY)))
			    core.playSound(Sounds.COPIED); else
			    core.eventNotProcessedMessage();
		    }),

	    new Cmd(
		    "copy-all",
		    (luwrain)->{
			final Area area = core.getActiveArea(true);
			if (area == null)
			    return;
			if (!area.onSystemEvent(new SystemEvent(SystemEvent.Code.CLIPBOARD_COPY_ALL)))
			{
			    core.eventNotProcessedMessage();
			    return;
			}
			final Map<String, Object> arg = new HashMap<>();
			//FIXME:
			final MapScriptObject argObj = new MapScriptObject(arg);
			if (!hooks.chainOfResponsibility(core.luwrain, "luwrain.clipboard.copy.all", new Object[]{argObj}))
			    core.eventNotProcessedMessage();
		    }),

	    new Cmd(
		    "cut",
		    (luwrain)->{
			final Area area = core.getActiveArea(true);
			if (area == null)
			    return;
			if (area.onSystemEvent(new SystemEvent(SystemEvent.Code.CLIPBOARD_CUT)))
			    core.playSound(Sounds.CUT);else
			    core.eventNotProcessedMessage();
		    }),

	    new Cmd(
		    "clear-region",
		    (luwrain)->{
			final Area area = core.getActiveArea(true);
			if (area == null)
			    return;
			if (area.onSystemEvent(new SystemEvent(SystemEvent.Code.CLEAR_REGION)))
			    core.playSound(Sounds.DELETED); else
			    core.eventNotProcessedMessage();
		    }),

	    new Cmd(
		    "paste",
		    (luwrain)->{
			if (luwrain.getClipboard().isEmpty())
			{
			    core.eventNotProcessedMessage();
			    return;
			}
			final Area area = core.getActiveArea(true);
			if (area == null)
			    return;
			if (area.onSystemEvent(new SystemEvent(SystemEvent.Code.CLIPBOARD_PASTE)))
			    core.playSound(Sounds.PASTE); else
			    core.eventNotProcessedMessage();
		    }),

	    new Cmd(
		    "clear",
		    (luwrain)->{
			final Area area = core.getActiveArea(true);
			if (area == null)
			    return;
			if (!area.onSystemEvent(new SystemEvent(SystemEvent.Code.CLEAR)))
			{
			    core.eventNotProcessedMessage();
			    return;
			}
			final Map<String, Object> arg = new HashMap<>();
			//FIXME:
			if (!hooks.chainOfResponsibility(core.luwrain, Hooks.AREA_CLEAR, new Object[]{new MapScriptObject(arg)}))
			    core.eventNotProcessedMessage();
		    }),

	    new Cmd(
		    "help",
		    (luwrain)->{
			core.enqueueEvent(new SystemEvent(SystemEvent.Code.HELP));
		    }),

	    new Cmd(
		    "switch-next-app",
		    (luwrain)->{
			core.onSwitchNextAppCommand();
		    }),

	    new Cmd(
		    "switch-next-area",
		    (luwrain)->{
			core.onSwitchNextAreaCommand();
		    }),

	    new Cmd(
		    "font-size-inc",
		    (luwrain)->{
			core.fontSizeInc();
		    }),

	    new Cmd(
		    "font-size-dec",
		    (luwrain)->{
			core.fontSizeDec();
		    }),

	    new Cmd(
		    "jobs",
		    (luwrain)->{
			core.launchApp(new org.luwrain.app.jobs.App(core.jobs));
		    }),

	    new Cmd(
		    "calc",
		    (luwrain)->{
			core.launchApp(new org.luwrain.app.calc.App());
		    }),

	    new Cmd(
		    "console",
		    (luwrain)->{
			core.launchApp(new org.luwrain.app.console.App());
		    }),

	    new Cmd(
		    "registry",
		    (luwrain)->{
			core.launchApp(new org.luwrain.app.registry.RegistryApp());
		    }),

	    new Cmd(
		    "context-menu",
		    (luwrain)->{
			core.showContextMenu();
		    }),

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

	    new Cmd(
		    "run",
		    (luwrain)->{
			final String cmd = Popups.editWithHistory(core.getObjForEnvironment(), luwrain.i18n().getStaticStr("RunPopupName"), luwrain.i18n().getStaticStr("RunPopupPrefix"), "", osCmdHistory);
			if (cmd == null || cmd.trim().isEmpty())
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
			luwrain.newJob("sys", new String[]{ cmd.trim() }, "", EnumSet.noneOf(Luwrain.JobFlags.class), null);
		    }),
	};    
    }

    static Command[] getNonStandaloneCommands(Core core, org.luwrain.core.shell.Conversations conversations)
    {
	return new Command[]{

	    new Cmd(
		    "control-panel",
		    (luwrain)->{
			final Application app = new org.luwrain.app.cpanel.ControlPanelApp(core.getControlPanelFactories());
			core.launchApp(app);
		    }),

	};
    }

    private interface Handler
    {
	void onCommand(Luwrain luwrain);
    }

    static private final class Cmd implements Command
    {
	private final String name;
	private final Handler handler;
	Cmd(String name, Handler handler)
	{
	    NullCheck.notEmpty(name, "name");
	    NullCheck.notNull(handler, "handler");
	    this.name = name;
	    this.handler = handler;
	}
	@Override public String getName()
	{
	    return name;
	}
			@Override public void onCommand(Luwrain luwrain)
			{
			    handler.onCommand(luwrain);
			}
    }
}
