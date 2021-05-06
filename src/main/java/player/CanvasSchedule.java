package player;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;
import java.util.Set;

class CanvasSchedule extends ScheduledService<Integer> {
    int startIndex;
    int lastIndex = Utils.FRAMES_LENGTH;
    Canvas canvas;
    Slider slider;
    Map<Integer, Double> frameToBright;
    MediaPlayer bgmPlayer;
    List<Integer> frames;
    ColorAdjust ca = new ColorAdjust();
    Set<Integer> breakPoints;
    GraphicsContext gc;
    
    public CanvasSchedule(Canvas canvas, Slider slider, int startIndex, Map<Integer, Double> frameToBright, MediaPlayer bgmPlayer, List<Integer> frames, Set<Integer> breakPoints) {
        this.canvas = canvas;
        this.slider = slider;
        this.startIndex = startIndex;
        this.frameToBright = frameToBright;
        this.bgmPlayer = bgmPlayer;
        this.frames = frames;
        this.breakPoints = breakPoints;
        gc = canvas.getGraphicsContext2D();
        
        setDelay(Duration.millis(0));
        setPeriod(Duration.millis(1000.0 / Utils.FRAME_RATE));
    }
    
    
    @Override
    protected Task<Integer> createTask() {
        return new Task<Integer>() {
            @Override
            protected Integer call() {
                if (startIndex >= lastIndex) {
                    bgmPlayer.pause();
                    Main.isPlaying = false;
                    this.cancel();
                }
                startIndex += 1;
                return startIndex;
            }
            
            @Override
            protected void updateValue(Integer value) {
                int actualFrame = frames.get(value);
                String framePath = Utils.directory + actualFrame + ".jpg";
                Image newImg = new Image(framePath, Utils.REQUEST_WIDTH, Utils.REQUEST_HEIGHT, true, true);
                double brightness = frameToBright.getOrDefault(actualFrame, 0.0);
                if (brightness != 0) {
                    ca.setBrightness(brightness);
                    gc.setEffect(ca);
                    bgmPlayer.setVolume(brightness + 1);
                }
                gc.drawImage(newImg, 0, 0);
                slider.setValue(value);
                if (breakPoints.contains(actualFrame)) {
                    System.out.println("found breakPoint: " + actualFrame);
                    bgmPlayer.seek(Duration.millis(actualFrame * 1000.0 / Utils.FRAME_RATE));
                }
            }
        };
    }
}