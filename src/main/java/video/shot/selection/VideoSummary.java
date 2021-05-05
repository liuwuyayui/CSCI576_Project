package video.shot.selection;

import analysis.HueSaturationValue;
import analysis.MotionVector;
import video.shot.segmentation.CSCI576VideoShotSegmentationProject;

import java.util.*;

public class VideoSummary {
  public static int width = 320;
  public static int height = 180;
  public static int totalFrames = 16200;
  public static String myRGBFramesFolderPath = "/Users/daddy/Movies/project_dataset/frames_rgb/concert";
  
  public static void main(String[] args) {
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
    ArrayList<int[]> result = videoSummaryShots();
    long endTime = System.currentTimeMillis(); //获取结束时间
    System.out.println("程序运行时间： " + (endTime - startTime) + "ms");
    System.out.println("程序运行时间： " + (endTime - startTime) / 1000 + "s");
  }
  
  public static ArrayList<int[]> videoSummaryShots() {
    HashMap<Double, int[]> videoShotsHashMap = new HashMap<>();
    System.out.println("Processing video shot segmentation of all frames: " + myRGBFramesFolderPath);
    // (shot index, begin and end frame of shot)
    int[][] videoShots = CSCI576VideoShotSegmentationProject.videoShotSegmentationColorSpaceHistogram(width, height, 1000, myRGBFramesFolderPath);
        /*
        System.out.print("Video Shots: ");
        CSCI576VideoShotSegmentationProject.printArray(videoShots);
        System.out.println("Number of Shots: "+videoShots.length);
        */
    System.out.println("\n\nProcessing motion vector scoring for all shots...");
    double[] normalizedMotionVectorScores = MotionVector.normalizeMotionVectorScore(MotionVector.motionVectorOfFrameShots(videoShots), videoShots);
    System.out.println("\nProcessing HSV scoring for all shots...");
    // (0 for saturation score and 1 for value/brightness score, shot index)
    double[][] normalizedHueSaturationValueScores = HueSaturationValue.normalizedHueSaturationValueScores(HueSaturationValue.hueSaturationValueOfFrameShots(videoShots), videoShots);
    Double[] analysisScores = new Double[videoShots.length];
    // Initialize all analysis scores to 0
    for (int i = 0; i < analysisScores.length; i++) {
      analysisScores[i] = 0.0;
    }
    // Sum all analysis scores
    for (int i = 0; i < videoShots.length; i++) {
      analysisScores[i] = analysisScores[i] + normalizedMotionVectorScores[i] + normalizedHueSaturationValueScores[0][i] + normalizedHueSaturationValueScores[0][i];
    }
    // Hash map all video shots
    for (int i = 0; i < videoShots.length; i++) {
      videoShotsHashMap.put(analysisScores[i], videoShots[i]);
    }
    // Analysis scores in descending order
    Arrays.sort(analysisScores, Collections.reverseOrder());
    // Select shots sum up to n frames
    int framesLimit = 300; // 90 seconds
    int maximumFramesOverLimit = 150; // 5 seconds
    int numFrames = 0;
    int shotIndex = 0;
    ArrayList<int[]> importantShots = new ArrayList<>();
    while (numFrames < framesLimit && shotIndex < videoShots.length) {
      int[] correspondingVideoShot = videoShotsHashMap.get(analysisScores[shotIndex]);
      int framesInShot = (correspondingVideoShot[1] - correspondingVideoShot[0] + 1);
      if (numFrames + framesInShot <= framesLimit + maximumFramesOverLimit) {
        importantShots.add(correspondingVideoShot);
        numFrames += framesInShot;
      }
      shotIndex++;
    }
    // Print all video shots
    System.out.print("\nVideo Shots: ");
    CSCI576VideoShotSegmentationProject.printArray(videoShots);
    System.out.println("Number of Shots: " + videoShots.length);
    // Print important shots and corresponding analysis value
    System.out.println("\nSummary Important Shots: ");
    for (int i = 0; i < importantShots.size(); i++) {
      int[] shot = importantShots.get(i);
      System.out.println("[" + shot[0] + ", " + shot[1] + "] = " + analysisScores[i]);
    }
    System.out.println("Number of Summary Important Shots: " + importantShots.size());
    System.out.println("Number of Frames For Summary Important Shots: " + numFrames);
    importantShots.sort(Comparator.comparingInt(array->array[0]));
    return importantShots;
  }
  
}
