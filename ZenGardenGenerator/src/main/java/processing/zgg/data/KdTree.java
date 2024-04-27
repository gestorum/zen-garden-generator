package processing.zgg.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NonNull;
import processing.zgg.data.KdTree.Node;

/**
 * K-d tree implementation to quickly get the nearest neighbors of a value.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/K-d_tree">K-d Tree</a>
 * 
 * @author gestorum
 */
public abstract class KdTree<V extends IdentifiedEntry> {

    private int k;
    private Node root;
    private Map<String, Node> nodeByValueId = new HashMap<>();
    private Node lastNearestNeighbor;
    private float bestDistance;

    @Data
    public class Node {
        private V value;
        private Node left;
        private Node right;

        public Node(@NonNull final V value) {
            this.value = value;
            this.left = null;
            this.right = null;
        }
        
        public float distance(@NonNull final Node target) {
            final float[] thisPoint = getPointFromValue(this.getValue());
            final float[] targetPoint = getPointFromValue(target.getValue());
            
            float dist = 0;
            for (int i = 0; i < thisPoint.length; ++i) {
                float d = thisPoint[i] - targetPoint[i];
                dist += d * d;
            }
            
            return dist;
        }
    }
   
    public KdTree(int k) {
        this.k = k;
    }
    
    public Node search(@NonNull final V value) {
        return nodeByValueId.get(value.getId());
    }
    
    public Node insert(final V value) {
        return insertRec(this.root, value, 0);
    }
    
    public Node delete(final V value) {
        return deleteRec(this.root, value, 0);
    }
    
    public Node update(@NonNull final V value) {
        final Node valueTreeNode = nodeByValueId.get(value.getId());
        if (valueTreeNode == null) {
            return insert(value);
        }
        
        deleteRec(valueTreeNode, valueTreeNode.getValue(), 0);
        
        return insert(value);
    }
    
    public List<V> findNearestNeighbors(@NonNull final V value,
            final int maxNeighbors) {
        if (this.root == null || maxNeighbors <= 0) {
            return Collections.emptyList();
        }
        
        // TODO: handle more nearest neighbors
        if (maxNeighbors > 1) {
            throw new IllegalArgumentException("maxNeighbors > 1 is not implemented yet!");
        }
        
        final Node valueTreeNode = search(value);
        if (valueTreeNode == null) {
            return Collections.emptyList();
        }
        
        this.lastNearestNeighbor = null;
        this.bestDistance = 0;
        nearestRec(this.root, valueTreeNode, 0);
        
        return this.lastNearestNeighbor != null ? Collections.singletonList(this.lastNearestNeighbor.getValue())
                : Collections.emptyList();
    }
    
    public List<Node> getAllNodes() {
        final List<Node> allNodes = new ArrayList<>();
        
        treeNodesToListRec(this.root, allNodes);
        
        return allNodes;
    }
    
    public List<V> getAllValues() {
        return getAllNodes().stream().map(Node::getValue).toList();
    }
    
    private void treeNodesToListRec(final Node node, final List<Node> list){
        if (node == null){
            return;
        }
        
        treeNodesToListRec(node.getLeft(), list);
        list.add(node);
        treeNodesToListRec(node.getRight(), list);
    }

    private Node insertRec(final Node current, final V value, final int depth) {
        if (current == null) {
            final Node newNode = newNode(value);
            if (this.root == null) {
                this.root = newNode;
            }
            
            nodeByValueId.put(value.getId(), newNode);
            
            return newNode;
        }
 
        final int cd = depth % k;
        final float[] point = getPointFromValue(value);
        final float[] currentPoint = getPointFromValue(current.getValue());
        if (point[cd] < currentPoint[cd]) {
            current.setLeft(insertRec(current.getLeft(), value, depth + 1));
        } else {
            current.setRight(insertRec(current.getRight(), value, depth + 1));
        }
 
        return current;
    }
    
    private Node deleteRec(final Node node, @NonNull final V value, final int depth) {
        if (node == null) {
            return null;
        }
        
        final V nodeValue = node.getValue();
        if (nodeValue == null) {
            throw new RuntimeException("Tree node has no value!");
        }
        
        int cd = depth % 3;
        final float[] nodeValuePoint = getPointFromValue(nodeValue);
        final float[] valuePoint = getPointFromValue(value);
        if (valuePoint[cd] < nodeValuePoint[cd]) {
            node.setLeft(deleteRec(node.getLeft(), value, depth + 1));
        } else if (valuePoint[cd] > nodeValuePoint[cd]) {
            node.setRight(deleteRec(node.getRight(), value, depth + 1));
        } else {
            // Node with only one child or no child
            if (node.getLeft() == null) {
                return node.getRight();
            } else if (node.getRight() == null) {
                return node.getLeft();
            }

            // Node with two children: Get the inorder successor (smallest in the right subtree)
            node.setValue(minValue(node.getRight()));

            // Delete the inorder successor
            node.setRight(deleteRec(node.getRight(), nodeValue, depth + 1));
        }
        
        nodeByValueId.remove(nodeValue.getId());
        
        return node;
    }
    
    private void nearestRec(final Node current, @NonNull final Node target, int depth) {
        if (current == null) {
            return;
        }
        
        final float d = current.distance(target);
        if (this.lastNearestNeighbor == null || d < this.bestDistance) {
            final boolean isItself = current.getValue().getId().equals(target.getValue().getId());
            if (!isItself) {
                this.bestDistance = d;
                this.lastNearestNeighbor = current;
            }
        }
        
        if (this.bestDistance == 0) {
            return;
        }
        
        final float[] curPoint = getPointFromValue(current.getValue());
        final float[] targetPoint = getPointFromValue(target.getValue());
        final double dx = curPoint[depth] - targetPoint[depth];
        
        depth = (depth + 1) % k;
        nearestRec(dx > 0 ? current.getLeft() : current.getRight(), target, depth);
        
        if (dx * dx >= this.bestDistance) {
            return;
        }
        
        nearestRec(dx > 0 ? current.getRight() : current.getLeft(), target, depth);
    }

    private V minValue(@NonNull Node node) {
        V minValue = node.getValue();
        while (node.getLeft() != null) {
            minValue = node.getLeft().getValue();
            node = node.getLeft();
        }
        
        return minValue;
    }

    private Node newNode(final V value) {
        return new Node(value);
    }

    protected abstract float[] getPointFromValue(V value);
}
