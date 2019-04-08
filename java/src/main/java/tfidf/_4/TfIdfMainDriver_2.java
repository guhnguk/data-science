package tfidf._4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TfIdfMainDriver_2 {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		// Document 1, 2, 3, 4
		// String doc1 = "The game of life is a game of everlasting learning";
		// String doc2 = "The unexamined life is not worth living";
		// String doc3 = "Never stop learning";
		// String doc4 = "life learning";
		// String[] docSet = new String[] { doc1, doc2, doc3, doc4 };

		// String[] docSet = new TfIdfMainDriver_2().getDocuments();
		// DocumentParser dp = new DocumentParser(docSet);
		
		DocumentParser dp = new DocumentParser("D:/Workspace/Data/Tf-Idf/data.selfintro");
		
		dp.calculateTfIdf();
		dp.calculateSimilarity();

		// new DocumentParser(docSet);
	}

	private String[] getDocuments() throws FileNotFoundException, IOException {
		// return readFiles("D:/Workspace/Data/Tf-Idf/data.back");
		return readFiles("D:/Workspace/Data/Tf-Idf/data.selfintro");
	}

	public String[] readFiles(String filePath) throws FileNotFoundException, IOException {
		File[] allfiles = new File(filePath).listFiles();
		BufferedReader in = null;

		int i = 0;
		String[] retContents = new String[allfiles.length];
		for (File f : allfiles) {
			if (f.getName().endsWith(".text")) {
				in = new BufferedReader(new FileReader(f));
				StringBuilder sb = new StringBuilder();
				String s = null;
				while ((s = in.readLine()) != null) {
					sb.append(s);
				}

				retContents[i++] = sb.toString();
			}
		}

		return retContents;
	}
}
