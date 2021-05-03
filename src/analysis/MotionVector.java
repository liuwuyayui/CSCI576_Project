package analysis;

import video.shot.segmentation.CSCI576VideoShotSegmentationProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;

public class MotionVector {
    public static int width = CSCI576VideoShotSegmentationProject.width;
    public static int height = CSCI576VideoShotSegmentationProject.height;
    public static int totalFrames = CSCI576VideoShotSegmentationProject.totalFrames;
    public static String myRGBFramesFolderPath = CSCI576VideoShotSegmentationProject.myRGBFramesFolderPath;

    public static void main(String[] args){
        System.out.println("Processing motion vector scoring for all shots...");
        int[][] videoShots = CSCI576VideoShotSegmentationProject.videoShotSegmentationColorSpaceHistogram(width, height, totalFrames, myRGBFramesFolderPath);
        double[] motionVectorScores = motionVectorOfFrameShots(videoShots);
        for(int i = 0; i < motionVectorScores.length; i++){
            motionVectorScores[i] = Math.round(motionVectorScores[i]*100.0)/100.0;
        }
        System.out.println("Motion Vector Scores Before Normalize: "+Arrays.toString(motionVectorScores));
        for(int i = 0; i < motionVectorScores.length; i++){
            motionVectorScores[i] = Math.round(motionVectorScores[i]/((videoShots[i][1]-videoShots[i][0])+1)*100.0)/100.0;
        }
        System.out.println("Motion Vector Scores After Normalizing to Each Shot: "+Arrays.toString(motionVectorScores));
        double maxScore = motionVectorScores[0];
        double minScore = motionVectorScores[0];
        for(int i = 1; i < motionVectorScores.length; i++){
            if(motionVectorScores[i] > maxScore){
                maxScore = motionVectorScores[i];
            }
            if(motionVectorScores[i] < minScore){
                minScore = motionVectorScores[i];
            }
        }
        System.out.println("Max Score: "+maxScore);
        System.out.println("Min Score: "+minScore);
        for(int i = 0; i < motionVectorScores.length; i++){
            motionVectorScores[i] = Math.round(motionVectorScores[i]/maxScore*100.0)/100.0;
        }
        System.out.println("Motion Vector Scores After Normalizing to Maximum Score: "+Arrays.toString(motionVectorScores));
        System.out.println("Number of Motion Vector Scores:"+motionVectorScores.length);
        System.out.println("Complete");
    }

    public static double[] motionVectorOfFrameShots(int[][] videoShots){
        System.out.print("Video Shots: ");
        CSCI576VideoShotSegmentationProject.printArray(videoShots);
        System.out.println("Number of Shots: "+videoShots.length);
        double[] motionVectorScores = new double[videoShots.length];

        // Process each shot
        for(int i = 0; i < videoShots.length; i++){
            System.out.print("\rShots Index: "+i);
            motionVectorScores[i] = calculateMotionVectorScore(videoShots[i][0], videoShots[i][1]);
        }
        System.out.println();

        return motionVectorScores;
    }

