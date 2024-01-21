/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.sketch.tuto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import processing.core.PVector;
import processing.zgg.audio.ZenGardenSoundGenerator;
import processing.zgg.data.GenericParticle;
import processing.zgg.sketch.ZenGardenSketch;

/**
 *
 * @author gestorum
 */
public class SphereCollisionSketch extends ZenGardenSketch {

    private static final int INITIAL_MAP_WIDTH = 800;
    private static final int INITIAL_MAP_HEIGHT = INITIAL_MAP_WIDTH;

    private static final int FRAME_RATE = 24;

    private final List<GenericParticle> particles = new ArrayList<>();

    private float x, y, z;

    @Override
    public void settings() {
        size(INITIAL_MAP_WIDTH, INITIAL_MAP_HEIGHT, P3D);

        initSoundGenerator();

        final GenericParticle p1 = GenericParticle.builder()
                .position(new PVector(10, 10, 10))
                .velocity(new PVector())
                .acceleration(new PVector())
                .radius(100)
                .personalSpaceRadiusFactor(1)
                .maxVelocityMagnitude(0.7f)
                .maxForceMagnitude(8)
                .build();
        particles.add(p1);

        final GenericParticle p2 = GenericParticle.builder()
                .position(new PVector(400, 400, 25))
                .velocity(p1.getVelocity().copy())
                .acceleration(p1.getAcceleration().copy())
                .radius(p1.getRadius() * 2)
                .personalSpaceRadiusFactor(p1.getPersonalSpaceRadiusFactor())
                .maxVelocityMagnitude(p1.getMaxVelocityMagnitude())
                .maxForceMagnitude(p1.getMaxForceMagnitude())
                .build();
        particles.add(p2);

        p1.seek(p2);
        p2.seek(p1);

        x = width / 2;
        y = height / 2;
        z = 0;
    }

    @Override
    protected void drawFrame() {
        translate(x, y, z);

        final float t = ((frameCount % FRAME_RATE) / (float) FRAME_RATE);

        particles.forEach(p -> {
            p.update();

            final PVector position = p.getPosition();

            push();
            translate(position.x, position.y, position.z);
            noFill();
            stroke(255);

            if (t > 0) {
                rotateX(PI / t);
                rotateZ(-PI / t);
            }

            sphere(p.getRadius());
            pop();

            final Optional<GenericParticle> otherParticleOpt = particles.stream()
                    .filter(op -> !op.equals(p) && p.isCollisionDetected(op))
                    .findFirst();
            if (otherParticleOpt.isPresent()) {
                zenGardenSoundGenerator.playFreq(ZenGardenSoundGenerator.Instrument.WHITE_WAVE,
                        ZenGardenSoundGenerator.Amplitude.MAX.getValue(),
                        220, 1f, null);

                otherParticleOpt.get().applyForce(p.getVelocity());
                p.applyForce(otherParticleOpt.get().getVelocity());
                p.seek(p);
                otherParticleOpt.get().seek(otherParticleOpt.get());
            }
        });
    }
}
