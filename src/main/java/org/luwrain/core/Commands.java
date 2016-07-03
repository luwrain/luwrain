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

class Commands
{
    static private final int SPEECH_STEP = 5;
    static private final int VOLUME_STEP = 5;

    static Command[] createStandardCommands(Environment env)
    {
	NullCheck.notNull(env, "env");
	final LinkedList<Command> res = new LinkedList<Command>();

	//Main menu
	res.add(new Command() {
		//		private Environment e = environment;
		public String getName()
		{
		    return "main-menu";
		}
		public void onCommand(Luwrain luwrain)
		{
		    env.mainMenu();
		}
	    });

	//Quit;
	res.add(new Command() {
		//		private Environment e = environment;
		public String getName()
		{
		    return "quit";
		}
		public void onCommand(Luwrain luwrain)
		{
		    env.quit();
		}
	    });

	//search;
	res.add(new Command() {
		//		final private Environment e = environment;
		@Override public String getName()
		{
		    return "search";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.activateAreaSearch();
		}
	    });

	//ok
	res.add(new Command() {
		//		private Environment e = environment;
		public String getName()
		{
		    return "ok";
		}
		public void onCommand(Luwrain luwrain)
		{
		    env.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.Code.OK));
		}
	    });

	//Cancel
	res.add(new Command() {
		//		private Environment e = environment;
		public String getName()
		{
		    return "cancel";
		}
		public void onCommand(Luwrain luwrain)
		{
		    env.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.Code.CANCEL));
		}
	    });

	//Close
	res.add(new Command() {
		//		private Environment e = environment;
		public String getName()
		{
		    return "close";
		}
		public void onCommand(Luwrain luwrain)
		{
		    env.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.Code.CLOSE));
		}
	    });

	//Save
	res.add(new Command() {
		//		private Environment e = environment;
		public String getName()
		{
		    return "save";
		}
		public void onCommand(Luwrain luwrain)
		{
		    env.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.Code.SAVE));
		}
	    });

	//open;
	res.add(new Command() {
		//		private Environment e = environment;
		@Override public String getName()
		{
		    return "open";
		}
		@Override public void onCommand(Luwrain luwrainArg)
		{
		    final Strings strings = env.strings();
		    final Luwrain luwrain = env.getObjForEnvironment();
	final Path current = Paths.get(luwrain.currentAreaDir());
	final FilePopup popup = new FilePopup(luwrain, 
					      strings.openPopupName(), strings.openPopupPrefix(), 
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
	    });

	//Introduce;
	res.add(new Command() {
		//		private Environment e = environment;
		public String getName()
		{
		    return "introduce";
		}
		public void onCommand(Luwrain luwrain)
		{
		    env.introduceActiveArea();
		}
	    });

	//Refresh;
	res.add(new Command() {
		//		private Environment e = environment;
		public String getName()
		{
		    return "refresh";
		}
		public void onCommand(Luwrain luwrain)
		{
		    env.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.Code.REFRESH));
		}
	    });

	//introduce-line;
	res.add(new Command() {
		//		private Environment e = environment;
		@Override public String getName()
		{
		    return "introduce-line";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.onIntroduceLineCommand();
		}
	    });

	//region-point;
	res.add(new Command() {
		//		private Environment e = environment;
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
			env.message(env.strings().regionPointSet(), Luwrain.MESSAGE_REGULAR); 
			env.playSound(Sounds.REGION_POINT);
		    }else
			env.eventNotProcessedMessage();
		}
	    });

	//copy;
	res.add(new Command() {
		//		private Environment e = environment;
		@Override public String getName()
		{
		    return "copy";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.onCopyCommand(true);
		}
	    });

	//cut;
	res.add(new Command() {
		//		private Environment e = environment;
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
	if (!area.onAreaQuery(query) || !query.containsResult())
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
	env.clipboard = res;
	    env.message("Вырезано строк: " + res.strings.length, Luwrain.MESSAGE_REGULAR);
		}
	    });

	//delete
	res.add(new Command() {
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
	    env.message(env.strings().linesDeleted(), Luwrain.MESSAGE_REGULAR);
	env.playSound(Sounds.DELETED);
	} else
env.eventNotProcessedMessage();
		}
	    });

	//paste
	res.add(new Command() {
		//		private Environment e = environment;
		@Override public String getName()
		{
		    return "paste";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
	if (env.clipboard == null || env.clipboard.isEmpty())
	{
	    env.message(env.strings().noClipboardContent(), Luwrain.MESSAGE_NOT_READY);
	    return;
	}
	final Area area = env.getValidActiveArea(true);
	if (area == null)
	    return;
	final InsertEvent event = new InsertEvent(env.clipboard);
	if (area.onEnvironmentEvent(event))
	    env.message(env.strings().linesInserted(env.clipboard.strings.length), Luwrain.MESSAGE_REGULAR); else
	    env.eventNotProcessedMessage();
		}
	    });

	//Help
	res.add(new Command() {
		//		private Environment e = environment;
		public String getName()
		{
		    return "help";
		}
		public void onCommand(Luwrain luwrain)
		{
		    env.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.Code.HELP));
		}
	    });

	//Switch to next App;
	res.add(new Command() {
		//		private Environment e = environment;
		public String getName()
		{
		    return "switch-next-app";
		}
		public void onCommand(Luwrain luwrain)
		{
		    env.onSwitchNextAppCommand();
		}
	    });

	//Switch to next area;
	res.add(new Command() {
		//		private Environment e = environment;
		public String getName()
		{
		    return "switch-next-area";
		}
		public void onCommand(Luwrain luwrain)
		{
		    env.onSwitchNextAreaCommand();
		}
	    });

	//Increase font size;
	res.add(new Command() {
		//		private Environment e = environment;
		public String getName()
		{
		    return "increase-font-size";
		}
		public void onCommand(Luwrain luwrain)
		{
		    env.onIncreaseFontSizeCommand();
		}
	    });

	//Decrease font size;
	res.add(new Command() {
		//		private Environment e = environment;
		public String getName()
		{
		    return "decrease-font-size";
		}
		public void onCommand(Luwrain luwrain)
		{
		    env.onDecreaseFontSizeCommand();
		}
	    });

	//control panel;
	res.add(new Command() {
		//		private Environment e = environment;
		@Override public String getName()
		{
		    return "control-panel";
}
		@Override public void onCommand(Luwrain luwrain)
		{
		    final Application app = new org.luwrain.app.cpanel.ControlPanelApp(env.getControlPanelFactories());
		    env.launchApp(app);
		}
	    });

	//registry;
	res.add(new Command() {
		//		private Environment e = environment;
		public String getName()
		{
		    return "registry";
		}
		public void onCommand(Luwrain luwrain)
		{
		    Application app = new org.luwrain.app.registry.RegistryApp();
		    env.launchApp(app);
		}
	    });

	//context-menu;
	res.add(new Command() {
		@Override public String getName()
		{
		    return "context-menu";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.onContextMenuCommand();
		}
	    });

	//copy-object-uniref
	res.add(new Command() {
		@Override public String getName()
		{
		    return "copy-object-uniref";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.onCopyObjectUniRefCommand();
		}
	    });


	//speech-pitch-inc
	res.add(new Command() {
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
	    });

	//speech-pitch-dec
	res.add(new Command() {
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
	    });

	//speech-speed-inc
	res.add(new Command() {
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
	    });

	//speech-speed-dec
	res.add(new Command() {
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
	    });

	//volume-inc
	res.add(new Command() {
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
	    });

	//volume-dec
	res.add(new Command() {
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
	    });

	//read-area
	res.add(new Command() {
		@Override public String getName()
		{
		    return "read-area";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.onReadAreaCommand();
		}
	    });

	//shutdown
	res.add(new Command() {
		@Override public String getName()
		{
		    return "shutdown";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.os().shutdown();
		}
	    });

	//reboot
	res.add(new Command() {
		@Override public String getName()
		{
		    return "reboot";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.os().reboot();
		}
	    });

	//suspend
	res.add(new Command() {
		@Override public String getName()
		{
		    return "suspend";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.os().suspend(false);
		}
	    });

	//say-current-word
	res.add(new Command() {
		@Override public String getName()
		{
		    return "say-current-word";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    env.onSayCurrentWordCommand();
		}
	    });

	//properties
	res.add(new Command() {
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
	    });



	return res.toArray(new Command[res.size()]);
    }
}
