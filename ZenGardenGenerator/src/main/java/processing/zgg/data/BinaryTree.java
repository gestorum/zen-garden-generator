package processing.zgg.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.NonNull;

/**
 *
 * @author gestorum
 */
public abstract class BinaryTree<V extends IdentifiedEntry> {
    private static final int K = 3;
    
    private BinaryTreeNode<V> root;
    private Map<String, BinaryTreeNode<V>> nodeByValueId = new HashMap<>();
    
    public BinaryTreeNode<V> search(@NonNull final V value) {
        return nodeByValueId.get(value.getId());
    }
    
    public List<V> findNearestNeighbors(@NonNull final V value) {
        final BinaryTreeNode<V> valueTreeNode = search(value);
        if (valueTreeNode == null) {
            return Collections.emptyList();
        }
        
        final List<BinaryTreeNode<V>> nearestNeighbors = new ArrayList<>();
        
        final BinaryTreeNode<V> parentTreeNode = valueTreeNode.getParent();
        if (parentTreeNode != null) {
            nearestNeighbors.add(parentTreeNode);
        }
        
        final BinaryTreeNode<V> leftTreeNode = valueTreeNode.getLeft();
        if (leftTreeNode != null) {
            nearestNeighbors.add(leftTreeNode);
        }
        
        final BinaryTreeNode<V> rightTreeNode = valueTreeNode.getRight();
        if (rightTreeNode != null) {
            nearestNeighbors.add(rightTreeNode);
        }
        
        return nearestNeighbors.stream().filter(Objects::nonNull)
                .map(BinaryTreeNode::getValue).toList();
    }
    
    public BinaryTreeNode<V> insert(final V value) {
        return insertRec(this.root, value, 0);
    }
    
    public BinaryTreeNode<V> delete(final V value) {
        return deleteRec(this.root, value, 0);
    }
    
    public BinaryTreeNode<V> update(@NonNull final V value) {
        final BinaryTreeNode<V> valueTreeNode = nodeByValueId.get(value.getId());
        if (valueTreeNode == null) {
            return insert(value);
        }
        
        deleteRec(valueTreeNode, valueTreeNode.getValue(), 0);
        
        return insert(value);
    }
    
    public List<BinaryTreeNode<V>> getAllNodes() {
        final List<BinaryTreeNode<V>> allNodes = new ArrayList<>();
        
        treeNodesToListRec(this.root, allNodes);
        
        return allNodes;
    }
    
    public List<V> getAllValues() {
        return getAllNodes().stream().map(BinaryTreeNode::getValue).toList();
    }
    
    private void treeNodesToListRec(final BinaryTreeNode<V> node,
            final List<BinaryTreeNode<V>> list){
        if (node == null){
            return;
        }
        
        treeNodesToListRec(node.getLeft(), list);
        list.add(node);
        treeNodesToListRec(node.getRight(), list);
    }

    private BinaryTreeNode<V> insertRec(final BinaryTreeNode<V> current,
            final V value, final int depth) {
        if (current == null) {
            final BinaryTreeNode<V> newNode = newNode(value);
            if (this.root == null) {
                this.root = newNode;
            }
            
            nodeByValueId.put(value.getId(), newNode);
            
            return newNode;
        }
 
        final int cd = depth % K;
        final float[] point = getPointFromValue(value);
        final float[] currentPoint = getPointFromValue(current.getValue());
        if (point[cd] < currentPoint[cd]) {
            current.setLeft(insertRec(current.getLeft(), value, depth + 1));
        } else {
            current.setRight(insertRec(current.getRight(), value, depth + 1));
        }
 
        return current;
    }
    
    private BinaryTreeNode<V> deleteRec(final BinaryTreeNode<V> node,
            @NonNull final V value, final int depth) {
        if (node == null) {
            return null;
        }
        
        final V nodeValue = node.getValue();
        if (nodeValue == null) {
            throw new RuntimeException("Tree node has no value!");
        }
        
        int cd = depth % K;
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

    private V minValue(@NonNull BinaryTreeNode<V> node) {
        V minValue = node.getValue();
        while (node.getLeft() != null) {
            minValue = node.getLeft().getValue();
            node = node.getLeft();
        }
        
        return minValue;
    }

    private BinaryTreeNode<V> newNode(final V value) {
        return new BinaryTreeNode(value);
    }

    protected abstract float[] getPointFromValue(V value);
}
