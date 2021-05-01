package sample;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.Map;

class CanvasSchedule extends ScheduledService<Integer> {
    int startFrame;
    int lastFrame = Utils.FRAMES_LENGTH;
    Canvas canvas;
    Slider slider;
    Map<Integer, Double> frameToBright;
    MediaPlayer bgmPlayer;
    
    public CanvasSchedule(Canvas canvas, Slider slider, int startFrame, Map<Integer, Double> frameToBright, MediaPlayer bgmPlayer) {
        this.canvas = canvas;
        this.slider = slider;
        this.startFrame = startFrame;
        this.frameToBright = frameToBright;
        this.bgmPlayer = bgmPlayer;
        
        setDelay(Duration.millis(0));
        setPeriod(Duration.millis(1000.0 / Utils.FRAME_RATE));
    }
    
//    public CanvasSchedule(CanvasSchedule cs) {
//        this.canvas = cs.canvas;
//        this.slider = cs.slider;
//        this.startFrame = cs.startFrame;
//        setDelay(Duration.millis(0));
//        setPeriod(Duration.millis(1000.0 / Utils.FRAME_RATE));
//    }
    
    
    @Override
    protected Task<Integer> createTask() {
        return new Task<Integer>() {
            @Override
            protected Integer call() {
                if (startFrame >= lastFrame) {
                    this.cancel();
                }
                startFrame += 1;
                return startFrame;
            }
            
            @Override
            protected void updateValue(Integer value) {
                String framePath = Utils.directory + "/frame" + startFrame + ".jpg";
                Image newImg = new Image(framePath, Utils.REQUEST_WIDTH, Utils.REQUEST_HEIGHT, true, true);
                ColorAdjust ca = new ColorAdjust();
                Double brightness = frameToBright.get(startFrame);
                if (brightness == null) {
                    brightness = 0.0;
                }
                ca.setBrightness(brightness);
                canvas.getGraphicsContext2D().setEffect(ca);
                canvas.getGraphicsContext2D().drawImage(newImg, 0, 0);
                slider.setValue(startFrame);    // update value
                double vol = brightnessToVol(brightness);
                bgmPlayer.setVolume(vol);
            }
        };
    }
    
    private double brightnessToVol(double brightness) {
        return brightness + 1;
    }
}