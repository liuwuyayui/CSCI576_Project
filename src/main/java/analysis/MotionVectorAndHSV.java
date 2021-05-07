package analysis;

import video.shot.segmentation.CSCI576VideoShotSegmentationProject;
import video.shot.selection.VideoSummary;

public class MotionVectorAndHSV {
    public static void main(String[] args){

        int width = VideoSummary.width;
        int height = VideoSummary.height;
        int totalFrames = VideoSummary.totalFrames;
        int minimumFramesPerShot = VideoSummary.minimumFramesPerShot;
        String myRGBFramesFolderPath = VideoSummary.myRGBFramesFolderPath;
        System.out.println("Processing motion vector and HSV scoring for all shots...");
        int[][] videoShots = CSCI576VideoShotSegmentationProject.videoShotSegmentationColorSpaceHistogram(width, height, totalFrames, minimumFramesPerShot, myRGBFramesFolderPath);
        // [0 for motion vector and 1 for HSV][video shots index]
        double[] motionVectorScores = new double[videoShots.length];
        // (0 for hue and 1 for saturation score and 2 for value/brightness score, number of shots)
        double[][] hueSaturationValueScores = new double[3][videoShots.length];
        calculateMotionVectorAndHSVScores(width, height, totalFrames, videoShots, motionVectorScores, hueSaturationValueScores, myRGBFramesFolderPath);
        //printStuff();
    }

    public static void calculateMotionVectorAndHSVScores(int width, int height, int totalFrames, int[][] videoShots, double[] motionVectorScores, double[][] hueSaturationValueScores, String myRGBFramesFolderPath){
        // [x][y][rgb]
        //int[][][] intRGBFrameSingleFrame = new int[height][width][3];


        // Process each shot
        for(int l = 0; l < videoShots.length; l++){
            System.out.print("\rShots Index: "+l);
            // Motion vector analysis
            double motionVectorScore = 0;
            double[][] blockMovement = new double[3][5]; // 15 blocks
            // (frame index, block x-coordinate, block y-coordinate, pixel x-y coordinate)
            int[][][][] blockPositions = new int[videoShots[l][1]-videoShots[l][0]+1][3][5][2]; // In each frame
            // HSV analysis
            double[] hueSaturationValueScore = new double[3]; // 0 for hue and 1 for saturation score 2 for value/brightness score

            // Initialize position of blocks on the first frame
            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 5; j++) {
                    // Position of each block on each frame
                    blockPositions[0][i][j][0] = i;
                    blockPositions[0][i][j][1] = j;
                }
            }
            // [x][y][frame][rgb]
            int[][][][] intRGBFramePair;
            int index = 0;
            for(int i = videoShots[l][0]; i < videoShots[l][1]; i++) {
                intRGBFramePair = CSCI576VideoShotSegmentationProject.readFrameRGBForPair(width, height, i, myRGBFramesFolderPath);
                // Motion vector analysis
                blockPositions[(i - videoShots[l][0]) + 1] = MotionVector.determinePositionOfEachBlockOnNextFrame(intRGBFramePair, blockPositions[i - videoShots[l][0]]);
                // HSV analysis
                if(index%2 == 0){
                    double[][][] doubleOriginalHSV1 = new double[height][width][3];
                    CSCI576VideoShotSegmentationProject.RGBtoHSVWithAnalysis(width, height, intRGBFramePair, doubleOriginalHSV1, 0);
                    double[][][] doubleOriginalHSV2 = new double[height][width][3];
                    CSCI576VideoShotSegmentationProject.RGBtoHSVWithAnalysis(width, height, intRGBFramePair, doubleOriginalHSV2, 1);
                    for(int j = 0; j < doubleOriginalHSV1.length; j++){
                        for(int k = 0; k < doubleOriginalHSV1[0].length; k++){

                            hueSaturationValueScore[0]+=(doubleOriginalHSV1[j][k][0]+doubleOriginalHSV2[j][k][0]);
                            hueSaturationValueScore[1]+=(doubleOriginalHSV1[j][k][1]+doubleOriginalHSV2[j][k][1]);
                            hueSaturationValueScore[2]+=(doubleOriginalHSV1[j][k][2]+doubleOriginalHSV2[j][k][2]);
                            hueSaturationValueScore[2]+=(Math.sqrt(0.241*intRGBFramePair[j][k][0][0]+0.691*intRGBFramePair[j][k][0][1]+0.068*intRGBFramePair[j][k][0][2])+Math.sqrt(0.241*intRGBFramePair[j][k][1][0]+0.691*intRGBFramePair[j][k][1][1]+0.068*intRGBFramePair[j][k][1][2]));
                            /*
                            if(index < 1000) {
                                System.out.println(index);
                                System.out.println("Before Hue Score: " + doubleOriginalHSV[j][k][0]);
                                System.out.println("After Hue Score: " + hueSaturationValueScore[0]);
                                index++;
                            }
                            */
                        }
                    }

                }
                index++;
            }

            // Calculate displacement of each block through the shot
            for(int i = 0; i < blockPositions.length-1; i++){
                for(int j = 0; j < 3; j++){
                    for(int k = 0; k < 5; k++) {
                        motionVectorScore += MotionVector.calculateDisplacement(blockPositions[i][j][k][0], blockPositions[i][j][k][1], blockPositions[i+1][j][k][0], blockPositions[i+1][j][k][0]);
                    }
                }
            }
            motionVectorScores[l] = motionVectorScore;

            // HSV analysis
            hueSaturationValueScores[0][l] = hueSaturationValueScore[0];
            hueSaturationValueScores[1][l] = hueSaturationValueScore[1];
            hueSaturationValueScores[2][l] = hueSaturationValueScore[2];

        }
    }

    public static void printStuff(){
        System.out.println("Stuff");
    }
}
