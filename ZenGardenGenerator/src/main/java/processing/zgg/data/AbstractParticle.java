/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.data;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import processing.core.PVector;
import processing.zgg.utils.MotionUtils;

/**
 *
 * @author gestorum
 */
@Data
@SuperBuilder
public abstract class AbstractParticle {
    
    private PVector position;
    private PVector velocity;
    private PVector acceleration;
    
    private float radius;
    private boolean dead;
    private int speedUpFactor;
    private int personalSpaceRadiusFactor;

    public void update() {
        if (this.velocity == null || this.position == null || this.acceleration == null) {
            throw new RuntimeException("Cannot update particle because velocity, position or acceleration has not been defined!");
        }
        
        this.velocity.add(this.acceleration);
        final int curSpeedUpFactor = Math.max(this.speedUpFactor, 1);
        this.velocity.limit(getMaxSpeed() * curSpeedUpFactor);
        this.position.add(this.velocity);
        this.acceleration.mult(0);
    }

    public void applyForce(@NonNull final PVector force) {
        if (this.acceleration == null) {
            throw new RuntimeException("Cannot apply force because acceleration has not been defined!");
        }
        
        this.acceleration.add(force);
    }
    
    public void seek(@NonNull final PVector target) {
        if (this.position == null || this.velocity == null) {
            throw new RuntimeException("Cannot seek target because either position or velocity has not been defined!");
        }
        
        final int curSpeedUpFactor = Math.max(this.speedUpFactor, 1);
        final float curMaxSpeed = getMaxSpeed() * curSpeedUpFactor;
        final float curMaxForce = getMaxForce() * curSpeedUpFactor;
        final int curPersonalSpaceRadiusFactor = Math.max(this.personalSpaceRadiusFactor, 1);
        final float curRadius = this.radius * curPersonalSpaceRadiusFactor;
        
        final PVector desired = PVector.sub(target, this.position);
        if (desired.mag() < curRadius) {
            final float moderatedSpeed = MotionUtils.map(desired.mag(), 0,
                    curRadius, 0, curMaxSpeed);
            desired.setMag(moderatedSpeed);
        } else {
            desired.setMag(curMaxSpeed);
        }
        
        final PVector steer = PVector.sub(desired, this.velocity);
        steer.limit(curMaxForce);
        this.applyForce(steer);
    }
    
    public boolean isCollisionDetected(@NonNull final PVector target) {
        final int curPersonalSpaceRadiusFactor = Math.max(this.personalSpaceRadiusFactor, 1);
        final float curRadius = this.radius * curPersonalSpaceRadiusFactor;
        final PVector diff = PVector.sub(target, this.position);
        return diff.mag() < curRadius;
    }
    
    public boolean isCollisionDetected(@NonNull final AbstractParticle particle) {
        return isCollisionDetected(particle.getPosition());
    }
    
    public abstract float getMaxSpeed();
    public abstract float getMaxForce();
}
