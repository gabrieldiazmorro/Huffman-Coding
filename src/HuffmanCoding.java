import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import DataStruc.Tree.BTNode;
import P3.DataStructures.Map.Map;
import P3.DataStructures.Map.HashTable.HashTableSC;
import P3.DataStructures.Map.HashTable.SimpleHashFunction;
import P3.DataStructures.SortedList.SortedArrayList;
import P3.DataStructures.SortedList.SortedList;
import P3.DataStructures.utils.BinaryTreePrinter;

/**
 * This class processes a input file and encodes the input file using Huffman encoding and prints it out.
 * Huffman Coding is an encoding algorithm that uses variable length codes so that the symbols
 * appearing more frequently will be given shorter codes. It was developed by David A. Huffman
 * and published in the 1952 paper "A Method for the Construction of Minimum-Redundancy Codes".
 * 
 * @author Gabriel Diaz Morro
 */
public class HuffmanCoding {
	/**
	 * This main method calls all the necessary methods to process the input data. Using the Huffman Coding algorithm.
	 */
	public static void main(String[] args) {
		
		//	1. Read an input file with a single line of text.
		String input =load_data("stringData.txt");
		
		//if the input has no symbols, we can't process it
		if (input==null) {
			System.out.println("Input can't be empty");
		}
		else {
			//	2. Determine the frequency distribution of every symbol in the line of text.
			Map<String, Integer> fqMap = compute_fd(input);
			
			//	3. Construct the Huffman tree.
			BTNode<Integer, String> root = huffman_tree(fqMap);
			
			//	4. Create a mapping between every symbol and its corresponding Huffman code.
			Map<String, String> symbolCodemap = huffman_code(root);
			
			//	5. Encode the line of text using the corresponding code for every symbol.
			String encodedInput = encode(symbolCodemap, input);
			
			//	6. Display the results on the screen.
			process_results(input, encodedInput, fqMap,symbolCodemap);
		}
	}

	/**
	 * This method receives a file name (including its path) and returns a single string with
	 * the string given by the inputFile/parameter.
	 *
	 * @param inputFile The path of inputFile.
	 * @return line  A string with all of the text in the input file.
	 */
	public static String load_data(String inputFile) {
		BufferedReader in = null;
		String line = "";

		try {
			/*We create a new reader that accepts UTF-8 encoding and extract the input string from the file, and we return it*/
			in = new BufferedReader(new InputStreamReader(new FileInputStream("inputData/" + inputFile), "UTF-8"));
			line = in.readLine();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) 
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return line;
	}

	/**
	 * This method receives the input string and returns a Map with the symbol frequency distribution.
	 * A hashtable is used to save time when a symbol appears again, you don't have to look through the whole list.
	 *
	 * @param input A string containing the input text.
	 * @return freqResult  A map with the frequency of every symbol in the input.
	 */
	public static Map<String, Integer> compute_fd(String input){
		Map<String, Integer> freqResult = new HashTableSC<String, Integer>(new SimpleHashFunction<String>());
		//Counts the frequency of a every symbol in the input string.
		for (int i = 0; i < input.length(); i++) {
			if (freqResult.containsKey(String.valueOf(input.charAt(i)))) {
				//if it appears again +1 to the frequency
				freqResult.put(String.valueOf(input.charAt(i)),freqResult.get(String.valueOf(input.charAt(i)))+1);
			}else {
				//if it is the first time it appears the frequency is 1
				freqResult.put(String.valueOf(input.charAt(i)),1);
			}	
		}
		return freqResult;
	}


	/**
	 * This method receives freqResult and returns the root node of the corresponding Huffman tree.
	 *
	 * @param map A map with the frequency of every symbol in the input.
	 * @return root The root of the Huffman Tree.
	 */
	public static BTNode<Integer,String>huffman_tree(Map<String, Integer> map){
		SortedList<BTNode<Integer,String>> sortedL = new SortedArrayList<BTNode<Integer,String>>(map.size());
		//sort the symbols in respect to the frequency in ascending order
		for (String key : map.getKeys()) {
			sortedL.add(new BTNode<Integer,String>(map.get(key), key)); 
		}

		/*We take the first two since they have the lowest frequencies in the SortedList and make them into separate nodes
		 * In the case that two nodes have the same frequency, we take care of that problem inside the compareTo()
		 * method of BTNode<K,V>. We first compare frequency, if they are equal we compare the keys.
		 * 
		 * This is good so we can just delegate that task to the compareTo() method of BTNode<K,V> 
		 * (see BTNode<K,V> inside the Tree package), and it saves us from cluttering this method.
		 */
		while (sortedL.size()>1) {
			//we remove the symbols with lowest frequencies, but we keep them in a temporary variables 
			BTNode<Integer,String> left = sortedL.removeIndex(0); 
			BTNode<Integer,String> right = sortedL.removeIndex(0);
			
			//We take the symbols with the lowest frequency and put them together to create the parent(which eventually will be the root).
			BTNode<Integer,String> parent = new BTNode<Integer,String>(left.getKey() + right.getKey(), 
					left.getValue()+ right.getValue());
			parent.setLeftChild(left); //set nodes used to make the parent as the children
			parent.setRightChild(right);
			//We add the parent node created until we have only one node left which is the root node
			sortedL.add(parent);
		}
		//Once sortedList.size =1, this means that the node left is the root of the Huffman tree.
		BTNode<Integer,String> root =sortedL.get(0);
		
		//BinaryTreePrinter.print(root); used for testing purpose, uncomment if you want to see the Huffman Tree.
		return root;
	}


