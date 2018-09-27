package algorithms.tfidf._5;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class TfIdf_5_Test {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test1() throws Exception {
		String doc1 = "사랑은 달콤한 꽃이나 그것을 따기 위해서는 무서운 벼랑 끝까지 갈 용기가 있어야 한다.";
		String doc2 = "진실한 사랑의 실체는 믿음이다.";
		String doc3 = "눈물은 눈동자로 말하는 고결한 언어.";
		String doc4 = "친구란 두 사람의 신체에 사는 하나의 영혼이다.";
		String doc5 = "흐르는 강물을 잡을수 없다면, 바다가 되어서 기다려라.";
		String doc6 = "믿음 소망 사랑 그중에 제일은 사랑이라.";
		String doc7 = "가장 소중한 사람은 가장 사랑하는 사람이다.";
		String doc8 = "사랑 사랑 사랑";
		String doc9 = "믿음을 주는 사랑";

		String[] docList = new String[] { doc1, doc2, doc3, doc4, doc5, doc6, doc7, doc8, doc9 };
		// DocumentParser dp = new DocumentParser(docList);

		DocumentParser dp = new DocumentParser("D:/Workspace/Data/Tf-Idf/data.selfintro");
		dp.tfidf();
		print2(dp);

	}

	@Test
	public void test2() throws IOException {
		boolean isFirstLineSkip = true;
		boolean isTokenize = true;

		DocumentParser dp = new DocumentParser("D:/Workspace/Data/Tf-Idf/self_intro", isFirstLineSkip, isTokenize);
		dp.tfidf();
		print(dp);
	}

	private void print(DocumentParser dp) throws IOException {
		StringBuilder toWriteString = new StringBuilder();

		Map<Document, Map<String, Double>> similarity2 = dp.similarity2(10);
		for (Document doc : similarity2.keySet()) {
			System.out.println("\n\n[" + doc.getName() + "]");

			Map<String, String[]> commonTerms = doc.getCommonTerms();
			StringBuilder sb = new StringBuilder();

			int i = 1;
			Map<String, Double> treeMap = similarity2.get(doc);
			for (String otherDocName : treeMap.keySet()) {

				String commonterms = null;
				int commontermsCnt = 0;
				if (commonTerms.containsKey(otherDocName)) {
					String[] terms = commonTerms.get(otherDocName);
					commontermsCnt = terms.length;
					for (String term : terms) {
						sb.append(term + "|");
					}
					commonterms = sb.toString().substring(0, sb.length() - 1);
					sb.setLength(0);
				}

				Double score = treeMap.get(otherDocName);
				// System.out.println("[" + i++ + "] " + otherDocName + " => " + score + " => (%): "
				// + String.format("%.2f", score * 100) + " => " + commonterms);
				System.out.println("[" + i++ + "] " + otherDocName + " => (%): " + String.format("%.2f", score * 100)
						+ " => " + commonterms);

				toWriteString.append(doc.getName()).append(",");
				toWriteString.append(otherDocName).append(",");
				toWriteString.append(score).append(",");
				toWriteString.append(String.format("%.2f", score * 100)).append(",");
				toWriteString.append(commontermsCnt).append(",");
				toWriteString.append(commonterms);

				System.out.println(toWriteString.toString());

				String data = toWriteString.toString() + "\n";
				FileUtils.writeStringToFile(new File("D:/Workspace/Data/Tf-Idf/result.csv"), data, "euc-kr", true);
				toWriteString.setLength(0);
			}
		}
	}

	private void print2(DocumentParser dp) {
		Map<String, Map<String, Double>> similarity = dp.similarity();
		for (String docName : similarity.keySet()) {
			System.out.println("\n\n[" + docName + "]");

			int i = 1;
			Map<String, Double> treeMap = similarity.get(docName);
			for (String otherDocName : treeMap.keySet()) {
				Double score = treeMap.get(otherDocName);
				System.out.println("[" + i++ + "] " + otherDocName + " => " + score + " => (%): "
						+ String.format("%.2f", score * 100));
			}
		}
	}

	@Test
	public void cmdTest() throws Exception {
		String[] args = { "test2", "D:/Workspace/Data/Tf-Idf/self_intro", "T", "D:/Workspace/Data/Tf-Idf/result.csv" };
		TfIdfMainDriver_5.main(args);
	}
}
