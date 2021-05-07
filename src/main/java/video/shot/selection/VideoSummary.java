package video.shot.selection;

import analysis.HueSaturationValue;
import analysis.MotionVector;
import analysis.MotionVectorAndHSV;
import audioAnalysis.AudioAnalysis;
import javaWavFileIO.WavFileException;
import video.shot.segmentation.CSCI576VideoShotSegmentationProject;

import java.io.IOException;
import java.util.*;

public class VideoSummary {
  public static int width = 320;
  public static int height = 180;
  public static int totalFrames = 16200;
  public static int minimumFramesPerShot = 90; // ~ 3 second
  public static int summaryFramesLimit = 2700;
  
  public static double motionWeight = 1;
  public static double hueWeight = 1;
  public static double saturationWeight = 1;
  public static double valueBrightnessWeight = 1;
  public static double audioAmplitudeWeight = 1;
  
  public static double audioFrequencyWeight = 1;
  //public static String myRGBFramesFolderPath = "/Users/daddy/Movies/project_dataset/frames_rgb/concert";
  //public static String myRGBFramesFolderPath = "/Users/edmondsitu/Desktop/project_dataset/frames_rgb/concert";
  public static String myRGBFramesFolderPath = "/Users/edmondsitu/Desktop/test_data/frames_rgb/test1";
  //public static String myWAVPath = "/Users/edmondsitu/Desktop/project_dataset/audio/concert.wav";
  public static String myWAVPath = "/Users/edmondsitu/Desktop/test_data/audio/test1.wav";
  
  public static void main(String[] args) throws IOException, WavFileException {
        /*
        System.out.println("Processing video shot segmentation of all frames: "+myRGBFramesFolderPath);
        int[][] videoShots = CSCI576VideoShotSegmentationProject.videoShotSegmentationColorSpaceHistogram(width, height, totalFrames, myRGBFramesFolderPath);
        System.out.print("Video Shots: ");
        CSCI576VideoShotSegmentationProject.printArray(videoShots);
        System.out.println("Number of Shots: "+videoShots.length);
        System.out.println("\nProcessing motion vector scoring for all shots...");
        double[] normalizedMotionVectorScores = MotionVector.normalizeMotionVectorScore(MotionVector.motionVectorOfFrameShots(videoShots), videoShots);
        System.out.println("\nProcessing HSV scoring for all shots...");
        // (0 for saturation score and 1 for value/brightness score, shot index)
        double[][] normalizedHueSaturationValueScores = HueSaturationValue.normalizedHueSaturationValueScores(HueSaturationValue.hueSaturationValueOfFrameShots(videoShots), videoShots);
        */
    long startTime = System.currentTimeMillis();   //获取开始时间
    ArrayList<int[]> result = videoSummaryShots(width, height, totalFrames, minimumFramesPerShot, summaryFramesLimit, myRGBFramesFolderPath, myWAVPath);
    long endTime = System.currentTimeMillis(); //获取结束时间
    System.out.println("程序运行时间： " + (endTime - startTime) / 1000 + "s");
  }
  
  public static ArrayList<int[]> videoSummaryShots(String myRGBFramesFolderPath, String myWAVPath) throws IOException, WavFileException {
    return videoSummaryShots(width, height, totalFrames, minimumFramesPerShot, summaryFramesLimit, myRGBFramesFolderPath, myWAVPath);
  }
  
