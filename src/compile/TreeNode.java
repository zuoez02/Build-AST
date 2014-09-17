package compile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class TreeNode {
	private int number;
	private String type;
	private List<TreeNode> children;
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setField(List<Field> field) {
		this.field = field;
	}

	private List<Field> field;

	public TreeNode(int number, String type) {
		this.number = number;
		this.type = type;
		this.field = new LinkedList<Field>();
		this.setChildren(new LinkedList<TreeNode>());
	}

	public List<Field> getField() {
		return this.field;
	}

	public String toString() {
		String s = new String();
		s = "number:" + this.number + " type:" + this.type;
		if(!this.field.isEmpty()) {
			s += "\tField: ";
			for(int i=0;i<this.field.size();i++)
				s += this.field.get(i).getName() + ": " + this.field.get(i).getValue() + "\t";
		}
		return s;
	}

	public void printAll(int num) throws IOException {
		for(int i = 0;i<num;i++)
			printFile("TreeNode.analysis", "  ");		
		printFile("TreeNode.analysis", this.toString() + "\n");
		for(int i = 0; i <this.children.size();i++) {
			this.children.get(i).printAll(num+1);
		}
	}
	public List<TreeNode> getChildren() {
		return children;
	}

	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}

	public void printFile(String name, String s) throws IOException {
		File file = new File(name);
		if(!file.exists())
			file.createNewFile();
		try {
			FileWriter fw = new FileWriter(file, true);
			fw.write(s);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
