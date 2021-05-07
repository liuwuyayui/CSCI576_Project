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
  
  public static List<int[]> getTest1Frames() {
    int[][] array = {{779, 1012}, {6739, 7018}, {7019, 7255}, {7256, 8501}, {10539, 10683}, {10994, 11451}, {12835, 12978}};
    return new ArrayList<>(Arrays.asList(array));
  }
}
