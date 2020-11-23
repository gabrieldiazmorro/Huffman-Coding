package DataStruc.Tree;

import P3.DataStructures.utils.BinaryTreePrinter.PrintableNode;

/**
 * This class represents a binary tree node with key, value,
 * left child, right child, and parent.
 * @author Juan O. Lopez 
 *
 * @param <K> Generic type for the key of the values to be stored in the nodes
 * @param <V> Generic type for the values to be stored in the nodes
 */
public class BTNode<K extends Comparable<? super K>, V extends Comparable<? super V>> implements Comparable<BTNode<K,V>>, PrintableNode  {
	private K key;
	private V value;
	private BTNode<K, V> leftChild, rightChild, parent;

	public BTNode() {
		this(null, null, null);
	}
	public BTNode(K key, V value) {
		this(key, value, null);
	}

	public BTNode(K key, V value, BTNode<K, V> parent) {
		this.key = key;
		this.value = value;
		this.parent = parent;
		this.leftChild = this.rightChild = null;
	}

	public K getKey() {
		return key;
	}
	public void setKey(K key) {
		this.key = key;
	}
	public V getValue() {
		return value;
	}
	public void setValue(V value) {
		this.value = value;
	}
	public BTNode<K, V> getLeftChild() {
		return leftChild;
	}
	public void setLeftChild(BTNode<K, V> leftChild) {
		this.leftChild = leftChild;
	}
	public BTNode<K, V> getRightChild() {
		return rightChild;
	}
	public void setRightChild(BTNode<K, V> rightChild) {
		this.rightChild = rightChild;
	}
	public BTNode<K, V> getParent() {
		return parent;
	}
	public void setParent(BTNode<K, V> newParent) {
		parent = newParent;
	}
	public void clear() {
		key = null;
		value = null;
		leftChild = rightChild = parent = null;
	}

	/* The methods below are merely to comply with the PrintableNode interface,
	 * used by the BinaryTreePrinter class to nicely display a binary tree.
	 * Could have renamed the methods, but just didn't feel like it.
	 */
	@Override
	public PrintableNode getLeft() {
		return getLeftChild();
	}
	@Override
	public PrintableNode getRight() {
		return getRightChild();
	}
	@Override
	public String getText() {
		return key.toString() + ":" + value.toString();
	}
	
	/**
	 * This method compares two node and returns a integer that represents the result.
	 *
	 * @param arg2 The node being compared to the this
	 * @return comparison  An integer that lets you know if (this) is smaller, equal or larger than arg2.
	 */
	@Override
	public int compareTo(BTNode<K, V> arg2) {
		//if (this) is smaller(<0),equal(==0) or larger(>0) than arg2.
		int freq = this.getKey().compareTo(arg2.getKey());
		int comparison;
		//if frequencies are not equal, return the comparison. 
		if (freq != 0) {
			comparison = freq;
			return comparison; 
		}
		//else frequencies are equal, check how the keys compare.
		else {
			comparison = this.getValue().compareTo(arg2.getValue());
			return comparison;
		}	
	}

}