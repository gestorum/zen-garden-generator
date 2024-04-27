package processing.zgg.particle;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import processing.zgg.particle.data.AbstractParticle;
import processing.zgg.particle.data.ParticleTree;
import processing.zgg.particle.event.ParticleSystemEvent;
import processing.zgg.particle.event.ParticleSystemEventListener;

/**
 *
 * @author gestorum
 */
public abstract class AbstractParticleSystem<T> implements ParticleSystem<T> {
    
    protected T system;
    protected int width;
    protected int height;
    protected int depth;
    
    private int speedUpFactor;
    
    private Set<ParticleSystemEventListener> eventListeners = new HashSet<>();
    private ParticleTree particleTree;

    public AbstractParticleSystem(final T system) {
        this.system = system;
    }
    
    @Override
    public void init(final int width, final int height, final int depth) {
        this.particleTree = new ParticleTree();
    }
    
    @Override
    public int getSpeedUpFactor() {
        return this.speedUpFactor;
    }
    
    @Override    
    public void setSpeedUpFactor(final int factor) {
        if (factor < 0) {
            throw new IllegalArgumentException("Cannot set a negative factor!");
        }
                
        this.speedUpFactor = factor;
    }
    
    @Override
    public void addEventListener(@NonNull final ParticleSystemEventListener eventListener) {
        eventListeners.add(eventListener);
    }
    
    @Override
    public void removeEventListener(@NonNull final ParticleSystemEventListener eventListener) {
        eventListeners.remove(eventListener);
    }
    
    protected void publishEvent(@NonNull ParticleSystemEvent event) {
        eventListeners.forEach(l -> l.processEvent(event));
    }
    
    protected void purgeDeadParticles(@NonNull final Collection<? extends AbstractParticle> particles) {
        particles.removeAll(particles.stream().filter(AbstractParticle::isDead).toList());
    }
    
    protected List<? extends AbstractParticle> findCollisions(@NonNull AbstractParticle particle) {
        return findNearestNeighbors(particle).stream()
                .filter(particle::isCollisionDetected).toList();
    }
    
    protected void update(@NonNull AbstractParticle particle) {
        particle.update();
        
        particleTree.update(particle);
    }
    
    private List<? extends AbstractParticle> findNearestNeighbors(@NonNull AbstractParticle particle) {
        return particleTree.findNearestNeighbors(particle);
    }
}
