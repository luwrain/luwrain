
package org.luwrain.core.init;

import java.io.*;
import java.util.*;

import org.luwrain.core.*;

final class RegistryExtractor
{
    static private final String DIR_PREFIX = "DIR ";
    static private final String FILE_PREFIX = "FILE ";

    private final File destDir;

    private File currentDir = null;
    private File currentFile = null;
    private final List<String> lines = new LinkedList();

    RegistryExtractor(File destDir)
    {
	NullCheck.notNull(destDir, "destDir");
	this.destDir = destDir;
    }

    void extract(InputStream is) throws IOException
    {
	NullCheck.notNull(is, "is");
	final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	String line = reader.readLine();
	while(line != null)
	{
	    line = line.trim();
	    if (line.isEmpty() || line.charAt(0) == '#')
	    {
		line = reader.readLine();
		continue;
	    }
	    if (line.startsWith(DIR_PREFIX))
		onDir(line.substring(DIR_PREFIX.length()).trim()); else
	    if (line.startsWith(FILE_PREFIX))
		onFile(line.substring(FILE_PREFIX.length()).trim()); else
		onValue(line);
	    line = reader.readLine();
	}
	saveLines();
    }

    private void onDir(String path) throws IOException
    {
	NullCheck.notNull(path, "path");
	if (path.isEmpty())
	    return;
	saveLines();
	currentDir = new File(destDir, path);
	createDirectories(currentDir);
	new File(currentDir, "strings.txt").createNewFile();
	new File(currentDir, "integers.txt").createNewFile();
	new File(currentDir, "booleans.txt").createNewFile();
    }

    private void onFile(String fileName) throws IOException
    {
	NullCheck.notNull(fileName, "fileName");
	if (fileName.isEmpty())
	    return;
	if (currentDir == null)
	    return;
	saveLines();
	currentFile = new File(currentDir, fileName);
	currentFile.createNewFile();
    }

    private void onValue(String line) throws IOException
    {
	NullCheck.notNull(line, "line");
	if (line.isEmpty())
	    return;
	if (currentDir == null || currentFile == null)
	    return;
	lines.add(line);
    }

    private void saveLines() throws IOException
    {
		if (currentDir == null || currentFile == null)
	    return;
	final BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile, true));
	try {
	    for(String s: lines)
	    {
		writer.write(s);
		writer.newLine();
	    }
	}
	finally {
	    writer.close();
	}
	lines.clear();
    }

    static private void createDirectories(File file) throws IOException
    {
	NullCheck.notNull(file, "file");
	final LinkedList<File> files = new LinkedList();
	File f = file;
	while(f != null)
	{
	    files.add(f);
	    f = f.getParentFile();
	}
	while(!files.isEmpty())
	{
	    final File dir = files.pollLast();
	    if (!dir.isDirectory())
		dir.mkdir();
	}
    }
}
