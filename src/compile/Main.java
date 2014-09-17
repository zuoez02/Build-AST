package compile;

import java.io.IOException;

public class Main {
	public static void main(String[] args) {
		Resolver resolver = new Resolver();
		if(args.length != 0) {
			String address = args[0]; 
			// input AST text
			try {
				resolver.InputAstText(address);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// delete redundant information
			resolver.DeleteRedundantInformation();
			
			
			// standardize AST text
			resolver.StandardizeASTText();

			// establish AST
			resolver.EstablishAST();

			// output AST
			resolver.OutputAST();
			
			System.out.println("Analysing Successfully, Exiting...");

		} else {
			System.out.println("Please input the address of the source file!");
		}

	}
}