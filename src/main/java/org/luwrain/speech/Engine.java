/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.speech;

import java.util.*;

/*
 * The interface for text-to-speech engines. This class behaves like a
 * factory for speech channels, which are used for synthesizing human
 * voice. Various engines are created by loaded extensions and registered
 * by LUWRAIN core. Any particular synthesizer is address by the name of
 * the corresponding engine.
 *
 * As it was mentioned previously, this interface mostly takes care of
 * creating the engine channels for speech synthesizing. Normally LUWRAIN
 * may request any number of speech channels of any particular engine and
 * all of them must work completely independently.
 *
 * Whenever creation of a new channel is requested, LUWRAIN provides a
 * map of channel parameters. The purpose of these parameters is
 * engine-dependent, LUWRAIN does not impose any exact meaning.
 * Typically the entire list of speech channel parameters is specified by
 * a user through the control panel or through the command line
 * arguments. The engine itself is addressed by its name, which is the
 * same as the name of ExtensionObject.
 *
 * In addition the Engine interface provides an set of engine features
 * helping the core to understand what this engine can do. LUWRAIN does
 * not expect that all engines support all features, it's absolutely
 * normal to have some engines with partial functionality.
 */
public interface Engine extends org.luwrain.base.ExtensionObject
{
    public enum Features {
	CAN_SYNTH_TO_STREAM,
	CAN_SYNTH_TO_SPEAKERS,
	CAN_NOTIFY_WHEN_FINISHED,
    };


    /**
     * Provides the set of features what this engine cat do.
     *
     * @return The set of features, must be never null.
     */
        Set<Features>  getFeatures();

    /**
     * Creates new channel of this engine with the requested parameters.
     *
     * @param params The map of settings values for the newly created channel (default voice, language, etc)
     * @return The newly created channel, must be never null (use the exception for errors indication)
     */
    Channel newChannel(Map<String, String> params); //FIXME:exception
}
