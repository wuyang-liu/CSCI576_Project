package video.shot.segmentation;

import video.shot.selection.VideoSummary;

import java.awt.*;
        import java.awt.image.*;
        import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IntSummaryStatistics;
import javax.swing.*;

/**
 *
 * @author edmondsitu
 */
public class CSCI576VideoShotSegmentationProject {

    /*
    // Video frame size 320x180
    public static int width = 320;
    public static int height = 180;
    //public static int frames = 16200;
    public static int totalFrames = 16200;
    //public static double threshold = 4000000;
    public static double threshold = 4000000;
    public static int minimumFramesPerShot = 30; // ~ 1 second
    public static String myRGBFramesFolderPath = "/Users/daddy/Movies/project_dataset/frames_rgb/concert";
    */

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        // Show image
        /*
        CSCI576VideoShotSegmentationProject ren = new CSCI576VideoShotSegmentationProject();
        args = new String[]{"/Users/edmondsitu/Desktop/project_dataset/frames_rgb/soccer/frame0.rgb"};
        ren.showIms(args);
        */
        //String RGBFramesFolderPath = "/Users/daddy/Movies/project_dataset/frames_rgb/concert";
        //String RGBFramesFolderPath = "/Users/edmondsitu/Desktop/project_dataset/frames_rgb/concert";
        int width = VideoSummary.width;
        int height = VideoSummary.height;
        int totalFrames = VideoSummary.totalFrames;
        int minimumFramesPerShot = VideoSummary.minimumFramesPerShot;
        String myRGBFramesFolderPath = VideoSummary.myRGBFramesFolderPath;
        System.out.println("Processing video shot segmentation of all frames...");
        //ArrayList<Integer> videoBreakpoints = CSCI576VideoShotSegmentationProject.videoShotSegmentationSumAbsoluteDifference(width, height, totalFrames, threshold, RGBFramesFolderPath);
        int[][] videoShots = CSCI576VideoShotSegmentationProject.videoShotSegmentationColorSpaceHistogram(width, height, totalFrames, minimumFramesPerShot, myRGBFramesFolderPath);
        System.out.print("Video Shots: ");
        printArray(videoShots);
        System.out.println("Number of Shots: "+videoShots.length);
        System.out.println("Complete");
    }

    public static int[][][][] readFrameRGBForPair(int width, int height, int firstFrame, String RGBFramesFolderPath){
        // [x][y][frame][rgb]
        int[][][][] intRGBFramePair = new int[height][width][2][3];

        File file1;
        File file2;
        RandomAccessFile raf1;
        RandomAccessFile raf2;
        int frameLength = width*height*3;
        long len = frameLength;
        byte[] bytes1;
        byte[] bytes2;

        try{
            // Process frames in pairs
            if(firstFrame%100 == 0) {
                //System.out.println("Frames: [" + firstFrame + ", " + (firstFrame + 1) + "]");
                //System.out.println("Frames Index: "+firstFrame);
                System.out.print("\rFrames Index: "+firstFrame);
            }
            file1 = new File(RGBFramesFolderPath+"/frame"+firstFrame+".rgb");
            raf1 = new RandomAccessFile(file1, "r");
            raf1.seek(0);
            bytes1 = new byte[(int) len];
            raf1.read(bytes1);

            file2 = new File(RGBFramesFolderPath+"/frame"+(firstFrame+1)+".rgb");
            raf2 = new RandomAccessFile(file2, "r");
            raf2.seek(0);
            bytes2 = new byte[(int) len];
            raf2.read(bytes2);

            // Single byte buffer to RGB 2D arrays
            // (R, G, B) = (0, 1, 2)
            int index = 0;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    intRGBFramePair[i][j][0][0] = bytes1[index] & 0xff;
                    intRGBFramePair[i][j][0][1] = bytes1[index + height * width] & 0xff;
                    intRGBFramePair[i][j][0][2] = bytes1[index + height * width * 2] & 0xff;

                    intRGBFramePair[i][j][1][0] = bytes2[index] & 0xff;
                    intRGBFramePair[i][j][1][1] = bytes2[index + height * width] & 0xff;
                    intRGBFramePair[i][j][1][2] = bytes2[index + height * width * 2] & 0xff;
                    index++;
                }
            }
            //System.out.println("("+intRGBFramePair[0][0][0][0]+", "+intRGBFramePair[0][0][0][1]+", "+intRGBFramePair[0][0][0][2]+")");
            raf1.close();
            raf2.close();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return intRGBFramePair;
    }

    public static int[][][] readFrameRGBForSingleFrame(int width, int height, int frameIndex, String RGBFramesFolderPath){
        // [x][y][frame][rgb]
        int[][][] intRGBFrameSingleFrame = new int[height][width][3];

        File file1;
        RandomAccessFile raf1;
        int frameLength = width*height*3;
        long len = frameLength;
        byte[] bytes1;

        try{
            // Process frames in pairs
            if(frameIndex%100 == 0) {
                //System.out.println("Frames: [" + firstFrame + ", " + (firstFrame + 1) + "]");
                //System.out.println("Frames Index: "+firstFrame);
                System.out.print("\rFrames Index: "+frameIndex);
            }
            file1 = new File(RGBFramesFolderPath+"/frame"+frameIndex+".rgb");
            raf1 = new RandomAccessFile(file1, "r");
            raf1.seek(0);
            bytes1 = new byte[(int) len];
            raf1.read(bytes1);

            // Single byte buffer to RGB 2D arrays
            // (R, G, B) = (0, 1, 2)
            int index = 0;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    intRGBFrameSingleFrame[i][j][0] = bytes1[index] & 0xff;
                    intRGBFrameSingleFrame[i][j][1] = bytes1[index + height * width] & 0xff;
                    intRGBFrameSingleFrame[i][j][2] = bytes1[index + height * width * 2] & 0xff;
                    index++;
                }
            }
            //System.out.println("("+intRGBFramePair[0][0][0][0]+", "+intRGBFramePair[0][0][0][1]+", "+intRGBFramePair[0][0][0][2]+")");
            raf1.close();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return intRGBFrameSingleFrame;
    }


    public static ArrayList<Integer> videoShotSegmentationSumAbsoluteDifference(int width, int height, int totalFrames, double threshold, String RGBFramesFolderPath){
        double[] framesValue = new double[totalFrames];
        ArrayList<Integer> videoBreakpoints = new ArrayList<>();
        // [x][y][frame][rgb]
        int[][][][] intRGBFramePair;

        // Calculate value of each frame(i) to frame(i+1)
        for(int i = 0; i < totalFrames-1; i++){
            intRGBFramePair = readFrameRGBForPair(width, height, i, RGBFramesFolderPath);
            framesValue[i] = sumOfAbsoluteDifference(width, height, intRGBFramePair, i);
            //System.out.println("Frame #"+i+" Absolute Difference: "+framesValue[i]);
        }

        // Determine threshold value
        double min = framesValue[0];
        double max = framesValue[0];
        double sum = 0;
        for(int i = 1; i < framesValue.length; i++){
            if(framesValue[i] < min){
                min = framesValue[i];
            }
            if(framesValue[i] > max){
                max = framesValue[i];
            }
            sum+=framesValue[i];
        }
        System.out.println("Count="+framesValue.length+", Average="+(sum/framesValue.length)+", Sum="+sum+", Max="+max+", Min="+min);

        int count = 0;
        for(int i = 0; i < framesValue.length; i++){
            if(framesValue[i] >= (max*0.25)){
                count++;
            }
        }
        System.out.println("Segments over threshold:"+count);
        System.out.println("Framesvalue Sum Absolute Difference: "+Arrays.toString(framesValue));

        // Ensure first and last frame is included in the set
        videoBreakpoints.add(0);
        // Select frames that meets the threshold
        for(int i = 1; i < framesValue.length-1; i++){
            if(framesValue[i] >= threshold){
                videoBreakpoints.add(i);
            }
        }
        videoBreakpoints.add(totalFrames-1);

        return videoBreakpoints;
    }


    public static int[][] videoShotSegmentationColorSpaceHistogram(int width, int height, int totalFrames, int minimumFramesPerShot, String RGBFramesFolderPath){
        double[] framesValue = new double[totalFrames];
        ArrayList<Integer> videoBreakpoints = new ArrayList<>();
        // [x][y][frame][rgb]
        int[][][][] intRGBFramePair;


        // Calculate value of each frame(i) to frame(i+1)
        for(int i = 0; i < totalFrames-1; i++){
            intRGBFramePair = readFrameRGBForPair(width, height, i, RGBFramesFolderPath);
            framesValue[i] = colorSpaceHistogramSumAbsoluteDifference(width, height, intRGBFramePair, i);
            //System.out.println("Frame #"+i+" Absolute Difference: "+framesValue[i]);
        }
        /*
        System.out.println();
        System.out.println("Framesvalue Color Space Histogram: "+Arrays.toString(framesValue));
        for(int i = 0; i < framesValue.length; i++){
            System.out.println(i+": "+framesValue[i]);
        }
        */
        // Video Shots: [[0,8], [9,77], [78,153], [154,215], [216,301], [302,421], [422,498], [499,540], [541,616], [617,680], [681,750], [751,780], [781,842], [843,886], [887,972], [973,999]]

        // Ensure first and last frame is included in the set
        videoBreakpoints.add(0);
        // Select frames that meets the threshold
        for(int i = 2; i < framesValue.length-2; i++){
            if(framesValue[i] >= (framesValue[i-1]*10)){
                videoBreakpoints.add(i + 1);
            }
        }
        videoBreakpoints.add(totalFrames-1);

        // Filter noise in frames with frame window
        filterNoiseWithFrameWindow(videoBreakpoints, totalFrames, minimumFramesPerShot);

        // Convert breakpoints to shots
        int[][] videoShots = formShots(videoBreakpoints);

        return videoShots;
    }

    public static double[] videoShotSegmentationColorSpaceHistogramForFramesValue(int width, int height, int totalFrames, int minimumFramesPerShot, String RGBFramesFolderPath){
        double[] framesValue = new double[totalFrames];
        ArrayList<Integer> videoBreakpoints = new ArrayList<>();
        // [x][y][frame][rgb]
        int[][][][] intRGBFramePair;


        // Calculate value of each frame(i) to frame(i+1)
        for(int i = 0; i < totalFrames-1; i++){
            intRGBFramePair = readFrameRGBForPair(width, height, i, RGBFramesFolderPath);
            framesValue[i] = colorSpaceHistogramSumAbsoluteDifference(width, height, intRGBFramePair, i);
            //System.out.println("Frame #"+i+" Absolute Difference: "+framesValue[i]);
        }
        //System.out.println();

        // Ensure first and last frame is included in the set
        videoBreakpoints.add(0);
        // Select frames that meets the threshold
        for(int i = 2; i < framesValue.length-2; i++){
            if(framesValue[i] >= (framesValue[i-1]*10)){
                videoBreakpoints.add(i + 1);
            }
        }
        videoBreakpoints.add(totalFrames-1);

        // Filter noise in frames with frame window
        filterNoiseWithFrameWindow(videoBreakpoints, totalFrames, minimumFramesPerShot);

        // Convert breakpoints to shots
        int[][] videoShots = formShots(videoBreakpoints);

        return framesValue;
    }


    public static double sumOfAbsoluteDifference(int width, int height, int[][][][] intRGBFramePair, int index){
        int sumAbsoluteDifference = 0;

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                sumAbsoluteDifference += Math.abs(intRGBFramePair[i][j][0][0]-intRGBFramePair[i][j][1][0]);
                sumAbsoluteDifference += Math.abs(intRGBFramePair[i][j][0][1]-intRGBFramePair[i][j][1][1]);
                sumAbsoluteDifference += Math.abs(intRGBFramePair[i][j][0][2]-intRGBFramePair[i][j][1][2]);
            }
        }

        return sumAbsoluteDifference;
    }


    public static double colorSpaceHistogramSumAbsoluteDifference(int width, int height, int[][][][] intRGBFramePair, int index){
        int sumAbsoluteDifferenceR = 0;
        int sumAbsoluteDifferenceG = 0;
        int sumAbsoluteDifferenceB = 0;

        // (2, 256, 3) = (2 frames, 256 color quantization, 0 for R and 1 for G and 2 for B)
        int[][][] intRGBFramePairHistogram = new int[2][256][3];

        // Map each pixel RGB values to histogram
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                // Frame 1
                intRGBFramePairHistogram[0][intRGBFramePair[i][j][0][0]][0]++;
                intRGBFramePairHistogram[0][intRGBFramePair[i][j][0][1]][1]++;
                intRGBFramePairHistogram[0][intRGBFramePair[i][j][0][2]][2]++;

                // Frame 2
                intRGBFramePairHistogram[1][intRGBFramePair[i][j][1][0]][0]++;
                intRGBFramePairHistogram[1][intRGBFramePair[i][j][1][1]][1]++;
                intRGBFramePairHistogram[1][intRGBFramePair[i][j][1][2]][2]++;
            }
        }

        // Calculate difference between the two frames' histogram
        for(int i = 0; i < intRGBFramePairHistogram[0].length; i++){
            sumAbsoluteDifferenceR += Math.abs(intRGBFramePairHistogram[0][i][0]-intRGBFramePairHistogram[1][i][0]);
            sumAbsoluteDifferenceG += Math.abs(intRGBFramePairHistogram[0][i][1]-intRGBFramePairHistogram[1][i][1]);
            sumAbsoluteDifferenceB += Math.abs(intRGBFramePairHistogram[0][i][2]-intRGBFramePairHistogram[1][i][2]);
        }

        return sumAbsoluteDifferenceR+sumAbsoluteDifferenceG+sumAbsoluteDifferenceB;
    }

    public static void filterNoiseWithFrameWindow(ArrayList<Integer> videoBreakpoints, int totalFrames, int minimumFramesPerShot){
        //System.out.println("Before Processing: "+videoBreakpoints.toString());
        // Filter breakpoint noise with minimal shot window
        int videoBreakpointsIndex = 1;
        while(videoBreakpointsIndex < videoBreakpoints.size()-3){
            if(videoBreakpoints.get(videoBreakpointsIndex+1) < (videoBreakpoints.get(videoBreakpointsIndex)+minimumFramesPerShot)) {
                videoBreakpoints.remove(videoBreakpointsIndex+1);
            }
            else{
                videoBreakpointsIndex++;
            }
        }
        //System.out.println("After Filter Processing: "+videoBreakpoints.toString());

        // Process frames adjacent to first and last frame
        if(videoBreakpoints.size() == 3){
            if((videoBreakpoints.get(1) < 5) || ((videoBreakpoints.get(1)+5) > (totalFrames-1))){
                videoBreakpoints.remove(1);
            }
        }
        else if(videoBreakpoints.size() > 3){
            if(videoBreakpoints.get(1) < 5){
                videoBreakpoints.remove(1);
            }
            if((videoBreakpoints.get(videoBreakpoints.size()-2)+5) > (totalFrames-1)){
                videoBreakpoints.remove((videoBreakpoints.size()-2));
            }
        }
        //System.out.println("After Adjacent Processing: "+videoBreakpoints.toString());
    }

    public static int[][] formShots(ArrayList<Integer> videoBreakpoints){
        // Convert breakpoints to shots
        int[][] videoShots = new int[videoBreakpoints.size()-1][2];
        for(int i = 0; i < videoShots.length-1; i++){
            videoShots[i][0] = videoBreakpoints.get(i);
            videoShots[i][1] = videoBreakpoints.get(i+1)-1;
        }
        videoShots[videoShots.length-1][0] = videoBreakpoints.get(videoShots.length-1);
        videoShots[videoShots.length-1][1] = videoBreakpoints.get(videoShots.length);

        return videoShots;
    }

    public static void RGBtoHSV(int width, int height, int[][][] intOriginalRGB, double[][][] doubleOriginalHSV){
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                double r = intOriginalRGB[y][x][0];
                double g = intOriginalRGB[y][x][1];
                double b = intOriginalRGB[y][x][2];

                double h = doubleOriginalHSV[y][x][0];
                double s = doubleOriginalHSV[y][x][1];
                double v = doubleOriginalHSV[y][x][2];

                double min = Math.min(Math.min(r, g), b);
                double max = Math.max(Math.max(r, g), b);
                double delta = max-min;
                v = max;

                if(max != 0){
                    s = delta/max;
                }
                else{
                    s = 0;
                    h = -1;
                    continue;
                }

                if(r == max){
                    h = (g-b)/delta;
                }
                else if(g == max){
                    h = 2 + (b-r)/delta;
                }
                else{
                    h = 4 + (r-g)/delta;
                }
                h *= 60;

                if(h < 0){
                    h += 360;
                }

                // Add to avoid NaN
                if(delta == 0){
                    h = 0;
                }

                doubleOriginalHSV[y][x][0] = h;
                /*
                System.out.println("RGB to HSV: Hue="+doubleOriginalHSV[y][x][0]);
                if(Double.isNaN(doubleOriginalHSV[y][x][0])){
                    System.out.println("delta="+delta);
                    System.out.println("min="+min);
                    System.out.println("max="+max);
                }
                */
                doubleOriginalHSV[y][x][1] = s;
                doubleOriginalHSV[y][x][2] = v;
            }
        }
    }

    public static void RGBtoHSVWithAnalysis(int width, int height, int[][][][] intOriginalRGB, double[][][] doubleOriginalHSV, int index){
        // intOriginalRGB[x][y][frame][rgb]

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                double r = intOriginalRGB[y][x][index][0];
                double g = intOriginalRGB[y][x][index][1];
                double b = intOriginalRGB[y][x][index][2];

                double h = doubleOriginalHSV[y][x][0];
                double s = doubleOriginalHSV[y][x][1];
                double v = doubleOriginalHSV[y][x][2];

                double min = Math.min(Math.min(r, g), b);
                double max = Math.max(Math.max(r, g), b);
                double delta = max-min;
                v = max;

                if(max != 0){
                    s = delta/max;
                }
                else{
                    s = 0;
                    h = -1;
                    continue;
                }

                if(r == max){
                    h = (g-b)/delta;
                }
                else if(g == max){
                    h = 2 + (b-r)/delta;
                }
                else{
                    h = 4 + (r-g)/delta;
                }
                h *= 60;

                if(h < 0){
                    h += 360;
                }

                // Add to avoid NaN
                if(delta == 0){
                    h = 0;
                }

                doubleOriginalHSV[y][x][0] = h;
                /*
                System.out.println("RGB to HSV: Hue="+doubleOriginalHSV[y][x][0]);
                if(Double.isNaN(doubleOriginalHSV[y][x][0])){
                    System.out.println("delta="+delta);
                    System.out.println("min="+min);
                    System.out.println("max="+max);
                }
                */
                doubleOriginalHSV[y][x][1] = s;
                doubleOriginalHSV[y][x][2] = v;
            }
        }
    }

    public static void printArray(int[][] videoShots){
        System.out.print("{");
        for(int i = 0; i < videoShots.length-1; i++){
            System.out.print("{"+videoShots[i][0]+","+videoShots[i][1]+"}, ");
        }
        System.out.print("{"+videoShots[videoShots.length-1][0]+","+videoShots[videoShots.length-1][1]+"}");
        System.out.println("}");
    }

    public static void printArrayList(ArrayList<int[]> videoShots){
        System.out.print("{");
        for(int i = 0; i < videoShots.size()-1; i++){
            System.out.print("{"+videoShots.get(i)[0]+","+videoShots.get(i)[1]+"}, ");
        }
        System.out.print("{"+videoShots.get(videoShots.size()-1)[0]+","+videoShots.get(videoShots.size()-1)[1]+"}");
        System.out.println("}");
    }

    public static void readImageRGB(int width, int height, String imgPath, BufferedImage img){
        try{
            int frameLength = width*height*3;

            File file = new File(imgPath);
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(0);

            long len = frameLength;
            byte[] bytes = new byte[(int) len];

            raf.read(bytes);

            int[][][] intOriginalRGB = new int[height][width][3];

            // Single byte buffer to RGB 2D arrays
            // (R, G, B) = (0, 1, 2)
            int index = 0;
            for(int i = 0; i < height; i++){
                for(int j = 0; j < width; j++){
                    intOriginalRGB[i][j][0] = bytes[index] & 0xff;
                    intOriginalRGB[i][j][1] = bytes[index+height*width] & 0xff;
                    intOriginalRGB[i][j][2] = bytes[index+height*width*2] & 0xff;
                    index++;
                }
            }

            // Set RGB to each pixel
            for(int y = 0; y < height; y++){
                for(int x = 0; x < width; x++){
                    byte a = 0;

                    int rOriginal = intOriginalRGB[y][x][0];
                    int gOriginal = intOriginalRGB[y][x][1];
                    int bOriginal = intOriginalRGB[y][x][2];

                    int pixOriginal = 0xff000000 | ((rOriginal & 0xff) << 16) | ((gOriginal & 0xff) << 8) | (bOriginal & 0xff);
                    img.setRGB(x,y,pixOriginal);
                }
            }
            raf.close();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
