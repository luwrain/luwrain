/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core;

import java.io.File;

public interface CommandEnvironment
{
    String[] getClipboard();

    /**
     * Returns a reference to the registry object. With this method you can
     * get access to the configuration information stored in the
     * registry. Please note that the real level of access may be different
     * depending on the context and the exact registry object may allow
     * dealing only with some part of the registry or prohibit write
     * operations.
     *
     * @return A reference to the registry object for access to configuration information
     */
    Registry getRegistry();

    void hint(String text);
    void hint(String text, int code);
    boolean hint(int code);
    I18n i18n();
    void launchApp(String shortcutName);
    void launchApp(String shortcutName, String[] args);
    LaunchContext launchContext();
    void message(String text);
    void message(String text, int semantic);
    void openFile(String fileName);
    void openFiles(String[] fileNames);

    /**
     * Plays an environment sound. Environment sounds are used to supplement
     * speech feedback and make it more informative. This method exits
     * immediately, launching the real playback in the background thread. The
     * playing can be interrupted with any consequent calls of this
     * method. The sound should be specified by the constants in {@link
     * Sounds} interface.
     *
     * @param code The integer identifier of a sound to play
     */
    void playSound(int code);

    boolean runCommand(String command);

    /**
     * Speaks the provided text with default pitch and rate. This method
     * speaks the text "as is" without any additional announcement. For
     * example, if you give a string containing spaces only, the result will
     * be silence. All digits and punctuation processing is perform by exact
     * speech back-end.
     *
     * @param text The utterance to say
     */
    void say(String text);

    /**
     * Speaks the provided text with specified pitch and default rate. This
     * method speaks the text "as is" without any additional
     * announcement. For example, if you give a string containing spaces
     * only, the result will be silence. All digits and punctuation
     * processing is perform by exact speech back-end. You can use constants
     * {@code Luwrain.PITCH_HIGH}, {@code Luwrain.PITCH_NORMAL}, 
     * {@code Luwrain.PITCH_LOW}, {@code Luwrain.PITCH_HINT} and 
     * {@code Luwrain.pitch_MESSAGE}
     * as pitch values.
     *
     * @param text The utterance to say
     * @param pitch The pitch value, must be between 0 and 100
     */
    void say(String text, int pitch);

    /**
     * Speaks the provided text with specified pitch and rate. This method
     * speaks the text "as is" without any additional announcement. For
     * example, if you give a string containing spaces only, the result will
     * be silence. All digits and punctuation processing is perform by exact
     * speech back-end. You can use constants {@code Luwrain.PITCH_HIGH},
     * {@code Luwrain.PITCH_NORMAL}, {@code Luwrain.PITCH_LOW}, 
     * {@code Luwrain.PITCH_HINT} and {@code Luwrain.pitch_MESSAGE} as pitch
     * value. For rate values there are constants {@code Luwrain.RATE_HIGH},
     * {@code Luwrain.RATE_NORMAL} and {@code Luwrain.RATE_LOW}.
     *
     * @param text The utterance to say
     * @param pitch The pitch value, must be between 0 and 100
     * @param rate The rate value, must be between 0 and 100
     */
    void say(String text, int pitch, int rate);
    void sayLetter(char letter);
    void sayLetter(char letter, int pitch);
    void sayLetter(char letter, int pitch, int rate);

    /**
     * Interrupts any speaking. This method asks speech back-end to
     * immediately stop the speech playback if there is any. All variants of
     * {@code say()} and {@code sayLetter()} methods do this automatically.
     */
    void silence();
    void setClipboard(String[] value);
}
