package analysis;

import video.shot.segmentation.CSCI576VideoShotSegmentationProject;

import java.util.Arrays;

public class HueSaturationValue {
    public static int width = CSCI576VideoShotSegmentationProject.width;
    public static int height = CSCI576VideoShotSegmentationProject.height;
    public static int totalFrames = CSCI576VideoShotSegmentationProject.totalFrames;
    public static String myRGBFramesFolderPath = CSCI576VideoShotSegmentationProject.myRGBFramesFolderPath;

    public static void main(String[] args){
        System.out.println("Processing HSV scoring for all shots...");
        int[][] videoShots = CSCI576VideoShotSegmentationProject.videoShotSegmentationColorSpaceHistogram(width, height, totalFrames, myRGBFramesFolderPath);
        double[][] hueSaturationValueScores = hueSaturationValueOfFrameShots(videoShots);
        /*
        for(int i = 0; i < hueSaturationValueScores[0].length; i++){
            hueSaturationValueScores[0][i] = Math.round(hueSaturationValueScores[0][i]*100.0)/100.0;
            hueSaturationValueScores[1][i] = Math.round(hueSaturationValueScores[1][i]*100.0)/100.0;
        }
        System.out.println("Saturation Scores Before Normalize: "+ Arrays.toString(hueSaturationValueScores[0]));
        System.out.println("Value/Brightness Scores Before Normalize: "+ Arrays.toString(hueSaturationValueScores[1]));
        for(int i = 0; i < hueSaturationValueScores[0].length; i++){
            hueSaturationValueScores[0][i] = Math.round(hueSaturationValueScores[0][i]/((videoShots[i][1]-videoShots[i][0])+1)*100.0)/100.0;
            hueSaturationValueScores[1][i] = Math.round(hueSaturationValueScores[1][i]/((videoShots[i][1]-videoShots[i][0])+1)*100.0)/100.0;
        }
        System.out.println("Saturation Scores After Normalizing to Each Shot: "+Arrays.toString(hueSaturationValueScores[0]));
        System.out.println("Value/Brightness Scores After Normalizing to Each Shot: "+Arrays.toString(hueSaturationValueScores[1]));
        double maxSaturationScore = hueSaturationValueScores[0][0];
        double minSaturationScore = hueSaturationValueScores[0][0];
        double maxValueBrightnessScore = hueSaturationValueScores[1][0];
        double minValueBrightnessScore = hueSaturationValueScores[1][0];
        for(int i = 1; i < hueSaturationValueScores[0].length; i++){
            if(hueSaturationValueScores[0][i] > maxSaturationScore){
                maxSaturationScore = hueSaturationValueScores[0][i];
            }
            if(hueSaturationValueScores[0][i] < minSaturationScore){
                minSaturationScore = hueSaturationValueScores[0][i];
            }
            if(hueSaturationValueScores[1][i] > maxValueBrightnessScore){
                maxValueBrightnessScore = hueSaturationValueScores[1][i];
            }
            if(hueSaturationValueScores[1][i] < minValueBrightnessScore){
                minValueBrightnessScore = hueSaturationValueScores[1][i];
            }
        }
        System.out.println("Max Saturation Score: "+maxSaturationScore);
        System.out.println("Min Saturation Score: "+minSaturationScore);
        System.out.println("Max Value/Brightness Score: "+maxValueBrightnessScore);
        System.out.println("Min Value/Brightness Score: "+minValueBrightnessScore);
        for(int i = 0; i < hueSaturationValueScores[0].length; i++){
            hueSaturationValueScores[0][i] = Math.round(hueSaturationValueScores[0][i]/maxSaturationScore*100.0)/100.0;
            hueSaturationValueScores[1][i] = Math.round(hueSaturationValueScores[1][i]/maxValueBrightnessScore*100.0)/100.0;
        }
        System.out.println("Saturation Scores After Normalizing to Maximum Score: "+Arrays.toString(hueSaturationValueScores[0]));
        System.out.println("Value/Brightness Scores After Normalizing to Maximum Score: "+Arrays.toString(hueSaturationValueScores[1]));
        System.out.println("Number of Saturation and Value/Brightness Scores:"+hueSaturationValueScores.length);
        */
        double[][] normalizedHueSaturationValueScores = normalizedHueSaturationValueScores(hueSaturationValueScores, videoShots);
        System.out.println("Complete");
        for(int i = 0; i < videoShots.length; i++){
            System.out.println("["+videoShots[i][0]+", "+videoShots[i][1]+"]"+" = Saturation: "+normalizedHueSaturationValueScores[0][i]+" and Value/Brightness: "+normalizedHueSaturationValueScores[1][i]);
        }
    }

