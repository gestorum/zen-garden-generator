/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.audio;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.instruments.DrumWoodFM;
import com.jsyn.instruments.DualOscillatorSynthVoice;
import com.jsyn.instruments.NoiseHit;
import com.jsyn.instruments.SubtractiveSynthVoice;
import com.jsyn.instruments.WaveShapingVoice;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.MixerStereo;
import com.jsyn.unitgen.MixerStereoRamped;
import com.jsyn.unitgen.UnitVoice;
import com.jsyn.util.WaveRecorder;
import com.softsynth.shared.time.TimeStamp;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import processing.zgg.audio.instrument.WhiteWave;

/**
 *
 * @author pbergeron
 */
public class ZenGardenSoundGenerator {

    public enum Instrument {
        NOISE_HIT,
        DRUM_WOOD,
        SYNTH,
        DUAL_SYNTH,
        WAVE_SHAPING,
        // Custom instruments
        WHITE_WAVE
    }

    public enum Amplitude {
        MIN(0),
        LOW(0.25f),
        MID(0.5f),
        HIGH(0.75f),
        MAX(1);

        private final float value;

        private Amplitude(final float value) {
            this.value = value;
        }

        public float getValue() {
            return this.value;
        }
    }

    private final Synthesizer synth;
    private final MixerStereo preLineOut;
    private final Map<Instrument, UnitVoice> unitVoiceByInstrumentMap = new HashMap<>();
    
    private WaveRecorder waveRecorder;

    public ZenGardenSoundGenerator() {
        this.synth = JSyn.createSynthesizer();

        final LineOut lineOut = new LineOut();
        synth.add(lineOut);

        preLineOut = new MixerStereoRamped(2);
        preLineOut.output.connect(0, lineOut.input, 0);
        preLineOut.output.connect(1, lineOut.input, 1);

        final DrumWoodFM drumWood = new DrumWoodFM();
        unitVoiceByInstrumentMap.put(Instrument.DRUM_WOOD, drumWood);
        synth.add(drumWood);

        final NoiseHit noiseHit = new NoiseHit();
        unitVoiceByInstrumentMap.put(Instrument.NOISE_HIT, noiseHit);
        synth.add(noiseHit);

        final SubtractiveSynthVoice synthVoice = new SubtractiveSynthVoice();
        unitVoiceByInstrumentMap.put(Instrument.SYNTH, synthVoice);
        synth.add(synthVoice);

        final DualOscillatorSynthVoice dualSynthVoice = new DualOscillatorSynthVoice();
        unitVoiceByInstrumentMap.put(Instrument.DUAL_SYNTH, dualSynthVoice);
        synth.add(dualSynthVoice);

        final WaveShapingVoice waveShapingVoice = new WaveShapingVoice();
        unitVoiceByInstrumentMap.put(Instrument.WAVE_SHAPING, waveShapingVoice);
        synth.add(waveShapingVoice);

        // Custom instruments
        final WhiteWave whiteWave = new WhiteWave();
        unitVoiceByInstrumentMap.put(Instrument.WHITE_WAVE, whiteWave);
        synth.add(whiteWave);

        // Start synthesizer using default stereo output at 44100 Hz.
        synth.start();

        // We only need to start the LineOut. It will pull data from the
        // oscillator.
        lineOut.start();

    }

    public void playFreq(@NonNull final Instrument instrument,
            final float amplitude, final Integer freq, final Float duration,
            final Float pan) {
        final UnitVoice unitVoice = unitVoiceByInstrumentMap.get(instrument);
        if (unitVoice == null) {
            System.err.println("No JSyn unitVoice mapped to instrument "
                    + instrument.name());
        }

        new PlayThread(unitVoice, amplitude, freq, duration, pan).start();
    }

    public void startRecorder(final String waveRecordingFilename) {
        stopRecorder();
        
        try {
            final File waveRecordingFile = new File(waveRecordingFilename);
            waveRecordingFile.getParentFile().mkdirs();
            waveRecorder = new WaveRecorder(synth, waveRecordingFile);
            preLineOut.output.connect(0,
                    waveRecorder.getInput(), 0);
            preLineOut.output.connect(1,
                    waveRecorder.getInput(), 1);
            waveRecorder.start();
            
        } catch (IOException e) {
            System.err.println("Cannot start audio recorder to " + waveRecordingFilename);
        }
    }

    public void stopRecorder() {
        try {
            if (waveRecorder != null) {
                waveRecorder.stop();
                waveRecorder.close();
            }
        } catch (IOException e) {
            System.err.println("Cannot stop audio recorder!");
        }
    }
    
    private class PlayThread extends Thread {

        private final UnitVoice unitVoice;
        private final int freq;
        private final float amp;
        private final Float duration;

        public PlayThread(@NonNull final UnitVoice unitVoice,
                final float amp, final Integer freq, final Float duration,
                final Float pan) {
            this.unitVoice = unitVoice;
            this.amp = amp;
            this.freq = freq != null ? freq : 0;
            this.duration = duration;
            
            final float panValue = pan != null ? pan : 0;
            preLineOut.pan.set(panValue);
            unitVoice.getOutput().connect(0, preLineOut.input,
                    0);
        }

        @Override
        public void run() {
            unitVoice.noteOn(freq, amp, new TimeStamp(0));

            if (duration != null) {
                // Sleep while the sound is generated in the background.
                try {
                    double time = synth.getCurrentTime();
                    synth.sleepUntil(time + duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                unitVoice.noteOff(new TimeStamp(0));
            }
        }
    }
}
