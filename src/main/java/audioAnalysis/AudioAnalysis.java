package audioAnalysis;

import javaWavFileIO.WavFile;
import javaWavFileIO.WavFileException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AudioAnalysis {
  public static final int FRAME_RATE = 30;
  public static final int SAMPLE_RATE_PER_SECOND = 48000;
  public static final int SAMPLE_RATE_PER_MILLI = 48;
  
  private static List<Long> rmsVols(String wavPath) throws IOException, WavFileException {
    List<Long> result = new ArrayList<>();
    
    // Open the wav file specified as the first argument
    WavFile wavFile = WavFile.openWavFile(new File(wavPath));
    
    // Display information about the wav file
//    wavFile.display();
    
    // Get the number of audio channels in the wav file
    int numChannels = wavFile.getNumChannels();
    
    // Create a buffer frames
    int bufferSize = 1000 / FRAME_RATE * SAMPLE_RATE_PER_MILLI;
    int[] buffer = new int[bufferSize * numChannels];
    
    int framesRead;
    
    while (true) {
      long rms = 0;
      // Read frames into buffer
      framesRead = wavFile.readFrames(buffer, bufferSize);
      if (framesRead == 0) {
        break;
      }
      // Loop through frames and look for minimum and maximum value
      for (int s = 0; s < framesRead * numChannels; s += 2) {
        rms += (long) buffer[s] * buffer[s];
      }
      rms = (long) Math.sqrt(rms / framesRead);
//      System.out.println(rms);
      result.add(rms);
    }
    
    
    // Close the wavFile
    wavFile.close();
    
    return result;
  }
  
  /**
   *
   * @param wavPath Path of wav file
   * @param startFrame Start frame of the shot to be analysed
   * @param endFrame End frame of the shot to be analysed
   * @return relative RMS volume of the given shot
   * @throws IOException
   * @throws WavFileException
   */
  public static long normalizedVolume(String wavPath, int startFrame, int endFrame, int index) throws IOException, WavFileException {
    if (startFrame > endFrame) {
      throw new IllegalArgumentException("Endframe must greater than startFrame");
    }
    long result = 0;
    List<Long> rmsVols = rmsVols(wavPath);
    for (int i = startFrame; i <= endFrame; i++) {
      long rmsVol = rmsVols.get(i);
      result += rmsVol * rmsVol;
    }
    
    result = (long) Math.sqrt(result / (endFrame - startFrame));
    
    //System.out.printf("Normalized Volume = %d for Shot[%d, %d]\n", result, startFrame, endFrame);
    System.out.print("\rShots Index: "+index);
    return result;
  }
  
  public static void main(String[] args) throws IOException, WavFileException {
    String path = "/Users/daddy/Movies/project_dataset/audio/steel.wav";
    AudioAnalysis audioAnalysis = new AudioAnalysis();
    for (int i = 0; i < 16200; i += 200) {
      audioAnalysis.normalizedVolume(path, i, i + 99, i);
    }
  }
}
