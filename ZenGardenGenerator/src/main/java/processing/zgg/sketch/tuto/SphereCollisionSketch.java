/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.sketch.tuto;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import processing.core.PVector;
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

    @Override
    public void settings() {
        size(INITIAL_MAP_WIDTH, INITIAL_MAP_HEIGHT, P3D);

        final GenericParticle p1 = GenericParticle.builder()
                .position(new PVector(120, 120, 0))
                .velocity(new PVector())
                .acceleration(new PVector())
                .radius(100)
                .personalSpaceRadiusFactor(1)
                .maxVelocityMagnitude(7.75f)
                .maxForceMagnitude(8)
                .build();
        particles.add(p1);

        final GenericParticle p2 = GenericParticle.builder()
                .position(new PVector(600, 200, 0))
                .velocity(p1.getVelocity().copy())
                .acceleration(p1.getAcceleration().copy())
                .radius(p1.getRadius() * 1.5f)
                .personalSpaceRadiusFactor(p1.getPersonalSpaceRadiusFactor())
                .maxVelocityMagnitude(3.25f)
                .maxForceMagnitude(9)
                .build();
        particles.add(p2);
        
        final GenericParticle p3 = GenericParticle.builder()
                .position(new PVector(90, 700, 0))
                .velocity(p1.getVelocity().copy())
                .acceleration(p1.getAcceleration().copy())
                .radius(p1.getRadius() * 0.75f)
                .personalSpaceRadiusFactor(p1.getPersonalSpaceRadiusFactor())
                .maxVelocityMagnitude(9.25f)
                .maxForceMagnitude(4)
                .build();
        particles.add(p3);
        
        final GenericParticle p4 = GenericParticle.builder()
                .position(new PVector(580, 640, 0))
                .velocity(p1.getVelocity().copy())
                .acceleration(p1.getAcceleration().copy())
                .radius(p1.getRadius() * 1.1f)
                .personalSpaceRadiusFactor(p1.getPersonalSpaceRadiusFactor())
                .maxVelocityMagnitude(6.75f)
                .maxForceMagnitude(12)
                .build();
        particles.add(p4);

        randomSeek();
    }

    @Override
    protected void drawFrame() {
        final float t = ((frameCount % FRAME_RATE) / (float) FRAME_RATE);

        noFill();
        stroke(Color.GRAY.getRGB());
        strokeWeight(1);
        rect(0, 0, width, height);
        
        particles.forEach(p -> {
            final PVector position = p.getPosition();

            push();
            translate(position.x, position.y, position.z);
            noFill();
            stroke(Color.WHITE.getRGB());

            if (p.getPosition().x - p.getEffectiveRadius() < 0
                    || p.getPosition().x + p.getEffectiveRadius() > width
                    || p.getPosition().y - p.getEffectiveRadius() < 0
                    || p.getPosition().y + p.getEffectiveRadius() > height) {
                p.unseek();
                
                if (p.getVelocity().mag() > 0) {
                    p.applyForce(PVector.mult(p.getVelocity(), -1f));
                }
            } else {
                final Optional<GenericParticle> otherParticleOpt = particles.stream()
                        .filter(op -> !op.equals(p) && p.isCollisionDetected(op))
                        .findFirst();
                if (otherParticleOpt.isPresent()) {
                    final GenericParticle otherParticle = otherParticleOpt.get();
                    otherParticle.unseek();
                    p.unseek();

                    otherParticle.applyForce(p.getVelocity());
                    p.applyForce(otherParticle.getVelocity());
                }
            }

            if (t > 0 && p.getVelocity().mag() != 0) {
                final float signum = Math.signum(p.getVelocity().heading()) * -1;
                rotateX(signum * PI / t);
            }

            rotateZ(-PI / 6);
            
            p.update();
            sphere(p.getRadius());
            
            pop();
        });
    }
    
    @Override
    public void keyPressed() {
        super.keyPressed();
        
        switch (Character.toLowerCase(key)) {
            case 's' -> randomSeek();
        }
    }
    
    private void randomSeek() {
        final Random random = new Random();
        
        particles.forEach(p -> {
            final List<GenericParticle> otherParticles = particles.stream()
                    .filter(op -> !op.equals(p)).toList();
            
            final int randomIndex = random
                    .ints(0, otherParticles.size())
                    .findFirst().getAsInt();
        
            p.seek(otherParticles.get(randomIndex));
        });
    }
}
