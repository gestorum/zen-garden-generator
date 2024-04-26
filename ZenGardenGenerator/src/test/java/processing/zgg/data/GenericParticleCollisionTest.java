package processing.zgg.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import processing.core.PVector;
import processing.zgg.particle.data.GenericParticle;

/**
 *
 * @author gestorum
 */
public class GenericParticleCollisionTest {

    private static final int MAX_COUNT = 250;

    @ParameterizedTest
    @CsvSource({"2,82", "3,105"})
    public void fullFrontalCollision(final int dimensions, final int expectedCount) {
        final PVector p1Position;
        final PVector p2Position;

        switch (dimensions) {
            case 2 -> {
                p1Position = new PVector(10, 10);
                p2Position = new PVector(40, 40);
            }

            case 3 -> {
                p1Position = new PVector(10, 10, 10);
                p2Position = new PVector(40, 40, 40);
            }

            default ->
                throw new IllegalArgumentException();
        }

        final GenericParticle p1 = GenericParticleFactory.build(p1Position);
        final GenericParticle p2 = GenericParticleFactory.build(p2Position);

        p1.seek(p2);
        p2.seek(p1);

        System.out.println(String.format("--- %dD", dimensions));

        int count = 0;
        float distance;
        do {
            p1.update();
            p2.update();

            distance = PVector.dist(p1.getPosition(), p2.getPosition());
            System.out.println(String.format("distance: %.3f", distance));

            count++;
        } while (!p1.isCollisionDetected(p2) && count < MAX_COUNT);

        assertTrue(distance < p1.getRadius() + p2.getRadius());
        assertEquals(expectedCount, count);
    }
    
    @ParameterizedTest
    @CsvSource({"0,0,0,true", "0,9,0,true", "0,10,0,false",
        "0,0,9,true", "0,0,10,false", "15,0,0,false", "15,15,15,false"})
    public void collisionWithDot(final int dotX, final int dotY, final int dotZ,
            final boolean collisionExpected) {
        final GenericParticle p = GenericParticleFactory.build(new PVector());
        p.setRadius(10);

        assertEquals(p.isCollisionDetected(new PVector(dotX, dotY, dotZ)),
                collisionExpected);
    }
}
