
package org.luwrain.core;

import java.io.*;
import java.net.*;

public final class LaunchFactoryImpl implements LaunchFactory
{
    @Override public Runnable newLaunch(String[] args, File dataDir, File userDataDir, File userHomeDir)
    {
	return new Launch(args, dataDir, userDataDir, userHomeDir);
    }
}