    public static double calculateMotionVectorScore(int beginFrame, int endFrame){
        double motionVectorScore = 0;
        double[][] blockMovement = new double[3][5]; // 15 blocks
        // (frame index, block x-coordinate, block y-coordinate, pixel x-y coordinate)
        int[][][][] blockPositions = new int[endFrame-beginFrame+1][3][5][2]; // In each frame

        // Initialize position of blocks on the first frame
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 5; j++) {
                // Position of each block on each frame
                blockPositions[0][i][j][0] = i;
                blockPositions[0][i][j][1] = j;
            }
        }
        //System.out.print("Block Position: ");
        //printBlockArray(blockPosition[0]);

        // [x][y][frame][rgb]
        int[][][][] intRGBFramePair;
        for(int i = beginFrame; i < endFrame; i++) {
            intRGBFramePair = CSCI576VideoShotSegmentationProject.readFrameRGBForPair(width, height, i, myRGBFramesFolderPath);
            // Continue logic here
            blockPositions[(i-beginFrame)+1] = determinePositionOfEachBlockOnNextFrame(intRGBFramePair, blockPositions[i-beginFrame]);
        }

        // Calculate displacement of each block through the shot
        for(int i = 0; i < blockPositions.length-1; i++){
            for(int j = 0; j < 3; j++){
                for(int k = 0; k < 5; k++) {
                    motionVectorScore += calculateDisplacement(blockPositions[i][j][k][0], blockPositions[i][j][k][1], blockPositions[i+1][j][k][0], blockPositions[i+1][j][k][0]);
                }
            }
        }

        return motionVectorScore;
    }

    public static int[][][] determinePositionOfEachBlockOnNextFrame(int[][][][] intRGBFramePairs, int[][][] currentFrameBlockPositions) {
        int[][][] nextFrameBlockPosition = new int[3][5][2];

        // (2, 3, 5, 256, 3) = (2 frames, 256 color quantization, 0 for R and 1 for G and 2 for B)
        int[][][][][] intRGBFramePairHistogram = new int[2][3][5][256][3];

        // Map each pixel RGB values of each block to histogram
        // Each block
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 5; j++) {
                // Each pixel in block
                for (int k = (i*60); k < ((i+1)*60); k++) {
                    for (int l = (j*64); l < ((j+1)*64); l++) {
                        // Frame 1
                        intRGBFramePairHistogram[0][i][j][intRGBFramePairs[k][l][0][0]][0]++;
                        intRGBFramePairHistogram[0][i][j][intRGBFramePairs[k][l][0][1]][1]++;
                        intRGBFramePairHistogram[0][i][j][intRGBFramePairs[k][l][0][2]][2]++;

                        // Frame 2
                        intRGBFramePairHistogram[1][i][j][intRGBFramePairs[k][l][1][0]][0]++;
                        intRGBFramePairHistogram[1][i][j][intRGBFramePairs[k][l][1][1]][1]++;
                        intRGBFramePairHistogram[1][i][j][intRGBFramePairs[k][l][1][2]][2]++;
                    }
                }
            }
        }

        // For each block on first frame, find the block's position on the next frame
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 5; j++){
                int[] position = findPositionOfMostSimilarColorHistogramOnNextFrame(intRGBFramePairHistogram, currentFrameBlockPositions[i][j][0], currentFrameBlockPositions[i][j][1]);
                nextFrameBlockPosition[i][j][0] = position[0];
                nextFrameBlockPosition[i][j][1] = position[1];
            }
        }

        return nextFrameBlockPosition;
    }

    public static int[] findPositionOfMostSimilarColorHistogramOnNextFrame(int[][][][][] intRGBFramePairsHistogram, int blockX, int blockY){
        int[] position = new int[2];
        int sumAbsoluteDifferenceR = 0;
        int sumAbsoluteDifferenceG = 0;
        int sumAbsoluteDifferenceB = 0;
        int sumAbsoluteDifferenceRGB;
        int minimumAbsoluteDifference;

        // Set first block as minimum absolute difference block first
        for(int i = 0; i < intRGBFramePairsHistogram[0][0][0].length; i++) {
            sumAbsoluteDifferenceR += Math.abs(intRGBFramePairsHistogram[0][blockX][blockY][i][0] - intRGBFramePairsHistogram[1][0][0][i][0]);
            sumAbsoluteDifferenceG += Math.abs(intRGBFramePairsHistogram[0][blockX][blockY][i][1] - intRGBFramePairsHistogram[1][0][0][i][1]);
            sumAbsoluteDifferenceB += Math.abs(intRGBFramePairsHistogram[0][blockX][blockY][i][2] - intRGBFramePairsHistogram[1][0][0][i][2]);
        }
        minimumAbsoluteDifference = sumAbsoluteDifferenceR+sumAbsoluteDifferenceG+sumAbsoluteDifferenceB;
        sumAbsoluteDifferenceR = 0;
        sumAbsoluteDifferenceG = 0;
        sumAbsoluteDifferenceB = 0;

        // Loop through each block of next frame
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 5; j++){
                // Each of 256 colors in histogram
                for(int k = 0; k < intRGBFramePairsHistogram[0][0][0].length; k++) {
                    sumAbsoluteDifferenceR += Math.abs(intRGBFramePairsHistogram[0][blockX][blockY][k][0] - intRGBFramePairsHistogram[1][i][j][k][0]);
                    sumAbsoluteDifferenceG += Math.abs(intRGBFramePairsHistogram[0][blockX][blockY][k][1] - intRGBFramePairsHistogram[1][i][j][k][1]);
                    sumAbsoluteDifferenceB += Math.abs(intRGBFramePairsHistogram[0][blockX][blockY][k][2] - intRGBFramePairsHistogram[1][i][j][k][2]);
                }
                sumAbsoluteDifferenceRGB = sumAbsoluteDifferenceR+sumAbsoluteDifferenceG+sumAbsoluteDifferenceB;
                if(sumAbsoluteDifferenceRGB < minimumAbsoluteDifference){
                    minimumAbsoluteDifference = sumAbsoluteDifferenceRGB;
                    position[0] = i;
                    position[1] = j;
                }
                sumAbsoluteDifferenceR = 0;
                sumAbsoluteDifferenceG = 0;
                sumAbsoluteDifferenceB = 0;
            }
        }

        return position;
    }

    /*
    public static int[][][][] readFramesRGBForShot(int width, int height, int beginFrame, int endFrame, String RGBFramesFolderPath){
        // [x][y][frame][rgb]
        int[][][][] intRGBFramesInShot = new int[180][180][endFrame-beginFrame+1][3];

        File file;
        RandomAccessFile raf;
        int frameLength = width*height*3;
        long len = frameLength;
        byte[] bytes;

        try{
            // Process frames in shot

            //if(firstFrame%100 == 0) {
                //System.out.println("Frames: [" + firstFrame + ", " + (firstFrame + 1) + "]");
                //System.out.println("Frames Index: "+firstFrame);
                //System.out.print("\rFrames Index: "+firstFrame);
            //}

            for(int k = beginFrame; k <= endFrame; k++) {
                file = new File(RGBFramesFolderPath + "/frame" + k + ".rgb");
                raf = new RandomAccessFile(file, "r");
                raf.seek(0);
                bytes = new byte[(int) len];
                raf.read(bytes);

                // Single byte buffer to RGB 2D arrays
                // (R, G, B) = (0, 1, 2)
                int index = 0;
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        if(j >= 70 && j < 250) {
                            intRGBFramesInShot[i][j][k][0] = bytes[index] & 0xff;
                            intRGBFramesInShot[i][j][k][1] = bytes[index + height * width] & 0xff;
                            intRGBFramesInShot[i][j][k][2] = bytes[index + height * width * 2] & 0xff;
                        }
                        index++;
                    }
                }
            }
            //System.out.println("("+intRGBFramePair[0][0][0][0]+", "+intRGBFramePair[0][0][0][1]+", "+intRGBFramePair[0][0][0][2]+")");
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return intRGBFramesInShot;
    }
    */

    /*
    public static int[][][][][] divideFramePairIntoBlocks(){ // 16 blocks each frame
        // (height, width, frame 0 or 1, block 0 to 15, RGB 0 or 1 or 2)
        int[][][][][] framePairBlocks = new int[height][width][2][16][3];


        return framePairBlocks;
    }
    */

    public static double calculateDisplacement(int x1, int y1, int x2, int y2){
        return Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2));
    }

    public static void printBlockArray(int[][][] myArray) {
        for (int i = 0; i < 4; i++){
            System.out.print("[");
            for(int j = 0; j < 4; j++){
                System.out.print("["+myArray[i][j][0]+", "+myArray[i][j][1]+"], ");
            }
            System.out.println("]");
        }
    }
}
