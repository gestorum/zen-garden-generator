/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.data;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import processing.zgg.data.AbstractParticle;

/**
 *
 * @author gestorum
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Builder
public class GenericParticle extends AbstractParticle {
    
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private String id = UUID.randomUUID().toString();
    
    private float maxVelocityMagnitude;
    private float maxForceMagnitude;
}
