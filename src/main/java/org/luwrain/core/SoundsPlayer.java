
package org.luwrain.core;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import javax.sound.sampled.*;

class SoundsPlayer
{
    static private class Player implements Runnable
    {
    private static final int BUF_SIZE = 512;

	private String fileName;
	private boolean interruptPlayback = false;
	boolean finished = false;
private SourceDataLine audioLine = null;

	Player(String fileName)
	{
	    NullCheck.notEmpty(fileName, "fileName");
	    this.fileName = fileName;
	}

	@Override public void run()
	{
	    try {
	    try {
	    final File soundFile = new File(fileName);
	    if (!soundFile.exists())
		return;
	    final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
	    final AudioFormat format = audioInputStream.getFormat();
	    final DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
	    synchronized (this) {
		audioLine = (SourceDataLine) AudioSystem.getLine(info);
		audioLine.open(format);
		audioLine.start();
	    }
	    int bytesRead = 0;
	    byte[] buf = new byte[BUF_SIZE];
		while (bytesRead != -1 && !interruptPlayback)
		{
		    bytesRead = audioInputStream.read(buf, 0, buf.length);
		    //		    System.out.println("bytesRead=" + bytesRead);
		    if (bytesRead >= 0)
			synchronized (this) {
			audioLine.write(buf, 0, bytesRead);
			}
		}
		audioLine.drain();
	}
	    finally
	    {
		if (audioLine != null)
		audioLine.close();
		finished = true;
	    }
	    }
	    catch(UnsupportedAudioFileException | IOException | LineUnavailableException e)
	    {
		e.printStackTrace();
	    }
		}

	void stopPlaying()
	{
	    interruptPlayback = true;
	    if (audioLine != null)
	    {
		//		audioLine.flush();
	    audioLine.stop();
	    }
	}
    }

    private final HashMap<Sounds, String> soundFiles = new HashMap<Sounds, String>();
    private Player previous;

    void play(Sounds sound)
    {
	if (sound == null)
	    return;
	if (!soundFiles.containsKey(sound) || soundFiles.get(sound).isEmpty())
	{
	    Log.error("core", "no sound for playing:" + sound);
	    return;
	}
	if (previous != null)
	    previous.stopPlaying();
	previous = new Player(soundFiles.get(sound));
	new Thread(previous).start();
    }

    boolean finished()
    {
	return previous == null || previous.finished;
    }

    void init(Registry registry, Path dataDir)
    {
	NullCheck.notNull(registry, "registry");
	NullCheck.notNull(dataDir, "dataDir");
	final Settings.SoundScheme scheme = Settings.createCurrentSoundScheme(registry);
	setSoundFile(dataDir, scheme.getEventNotProcessed(""), Sounds.EVENT_NOT_PROCESSED);
	setSoundFile(dataDir, scheme.getNoApplications(""), Sounds.NO_APPLICATIONS);
	setSoundFile(dataDir, scheme.getStartup(""), Sounds.STARTUP);
	setSoundFile(dataDir, scheme.getShutdown(""), Sounds.SHUTDOWN);
	setSoundFile(dataDir, scheme.getMainMenu(""), Sounds.MAIN_MENU);
	setSoundFile(dataDir, scheme.getMainMenuItem(""), Sounds.MAIN_MENU_ITEM);
	setSoundFile(dataDir, scheme.getMainMenuEmptyLine(""), Sounds.MAIN_MENU_EMPTY_LINE);
	setSoundFile(dataDir, scheme.getError(""), Sounds.ERROR);
	setSoundFile(dataDir, scheme.getFatal(""), Sounds.FATAL);
	setSoundFile(dataDir, scheme.getOk(""), Sounds.OK);
	setSoundFile(dataDir, scheme.getDone(""), Sounds.DONE);
	setSoundFile(dataDir, scheme.getBlocked(""), Sounds.BLOCKED);
	setSoundFile(dataDir, scheme.getIntroRegular(""), Sounds.INTRO_REGULAR);
	setSoundFile(dataDir, scheme.getIntroPopup(""), Sounds.INTRO_POPUP);
	setSoundFile(dataDir, scheme.getIntroApp(""), Sounds.INTRO_APP);
	setSoundFile(dataDir, scheme.getNoItemsAbove(""), Sounds.NO_ITEMS_ABOVE);
	setSoundFile(dataDir, scheme.getNoItemsBelow(""), Sounds.NO_ITEMS_BELOW);
	setSoundFile(dataDir, scheme.getNoLinesAbove(""), Sounds.NO_LINES_ABOVE);
	setSoundFile(dataDir, scheme.getNoLinesBelow(""), Sounds.NO_LINES_BELOW);
	setSoundFile(dataDir, scheme.getCommanderLocation(""), Sounds.COMMANDER_LOCATION);
	setSoundFile(dataDir, scheme.getListItem(""), Sounds.LIST_ITEM);
	setSoundFile(dataDir, scheme.getParagraph(""), Sounds.PARAGRAPH);
	setSoundFile(dataDir, scheme.getGeneralTime(""), Sounds.GENERAL_TIME);
	setSoundFile(dataDir, scheme.getTermBell(""), Sounds.TERM_BELL);
	setSoundFile(dataDir, scheme.getDocSection(""), Sounds.DOC_SECTION);
	setSoundFile(dataDir, scheme.getNoContent(""), Sounds.NO_CONTENT);
	setSoundFile(dataDir, scheme.getSearch(""), Sounds.SEARCH);
	setSoundFile(dataDir, scheme.getDeleted(""), Sounds.DELETED);
	setSoundFile(dataDir, scheme.getCancel(""), Sounds.CANCEL);
	setSoundFile(dataDir, scheme.getRegionPoint(""), Sounds.REGION_POINT);
	setSoundFile(dataDir, scheme.getPaste(""), Sounds.PASTE);
	setSoundFile(dataDir, scheme.getCopied(""), Sounds.COPIED);
	setSoundFile(dataDir, scheme.getCut(""), Sounds.CUT);
    }

    private void setSoundFile(Path dataDir, String fileName,
			      Sounds sound)
    {
NullCheck.notNull(sound, "sound");
	if (fileName.isEmpty())
	    return;
	Path path = Paths.get(fileName);
	if (!path.isAbsolute())
	    path = dataDir.resolve(path);
	soundFiles.put(sound, path.toString());
    }
    }
