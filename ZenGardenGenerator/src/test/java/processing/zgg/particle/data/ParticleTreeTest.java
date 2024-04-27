package processing.zgg.particle.data;

import java.util.List;
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import processing.core.PVector;
import processing.zgg.data.BinaryTreeNode;
import processing.zgg.data.GenericParticleFactory;

/**
 *
 * TODO: ParticleTree is a real mess right now but still pretty cool to watch.
 * Please fix BinaryTree ASAP.
 * 
 * @author gestorum
 */
public class ParticleTreeTest {
    
    @Test
    public void insert() {
        final ParticleTree particleTree = new ParticleTree();
        
        final GenericParticle p1 = GenericParticleFactory.build(new PVector(15, 20, 0));
        particleTree.insert(p1);
        
        final GenericParticle p2 = GenericParticleFactory.build(new PVector(10, 5, 0));
        particleTree.insert(p2);
        
        final GenericParticle p3 = GenericParticleFactory.build(new PVector(5, 2, 0));
        particleTree.insert(p3);
        
        final GenericParticle p4 = GenericParticleFactory.build(new PVector(5, 1, 0));
        particleTree.insert(p4);
        
        final GenericParticle p5 = GenericParticleFactory.build(new PVector(15, 20, 5));
        particleTree.insert(p5);
        
        final GenericParticle p6 = GenericParticleFactory.build(new PVector(15, 20, 1));
        particleTree.insert(p6);
        
        final List<AbstractParticle> allParticles = particleTree.getAllValues();
        final List<AbstractParticle> expectedOrder = List.of(p4, p3, p2, p1, p6, p5);
        assertTrue(allParticles.size() == expectedOrder.size());
        IntStream.range(0, expectedOrder.size()).forEach(i -> {
            assertTrue(expectedOrder.get(i).equals(allParticles.get(i)));
        });
    }
    
    @Test
    public void update() {
        final ParticleTree particleTree = new ParticleTree();
        
        final GenericParticle p1 = GenericParticleFactory.build(new PVector(5, 20, 0));
        particleTree.insert(p1);
        
        final GenericParticle p2 = GenericParticleFactory.build(new PVector(10, 5, 0));
        particleTree.insert(p2);
        
        p1.setPosition(new PVector(15, 0, 0));
        particleTree.update(p1);
        
        final List<AbstractParticle> allParticles = particleTree.getAllValues();
        assertTrue(allParticles.size() == 2);
        assertTrue(p2.equals(allParticles.get(0)));
    }
    
    @Test
    public void delete() {
        final ParticleTree particleTree = new ParticleTree();
        
        final GenericParticle p1 = GenericParticleFactory.build(new PVector(5, 20, 0));
        particleTree.insert(p1);
        
        final GenericParticle p2 = GenericParticleFactory.build(new PVector(10, 5, 0));
        particleTree.insert(p2);
        
        final GenericParticle p3 = GenericParticleFactory.build(new PVector(2, 1, 0));
        particleTree.insert(p3);
        
        final GenericParticle p4 = GenericParticleFactory.build(new PVector(15, 99, 0));
        particleTree.insert(p4);
        
        final BinaryTreeNode<AbstractParticle> deletedP2 = particleTree.delete(p2);
        assertNotNull(deletedP2);
        assertNull(particleTree.search(p2));
        
        final BinaryTreeNode<AbstractParticle> deletedP1 = particleTree.delete(p1);
        assertNotNull(deletedP1);
        assertNull(particleTree.search(p1));
        
        assertNotNull(particleTree.search(p3));
        assertNotNull(particleTree.search(p4));
    }
}
