package player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
  
  // Urls of icons
  public static final String icon_playerPlay = "/icon/Playerplay.png";
  public static final String icon_playerPause = "/icon/Playerpause.png";
  public static final String ICON_MUSIC = "/icon/icon_music.png";
  public static final String ICON_FOLDER = "/icon/icon_folder.png";
  public static final String ICON_VOL = "/icon/volume3.png";
  public static final String ICON_MUTE = "/icon/volume-x.png";
  
  // video properties
  public static int FRAME_RATE = 30;
  public static int FRAMES_LENGTH;
  
  public static final int REQUEST_WIDTH = 640;
  public static final int REQUEST_HEIGHT = 360;
  
  public static final double MILLS_PER_FRAME = 33.333333;
  
  // Application window properties
  public static final int STAGE_WIDTH = 640;
  public static final int STAGE_HEIGHT = 400;
  public static final int PROGRESS_SLIDER_PRE_WIDTH = 400;
  
  // directory
  public static String directory;
  public static String bgmDir;
  public static String rgbFramesPath;
  public static final String DEFAULT_OPEN_DIR = "/Users/daddy/Movies";
  //public static final String DEFAULT_OPEN_DIR = "/Users/edmondsitu/Desktop";
  
  public static List<int[]> getTest1Frames() {
    int[][] array = {{6739, 7018}, {7256, 8501}, {12835, 12978}, {8846, 8989}, {8615, 8717}, {12979, 13199}, {6479, 6738}, {13646, 13803}, {14075, 14220}};
    return new ArrayList<>(Arrays.asList(array));
  }
  
  
  public static List<int[]> getTest2Frames() {
    int[][] array = {{2168, 2296}, {2297, 2406}, {2407, 2503}, {2504, 2645}, {3009, 3102}, {3103, 3271}, {3272, 3508}, {13167, 13442}, {13443, 13626}, {13627, 13866}, {13867, 13971}, {13972, 14230}, {14435, 14676}, {14811, 14912}, {14913, 15099}, {15100, 15205}, {15206, 15308}};
    return new ArrayList<>(Arrays.asList(array));
  }
}
