package tfidf._4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentParser {
	private final Logger logger = LoggerFactory.getLogger(DocumentParser.class);

	private Map<String, Document> allDocuments;
	private Map<String, TreeMap<String, Double>> similarity;
	private Set<String> allTerms;

	public DocumentParser(String[] docList) {
		allDocuments = new TreeMap<String, Document>();
		String docName = "DOC";
		int index = 1;
		for (String doc : docList) {
			String[] words = tokenize(doc);
			String keyName = docName + "_" + index++;

			allDocuments.put(keyName, new Document(keyName, words, words.length));
		}
	}

	public DocumentParser(String filePath) throws IOException {
		allDocuments = new TreeMap<String, Document>();
		File[] allfiles = new File(filePath).listFiles();
		BufferedReader in = null;

		String[] words = null;
		for (File f : allfiles) {
			String keyName = f.getName().split("[.]")[0].trim();

			if (f.getName().endsWith(".text")) {
				in = new BufferedReader(new FileReader(f));

				StringBuilder sb = new StringBuilder();
				String s = null;
				while ((s = in.readLine()) != null) {
					sb.append(s);
				}

				words = tokenize(sb.toString());
			}
			allDocuments.put(keyName, new Document(keyName, words, words.length));
		}
	}

	private String[] tokenize(String doc) {
		StringTokenizer tokens = new StringTokenizer(doc.toLowerCase(), ":; \"\',.[]{}()!?-/");
		int i = 0;
		String[] words = new String[tokens.countTokens()];
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			words[i++] = token;
		}

		return words;
	}

	public void calculateTf() {
		Set<String> docs = allDocuments.keySet();
		allTerms = new TreeSet<String>();

		for (String docName : docs) {
			caculateTf(docName);
		}

		if (!logger.isDebugEnabled()) {
			System.out.println("#################### TF ######################");
			printConsole();
		}
	}

	private void caculateTf(String docName) {
		Document doc = allDocuments.get(docName);
		String[] Terms = doc.getWords();

		// TF for a document
		Double[] values = null;
		Map<String, Double[]> tfs = new TreeMap<String, Double[]>();
		Double numberOfwords = 0.0;
		for (String term : Terms) {
			if (term.length() < 2) {
				continue;
			}

			if (tfs.containsKey(term)) {
				Double[] tempValues = tfs.get(term);
				tempValues[0] += 1.0;
				tfs.put(term, tempValues);
			} else {
				values = new Double[] { 1.0, 0.0, 0.0, 0.0 }; // TF, Normalized-TF, IDF, TF-IDF
				tfs.put(term, values);
			}

			numberOfwords++;
			allTerms.add(term);
		}

		// Normalized Tf for a document
		Set<String> TermsSet = tfs.keySet();
		for (String term : TermsSet) {
			Double[] tempValues = tfs.get(term);
			tempValues[1] = tempValues[0] / numberOfwords;
			tfs.put(term, tempValues);
		}

		doc.setTf(tfs);
	}

	public void calculateIdf() {
		Set<String> docs = allDocuments.keySet();

		int numberOfAllDocuments = allDocuments.size();
		Double numberOfDocument = 0.0;

		Iterator<String> it = allTerms.iterator();
		while (it.hasNext()) {
			String term = it.next();
			for (String doc : docs) {
				Document docu = allDocuments.get(doc);
				if (docu.getTfIdfs().containsKey(term)) {
					numberOfDocument++;
				}
			}

			double idf = 1 + Math.log((double) numberOfAllDocuments / numberOfDocument);
			// double idf = Math.log((double) numberOfAllDocuments / numberOfDocument);
			// double idf = (double) numberOfAllDocuments / numberOfDocument;
			for (String doc : docs) {
				Document docu = allDocuments.get(doc);
				if (docu.getTfIdfs().containsKey(term)) {
					Double[] values = docu.getTfIdfs().get(term);
					values[2] = idf;
				}
			}

			numberOfDocument = 0.0;
		}

		if (!logger.isDebugEnabled()) {
			System.out.println("\n#################### IDF ######################");
			printConsole();
		}
	}

	private void computeTfIdf() {
		Set<String> documents = allDocuments.keySet();
		for (String docsName : documents) {
			Document document = allDocuments.get(docsName);
			Map<String, Double[]> tfIdfs = document.getTfIdfs();
			Set<String> terms = tfIdfs.keySet();

			double[] vertor = new double[terms.size()];
			int i = 0;
			for (String term : terms) {
				Double[] values = tfIdfs.get(term);
				values[3] = values[1] * values[2];

				vertor[i++] = values[3];
			}
			document.setVectors(vertor);
		}

		if (!logger.isDebugEnabled()) {
			System.out.println("\n#################### TF-IDF ######################");
			printConsole();
		}
	}

	private void computeTfIdf2() {
		Set<String> documents = allDocuments.keySet();
		for (String docsName : documents) {
			Document document = allDocuments.get(docsName);
			Map<String, Double[]> tfIdfs = document.getTfIdfs();
			Set<String> terms = tfIdfs.keySet();

			// double[] vertor = new double[terms.size()];
			double[] vertor = new double[allTerms.size()];
			int i = 0;

			Iterator<String> it = allTerms.iterator();
			while (it.hasNext()) {
				String term = it.next();
				if (terms.contains(term)) {
					Double[] values = tfIdfs.get(term);
					values[3] = values[1] * values[2];

					vertor[i++] = values[3];
				} else {
					vertor[i++] = 0.0;
				}
			}

			document.setVectors(vertor);
		}

		if (!logger.isDebugEnabled()) {
			System.out.println("\n#################### TF-IDF ######################");
			printConsole();
		}
	}

	public void calculateTfIdf() {
		calculateTf();
		calculateIdf();
		// computeTfIdf();
		computeTfIdf2();
	}

	private void printConsole() {
		Set<String> debugDocs = allDocuments.keySet();
		for (String docName : debugDocs) {
			System.out.println("================= " + docName + " =================");
			Document document = allDocuments.get(docName);
			Map<String, Double[]> tfIdfs = document.getTfIdfs();
			Set<String> terms = tfIdfs.keySet();
			int index = 1;
			for (String term : terms) {
				Double[] tempValues = tfIdfs.get(term);
				System.out.println("(" + index++ + ") Term => " + term + "\t TF: " + tempValues[0] + "\t NTF: "
						+ tempValues[1] + "\t IDF: " + tempValues[2] + "\t TF-IDF: " + tempValues[3]);

			}
			index = 0;
		}
	}

	public void calculateSimilarity() {
		similarity = new TreeMap<String, TreeMap<String, Double>>();

		TreeMap<String, Double> similarDocuments = null;
		Set<String> docs = allDocuments.keySet();
		for (String docName : docs) {
			Document pivotDoc = allDocuments.get(docName);
			similarDocuments = similarDocuments(pivotDoc);

			System.out.println("\n\n[" + docName + "]");
			int i = 1;
			Set<String> keySet = similarDocuments.keySet();
			for (String doc : keySet) {
				Double score = similarDocuments.get(doc);
				System.out.println(
						"(" + i++ + ") " + doc + " => " + score + " => (%): " + String.format("%.2f", score * 100));
			}
		}
	}

	private TreeMap<String, Double> similarDocuments(Document pivotDoc) {
		Map<String, Double> similarDocs = new HashMap<String, Double>();
		ScoreCompare scoreComp = new ScoreCompare(similarDocs);
		TreeMap<String, Double> sortedSimilars = new TreeMap<String, Double>(scoreComp);

		String pivotDocName = pivotDoc.getDocumentName();

		Set<String> docs = allDocuments.keySet();
		for (String otherDocName : docs) {
			if (pivotDocName.equals(otherDocName)) {
				similarDocs.put(otherDocName, 1.0);
				continue;
			}

			similarDocs.put(otherDocName, consineSimlarity(pivotDoc, allDocuments.get(otherDocName)));
		}

		sortedSimilars.putAll(similarDocs);
		return sortedSimilars;
	}

	private Double consineSimlarity(Document pivotDoc, Document otherDoc) {
		String[] pivotTerms = pivotDoc.getTerms();
		double[] numeratorVectors = new double[allTerms.size()];

		int i = 0;
		for (String pivotTerm : pivotTerms) {
			// logger.debug("pivot term => " + pivotTerm);
			Map<String, Double[]> pivotTfIdfs = pivotDoc.getTfIdfs();
			Map<String, Double[]> otherTfIdfs = otherDoc.getTfIdfs();

			if (otherTfIdfs.containsKey(pivotTerm)) {
				double otherTfIdf = otherTfIdfs.get(pivotTerm)[3].doubleValue();
				double pivotTfIdf = pivotTfIdfs.get(pivotTerm)[3].doubleValue();

				// logger.debug("[HIT] pivot term => " + pivotTerm + ": " + pivotTfIdf);
				// logger.debug("[HIT] other term => " + pivotTerm + ": " + otherTfIdf);
				numeratorVectors[i++] = otherTfIdf * pivotTfIdf;
			}
		}

		// double dotProduct = 0.0;
		// for (i = 0; i < numeratorVectors.length; i++) {
		// dotProduct += numeratorVectors[i]; // a.b
		// }
		//
		// double cosineSimilarity = 0.0;
		// double pivotMagnitude = pivotDoc.getMagnitude();
		// double otherMagnitude = otherDoc.getMagnitude();
		//
		// if (pivotMagnitude != 0.0 | otherMagnitude != 0.0) {
		// cosineSimilarity = dotProduct / (pivotMagnitude * otherMagnitude);
		// } else {
		// return 0.0;
		// }
		//
		// logger.debug(pivotDoc.getDocumentName() + " vs. " + otherDoc.getDocumentName() + " similarity value ==> "
		// + cosineSimilarity);

		Double cosineSimilarity2 = cosineSimilarity(pivotDoc.getVectors(), otherDoc.getVectors(), numeratorVectors);
		// logger.debug(pivotDoc.getDocumentName() + " vs. " + otherDoc.getDocumentName() + " similarity2 value ==> "
		// + cosineSimilarity2);

		// return cosineSimilarity;
		return cosineSimilarity2;
	}

	private Double cosineSimilarity(double[] pivotVectors, double[] otherVectors, double[] numeratorVectors) {
		double dotProduct = 0.0;
		double magnitude1 = 0.0;
		double magnitude2 = 0.0;
		double cosineSimilarity = 0.0;

		for (int i = 0; i < numeratorVectors.length; i++) {
			dotProduct += numeratorVectors[i]; // a.b
		}

		for (int i = 0; i < pivotVectors.length; i++) // docVector1 and docVector2 must be of same length
		{
			magnitude1 += Math.pow(pivotVectors[i], 2); // (a^2)
			magnitude2 += Math.pow(otherVectors[i], 2); // (b^2)
		}

		magnitude1 = Math.sqrt(magnitude1);// sqrt(a^2)
		magnitude2 = Math.sqrt(magnitude2);// sqrt(b^2)

		if (magnitude1 != 0.0 | magnitude2 != 0.0) {
			cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
		} else {
			return 0.0;
		}

		return cosineSimilarity;
	}

	public Double cosineSimilarity(double[] docVector1, double[] docVector2) {
		double dotProduct = 0.0;
		double magnitude1 = 0.0;
		double magnitude2 = 0.0;
		double cosineSimilarity = 0.0;

		for (int i = 0; i < docVector1.length; i++) // docVector1 and docVector2 must be of same length
		{
			dotProduct += docVector1[i] * docVector2[i]; // a.b
			magnitude1 += Math.pow(docVector1[i], 2); // (a^2)
			magnitude2 += Math.pow(docVector2[i], 2); // (b^2)
		}

		magnitude1 = Math.sqrt(magnitude1);// sqrt(a^2)
		magnitude2 = Math.sqrt(magnitude2);// sqrt(b^2)

		if (magnitude1 != 0.0 | magnitude2 != 0.0) {
			cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
		} else {
			return 0.0;
		}

		return cosineSimilarity;
	}
}
