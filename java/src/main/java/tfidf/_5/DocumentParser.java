package algorithms.tfidf._5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snu.ids.ha.index.Keyword;
import org.snu.ids.ha.index.KeywordExtractor;
import org.snu.ids.ha.index.KeywordList;

public class DocumentParser {
	private final Logger logger = LoggerFactory.getLogger(DocumentParser.class);

	private Set<String> allTerms;
	private Map<String, Document> allDocuments;

	public DocumentParser(String[] docs) {
		allTerms = new HashSet<String>();
		allDocuments = new TreeMap<String, Document>();

		int index = 1;
		Map<String, double[]> terms = null;
		KeywordExtractor ke = new KeywordExtractor();
		for (String doc : docs) {
			// temp key creation
			String docName = "doc_" + index++;

			terms = new TreeMap<String, double[]>();
			KeywordList kl = ke.extractKeyword(doc, true);
			double[] values = null;
			for (int i = 0; i < kl.size(); i++) {
				Keyword keyword = kl.get(i);

				String term = keyword.getString();
				if (term.length() < 2) {
					continue;
				}

				int cnt = keyword.getCnt();
				double tf = keyword.getFreq();

				values = new double[] { cnt, tf, 0.0, 0.0 };
				terms.put(term, values);
				allTerms.add(term);

			}

			allDocuments.put(docName, new Document(docName, terms));
		}

		printDebug();
	}

	public DocumentParser(String filePath) throws IOException {
		allTerms = new HashSet<String>();
		allDocuments = new TreeMap<String, Document>();
		File[] allfiles = new File(filePath).listFiles();
		BufferedReader in = null;

		Map<String, double[]> terms = null;
		KeywordExtractor ke = new KeywordExtractor();
		for (File f : allfiles) {
			String docName = f.getName().split("[.]")[0].trim();

			if (f.getName().endsWith(".text")) {
				in = new BufferedReader(new FileReader(f));

				StringBuilder sb = new StringBuilder();
				String s = null;
				while ((s = in.readLine()) != null) {
					sb.append(s);
				}

				terms = new TreeMap<String, double[]>();
				KeywordList kl = ke.extractKeyword(sb.toString(), true);
				double[] values = null;
				for (int i = 0; i < kl.size(); i++) {
					Keyword keyword = kl.get(i);

					String term = keyword.getString();
					if (term.length() < 2) {
						continue;
					}

					int cnt = keyword.getCnt();
					double tf = keyword.getFreq();

					values = new double[] { cnt, tf, 0.0, 0.0 };
					terms.put(term, values);
					logger.debug("[" + term + "] => cnt: " + cnt + " => tf: " + tf);
					allTerms.add(term);
				}

				allDocuments.put(docName, new Document(docName, terms));
			}
		}
	}

	public DocumentParser(String filePath, boolean firstLineSkip, boolean isTokenize) throws IOException {
		logger.info(filePath);
		allTerms = new HashSet<String>();
		allDocuments = new TreeMap<String, Document>();
		File[] allfiles = new File(filePath).listFiles();

		logger.debug("input file path => " + filePath);

		Map<String, double[]> terms = null;
		if (!isTokenize) {
			KeywordExtractor ke = new KeywordExtractor();
			for (File file : allfiles) {
				if (file.getName().endsWith(".text")) {
					Map<String, String> docs = makeDocument(firstLineSkip, file);

					for (String docName : docs.keySet()) {
						String content = docs.get(docName);
						terms = new TreeMap<String, double[]>();

						double[] values = null;
						KeywordList kl = ke.extractKeyword(content, true);
						for (int i = 0; i < kl.size(); i++) {
							Keyword keyword = kl.get(i);

							String term = keyword.getString();
							if (term.length() < 2) {
								continue;
							}

							int cnt = keyword.getCnt();
							double tf = keyword.getFreq();

							values = new double[] { cnt, tf, 0.0, 0.0 };
							logger.debug("[" + term + "] => cnt: " + cnt + " => tf: " + tf);
							terms.put(term, values);
							allTerms.add(term);
						}

						allDocuments.put(docName, new Document(docName, terms));
					}
				}

			}
		} else {
			for (File file : allfiles) {
				if (file.getName().endsWith(".text")) {
					Map<String, String> docs = makeDocument(firstLineSkip, file);

					for (String docName : docs.keySet()) {
						String content = docs.get(docName);
						String[] words = tokenize(content);
						terms = new TreeMap<String, double[]>();
						double[] values = null;

						// keyword count
						double numberOfwords = 0.0;
						for (String keyword : words) {
							if (keyword.length() < 2) {
								continue;
							}

							if (terms.containsKey(keyword)) {
								double[] tempValues = terms.get(keyword);
								tempValues[0] += 1.0;
								terms.put(keyword, tempValues);
							} else {
								values = new double[] { 1.0, 0.0, 0.0, 0.0 }; // Cnt, TF, IDF, TF-IDF
								terms.put(keyword, values);
							}
							numberOfwords++;
						}

						// keyword TF
						for (String keyword : terms.keySet()) {
							double[] tempValues = terms.get(keyword);
							tempValues[1] = tempValues[0] / numberOfwords;
							logger.debug("[" + keyword + "] => cnt: " + tempValues[0] + " => tf: " + tempValues[1]);
							terms.put(keyword, tempValues);
							allTerms.add(keyword);
						}

						allDocuments.put(docName, new Document(docName, terms));
					}
				}
			}
		}
	}

