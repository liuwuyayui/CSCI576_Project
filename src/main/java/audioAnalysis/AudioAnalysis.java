package audioAnalysis;

import javaWavFileIO.WavFile;
import javaWavFileIO.WavFileException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

  public static final int BITS_PER_SAMPLE = 16;
  private static final double MAX_16_BIT = 32768;

  public static List<Double> read(String filename) throws IOException, UnsupportedAudioFileException, UnsupportedAudioFileException {
    File file = new File(filename);
    AudioInputStream ais = AudioSystem.getAudioInputStream(file);
    AudioFormat audioFormat = ais.getFormat();

    byte[] bytes = null;
    int bytesToRead = ais.available();
    bytes = new byte[bytesToRead];
    int bytesRead = ais.read(bytes);
    int n = bytes.length;

    double[] data = new double[n/4];
    for(int i = 0; i < n/4; i++) {
      double left = ((short) (((bytes[4 * i + 1] & 0xFF) << 8) | (bytes[4 * i + 0] & 0xFF))) / ((double) MAX_16_BIT);
      double right = ((short) (((bytes[4 * i + 3] & 0xFF) << 8) | (bytes[4 * i + 2] & 0xFF))) / ((double) MAX_16_BIT);
      data[i] = (left + right) / 2.0;
      //System.out.println(data[i]);
    }
    double total = 0;

    List<Double> fResult = new ArrayList<>();
    for(int j = 0; j < data.length; j++){
//      if(j % 1600 == 0){
//        System.out.println(j+" "+total/1600);
//        fResult.add(total/1600);
//        total = 0;
//      }else{
//        total += data[j];
//      }

      if(j % 48000 == 0){
        System.out.println(j/48000+" "+total/48000);
        fResult.add(total/48000);
        total = 0;
      }else {
        total += data[j];
      }
    }
    Collections.sort(fResult);
    System.out.println(fResult);
    return fResult;
  }
  
  public static void main(String[] args) throws IOException, WavFileException, UnsupportedAudioFileException {
    String path = "/Users/ziqinghuang/Documents/project_dataset/audio/meridian.wav";
    AudioAnalysis.read(path);

//    String path = "/Users/daddy/Movies/project_dataset/audio/steel.wav";
//    AudioAnalysis audioAnalysis = new AudioAnalysis();
//    for (int i = 0; i < 16200; i += 200) {
//      audioAnalysis.normalizedVolume(path, i, i + 99, i);
//    }
  }
}
