package processing.zgg.audio.instrument;

import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.Circuit;
import com.jsyn.unitgen.EnvelopeAttackDecay;
import com.jsyn.unitgen.FilterLowPass;
import com.jsyn.unitgen.PassThrough;
import com.jsyn.unitgen.UnitVoice;
import com.jsyn.unitgen.WhiteNoise;
import com.softsynth.shared.time.TimeStamp;

/**
 *
 * @author gestorum
 */
public class WhiteWave extends Circuit implements UnitVoice {
    // Declare units and ports.
    private final PassThrough mAmplitudePassThrough;
    private final UnitInputPort amplitude;
    private final WhiteNoise mWhiteNoise;
    private final PassThrough mOutputPassThrough;
    private final UnitOutputPort output;
    private final FilterLowPass mLowPass;
    private final EnvelopeAttackDecay mEnvAD;
    private final PassThrough mFrequencyPassThrough;
    private final UnitInputPort frequency;

    // Declare inner classes for any child circuits.

    public WhiteWave() {
        // Create unit generators.
        add(mAmplitudePassThrough = new PassThrough());
        addPort(amplitude = mAmplitudePassThrough.input, "amplitude");
        add(mWhiteNoise = new WhiteNoise());
        add(mOutputPassThrough = new PassThrough());
        addPort(output = mOutputPassThrough.output, "output");
        add(mLowPass = new FilterLowPass());
        add(mEnvAD = new EnvelopeAttackDecay());
        add(mFrequencyPassThrough = new PassThrough());
        addPort(frequency = mFrequencyPassThrough.input, "frequency");
        
        // Connect units and ports.
        mAmplitudePassThrough.output.connect(mWhiteNoise.amplitude);
        mWhiteNoise.output.connect(mLowPass.input);
        mLowPass.output.connect(mEnvAD.amplitude);
        mEnvAD.output.connect(mOutputPassThrough.input);
        mFrequencyPassThrough.output.connect(mLowPass.frequency);
        
        mLowPass.Q.set(5.5);
        mEnvAD.input.set(0.0);
        mEnvAD.attack.set(0.8);
        mEnvAD.decay.set(4.6);
    }

    public void noteOn(double frequency, double amplitude, TimeStamp timeStamp) {
        this.amplitude.set(amplitude, timeStamp);
        this.frequency.set(frequency, timeStamp);
        
        mEnvAD.input.on(timeStamp);
    }

    public void noteOff(TimeStamp timeStamp) {
        mEnvAD.input.off(timeStamp);
    }
    
    public UnitOutputPort getOutput() {
        return output;
    }
}
