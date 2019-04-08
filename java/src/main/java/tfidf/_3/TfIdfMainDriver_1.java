package tfidf._3;

import java.io.FileNotFoundException;
import java.io.IOException;

public class TfIdfMainDriver_1 {
	public static void main(String args[]) throws FileNotFoundException, IOException {
		DocumentParser dp = new DocumentParser();

		// dp.parseFiles("D:\\FolderToCalculateCosineSimilarityOf"); // give the location of source file
		dp.parseFiles("D:/Workspace/Data/Tf-Idf/data");
		dp.tfIdfCalculator(); // calculates tfidf
		dp.getCosineSimilarity(); // calculates cosine similarity
	}
}
