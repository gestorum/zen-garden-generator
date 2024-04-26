package processing.zgg.particle.event;

import processing.zgg.particle.data.AbstractParticle;

/**
 *
 * @author gestorum
 */
public interface ParticleSystemEvent {
 
    ParticleSystemEventType getType();
    AbstractParticle getParticle();
}
