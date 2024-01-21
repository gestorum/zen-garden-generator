/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package processing.zgg.data;

import java.util.List;

/**
 *
 * @author pbergeron
 */
public interface ParticleSystem<T> {
    
    void init(int width, int height);
    void update();
    
    List<Particle> getParticles();
    
    int getSpeedUpFactor();
    void setSpeedUpFactor(int factor);
}