/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package processing.zgg.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import processing.core.PVector;

/**
 *
 * @author gestorum
 */
public class GenericParticleCollisionTest {
    
    private static final int MAX_COUNT = 50;
    
    @ParameterizedTest
    @CsvSource({"2,1,23", "2,2,11", "2,3,1", "3,1,31", "3,2,19", "3,3,6"})
    public void fullFrontalCollision(final int dimensions, final int personalSpaceFactor,
            final int expectedCount) {
        final PVector p1Position;
        final PVector p2Position;
        
        switch (dimensions) {
            case 2 -> {
                p1Position = new PVector(10, 10);
                p2Position = new PVector(20, 20);
            }
            
            case 3 -> {
                p1Position = new PVector(10, 10, 10);
                p2Position = new PVector(20, 20, 20);
            }
            
            default ->
                throw new IllegalArgumentException();
        }
        
        final GenericParticle p1 = GenericParticleFactory.build(p1Position);
        p1.setPersonalSpaceRadiusFactor(personalSpaceFactor);
        
        final GenericParticle p2 = GenericParticleFactory.build(p2Position);
        p2.setPersonalSpaceRadiusFactor(personalSpaceFactor);
        
        p1.seek(p2.getPosition());
        p2.seek(p1.getPosition());
        
        System.out.println(String.format("--- %dD; personalSpaceFactor: %d ---",
                dimensions, personalSpaceFactor));
        
        int count = 0;
        float distance;
        do {
            p1.update();
            p2.update();
            
            distance = PVector.dist(p1.getPosition(), p2.getPosition());
            System.out.println(String.format("distance: %.3f", distance));
            
            count++;
        } while (!p1.isCollisionDetected(p2) && count < MAX_COUNT);
        
        assertTrue(distance < p1.getRadius() * p1.getPersonalSpaceRadiusFactor());
        assertEquals(expectedCount, count);
    }
}
