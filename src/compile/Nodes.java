package compile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Nodes {
	private List<String> nodes; // every node's information
	private int num; // the number of all nodes

	public Nodes() {
		this.nodes = new LinkedList<String>();
		this.num = 0;
	}

	// add a new node information in nodes
	public void addNew(String s) {
		nodes.add(s);
		num++;
	}

	// append information to the last node
	public void append(String s) {
		if (!s.isEmpty()) {
			s = ' ' + s;
			String temp = nodes.remove(num - 1);
			temp += s;
			nodes.add(temp);
		}
	}

	// get node information by index
	public String getNode(int index) {
		return nodes.get(index);
	}

	// return the number of all nodes
	public int getNum() {
		return this.num;
	}

	// print nodes
	public void print() {
		for (int i = 0; i < this.nodes.size(); i++)
			System.out.println(this.nodes.get(i));
	}

	public void printFile(String fileName) throws IOException {
		File file = new File("/home/lut0/dev/java-workspace/AST/", fileName);
		if (!file.exists())
			file.createNewFile();
		try {
			FileWriter fw = new FileWriter(file);
			BufferedWriter bufw = new BufferedWriter(fw);
			for (int i = 0; i < num; i++) {
				bufw.write(nodes.get(i));
				bufw.newLine();
			}
			bufw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}