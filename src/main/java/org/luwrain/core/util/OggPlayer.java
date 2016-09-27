/*                                                                              
 * Copyright © Jon Kristensen, 2008.                                          
 * All rights reserved.                                                         
 *                                                                              
 * This is version 1.0 of this source code, made to work with JOrbis 1.x. The   
 * last time this file was updated was the 15th of March, 2008.                 
 *                                                                              
 * Version history:                                                             
 *                                                                              
 * 1.0: Initial release.                                                        
 *                                                                              
 * Redistribution and use in source and binary forms, with or without           
 * modification, are permitted provided that the following conditions are met:  
 *                                                                              
 *   * Redistributions of source code must retain the above copyright notice,   
 *     this list of conditions and the following disclaimer.                    
 *                                                                              
 *   * Redistributions in binary form must reproduce the above copyright        
 *     notice, this list of conditions and the following disclaimer in the      
 *     documentation and/or other materials provided with the distribution.     
 *                                                                              
 *   * Neither the name of jonkri.com nor the names of its contributors may be  
 *     used to endorse or promote products derived from this software without   
 *     specific prior written permission.                                       
 *                                                                              
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE    
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE   
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE     
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR          
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF         
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS     
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN      
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)      
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE   
 * POSSIBILITY OF SUCH DAMAGE.                                                  
 */                                                                             

package org.luwrain.core.util;

import com.jcraft.jogg.*;                                                       
import com.jcraft.jorbis.*;                                                     
import java.io.InputStream;                                                     
import java.io.IOException;                                                     
import java.net.MalformedURLException;                                          
import java.net.URL;                                                            
import java.net.URLConnection;                                                  
import java.net.UnknownServiceException;                                        
import javax.sound.sampled.AudioFormat;                                         
import javax.sound.sampled.AudioSystem;                                         
import javax.sound.sampled.DataLine;                                            
import javax.sound.sampled.LineUnavailableException;                            
import javax.sound.sampled.SourceDataLine;                                      

public class OggPlayer extends Thread                                       
{                                                                               
    private final boolean debugMode = true;                                     
    private URLConnection urlConnection = null;                                 
    private InputStream inputStream = null;                                     
    private byte[] buffer = null;                                                       
    private final int bufferSize = 2048;
    private int count = 0;                                                              
    private int index = 0;                                                              
    private byte[] convertedBuffer;                                                     
    private int convertedBufferSize;                                                    
    private SourceDataLine outputLine = null;                                   
    private float[][][] pcmInfo;                                                
    private int[] pcmIndex;                                                     
    private Packet joggPacket = new Packet();                                   
    private Page joggPage = new Page();                                         
    private StreamState joggStreamState = new StreamState();                    
    private SyncState joggSyncState = new SyncState();                          
    private DspState jorbisDspState = new DspState();                           
    private Block jorbisBlock = new Block(jorbisDspState);                      
    private Comment jorbisComment = new Comment();                              
    private Info jorbisInfo = new Info();                                       

    private boolean toContinue = true;

    public OggPlayer(String pUrl)                                                  
    {                                                                           
        configureInputStream(getUrl(pUrl));                                     
    }                                                                           

    private URL getUrl(String pUrl)                                              
    {                                                                           
        URL url = null;                                                         
	try                                                                     
        {                                                                       
            url = new URL(pUrl);                                                
        }                                                                       
        catch(MalformedURLException exception)                                  
        {                                                                       
            System.err.println("Malformed \"url\" parameter: \"" + pUrl + "\"");
        }                                                                       
	return url;                                                             
    }                                                                           

    private void configureInputStream(URL pUrl)                                 
    {                                                                           
        try                                                                     
        {                                                                       
            urlConnection = pUrl.openConnection();                              
        }                                                                       
        catch(UnknownServiceException exception)                                
        {                                                                       
            System.err.println("The protocol does not support input.");         
        }                                                                       
        catch(IOException exception)                                            
        {                                                                       
            System.err.println("An I/O error occoured while trying create the " 
			       + "URL connection.");                                           
        }                                                                       
        if(urlConnection != null)                                               
        {                                                                       
            try                                                                 
            {                                                                   
                inputStream = urlConnection.getInputStream();                   
            }                                                                   
            catch(IOException exception)                                        
            {                                                                   
                System.err                                                      
		.println("An I/O error occoured while trying to get an "    
			 + "input stream from the URL.");                        
                System.err.println(exception);                                  
            }                                                                   
        }                                                                       
    }                                                                           

    @Override public void run()                                                           
    {                                                                           
        if(inputStream == null)                                                 
        {                                                                       
            System.err.println("We don't have an input stream and therefor "    
			       + "cannot continue.");                                          
            return;                                                             
        }                                                                       
        initializeJOrbis();                                                     
        if(readHeader() && initializeSound())
                readBody();                                                     
        cleanUp();                                                              
    }                                                                          

    public void stopPlay()
    {
	toContinue = false;
	outputLine.stop();
    }

