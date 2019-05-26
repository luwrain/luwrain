
package org.luwrain.core;

import java.io.*;
import java.net.*;

public final class LaunchFactoryImpl implements LaunchFactory
{
    @Override public Runnable newLaunch(String[] args, File dataDir)
    {
	return new Launch(args, dataDir);
    }
}
