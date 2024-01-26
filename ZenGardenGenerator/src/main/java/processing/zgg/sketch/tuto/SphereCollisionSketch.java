/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.sketch.tuto;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import lombok.NonNull;
import processing.core.PVector;
import processing.zgg.audio.ZenGardenSoundGenerator;
import processing.zgg.data.AbstractParticle;
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
    
    private static final Color BIG_BOX_STROKE_COLOR = Color.DARK_GRAY;
    private static final float BIG_BOX_ROTATION_STEP = 0.2f;
    
    private static final Color SPHERE_FILL_COLOR = Color.WHITE;
    private static final Color SPHERE_STROKE_COLOR = Color.GREEN;
    private static final Color SPHERE_COLLISION_STROKE_COLOR = Color.RED;
    
    private static final Color DIRECTIONAL_LIGHT_COLOR = Color.ORANGE;
    
    private static final float EARTH_GRAVITY = 0.1f;
    private static final PVector GRAVITY_FORCE = new PVector(0, EARTH_GRAVITY, 0);

    private static final long COLLISION_DEATH_THRESHOLD_MILLIS = 3000;
    
    private final List<GenericParticle> particles = new ArrayList<>();
    private final Map<String, Long> firstCollisionMillisByParticuleId = new HashMap<>();

    private float x, y, z;
    
    private float bigBoxDimension;
    private float halfBigBoxDimension;
    private PVector bigBoxRotation;
    private PVector directionalLightDirection;
    
    private final boolean applyGravityForce = true;

    @Override
    public void settings() {
        size(INITIAL_MAP_WIDTH, INITIAL_MAP_HEIGHT, P3D);
        
        initSoundGenerator();
        smooth(8);
    }

    @Override
    public void setup() {
        super.setup();

        frameRate(FRAME_RATE);

        surface.setResizable(true);

        windowResized();
    }

    @Override
    protected void drawFrame() {
        final float t = ((frameCount % FRAME_RATE) / (float) FRAME_RATE);

        translate(x, y, z);

        noFill();
        stroke(BIG_BOX_STROKE_COLOR.getRGB());
        strokeWeight(1);
        rotateX(bigBoxRotation.x);
        rotateY(bigBoxRotation.y);
        box(bigBoxDimension);

        particles.forEach(p -> {
            final PVector position = p.getPosition();
            
            push();
            translate(position.x, position.y, position.z);

            final float bigBoxMinLimit = (halfBigBoxDimension * -1) + p.getEffectiveRadius();
            final float bigBoxMaxLimit = halfBigBoxDimension - p.getEffectiveRadius();

            if (p.getPosition().x < bigBoxMinLimit) {
                playHitBorderSound(-1);
                p.getPosition().x = bigBoxMinLimit;
                p.getVelocity().x *= -1;
            }

            if (p.getPosition().x > bigBoxMaxLimit) {
                playHitBorderSound(1);
                p.getPosition().x = bigBoxMaxLimit;
                p.getVelocity().x *= -1;
            }

            if (p.getPosition().y < bigBoxMinLimit) {
                playHitBorderSound(0);
                p.getPosition().y = bigBoxMinLimit;
                p.getVelocity().y *= -1;
            }

            if (p.getPosition().y > bigBoxMaxLimit) {
                playHitBorderSound(0);
                p.getPosition().y = bigBoxMaxLimit;
                p.getVelocity().y *= -1;
            }

            if (p.getPosition().z < bigBoxMinLimit) {
                playHitBorderSound(0);
                p.getPosition().z = bigBoxMinLimit;
                p.getVelocity().z *= -1;
            }

            if (p.getPosition().z > bigBoxMaxLimit) {
                playHitBorderSound(0);
                p.getPosition().z = bigBoxMaxLimit;
                p.getVelocity().z *= -1;
            }

            final Color strokeColor;
            final Optional<GenericParticle> otherParticleOpt = particles.stream()
                    .filter(op -> !op.equals(p) && p.isCollisionDetected(op))
                    .findFirst();
            if (otherParticleOpt.isPresent()) {
                playSphereCollisionSound(p.getPosition().x / width);
                strokeColor = SPHERE_COLLISION_STROKE_COLOR;
                
                final GenericParticle otherParticle = otherParticleOpt.get();
                otherParticle.unseek();
                p.unseek();

                otherParticle.applyForce(p.getVelocity());
                p.applyForce(otherParticle.getVelocity());
                
                final long now = System.currentTimeMillis();
                final Long lastCollision = firstCollisionMillisByParticuleId.get(p.getId());
                
                if (lastCollision == null) {
                    firstCollisionMillisByParticuleId.put(p.getId(), now);
                } else if ((now - lastCollision.longValue()) > COLLISION_DEATH_THRESHOLD_MILLIS) {
                    playSphereMergeSound(0);
                    firstCollisionMillisByParticuleId.remove(p.getId());
                    p.setDead(true);
                    otherParticle.setRadius(otherParticle.getRadius() + p.getRadius() / 2);
                }
            } else {
                firstCollisionMillisByParticuleId.remove(p.getId());
                strokeColor = SPHERE_STROKE_COLOR;
            }

            if (applyGravityForce) {
                p.applyForce(GRAVITY_FORCE);
            }
            
            p.update();

            if (t > 0 && p.getVelocity().mag() != 0) {
                final float signum = Math.signum(p.getVelocity().heading()) * -1;
                rotateX(signum * PI / t);
            }

            rotateZ(-PI / 6);

            stroke(strokeColor.getRGB());
            fill(SPHERE_FILL_COLOR.getRGB());
            
            directionalLight(DIRECTIONAL_LIGHT_COLOR.getRed(),
                    DIRECTIONAL_LIGHT_COLOR.getGreen(),
                    DIRECTIONAL_LIGHT_COLOR.getBlue(),
                    directionalLightDirection.x,
                    directionalLightDirection.y,
                    directionalLightDirection.z);
            
            sphere(p.getRadius());

            pop();
        });
        
        particles.removeAll(particles.stream()
                .filter(AbstractParticle::isDead).toList());
    }
    
    @Override
    public void keyPressed() {
        super.keyPressed();
        
        switch (keyCode) {
            case UP -> {
                bigBoxRotation.x = (bigBoxRotation.x + BIG_BOX_ROTATION_STEP) % PI;
                directionalLightDirection.x += BIG_BOX_ROTATION_STEP % 1f;
            }

            case RIGHT -> {
                bigBoxRotation.y = (bigBoxRotation.y + BIG_BOX_ROTATION_STEP) % PI;
                directionalLightDirection.y += BIG_BOX_ROTATION_STEP % 1f;
            }

            case DOWN -> {
                bigBoxRotation.x = (bigBoxRotation.x - BIG_BOX_ROTATION_STEP) % -PI;
                directionalLightDirection.x -= BIG_BOX_ROTATION_STEP % -1f;
            }

            case LEFT -> {
                bigBoxRotation.y = (bigBoxRotation.y - BIG_BOX_ROTATION_STEP) % -PI;
                directionalLightDirection.y -= BIG_BOX_ROTATION_STEP % -1f;
            }
        }

        switch (Character.toLowerCase(key)) {
            case 'g' -> generateParticules();
        }
    }

    @Override
    public void windowResized() {
        super.windowResized();
        
        x = width / 2;
        y = height / 2;
        z = 0;

        bigBoxDimension = (x + y) / 2;
        halfBigBoxDimension = bigBoxDimension / 2;
        bigBoxRotation = new PVector(-PI / 6, PI / 3);
        directionalLightDirection = new PVector(0, -1, 0);
        
        generateParticules();
    }
    
    private void generateParticules() {
        final float minRadius = bigBoxDimension / 7;
        final float maxRadius = bigBoxDimension / 5;
        final float gap = minRadius;

        float pX = 0 - bigBoxDimension * 0.2f;
        float pY = pX;
        float pZ = pY;

        final List<PVector> positions = List.of(
                new PVector(pX, pY, pZ),
                new PVector(pX + maxRadius + gap, pY, pZ + maxRadius + gap),
                new PVector(pX, pY + maxRadius + gap, pZ + maxRadius + gap),
                new PVector(pX + maxRadius + gap, pY + maxRadius + gap, pZ)
        );

        final Random random = new Random();

        firstCollisionMillisByParticuleId.clear();
        particles.clear();
        
        positions.forEach(p -> {
            final float r = (float) random.doubles(minRadius, maxRadius + 1)
                    .findFirst().getAsDouble();
            final float velocityMag = (float) random.doubles(7, 21)
                    .findFirst().getAsDouble();
            final float forceMag = (float) random.doubles(7, 21)
                    .findFirst().getAsDouble();
            final float mass = (float) random.doubles(1, 1.26)
                    .findFirst().getAsDouble();

            particles.add(GenericParticle.builder()
                    .position(p)
                    .velocity(new PVector())
                    .acceleration(new PVector())
                    .radius(r)
                    .personalSpaceRadiusFactor(1)
                    .maxVelocityMagnitude(velocityMag)
                    .maxForceMagnitude(forceMag)
                    .mass(mass)
                    .build());
        });

        randomSeek();
    }
    
    private void randomSeek(@NonNull final GenericParticle p) {
        final Random random = new Random();

        final List<GenericParticle> otherParticles = particles.stream()
                .filter(op -> !op.equals(p)).toList();

        if (!otherParticles.isEmpty()) {
            final int randomIndex = random
                    .ints(0, otherParticles.size())
                    .findFirst().getAsInt();

            p.seek(otherParticles.get(randomIndex));
        }
    }

    private void randomSeek() {
        particles.forEach(p -> randomSeek(p));
    }
    
    private void playHitBorderSound(final float pan) {
        playFreq(ZenGardenSoundGenerator.Instrument.DRUM_WOOD,
                ZenGardenSoundGenerator.Amplitude.MID.getValue(),
                440, 1f, pan);
    }
    
    private void playSphereCollisionSound(final float pan) {
        playFreq(ZenGardenSoundGenerator.Instrument.WHITE_WAVE,
                ZenGardenSoundGenerator.Amplitude.MID.getValue(),
                440, 1f, pan);
    }
    
    private void playSphereMergeSound(final float pan) {
        playFreq(ZenGardenSoundGenerator.Instrument.SYNTH,
                ZenGardenSoundGenerator.Amplitude.MID.getValue(),
                580, 1f, pan);
    }
}
