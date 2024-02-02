/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package processing.zgg.particle;

import java.util.List;
import processing.zgg.particle.data.AbstractParticle;
import processing.zgg.particle.event.ParticleSystemEventListener;

/**
 *
 * @author gestorum
 */
public interface ParticleSystem<T> {
    
    void init(int width, int height, int depth);
    void update();
    
    List<AbstractParticle> getParticles();
    
    int getSpeedUpFactor();
    void setSpeedUpFactor(int factor);
    
    void addEventListener(ParticleSystemEventListener eventListener);
    void removeEventListener(ParticleSystemEventListener eventListener);
}
