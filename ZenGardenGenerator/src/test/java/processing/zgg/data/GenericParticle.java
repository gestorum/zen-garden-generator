/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import processing.zgg.data.AbstractParticle;

/**
 *
 * @author gestorum
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class GenericParticle extends AbstractParticle {
    
    private float maxVelocityMagnitude;
    private float maxForceMagnitude;
}