	private Map<String, String> makeDocument(boolean firstLineSkip, File file) throws IOException {
		Map<String, String> docs = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));

			if (firstLineSkip) {
				br.readLine();
			}

			String key = null;
			String line = null;
			docs = new HashMap<String, String>();
			while ((line = br.readLine()) != null) {
				String[] splitedLine = line.split("[,]");
				key = splitedLine[0] + "_" + splitedLine[1] + "_" + splitedLine[2] + "_" + splitedLine[3];

				docs.put(key, splitedLine[4]);
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		} finally {
			br.close();
		}
		return docs;
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

	private void printDebug() {
		if (!logger.isDebugEnabled()) {
			Iterator<String> it = allTerms.iterator();
			StringBuilder sb = new StringBuilder();
			while (it.hasNext()) {
				String term = it.next();
				sb.append(term + " ");
			}
			logger.debug("All terms(" + allTerms.size() + ") => " + sb.toString());
			sb.setLength(0);

			Set<String> allDocs = allDocuments.keySet();
			for (String docName : allDocs) {
				Document document = allDocuments.get(docName);

				Map<String, double[]> termsOfDocMap = document.getTerms();
				Set<String> termsOfDoc = termsOfDocMap.keySet();
				Iterator<String> itTermsOfDoc = termsOfDoc.iterator();
				while (itTermsOfDoc.hasNext()) {
					String term = itTermsOfDoc.next();
					sb.append(term + " ");
				}
				logger.debug(document.getName() + "'s terms => " + sb.toString());
				sb.setLength(0);
			}
		}
	}

	public void tfidf() {
		calculateIdf();
		calculateTfIdf();
	}

	private void calculateIdf() {
		Set<String> docs = allDocuments.keySet();

		double numberOfAllDocuments = (double) allDocuments.size();
		double numberOfDocument = 0.0;

		Iterator<String> it = allTerms.iterator();
		while (it.hasNext()) {
			String term = it.next();
			for (String doc : docs) {
				Document document = allDocuments.get(doc);
				if (document.getTerms().containsKey(term)) {
					numberOfDocument++;
				}
			}

			double idf = 1 + Math.log(numberOfAllDocuments / numberOfDocument);
			// double idf = Math.log(numberOfAllDocuments / numberOfDocument);
			// double idf = numberOfAllDocuments / numberOfDocument;
			logger.info("=> [" + term + "] in " + (int) numberOfDocument + " documenst(s)! ==> IDF: " + idf);

			for (String doc : docs) {
				Document document = allDocuments.get(doc);
				Map<String, double[]> terms = document.getTerms();
				if (terms.containsKey(term)) {
					double[] values = terms.get(term);
					values[2] = idf;
				}
			}

			numberOfDocument = 0.0;
		}
	}

	private void calculateTfIdf() {
		Set<String> documents = allDocuments.keySet();
		for (String name : documents) {
			Document document = allDocuments.get(name);
			Map<String, double[]> termsOfDoc = document.getTerms();
			Set<String> terms = termsOfDoc.keySet();

			double[] vectors = new double[allTerms.size()];

			int i = 0;
			Iterator<String> it = allTerms.iterator();
			while (it.hasNext()) {
				String term = it.next();
				if (terms.contains(term)) {
					double[] values = termsOfDoc.get(term);
					values[3] = values[1] * values[2];
					vectors[i++] = values[3];
				} else {
					vectors[i++] = 0.0;
				}
			}
			document.setVector(vectors);
		}
	}

	public Map<String, Map<String, Double>> similarity() {
		Map<String, Map<String, Double>> similarity = new HashMap<String, Map<String, Double>>();
		// Map<Document, Map<String, Double>> similarity = new HashMap<Document, Map<String, Double>>();
		Map<String, Double> similarDocuments = null;
		Set<String> docs = allDocuments.keySet();

		for (String docName : docs) {
			Document pivotDoc = allDocuments.get(docName);
			similarDocuments = similarDocuments(pivotDoc, 0);

			if (logger.isDebugEnabled()) {
				logger.debug("[" + docName + "]");
				for (String otherDocName : similarDocuments.keySet()) {
					Double score = similarDocuments.get(otherDocName);
					logger.debug(otherDocName + ":" + score);
				}
			}

			similarity.put(pivotDoc.getName(), similarDocuments);
		}

		return similarity;
	}

	public Map<Document, Map<String, Double>> similarity2(int rank) {
		Map<Document, Map<String, Double>> similarity = new HashMap<Document, Map<String, Double>>();
		Map<String, Double> similarDocuments = null;

		int docCnt = allDocuments.size();
		if (rank != 0 && docCnt < rank) {
			rank = docCnt;
		}

		for (String docName : allDocuments.keySet()) {
			Document pivotDoc = allDocuments.get(docName);
			// similarDocuments = similarDocuments(pivotDoc);
			similarDocuments = similarDocuments(pivotDoc, rank);

			if (logger.isDebugEnabled()) {
				int i = 1;
				logger.debug("#(" + i++ + ") [" + docName + "]");
				for (String otherDocName : similarDocuments.keySet()) {
					Double score = similarDocuments.get(otherDocName);
					logger.debug(otherDocName + ":" + score);
				}
			}

			similarity.put(pivotDoc, similarDocuments);
		}

		return similarity;
	}

	private Map<String, Double> similarDocuments(Document pivotDoc, int rank) {
		Map<String, Double> similarDocs = new TreeMap<String, Double>();
		Map<String, Double> rankSimilarDocs = new TreeMap<String, Double>();

		String pivotDocName = pivotDoc.getName();

		Set<String> docs = allDocuments.keySet();
		for (String otherDocName : docs) {
			if (pivotDocName.equals(otherDocName)) {
				// similarDocs.put(otherDocName, 1.0);
				continue;
			}

			similarDocs.put(otherDocName, consineSimlarity(pivotDoc, allDocuments.get(otherDocName)));
		}

		int i = 0;
		Map<String, Double> sortedSimilarDocs = sortByComparator(similarDocs);
		for (String docName : sortedSimilarDocs.keySet()) {
			if (i < rank) {
				rankSimilarDocs.put(docName, sortedSimilarDocs.get(docName));
			}

			i++;
		}

		// return sortByComparator(similarDocs);
		return sortByComparator(rankSimilarDocs);
		// return rankSimilarDocs;
	}

	private Map<String, Double> sortByComparator(Map<String, Double> similarDocs) {
		List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(similarDocs.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Collections.reverse(list);

		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		for (Iterator<Map.Entry<String, Double>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Double> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	private Double consineSimlarity(Document pivotDoc, Document otherDoc) {
		List<String> commonTermsList = new ArrayList<String>();
		Set<String> pivotTerms = pivotDoc.getTerms().keySet();
		double[] numeratorVectors = new double[allTerms.size()];

		int index = 0;
		for (String pivotTerm : pivotTerms) {
			Map<String, double[]> pivotTermsList = pivotDoc.getTerms();
			Map<String, double[]> otherTermsList = otherDoc.getTerms();

			if (otherTermsList.containsKey(pivotTerm)) {
				double otherTfIdf = otherTermsList.get(pivotTerm)[3];
				double pivotTfIdf = pivotTermsList.get(pivotTerm)[3];

				numeratorVectors[index++] = otherTfIdf * pivotTfIdf;
				commonTermsList.add(pivotTerm);
			}
		}

		if (commonTermsList.size() > 0) {
			String[] commonTerms = commonTermsList.toArray(new String[commonTermsList.size()]);
			Map<String, String[]> commonTermsMap = new TreeMap<String, String[]>();
			commonTermsMap.put(otherDoc.getName(), commonTerms);
			pivotDoc.setCommonTerms(commonTermsMap);
		}

		double[] pivotVectors = pivotDoc.getVectors();
		double[] otherVectors = otherDoc.getVectors();

		double dotProduct = 0.0;
		double magnitude1 = 0.0;
		double magnitude2 = 0.0;
		double cosineSimilarity = 0.0;

		for (int i = 0; i < numeratorVectors.length; i++) {
			dotProduct += numeratorVectors[i];
		}

		for (int i = 0; i < pivotVectors.length; i++) {
			magnitude1 += Math.pow(pivotVectors[i], 2);
			magnitude2 += Math.pow(otherVectors[i], 2);
		}

		magnitude1 = Math.sqrt(magnitude1);
		magnitude2 = Math.sqrt(magnitude2);

		if (magnitude1 != 0.0 | magnitude2 != 0.0) {
			cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
		} else {
			return 0.0;
		}

		return cosineSimilarity;
	}
}
