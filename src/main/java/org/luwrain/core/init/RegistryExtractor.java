
package org.luwrain.core.init;

import java.io.*;
import java.util.*;

import org.luwrain.core.*;

public final class RegistryExtractor
{
    static private final String DIR_PREFIX = "DIR ";
    static private final String FILE_PREFIX = "FILE ";

    private final File destDir;

    private File currentDir = null;
    private File currentFile = null;
    private final List<String> lines = new LinkedList();

    public RegistryExtractor(File destDir)
    {
	NullCheck.notNull(destDir, "destDir");
	this.destDir = destDir;
    }

    public void extract(InputStream is) throws IOException
    {
	NullCheck.notNull(is, "is");
	final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	String line = reader.readLine();
	while(line != null)
	{
	    line = line.trim();
	    if (line.isEmpty() || line.charAt(0) == '#')
		continue;
	    if (line.startsWith(DIR_PREFIX))
		onDir(line.substring(DIR_PREFIX.length()).trim());
	    if (line.startsWith(FILE_PREFIX))
		onFile(line.substring(FILE_PREFIX.length()).trim()); else
		onValue(line);
	    line = reader.readLine();
	}
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

    private void saveLines()
    {
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