	/**
	 * This method receives the root of a Huffman tree and returns a mapping 
	 * of every symbol to its corresponding Huffman code.
	 *
	 * @param root The root node of the Huffman Tree.
	 * @return mapped A map with mapping of every symbol to its corresponding huffman code.
	 */
	public static Map<String, String> huffman_code(BTNode<Integer,String> root){
		Map<String, String> mapped = new HashTableSC<String, String>(new SimpleHashFunction<String>());
		//start the recursive helper method
		getToLeaf(mapped, root, "");

		return mapped;
	}
	/**
	 * This helper method is used to get to the leaf nodes, because that means they are the original symbols
	 * it keeps track if we go left(0) or right(1) to build the huffman code for that symbol.
	 *
	 * @param mapped The map that holds the <symbol,code> pair nodes.
	 * @param curNode The node we are currently in.
	 * @param code Keeps track if we have gone left or right to construct the symbol's code.
	 */
	private static void getToLeaf(Map<String, String> mapped, BTNode<Integer,String> curNode, String code) {
		//if the node is a leafNode which means it is a symbols of the input, then add it to the map.
		if (isLeaf(curNode)) {
			mapped.put(curNode.getValue(), code);
		}else {
			//if it is not a leaf node go further down the tree to left && right
			getToLeaf(mapped, curNode.getLeftChild(), code+"0"); //left
			getToLeaf(mapped, curNode.getRightChild(), code+"1"); //right
		}
	}
	/**
	 * This method checks if the node is a leaf node, by checking if it has no children.
	 * 
	 * @param curNode The node we are currently in.
	 * @return boolean If it is a leaf node it returns true.
	 */
	private static boolean isLeaf(BTNode<Integer,String> curNode) {
		//if  the node has no children it is a leaf node
		if (curNode.getLeftChild()==null && curNode.getRightChild()==null) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 * This method receives the Huffman code map(which contains every symbol and it's appropriate code)
	 *  and the input string, and returns the input encoded. 
	 *
	 * @param symbolCodemap A map with mapping of every symbol to its corresponding huffman code.
	 * @param input The input string that we got from the input file, that we need to encode.
	 * @return result The input string encoded with Huffman code.
	 */
	public static String encode(Map<String, String> symbolCodemap, String input) {
		String result ="";

		//convert each Character of the input string to its appropriate code
		for (Character character : input.toCharArray()) {
			String letter = String.valueOf(character);
			result += symbolCodemap.get(letter);
		}

		return result;
	}

	/**
	 * This method receives the frequency distribution map, the Huffman code map,the input string, 
	 * and the output string, and prints the results to the screen (per specifications).
	 *
	 * @param input The input string that we got from the input file.
	 * @param encodedInput The input string encoded with Huffman code.
	 * @param fqMap The map with the frequency of every symbol in the input.
	 * @param symbolCodemap A map with mapping of every symbol to its corresponding huffman code.
	 */
	public static void process_results(String input, String encodedInput, Map<String, Integer> fqMap,
			Map<String, String> symbolCodemap) {
		//Starts to print according to the specifications
		System.out.println("Symbol\t"+ "Frequency\t" + "Code");
		System.out.println("------\t"+ "---------\t" + "----");
		
		SortedArrayList<BTNode<Integer, String>> sortingFQ = new SortedArrayList<BTNode<Integer,String>>(fqMap.size());
		//Organizes the Symbols in ascending order(smallest to largest) according to the frequency.
		for (String key : fqMap.getKeys()) {
												//(  frequency,     symbol)
			sortingFQ.add(new BTNode<Integer,String>(fqMap.get(key), key));
		}
		//Print out according to the frequencies in descending order(largest to smallest) this is why start at the end.
		for (int i = sortingFQ.size()-1; i>=0; i--) {
			System.out.println(sortingFQ.get(i).getValue()+ "\t"+ sortingFQ.get(i).getKey() + "\t\t" + symbolCodemap.get(sortingFQ.get(i).getValue()));
			//                 gets the key                            gets the frequency                     gets the code

		}
		//Prints the original string input and the encoded input
		System.out.println();
		System.out.println("Original string:");
		System.out.println(input);
		System.out.println("Encoded string:");
		System.out.println(encodedInput);

		System.out.println();//to leave a line

		int inputBytes = input.length();
		System.out.println("The original string requires " +  inputBytes +" bytes.");
		int encodedCodeBytes = (int) Math.ceil((float) encodedInput.length()/ 8);
		System.out.println("The encoded string requires "+ encodedCodeBytes+ " bytes.");
		
		//calculates how much space the encoded form saves
		DecimalFormat d = new DecimalFormat("##.##");
		String savings =  d.format(100 - (((float) ((float)encodedCodeBytes / (float)inputBytes)) * 100));

		System.out.println("Difference in space required is "+ savings +  "%.");
		//END of process
	}

}