    private void initializeJOrbis()                                             
    {                                                                           
        debugOutput("Initializing JOrbis.");                                    
        joggSyncState.init();                                                   
        joggSyncState.buffer(bufferSize);                                       
        buffer = joggSyncState.data;                                            
	debugOutput("Done initializing JOrbis.");                               
    }                                                                           

    private boolean readHeader()                                                
    {                                                                           
        debugOutput("Starting to read the header.");                            
        boolean needMoreData = true;                                            
        int packet = 1;                                                         
        while(needMoreData)                                                     
        {                                                                       
            try {
                count = inputStream.read(buffer, index, bufferSize);            
            }                                                                   
            catch(IOException exception)                                        
            {                                                                   
                System.err.println("Could not read from the input stream.");    
                System.err.println(exception);                                  
            }                                                                   
            joggSyncState.wrote(count);                                         
            switch(packet)                                                      
            {                                                                   
	    case 1:                                                         
                {                                                               
                    switch(joggSyncState.pageout(joggPage))                     
                    {                                                           
		    case -1:                                                
                        {                                                       
                            System.err.println("There is a hole in the first "  
					       + "packet data.");                              
                            return false;                                       
                        }                                                       
		    case 0:                                                 
                        {                                                       
                            break;                                              
                        }                                                       
		    case 1:                                                 
                        {                                                       
                            joggStreamState.init(joggPage.serialno());          
                            joggStreamState.reset();                            
                            jorbisInfo.init();                                  
                            jorbisComment.init();                               
                            if(joggStreamState.pagein(joggPage) == -1)          
                            {                                                   
                                System.err.println("We got an error while "     
						   + "reading the first header page.");        
                                return false;                                   
                            }                                                   
                            if(joggStreamState.packetout(joggPacket) != 1)      
                            {                                                   
                                System.err.println("We got an error while "     
						   + "reading the first header packet.");      
                                return false;                                   
                            }                                                   
                            if(jorbisInfo.synthesis_headerin(jorbisComment,     
							     joggPacket) < 0)                                
                            {                                                   
                                System.err.println("We got an error while "     
						   + "interpreting the first packet. "         
						   + "Apparantly, it's not Vorbis data.");     
                                return false;                                   
                            }                                                   
                            packet++;                                           
                            break;                                              
                        }                                                       
                    }                                                           
                    if(packet == 1) 
			break;                                      
                }                                                               
	    case 2:    case 3:                                              
                {                                                               
                    switch(joggSyncState.pageout(joggPage))                     
                    {                                                           
		    case -1:                                                
                        {                                                       
                            System.err.println("There is a hole in the second " 
					       + "or third packet data.");                     
                            return false;                                       
                        }                                                       
		    case 0:                                                 
                        {                                                       
                            break;                                              
                        }                                                       
		    case 1:                                                 
                        {                                                       
                            joggStreamState.pagein(joggPage);                   
                            switch(joggStreamState.packetout(joggPacket))       
                            {                                                   
			    case -1:                                        
                                {                                               
                                    System.err                                  
				    .println("There is a hole in the first" 
					     + "packet data.");                  
                                    return false;                               
                                }                                               
			    case 0:                                         
                                {                                               
                                    break;                                      
                                }                                               
			    case 1:                                         
                                {                                               
                                    jorbisInfo.synthesis_headerin(              
								  jorbisComment, joggPacket);             
                                    packet++;                                   
				    if(packet == 4)                             
                                    {                                           
                                        needMoreData = false;                   
                                    }                                           
				    break;                                      
                                }                                               
                            }                                                   
			    break;                                              
                        }                                                       
                    }                                                           
		    break;                                                      
                }                                                               
            }                                                                   
            index = joggSyncState.buffer(bufferSize);                           
            buffer = joggSyncState.data;                                        
            if(count == 0 && needMoreData)                                      
            {                                                                   
                System.err.println("Not enough header data was supplied.");     
                return false;                                                   
            }                                                                   
        }                                                                       
	debugOutput("Finished reading the header.");                            
	return true;                                                            
    }                                                                           

    private boolean initializeSound()                                           
    {                                                                           
        debugOutput("Initializing the sound system.");                          
        convertedBufferSize = bufferSize * 2;
        convertedBuffer = new byte[convertedBufferSize];                        
        jorbisDspState.synthesis_init(jorbisInfo);                              
        jorbisBlock.init(jorbisDspState);                                       
        int channels = jorbisInfo.channels;                                     
        int rate = jorbisInfo.rate;                                             
        AudioFormat audioFormat = new AudioFormat((float) rate, 16, channels,   
						  true, false);                                                       
        DataLine.Info datalineInfo = new DataLine.Info(SourceDataLine.class,    
						       audioFormat, AudioSystem.NOT_SPECIFIED);                            
        if(!AudioSystem.isLineSupported(datalineInfo))                          
        {                                                                       
            System.err.println("Audio output line is not supported.");          
            return false;                                                       
        }                                                                       
        try                                                                     
        {                                                                       
            outputLine = (SourceDataLine) AudioSystem.getLine(datalineInfo);    
            outputLine.open(audioFormat);                                       
        }                                                                       
        catch(LineUnavailableException exception)                               
        {                                                                       
            System.out.println("The audio output line could not be opened due " 
			       + "to resource restrictions.");                                 
            System.err.println(exception);                                      
            return false;                                                       
        }                                                                       
        catch(IllegalStateException exception)                                  
        {                                                                       
            System.out.println("The audio output line is already open.");       
            System.err.println(exception);                                      
            return false;                                                       
        }                                                                       
        catch(SecurityException exception)                                      
        {                                                                       
            System.out.println("The audio output line could not be opened due " 
			       + "to security restrictions.");                                 
            System.err.println(exception);                                      
            return false;                                                       
        }                                                                       
        outputLine.start();                                                     
        pcmInfo = new float[1][][];                                             
        pcmIndex = new int[jorbisInfo.channels];                                
	debugOutput("Done initializing the sound system.");                     
	return true;                                                            
    }                                                                           

