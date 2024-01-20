/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.sketch.rss.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import processing.zgg.data.AbstractParticle;

/**
 *
 * @author pbergeron
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class RideableParticle extends AbstractParticle {
    
    private static final float MAX_SPEED = 0.2f;
    private static final float MAX_FORCE = 8;
    
    private String id;
    private String stationId;

    @Override
    public float getMaxSpeed() {
        return MAX_SPEED;
    }

    @Override
    public float getMaxForce() {
        return MAX_FORCE;
    }
}