  public static ArrayList<int[]> videoSummaryShots(int width, int height, int totalFrames, int minimumFramesPerShot, int summaryFramesLimit, String myRGBFramesFolderPath, String myWAVPath) throws IOException, WavFileException {
    HashMap<Double, int[]> videoShotsHashMap = new HashMap<>();
    System.out.println("Processing video shot segmentation of all frames: " + myRGBFramesFolderPath);
    // (shot index, begin and end frame of shot)
    int[][] videoShots = CSCI576VideoShotSegmentationProject.videoShotSegmentationColorSpaceHistogram(width, height, totalFrames, minimumFramesPerShot, myRGBFramesFolderPath);
        /*
        System.out.print("Video Shots: ");
        CSCI576VideoShotSegmentationProject.printArray(videoShots);
        System.out.println("Number of Shots: "+videoShots.length);
        */

    // Combine motion vector and HSV
    // [0 for motion vector and 1 for HSV][video shots index]
    double[] motionVectorScores = new double[videoShots.length];
    // (0 for hue and 1 for saturation score and 2 for value/brightness score, number of shots)
    double[][] hueSaturationValueScores = new double[3][videoShots.length];
    MotionVectorAndHSV.calculateMotionVectorAndHSVScores(width, height, totalFrames, videoShots, motionVectorScores, hueSaturationValueScores, myRGBFramesFolderPath);

    System.out.println("\n\nProcessing motion vector scoring for all shots...");
    //double[] normalizedMotionVectorScores = MotionVector.normalizeMotionVectorScore(MotionVector.motionVectorOfFrameShots(width, height, videoShots, myRGBFramesFolderPath), videoShots);
    double[] normalizedMotionVectorScores = MotionVector.normalizeMotionVectorScore(motionVectorScores, videoShots, motionWeight);
    System.out.println("\nProcessing HSV scoring for all shots...");
    // (0 for hue and 1 for saturation score and 2 for value/brightness score, shot index)
    //double[][] normalizedHueSaturationValueScores = HueSaturationValue.normalizedHueSaturationValueScores(HueSaturationValue.hueSaturationValueOfFrameShots(width, height, videoShots, myRGBFramesFolderPath), videoShots);
    double[][] normalizedHueSaturationValueScores = HueSaturationValue.normalizedHueSaturationValueScores(hueSaturationValueScores, videoShots, hueWeight, saturationWeight, valueBrightnessWeight);
    System.out.println("\nProcessing Audio scoring for all shots...");
    // Audio
    double[] normalizedAudioScores = new double[videoShots.length];
    normalizedAudioScores[0] = (double)AudioAnalysis.normalizedVolume(myWAVPath, videoShots[0][0], videoShots[0][1], 0);
    double maxAudioScore = normalizedAudioScores[0];
    // Compute audio scores and maximum audio score
    for(int i = 1; i < normalizedAudioScores.length; i++){
      normalizedAudioScores[i] = (double)AudioAnalysis.normalizedVolume(myWAVPath, videoShots[i][0], videoShots[i][1], i);
      if(normalizedAudioScores[i] > maxAudioScore){
        maxAudioScore = normalizedAudioScores[i];
      }
    }
    // Normalize audio scores between 0 and 1
    for(int i = 0; i < normalizedAudioScores.length; i++){
      normalizedAudioScores[i] = normalizedAudioScores[i]/maxAudioScore*audioAmplitudeWeight;
    }
    //System.out.println("Normalized Audio Scores: "+Arrays.toString(normalizedAudioScores));
    Double[] analysisScores = new Double[videoShots.length];
    // Initialize all analysis scores to 0
    for (int i = 0; i < analysisScores.length; i++) {
      analysisScores[i] = 0.0;
    }
    // Sum all analysis scores
    for (int i = 0; i < videoShots.length; i++) {
      analysisScores[i] = analysisScores[i] + normalizedMotionVectorScores[i] + normalizedHueSaturationValueScores[0][i] + normalizedHueSaturationValueScores[1][i] + normalizedHueSaturationValueScores[2][i] + normalizedAudioScores[i];
    }
    // Hash map all video shots
    for (int i = 0; i < videoShots.length; i++) {
      videoShotsHashMap.put(analysisScores[i], videoShots[i]);
    }
    // Analysis scores in descending order
    Arrays.sort(analysisScores, Collections.reverseOrder());
    // Select shots sum up to n frames
    //int framesLimit = 300; // 90 seconds
    int maximumFramesOverLimit = 150; // 5 seconds
    int numFrames = 0;
    int shotIndex = 0;
    ArrayList<int[]> importantShots = new ArrayList<>();
    while (numFrames < summaryFramesLimit && shotIndex < videoShots.length) {
      int[] correspondingVideoShot = videoShotsHashMap.get(analysisScores[shotIndex]);
      int framesInShot = (correspondingVideoShot[1] - correspondingVideoShot[0] + 1);
      if (numFrames + framesInShot <= summaryFramesLimit + maximumFramesOverLimit) {
        importantShots.add(correspondingVideoShot);
        numFrames += framesInShot;
      }
      shotIndex++;
    }
    // Print all video shots
    System.out.print("\n\nVideo Shots: ");
    CSCI576VideoShotSegmentationProject.printArray(videoShots);
    System.out.println("Number of Shots: " + videoShots.length);
    // Print important shots and corresponding analysis value
    System.out.println("\nSummary Important Shots: ");
    for (int i = 0; i < importantShots.size(); i++) {
      int[] shot = importantShots.get(i);
      System.out.println("{" + shot[0] + ", " + shot[1] + "} = total: " + analysisScores[i] + " = motion: " + normalizedMotionVectorScores[i] + " + hue: " + normalizedHueSaturationValueScores[0][i] + " + saturation: " + normalizedHueSaturationValueScores[1][i] + " + brightness: " + normalizedHueSaturationValueScores[2][i] + " + audio: " + normalizedAudioScores[i]);
    }
    System.out.println("Number of Summary Important Shots: " + importantShots.size());
    System.out.println("Number of Frames For Summary Important Shots: " + numFrames);
    importantShots.sort(Comparator.comparingInt(array->array[0]));
    System.out.print("\nImportant Shots: ");
    CSCI576VideoShotSegmentationProject.printArrayList(importantShots);
    return importantShots;
  }
  
}