    private void readBody()                                                     
    {                                                                           
        debugOutput("Reading the body.");                                       
        boolean needMoreData = true;                                            
	while(needMoreData && toContinue)
        {                                                                       
            switch(joggSyncState.pageout(joggPage))                             
            {                                                                   
	    case -1:                                                        
                    debugOutput("There is a hole in the data. We proceed.");    
		    break;
	    case 0:                                                         
                    break;                                                      
	    case 1:                                                         
                {                                                               
                    joggStreamState.pagein(joggPage);                           
                    if(joggPage.granulepos() == 0)                              
                    {                                                           
                        needMoreData = false;                                   
                        break;                                                  
                    }                                                           
                    processPackets:
while(true)
                    {                                                           
                        switch(joggStreamState.packetout(joggPacket))           
                        {                                                       
			case -1:                                            
                            {                                                   
                                debugOutput("There is a hole in the data, we "  
					    + "continue though.");                      
                            }                                                   
			case 0:                                             
                            {                                                   
                                break processPackets;                           
                            }                                                   
			case 1:                                             
                                decodeCurrentPacket();                          
                        }                                                       
                    }                                                           
                    if(joggPage.eos() != 0) 
			needMoreData = false;               
                }                                                               
            }                                                                   
            if(needMoreData)                                                    
            {                                                                   
                index = joggSyncState.buffer(bufferSize);                       
                buffer = joggSyncState.data;                                    
                try {                                                               
                    count = inputStream.read(buffer, index, bufferSize);        
                }                                                               
                catch(Exception e)                                              
                {                                                               
		    e.printStackTrace();
                    return;                                                     
                }                                                               
                joggSyncState.wrote(count);                                     
                if(count == 0) 
		    needMoreData = false;                            
            }                                                                   
        }                                                                       
        debugOutput("Done reading the body.");                                  
    }                                                                           

    private void cleanUp()                                                      
    {                                                                           
        debugOutput("Cleaning up.");                                            
        joggStreamState.clear();                                                
        jorbisBlock.clear();                                                    
        jorbisDspState.clear();                                                 
        jorbisInfo.clear();                                                     
        joggSyncState.clear();                                                  
        try                                                                     
        {                                                                       
            if(inputStream != null) inputStream.close();                        
        }                                                                       
        catch(Exception e)                                                      
        {                                                                       
        }                                                                       
	debugOutput("Done cleaning up.");                                       
    }                                                                           

    private void decodeCurrentPacket()                                          
    {                                                                           
        int samples;                                                            
        if(jorbisBlock.synthesis(joggPacket) == 0)                              
            jorbisDspState.synthesis_blockin(jorbisBlock);                      
        int range;                                                              
        while((samples = jorbisDspState.synthesis_pcmout(pcmInfo, pcmIndex)) > 0)
        {                                                                       
            if(samples < convertedBufferSize)                                   
                range = samples; else
                range = convertedBufferSize;                                    
            for(int i = 0; i < jorbisInfo.channels; i++)                        
            {                                                                   
                int sampleIndex = i * 2;                                        
                for(int j = 0; j < range; j++)                                  
                {                                                               
                    int value = (int) (pcmInfo[0][i][pcmIndex[i] + j] * 32767); 
                    if(value > 32767)                                           
                        value = 32767;                                          
                    if(value < -32768)                                          
                        value = -32768;                                         
                    if(value < 0) 
			value = value | 32768;                        
                    convertedBuffer[sampleIndex] = (byte) (value);              
                    convertedBuffer[sampleIndex + 1] = (byte) (value >>> 8);    
                    sampleIndex += 2 * (jorbisInfo.channels);                   
                }                                                               
            }                                                                   
	    if (toContinue)
            outputLine.write(convertedBuffer, 0, 2 * jorbisInfo.channels * range);
            jorbisDspState.synthesis_read(range);                               
	}
    }                                                                           

    private void debugOutput(String output)                                     
    {                                                                           
        if(debugMode) System.out.println("Debug: " + output);                   
    }                                                                           
}                                                                               

