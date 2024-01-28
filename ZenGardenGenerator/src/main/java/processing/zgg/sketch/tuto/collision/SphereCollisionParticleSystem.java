/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.sketch.tuto.collision;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.NonNull;
import processing.core.PVector;
import processing.zgg.particle.AbstractParticleSystem;
import processing.zgg.particle.data.AbstractParticle;
import processing.zgg.particle.data.GenericParticle;

/**
 *
 * @author gestorum
 */
public class SphereCollisionParticleSystem
        extends AbstractParticleSystem<GenericParticle> {

    private final List<AbstractParticle> particles = new ArrayList<>();
    
    private float halfWidth;
    private float halfHeight;
    private float halfDepth;
    
    public SphereCollisionParticleSystem() {
        super(null);
    }
    
    @Override
    public void init(final int width, final int height, final int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        
        this.halfWidth = width / 2;
        this.halfHeight = height / 2;
        this.halfDepth = depth / 2;
    }

    @Override
    public void update() {
        purgeDeadParticles(particles);
        
        particles.forEach(p -> {
            final PVector position = p.getPosition();
            
            final float minWidthLimit = halfWidth * -1 + p.getEffectiveRadius();
            if (position.x < minWidthLimit) {
                position.x = minWidthLimit;
                p.getVelocity().x *= -1;
            } else {
                final float maxWidthLimit = halfWidth - p.getEffectiveRadius();
                if (position.x > maxWidthLimit) {
                    position.x = maxWidthLimit;
                    p.getVelocity().x *= -1;
                }
            }

            final float minHeightLimit = halfHeight * -1 + p.getEffectiveRadius();
            if (position.y < minHeightLimit) {
                position.y = minHeightLimit;
                p.getVelocity().y *= -1;
            } else {
                final float maxHeightLimit = halfHeight - p.getEffectiveRadius();
                if (position.y > maxHeightLimit) {
                    position.y = maxHeightLimit;
                    p.getVelocity().y *= -1;
                }
            }

            final float minDepthLimit = halfDepth * -1 + p.getEffectiveRadius();
            if (position.z < minDepthLimit) {
                position.z = minDepthLimit;
                p.getVelocity().z *= -1;
            } else {
                final float maxDepthLimit = halfDepth - p.getEffectiveRadius();
                if (position.z > maxDepthLimit) {
                    position.z = maxDepthLimit;
                    p.getVelocity().z *= -1;
                }
            }

            final Optional<AbstractParticle> otherParticleOpt = particles.stream()
                    .filter(op -> !op.equals(p) && p.isCollisionDetected(op))
                    .findFirst();
            if (otherParticleOpt.isPresent()) {
                final AbstractParticle otherParticle = otherParticleOpt.get();
                otherParticle.unseek();
                p.unseek();

                otherParticle.applyForce(p.getVelocity());
                p.applyForce(otherParticle.getVelocity());
            }
            
            p.update();
        });
    }
    
    @Override
    public List<AbstractParticle> getParticles() {
        return particles;
    }
    
    public void generateParticules() {
        final float minDimension = Math.min(width, height);
        final float minRadius = minDimension / 7;
        final float maxRadius = minDimension / 5;
        final float gap = minRadius;

        float pX = 0 - minDimension * 0.2f;
        float pY = pX;
        float pZ = pY;

        final List<PVector> positions = List.of(
                new PVector(pX, pY, pZ),
                new PVector(pX + maxRadius + gap, pY, pZ + maxRadius + gap),
                new PVector(pX, pY + maxRadius + gap, pZ + maxRadius + gap),
                new PVector(pX + maxRadius + gap, pY + maxRadius + gap, pZ)
        );

        final Random random = new Random();

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
    
    private void randomSeek(@NonNull final AbstractParticle p) {
        final Random random = new Random();

        final List<AbstractParticle> otherParticles = particles.stream()
                .filter(op -> !op.equals(p)).toList();

        if (!otherParticles.isEmpty()) {
            final int randomIndex = random
                    .ints(0, otherParticles.size())
                    .findFirst().getAsInt();

            p.seek(otherParticles.get(randomIndex));
        }
    }

    private void randomSeek() {
        particles.forEach(this::randomSeek);
    }
}
