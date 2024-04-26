package processing.zgg.sketch;

import java.awt.Color;
import java.time.Instant;
import lombok.NonNull;
import processing.core.PApplet;
import processing.zgg.audio.ZenGardenSoundGenerator;

/**
 *
 * @author gestorum
 */
public abstract class ZenGardenSketch extends PApplet {

    protected static final Color DEFAULT_BACKGROUND_COLOR = Color.BLACK;
    protected static final Color DEFAULT_FRAME_CAPTURE_BACKGROUND_COLOR = Color.WHITE;

    private static final String FRAME_RECORDING_FILENAME = "output/recording/%s-######.png";
    private static final String WAVE_RECORDING_FILENAME = "output/recording/audio/%s-######.wav";
    private static final String FRAME_CAPTURE_FILENAME = "output/capture/%s-%s.png";
    
    private Color backgroundColor = DEFAULT_BACKGROUND_COLOR;
    private Color frameCaptureBackgroundColor = DEFAULT_FRAME_CAPTURE_BACKGROUND_COLOR;

    protected boolean soundGeneratorEnabled = false;
    protected boolean pauseMode = false;
    protected boolean recording = false;

    protected ZenGardenSoundGenerator zenGardenSoundGenerator;
    
    @Override
    public void setup() {
        hint(ENABLE_ASYNC_SAVEFRAME);
    }
    
    @Override
    public final void draw() {
        if (pauseMode) {
            return;
        }
        
        background(getBackgroundColorRGB());

        drawFrame();

        if (recording) {
            saveFrame(String.format(FRAME_RECORDING_FILENAME,
                    getFrameRecordingFilenamePrefix()));
        }

        refreshTitle();
    }

    @Override
    public void keyPressed() {
        switch (Character.toLowerCase(key)) {
            case 's' ->
                soundGeneratorEnabled = !soundGeneratorEnabled;

            case 'p' -> {
                pauseMode = !pauseMode;
                refreshTitle();
            }

            case 'c' -> {
                saveFrame(String.format(FRAME_CAPTURE_FILENAME,
                        getFrameCaptureFilenamePrefix(), Instant.now().toString()));

                if (!pauseMode) {
                    background(getFrameCaptureBackgroundColorRGB());
                }

                playFreq(ZenGardenSoundGenerator.Instrument.NOISE_HIT,
                        ZenGardenSoundGenerator.Amplitude.MID.getValue(),
                        null, null, null);
            }

            case 'r' -> {
                recording = !recording;
                
                if (zenGardenSoundGenerator != null) {
                    if (recording) {
                        if (soundGeneratorEnabled) {
                            zenGardenSoundGenerator.startRecorder(
                                    getWaveRecordingFilename());
                        }
                    } else {
                        zenGardenSoundGenerator.stopRecorder();
                    }
                }
            }
        }
    }

    protected Color getBackgroundColor() {
        return backgroundColor;
    }

    protected int getBackgroundColorRGB() {
        return backgroundColor != null ? backgroundColor.getRGB() : -1;
    }

    protected void setBackgroundColor(@NonNull final Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    protected Color getFrameCaptureBackgroundColor() {
        return frameCaptureBackgroundColor;
    }

    protected int getFrameCaptureBackgroundColorRGB() {
        return frameCaptureBackgroundColor != null ? frameCaptureBackgroundColor.getRGB() : -1;
    }

    protected void setFrameCaptureBackgroundColor(@NonNull final Color backgroundColor) {
        this.frameCaptureBackgroundColor = backgroundColor;
    }

    protected void refreshTitle() {
        final String currentState;

        if (pauseMode) {
            currentState = "[PAUSED] ";
        } else if (recording) {
            currentState = "[REC] ";
        } else {
            currentState = "";
        }

        final String title = currentState + getWindowTitle();
        surface.setTitle(title);
    }

    protected void initSoundGenerator() {
        try {
            zenGardenSoundGenerator = new ZenGardenSoundGenerator();
            soundGeneratorEnabled = true;
        } catch (Exception e) {
            System.err.println("Cannot initialize sound engine!");
        }
    }

    protected void playFreq(@NonNull final ZenGardenSoundGenerator.Instrument instrument,
            final float amplitude, final Integer freq, final Float duration,
            final Float pan) {
        if (zenGardenSoundGenerator == null || !soundGeneratorEnabled) {
            return;
        }

        zenGardenSoundGenerator.playFreq(instrument, amplitude, freq, duration, pan);
    }
    
    protected String getFrameRecordingFilenamePrefix() {
        return getSketchName().toLowerCase();
    }
    
    protected String getWaveRecordingFilename() {
        return insertFrame(String.format(WAVE_RECORDING_FILENAME,
                getFrameRecordingFilenamePrefix()));
    }

    protected String getFrameCaptureFilenamePrefix() {
        return getSketchName().toLowerCase();
    }

    protected String getSketchName() {
        return getClass().getSimpleName();
    }
    
    protected String getWindowTitle() {
        return getSketchName();
    }
    
    protected abstract void drawFrame();
}
