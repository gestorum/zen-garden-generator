/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.sketch.rss.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import processing.core.PVector;
import processing.zgg.data.AbstractParticle;

/**
 *
 * @author gestorum
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class StationParticle extends AbstractParticle {
    
    private static final float MAX_VELOCITY_MAGNITUDE = 0;
    private static final float MAX_FORCE_MAGNITUDE = 0;
    
    private String id;
    
    @Override
    public void setVelocity(final PVector velocity) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setAcceleration(final PVector acceleration) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public float getMaxVelocityMagnitude() {
        return MAX_VELOCITY_MAGNITUDE;
    }

    @Override
    public float getMaxForceMagnitude() {
        return MAX_FORCE_MAGNITUDE;
    }
}
