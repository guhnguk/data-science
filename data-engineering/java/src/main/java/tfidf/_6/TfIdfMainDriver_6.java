package algorithms.tfidf._6;

public class TfIdfMainDriver_6 {
	public static void main(String[] args) {
		if (args.length != 4) {
			String usage = "Usage: java algorithms.tfidf._6.TfIdfMainDriver_6 {input_file} {output_file}";
			System.out.println(usage);
			System.exit(1);
		}

		new TfIdfMainDriver_6().execute(args[0], args[1]);
	}

	private void execute(String string, String string2) {
		// TODO Auto-generated method stub
		
	}

//	private void execute(String inputPath, String outputPath) {
//		parseFile()
//	}
}
