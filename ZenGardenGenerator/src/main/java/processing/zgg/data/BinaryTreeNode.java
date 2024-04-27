/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.data;

import lombok.Data;
import lombok.NonNull;

/**
 *
 * @author gestorum
 */
@Data
public class BinaryTreeNode<T> {
    private T value;
    private BinaryTreeNode<T> parent;
    private BinaryTreeNode<T> left;
    private BinaryTreeNode<T> right;
 
    public BinaryTreeNode(@NonNull final T value) {
        this.value = value;
        this.parent = null;
        this.left = null;
        this.right = null;
    }
    
    public void setLeft(final BinaryTreeNode<T> node) {
        this.left = node;
        
        if (node != null) {
            node.setParent(this);
        }
    }
    
    public void setRight(final BinaryTreeNode<T> node) {
        this.right = node;
        
        if (node != null) {
            node.setParent(this);
        }
    }
}
