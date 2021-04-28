package sample;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.util.Duration;

class CanvasSchedule extends ScheduledService<Integer> {
    int startFrame;
    int lastFrame = Utils.FRAMES_LENGTH;
    Canvas canvas;
    Slider slider;
    
    public CanvasSchedule(Canvas canvas, Slider slider, int startFrame) {
        this.canvas = canvas;
        this.slider = slider;
        this.startFrame = startFrame;
        setDelay(Duration.millis(0));
        setPeriod(Duration.millis(1000.0 / Utils.FRAME_RATE));
    }
    
    public CanvasSchedule(CanvasSchedule cs) {
        this.canvas = cs.canvas;
        this.slider = cs.slider;
        this.startFrame = cs.startFrame;
        setDelay(Duration.millis(0));
        setPeriod(Duration.millis(1000.0 / Utils.FRAME_RATE));
    }
    
    
    @Override
    protected Task<Integer> createTask() {
        return new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                if (startFrame >= lastFrame) {
                    this.cancel();
                }
                startFrame += 1;
                return startFrame;
            }
            
            @Override
            protected void updateValue(Integer value) {
                String framePath = "/soccer/frame" + startFrame + ".jpg";
                canvas.getGraphicsContext2D().drawImage(new Image(framePath), 0, 0);
                slider.setValue(startFrame);
            }
        };
    }
}