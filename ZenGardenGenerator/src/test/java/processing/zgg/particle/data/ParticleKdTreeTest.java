package processing.zgg.particle.data;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import processing.core.PVector;
import processing.zgg.data.KdTree;
import processing.zgg.data.GenericParticleFactory;

/**
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
        
        assertTrue(particleKdTree.size() == 6);
    }
    
    @Test
    public void update() {
        final ParticleKdTree particleKdTree = new ParticleKdTree();
        
        final GenericParticle p1 = GenericParticleFactory.build(new PVector(5, 20, 0));
        particleKdTree.insert(p1);
        
        final GenericParticle p2 = GenericParticleFactory.build(new PVector(10, 5, 0));
        particleKdTree.insert(p2);
        
        final PVector p1NewPosition = new PVector(15, 0, 0);
        p1.setPosition(p1NewPosition);
        particleKdTree.update(p1);
        
        assertTrue(particleKdTree.size() == 2);
        final ParticleKdTree.Node p1Node = particleKdTree.search(p1);
        assertTrue(p1Node.getValue().getPosition().equals(p1NewPosition));
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
        assertTrue(particleKdTree.size() == 2);
    }
}
