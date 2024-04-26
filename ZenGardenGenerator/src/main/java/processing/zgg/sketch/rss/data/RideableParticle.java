package processing.zgg.sketch.rss.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import processing.zgg.particle.data.AbstractParticle;

/**
 *
 * @author gestorum
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class RideableParticle extends AbstractParticle {
    
    private static final float MAX_VELOCITY_MAGNITUDE = 0.2f;
    private static final float MAX_FORCE_MAGNITUDE = 8;
    
    private String id;
    private String stationId;

    @Override
    public float getMaxVelocityMagnitude() {
        return MAX_VELOCITY_MAGNITUDE;
    }

    @Override
    public float getMaxForceMagnitude() {
        return MAX_FORCE_MAGNITUDE;
    }
}
