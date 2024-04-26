package processing.zgg.sketch.tuto;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import processing.zgg.sketch.ZenGardenSketch;
import processing.zgg.utils.MotionUtils;

/**
 *
 * @author gestorum
 */
public class TwoDMotionSketch extends ZenGardenSketch {

    private static final int INITIAL_MAP_WIDTH = 400;
    private static final int INITIAL_MAP_HEIGHT = INITIAL_MAP_WIDTH;
    
    private static final int FRAME_RATE = 24;

    private static final int NUM_FRAMES = FRAME_RATE * 4;
    private static final int NUM_SAMPLES = 12;
    private static final float R = 100;
    
    private static final float motion_radius = 0.5f;

    private float t;
    private boolean counterClockwise;
    private List<Point> samplePositions;
    
    @Override
    public void settings() {
        size(INITIAL_MAP_WIDTH, INITIAL_MAP_HEIGHT);
        
        setBackgroundColor(Color.WHITE);
        
        samplePositions = getSamplePositions(NUM_SAMPLES);
    }
    
    @Override
    public void setup() {
        frameRate(FRAME_RATE);
    }

    @Override
    protected void drawFrame() {
        t = (float) ((frameCount - 1) % NUM_FRAMES) / NUM_FRAMES;
        if (counterClockwise) {
            t = 1 - t;
        }

        final int i = (int) Math.floor(t * NUM_SAMPLES);
        final Point samplePoint1 = samplePositions.get(i % NUM_SAMPLES);
        final Point samplePoint2 = samplePositions.get((i+1) % NUM_SAMPLES);
        
        final List<Point> initialPoints = List.of(samplePoint1, samplePoint2);
        MotionUtils.interpolatePoints(initialPoints, 6).forEach(p -> {
            final Color strokeColor;
            final float strokeWeight;
            if (initialPoints.contains(p)) {
                strokeColor = Color.BLUE;
                strokeWeight = 5f;
            } else {
                strokeColor = Color.DARK_GRAY;
                strokeWeight = 1f;
            }
            
            stroke(strokeColor.getRGB());
            strokeWeight(strokeWeight);
            point((float) p.getX(), (float) p.getY());
        });
        /*
        line((float) samplePoint1.getX(), (float) samplePoint1.getY(),
                (float) samplePoint2.getX(), (float) samplePoint2.getY());
        
        final float ease = MotionUtils.ease(t, 8);
        final float x = lerp((float) samplePoint1.getX(), (float) samplePoint2.getX(), ease);
        final float y = lerp((float) samplePoint1.getY(), (float) samplePoint2.getY(), ease);
        
        stroke(Color.RED.getRGB());
        strokeWeight(12f);
        point(x, y);
*/
        
        if (frameCount % NUM_FRAMES == 0) {
            counterClockwise = !counterClockwise;
        }
    }
    
    private List<Point> getSamplePositions(final int samples) {
        if (samples <= 0) {
            return Collections.emptyList();
        }
        
        final List<Point> positions = new ArrayList<>();
        
        final float sampleStep = 1f / samples;
        float sampleTime = 0;
        for (int i = 0; i < samples; i++) {
            positions.add(getCirclePoint(sampleTime));
            sampleTime += sampleStep;
        }
        
        return positions;
    }
    
    private Point getCirclePoint(final float t) {
        final int x = (int) (width / 2 + R * cos(TWO_PI * t));
        final int y = (int) (height / 2 + R * sin(TWO_PI * t));
        
        return new Point(x, y);
    }
}
