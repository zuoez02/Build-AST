package compile;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class Resolver {
	private Nodes nodes;		// information of every node
	private Nodes useful;		// information of useful node
	private int nodesNum;		// the number of nodes
	private NodeType[] nodesType;	// the type of every node, useful ,useless or TBD 
	private File file;
	private List<TreeNode> treeNodes;

	public Resolver() {
		nodes = new Nodes();
		nodesNum = 0;
	}

	// input AST text
	public void InputAstText(String address) throws IOException {
		this.file = new File(address);
		if(file.exists()) {
			System.out.println("Begin to analysis...");
			CreateNodeList();
		}
	}

	// begin to create list of nodes
	private void CreateNodeList() throws IOException {
		System.out.println("Creating Node List...");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String info = null;
		int line = 0;
		// read every line and merge related lines
		while((info = br.readLine()) != null) {
			info = info.trim();
			if(info.length() != 0)
				if(info.charAt(0) == '@') {
					this.nodes.addNew(info);
					line++;
				} else {
					this.nodes.append(info);
				}
		}
		br.close();
		// disposal line number and initialize type array
		this.nodesNum = line;
		this.nodesType = new NodeType[line];	
		System.out.println("Created!");
	}

	public void DeleteRedundantInformation() {
		System.out.println("Deleting redundant information...");
		// make all nodes' type TBD
		for(int i = 0;i < this.nodesNum;i++) {
			this.nodesType[i] = NodeType.TBD;
		}

		// search for useful and useless node
		for(int i = 0;i < this.nodesNum;i++) {
			int position = this.nodes.getNode(i).lastIndexOf("srcp");
			if(position != -1) {
				String fileName = file.getName();
				String[] firstName = fileName.split(".c");
				int length = firstName[0].length();
				position += 6;
				String name = this.nodes.getNode(i).substring(position, position + length);
				if(name.equals(firstName[0]))
					this.nodesType[i] = NodeType.USEFUL;
				else
					this.nodesType[i] = NodeType.USELESS;
			}
		}
		// print nodes' type at step 1 
		try {
			this.printFile("nodes1.analysis");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// determine child nodes' type
		int number = 0; // position of first child number in the string
		for(int i = 0;i < this.nodesNum;i++) {
			if(this.nodesType[i] != NodeType.TBD) {
				number = this.nodes.getNode(i).indexOf("@", number + 1);
				while(number != -1) {					
					if(number != -1) {
						int child = findChild(number, this.nodes.getNode(i));						
						if(this.nodesType[i] == NodeType.USEFUL && this.nodesType[child-1] == NodeType.TBD) {
							this.nodesType[child-1] = NodeType.USEFUL;
						}							
						else if(this.nodesType[i] == NodeType.USELESS && this.nodesType[child-1] == NodeType.TBD)
							this.nodesType[child-1] = NodeType.USELESS;
					}
					number = this.nodes.getNode(i).indexOf("@", number + 1);
				}
			}
			number = 0;
		}
		// print nodes' type at step 2 
		try {
			this.printFile("nodes2.analysis");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		useful = new Nodes();
		for(int i = 0;i < this.nodesNum;i++) {
			if(this.nodesType[i] == NodeType.USEFUL)
				useful.addNew(this.nodes.getNode(i));
		}
		// print useful nodes to file
		try {
			useful.printFile("useful.analysis");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Deleted!");
	}

	private int findChild(int number, String string) {
		String s = new String();
		char c = string.charAt(number + 1);
		while(c >= '0' && c <= '9' ) {
			s += c;
			number++;
			if((number + 1) < string.length())
				c = string.charAt(number + 1);
			else
				break;
		}
		return Integer.parseInt(s);
	}

	public void StandardizeASTText() {
		System.out.println("Standardizing AST text");
		// create a linked list of nodes
		treeNodes = new LinkedList<TreeNode>();
		for(int i = 0;i<useful.getNum();i++)
			treeNodes.add(AnalysisNode(useful.getNode(i)));
		// create an array about numbers of nodes
		int[] numbers = new int[treeNodes.size()];
		for(int i=0;i<treeNodes.size();i++)
			numbers[i] = treeNodes.get(i).getNumber();
		// remove redundant field		
		List<Field> field;
		for(int i=0;i<treeNodes.size();i++) {
			// remove all fields of type node
			if(treeNodes.get(i).getType().endsWith("_type"))
				treeNodes.get(i).getField().clear();
			// scope_stmt node disposal
			if(treeNodes.get(i).getType().equals("scope_stmt")) {
				field = treeNodes.get(i).getField();
				for(int j=field.size() - 1;j>=0;j--)
					if(field.get(j).getName() != "begin" && field.get(j).getName() != "end" &&
					field.get(j).getName() != "line" && field.get(j).getName() != "next")
						field.remove(j);
			}
			// remove fields that point node is invalid
			for(int j = treeNodes.get(i).getField().size() - 1;j>=0;j--) {
				if(!exist(treeNodes.get(i).getField().get(j).getValue(), numbers))
					treeNodes.get(i).getField().remove(j);
			}
			for(int j = treeNodes.get(i).getField().size() - 1;j>=0;j--)
				if(treeNodes.get(i).getField().get(j).getName().equals("scpe"))
					treeNodes.get(i).getField().remove(j);
		}
		for(int i = 0;i < treeNodes.size();i++)
			for(int j = 0; j < treeNodes.get(i).getField().size();j++)
				treeNodes.get(i).getField().get(j).setValue(treeNodes.get(i).getField().get(j).getValue().substring(1));
		// number mapping
		for(int i =0;i<treeNodes.size();i++) {
			int n = treeNodes.get(i).getNumber();
			for(int j = 0;j < treeNodes.size();j++) {
				for(int k = 0;k<treeNodes.get(j).getField().size();k++) {
					if(n == Integer.parseInt(treeNodes.get(j).getField().get(k).getValue()))
						treeNodes.get(j).getField().get(k).setValue(Integer.toString(i));
				}
			}
			treeNodes.get(i).setNumber(i);
		}
		//		for(int i = 0;i<treeNodes.size();i++)
		//			System.out.println(treeNodes.get(i));
		System.out.println("Standardized!");
	}

	public void EstablishAST() {
		System.out.println("Establishing AST");
		for(int i = 0;i < treeNodes.size();i++) {
			for(int j = 0;j < treeNodes.get(i).getField().size();j++) {
				int n = Integer.parseInt(treeNodes.get(i).getField().get(j).getValue());
				treeNodes.get(i).getChildren().add(treeNodes.get(n));
			}
		}
		System.out.println("Established!");
	}

	public void OutputAST() {
		System.out.println("Outputing AST");
		try {
			this.treeNodes.get(0).printAll(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Outputed!");
	}

	public void printFile(String name) throws IOException {
		File file = new File(name);
		if(!file.exists())
			file.createNewFile();
		try {
			FileWriter fw = new FileWriter(file);
			BufferedWriter bufw = new BufferedWriter(fw);
			for(int i = 0;i < nodesNum; i++) {
				bufw.write("Line:\t" + (i+1) + "\t "+ nodesType[i].toString());
				bufw.newLine();
			}
			bufw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public TreeNode AnalysisNode(String s) {
		s = s.replaceAll("op ", "op");
		String[] array = s.split(": | ");
		List<String> list = new LinkedList<String>();
		String ss = new String();
		for(int i=0;i<array.length;i++)
			if(array[i] != "" && !array[i].equals(ss))
				list.add(array[i]);
		TreeNode treeNode = new TreeNode(Integer.parseInt(list.get(0).substring(1)), list.get(1));
		for(int i=2;i<list.size();i=i+2)
			treeNode.getField().add(new Field(list.get(i),list.get(i+1)));
		for(int i=treeNode.getField().size() - 1;i>=0;i--)
			if(treeNode.getField().get(i).getValue().charAt(0) != '@')
				treeNode.getField().remove(i);
		return treeNode;
	}

	private boolean exist(String s, int[] array) {
		int number = Integer.parseInt(s.substring(1));
		for(int i = 0;i<array.length;i++) {
			if(number == array[i])
				return true;
		}
		return false;

	}
} 