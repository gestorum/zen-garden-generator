/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package processing.zgg.data;

import processing.core.PVector;

/**
 *
 * @author pbergeron
 */
public interface Particle {
    
    PVector getPosition();
    void setPosition(PVector position);
    
    PVector getVelocity();
    void setVelocity(PVector velocity);
    
    PVector getAcceleration();
    void setAcceleration(PVector acceleration);
    
    float getMaxSpeed();
    float getMaxForce();
    
    float getRadius();
    void setRadius(float radius);
    
    boolean isDead();
    void setDead(boolean dead);
    
    int getSpeedUpFactor();
    void setSpeedUpFactor(int factor);
    
    int getSlowDownRadiusFactor();
    void setSlowDownRadiusFactor(int factor);
    
    void update();
    void applyForce(PVector force);
    void seek(PVector target);
    boolean isCollisionDetected(PVector target);
    boolean isCollisionDetected(Particle particle);
}
