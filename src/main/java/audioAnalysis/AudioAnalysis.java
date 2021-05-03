package audioAnalysis;

import javaWavFileIO.WavFile;
import javaWavFileIO.WavFileException;

import java.io.File;
import java.io.IOException;

public class AudioAnalysis {
  public static final int FRAME_RATE = 30;
  public static final int SAMPLE_RATE_PER_SECOND = 48000;
  public static final int SAMPLE_RATE_PER_MILLI = 48;
  public static final int BUFFER_SIZE = 96;
  
  private long volumeSum(String wavPath, int startFrame, int endFrame) throws IOException, WavFileException {
    int startTime = (int) Math.round(startFrame * 1000.0 / FRAME_RATE);  // in millis
    int endTime = (int) Math.round(endFrame * 1000.0 / FRAME_RATE);

    
    
    int startWavFrame = startTime * SAMPLE_RATE_PER_MILLI;
    int endWavFrame = endTime * SAMPLE_RATE_PER_MILLI;
    int leftWavFrameLength = endWavFrame - startFrame;
    
//    System.out.printf("Shot starts at %d ms, ends at %d ms\n", startTime, endTime);
//    System.out.printf(".wav starts at %d frame, ends at %d frame\n", startWavFrame, endWavFrame);
    
    // open wav file
    WavFile wavFile = WavFile.openWavFile(new File(wavPath));
    
    // Display information about the wav file
//    wavFile.display();
    
    // Get the number of audio channels in the wav file
    int numChannels = wavFile.getNumChannels();
    
    // Create a buffer of 96 frames
    int[] buffer = new int[BUFFER_SIZE * numChannels];
  
    long rmsSum = 0;
    
    while (leftWavFrameLength > 0) {
      int rms = 0;
      int framesRead = 0;
      if (leftWavFrameLength >= BUFFER_SIZE) {
        framesRead = wavFile.readFrames(buffer, BUFFER_SIZE);
      } else {
        framesRead = wavFile.readFrames(buffer, leftWavFrameLength);
      }
      leftWavFrameLength -= framesRead;
      // Loop through frames and look for minimum and maximum value
      for (int s = 0; s < framesRead * numChannels; s += 2) {
        rms += buffer[s] * buffer[s];
      }
      rms = (int) Math.round(Math.sqrt(rms / framesRead));
//      System.out.println(rms);
      rmsSum += rms;
    }
    
    // Close the wavFile
    wavFile.close();
//    System.out.println("Long.MAX_VALUE: " + Long.MAX_VALUE);
//    System.out.println("Total Volume RMS Sum: " + rmsSum);
    return rmsSum;
  }
  
  public long normalizedVolume(String wavPath, int startFrame, int endFrame) throws IOException, WavFileException {
    if (startFrame > endFrame) {
      throw new IllegalArgumentException("Endframe must greater than startFrame");
    }
    long sum = volumeSum(wavPath, startFrame, endFrame);
    long result = sum / (endFrame - startFrame);
    System.out.printf("Normalized Volume = %d for Shot[%d, %d]\n", result, startFrame, endFrame);
    return result;
  }
  
  public static void main(String[] args) throws IOException, WavFileException {
    String path = "/Users/daddy/Movies/project_dataset/audio/meridian.wav";
    AudioAnalysis audioAnalysis = new AudioAnalysis();
    audioAnalysis.normalizedVolume(path, 0, 100);
    audioAnalysis.normalizedVolume(path, 101, 200);
    audioAnalysis.normalizedVolume(path, 201, 300);
    audioAnalysis.normalizedVolume(path, 500, 1000);
  }
}