    public static double[][] hueSaturationValueOfFrameShots(int[][] videoShots){
        //System.out.print("Video Shots: ");
        //CSCI576VideoShotSegmentationProject.printArray(videoShots);
        //System.out.println("Number of Shots: "+videoShots.length);
        // (0 for saturation score and 1 for value/brightness score, number of shots)
        double[][] hueSaturationValueScores = new double[2][videoShots.length];

        // Process each shot
        for(int i = 0; i < videoShots.length; i++){
            System.out.print("\rShots Index: "+i);
            hueSaturationValueScores[0][i] = calculateHueSaturationValueScore(videoShots[i][0], videoShots[i][1])[0];
            hueSaturationValueScores[1][i] = calculateHueSaturationValueScore(videoShots[i][0], videoShots[i][1])[1];
        }
        System.out.println();

        return hueSaturationValueScores;
    }

    public static double[] calculateHueSaturationValueScore(int beginFrame, int endFrame){
        double[] hueSaturationValueScore = new double[2]; // 0 for saturation score 1 for value/brightness score

        // [x][y][rgb]
        int[][][] intRGBFrameSingleFrame;
        for(int i = beginFrame; i <= endFrame; i++) {
            intRGBFrameSingleFrame = CSCI576VideoShotSegmentationProject.readFrameRGBForSingleFrame(width, height, i, myRGBFramesFolderPath);
            // Convert RGB to HSV
            double[][][] doubleOriginalHSV = new double[height][width][3];
            CSCI576VideoShotSegmentationProject.RGBtoHSV(intRGBFrameSingleFrame, doubleOriginalHSV);
            for(int j = 0; j < doubleOriginalHSV.length; j++){
                for(int k = 0; k < doubleOriginalHSV[0].length; k++){
                    hueSaturationValueScore[0]+=doubleOriginalHSV[j][k][1];
                    hueSaturationValueScore[1]+=doubleOriginalHSV[j][k][2];
                }
            }
        }

        return hueSaturationValueScore;
    }

    public static double[][] normalizedHueSaturationValueScores(double[][] hueSaturationValueScores, int[][] videoShots){
        double[][] normalizedHueSaturationValueScores = new double[2][hueSaturationValueScores[0].length];
        // Normalize to length of each shot
        for(int i = 0; i < hueSaturationValueScores[0].length; i++){
            normalizedHueSaturationValueScores[0][i] = hueSaturationValueScores[0][i]/((videoShots[i][1]-videoShots[i][0])+1);
            normalizedHueSaturationValueScores[1][i] = hueSaturationValueScores[1][i]/((videoShots[i][1]-videoShots[i][0])+1);
        }
        // Find maximum saturation and value/brightness scores
        double maxSaturationScore = normalizedHueSaturationValueScores[0][0];
        double maxValueBrightnessScore = normalizedHueSaturationValueScores[1][0];
        for(int i = 1; i < normalizedHueSaturationValueScores[0].length; i++){
            if(normalizedHueSaturationValueScores[0][i] > maxSaturationScore){
                maxSaturationScore = normalizedHueSaturationValueScores[0][i];
            }
            if(normalizedHueSaturationValueScores[1][i] > maxValueBrightnessScore){
                maxValueBrightnessScore = normalizedHueSaturationValueScores[1][i];
            }
        }
        // Normalize between 0 and 1
        for(int i = 0; i < normalizedHueSaturationValueScores[0].length; i++){
            normalizedHueSaturationValueScores[0][i] = normalizedHueSaturationValueScores[0][i]/maxSaturationScore;
            normalizedHueSaturationValueScores[1][i] = normalizedHueSaturationValueScores[1][i]/maxValueBrightnessScore;
        }

        return normalizedHueSaturationValueScores;
    }
}
