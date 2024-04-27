package processing.zgg.particle.data;

import java.util.List;
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import processing.core.PVector;
import processing.zgg.data.KdTree;
import processing.zgg.data.GenericParticleFactory;

/**
 *
 * TODO: Fix these unit tests and KdTree ASAP.
 * 
 * @author gestorum
 */
public class ParticleKdTreeTest {
    
    @Test
    public void insert() {
        final ParticleKdTree particleKdTree = new ParticleKdTree();
        
        final GenericParticle p1 = GenericParticleFactory.build(new PVector(15, 20, 0));
        particleKdTree.insert(p1);
        
        final GenericParticle p2 = GenericParticleFactory.build(new PVector(10, 5, 0));
        particleKdTree.insert(p2);
        
        final GenericParticle p3 = GenericParticleFactory.build(new PVector(5, 2, 0));
        particleKdTree.insert(p3);
        
        final GenericParticle p4 = GenericParticleFactory.build(new PVector(5, 1, 0));
        particleKdTree.insert(p4);
        
        final GenericParticle p5 = GenericParticleFactory.build(new PVector(15, 20, 5));
        particleKdTree.insert(p5);
        
        final GenericParticle p6 = GenericParticleFactory.build(new PVector(15, 20, 1));
        particleKdTree.insert(p6);
        
        final List<AbstractParticle> allParticles = particleKdTree.getAllValues();
        final List<AbstractParticle> expectedOrder = List.of(p4, p3, p2, p1, p6, p5);
        assertTrue(allParticles.size() == expectedOrder.size());
        IntStream.range(0, expectedOrder.size()).forEach(i -> {
            assertTrue(expectedOrder.get(i).equals(allParticles.get(i)));
        });
    }
    
    @Test
    public void update() {
        final ParticleKdTree particleKdTree = new ParticleKdTree();
        
        final GenericParticle p1 = GenericParticleFactory.build(new PVector(5, 20, 0));
        particleKdTree.insert(p1);
        
        final GenericParticle p2 = GenericParticleFactory.build(new PVector(10, 5, 0));
        particleKdTree.insert(p2);
        
        p1.setPosition(new PVector(15, 0, 0));
        particleKdTree.update(p1);
        
        final List<AbstractParticle> allParticles = particleKdTree.getAllValues();
        assertTrue(allParticles.size() == 2);
        assertTrue(p2.equals(allParticles.get(0)));
    }
    
    @Test
    public void delete() {
        final ParticleKdTree particleKdTree = new ParticleKdTree();
        
        final GenericParticle p1 = GenericParticleFactory.build(new PVector(5, 20, 0));
        particleKdTree.insert(p1);
        
        final GenericParticle p2 = GenericParticleFactory.build(new PVector(10, 5, 0));
        particleKdTree.insert(p2);
        
        final GenericParticle p3 = GenericParticleFactory.build(new PVector(2, 1, 0));
        particleKdTree.insert(p3);
        
        final GenericParticle p4 = GenericParticleFactory.build(new PVector(15, 99, 0));
        particleKdTree.insert(p4);
        
        final KdTree.Node deletedP2 = particleKdTree.delete(p2);
        assertNotNull(deletedP2);
        assertNull(particleKdTree.search(p2));
        
        final KdTree.Node deletedP1 = particleKdTree.delete(p1);
        assertNotNull(deletedP1);
        assertNull(particleKdTree.search(p1));
        
        assertNotNull(particleKdTree.search(p3));
        assertNotNull(particleKdTree.search(p4));
    }
}
