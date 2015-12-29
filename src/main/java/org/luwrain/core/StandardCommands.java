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

import java.util.*;

import org.luwrain.core.events.*;

class StandardCommands
{
    static private final int SPEECH_STEP = 5;
    static Command[] createStandardCommands(Environment env)
    {
	NullCheck.notNull(env, "env");
	final Environment environment = env;
	final LinkedList<Command> res = new LinkedList<Command>();

	//Main menu;
	res.add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "main-menu";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.mainMenu();
		}
	    });

	//Quit;
	res.add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "quit";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.quit();
		}
	    });

	//search;
	res.add(new Command() {
		final private Environment e = environment;
		@Override public String getName()
		{
		    return "search";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    e.activateAreaSearch();
		}
	    });

	//ok;
	res.add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "ok";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.OK));
		}
	    });

	//Cancel;
	res.add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "cancel";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.CANCEL));
		}
	    });

	//Close;
	res.add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "close";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.CLOSE));
		}
	    });

	//Save;
	res.add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "save";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.SAVE));
		}
	    });

	//open;
	res.add(new Command() {
		private Environment e = environment;
		@Override public String getName()
		{
		    return "open";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    e.onOpenCommand();
		}
	    });

	//Introduce;
	res.add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "introduce";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.introduceActiveArea();
		}
	    });

	//Refresh;
	res.add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "refresh";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.REFRESH));
		}
	    });

	//introduce-line;
	res.add(new Command() {
		private Environment e = environment;
		@Override public String getName()
		{
		    return "introduce-line";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    e.onIntroduceLineCommand();
		}
	    });

	//region-point;
	res.add(new Command() {
		private Environment e = environment;
		@Override public String getName()
		{
		    return "region-point";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    e.onRegionPointCommand();
		}
	    });

	//copy;
	res.add(new Command() {
		private Environment e = environment;
		@Override public String getName()
		{
		    return "copy";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    e.onCopyCommand(true);
		}
	    });

	//cut;
	res.add(new Command() {
		private Environment e = environment;
		@Override public String getName()
		{
		    return "cut";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    e.onCutCommand();
		}
	    });

	//delete;
	res.add(new Command() {
		@Override public String getName()
		{
		    return "delete";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    environment.onDeleteCommand();
		}
	    });

	//paste;
	res.add(new Command() {
		private Environment e = environment;
		@Override public String getName()
		{
		    return "paste";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    e.onPasteCommand();
		}
	    });

	//Help;
	res.add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "help";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.HELP));
		}
	    });

	//Switch to next App;
	res.add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "switch-next-app";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.onSwitchNextAppCommand();
		}
	    });

	//Switch to next area;
	res.add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "switch-next-area";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.onSwitchNextAreaCommand();
		}
	    });

	//Increase font size;
	res.add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "increase-font-size";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.onIncreaseFontSizeCommand();
		}
	    });

	//Decrease font size;
	res.add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "decrease-font-size";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.onDecreaseFontSizeCommand();
		}
	    });

	//control panel;
	res.add(new Command() {
		private Environment e = environment;
		@Override public String getName()
		{
		    return "control-panel";
}
		@Override public void onCommand(Luwrain luwrain)
		{
		    final Application app = new org.luwrain.app.cpanel.ControlPanelApp(e.getControlPanelSections());
		    e.launchApp(app);
		}
	    });

	//registry;
	res.add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "registry";
		}
		public void onCommand(Luwrain luwrain)
		{
		    Application app = new org.luwrain.app.registry.RegistryApp();
		    e.launchApp(app);
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
		    environment.onContextMenuCommand();
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
		    environment.onCopyObjectUniRefCommand();
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
		    final Speech speech = environment.getSpeech();
		    speech.setPitch(speech.getPitch() + SPEECH_STEP);
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
		    final Speech speech = environment.getSpeech();
		    speech.setPitch(speech.getPitch() - SPEECH_STEP);
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
		    final Speech speech = environment.getSpeech();
		    speech.setRate(speech.getRate() - SPEECH_STEP);
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
		    final Speech speech = environment.getSpeech();
		    speech.setRate(speech.getRate() + SPEECH_STEP);
		}
	    });






	return res.toArray(new Command[res.size()]);
    }
}
