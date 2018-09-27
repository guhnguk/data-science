package tfidf._2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

/**
 * An instance of TfIdf contains TreeMaps for documents and corpus Documents dictionary uses file names as keys, and the
 * document class as values Corpus dictionary uses words as keys and [#articles they appear in, idf] as values
 * 
 * To use TfIdf algorithm, user needs to create an instance of it by giving it a folder address. When user calls
 * BuildAllDocuments(), TfIdf values for words in each document is calculated User can call methods such as bestWordList
 * or similarDocuments using the filename to get results. See Document class documentation for more info
 * 
 * @author Barkin Aygun
 *
 */
public class Tfidf {
	public TreeMap<String, Document> documents;
	public TreeMap<String, Double[]> allwords; // d_j: t_i elem d_j, idf_j
	public boolean corpusUpdated;
	public int docSize;

	/**
	 * Filename filter to accept .txt files
	 */
	FilenameFilter filter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			if (name.toLowerCase().endsWith(".text"))
				return true;
			return false;
		}
	};

	/**
	 * This comparator lets the library sort the documents according to their similarities
	 */
	private static class ValueComparer implements Comparator<String> {
		private TreeMap<String, Double> _data = null;

		public ValueComparer(TreeMap<String, Double> data) {
			super();
			_data = data;
		}

		public int compare(String o1, String o2) {
			double e1 = _data.get(o1);
			double e2 = _data.get(o2);
			if (e1 > e2)
				return -1;
			if (e1 == e2)
				return 0;
			if (e1 < e2)
				return 1;
			return 0;
		}
	}

	/**
	 * Loads all files in the folder name into the corpus, and updates if necessary
	 * 
	 * @param foldername
	 *            Location of text files
	 */
	public Tfidf(String foldername) {
		allwords = new TreeMap<String, Double[]>();
		documents = new TreeMap<String, Document>();
		docSize = 0;

		// Document 1: The game of life is a game of everlasting learning
		// Document 2: The unexamined life is not worth living
		// Document 3: Never stop learning
		// Query: life learning

		File datafolder = new File(foldername);
		if (datafolder.isDirectory()) {
			String[] files = datafolder.list(filter);
			for (int i = 0; i < files.length; i++) {
				docSize++;
				insertDocument(foldername + "/" + files[i]);
			}
		} else {
			docSize++;
			insertDocument(foldername);
		}
		corpusUpdated = false;
		if (corpusUpdated == false) {
			updateCorpus();
		}
	}

	/**
	 * Updates the corpus, going through every word and changing their frequency
	 */
	public void updateCorpus() {
		String word;
		Double[] corpusdata;
		for (Iterator<String> it = allwords.keySet().iterator(); it.hasNext();) {
			word = it.next();
			corpusdata = allwords.get(word);

			// IDF calculation
			// corpusdata[1] = Math.log(docSize / corpusdata[0]);
			corpusdata[1] = 1 + Math.log(docSize / corpusdata[0]);
			allwords.put(word, corpusdata);
		}
		corpusUpdated = true;
	}

	/**
	 * Calculates the Tf-Idf of the given document
	 * 
	 * @param documentName
	 */
	public void buildDocument(String documentName) {
		Document doc = documents.get(documentName);
		if (doc == null)
			return;
		doc.calculateTfIdf(this);
	}

	/**
	 * Calculates tf-idf of all documents in the library
	 */
	public void buildAllDocuments() {
		String word;
		for (Iterator<String> it = documents.keySet().iterator(); it.hasNext();) {
			word = it.next();
			documents.get(word).calculateTfIdf(this);
		}
	}

	/**
	 * Inserts new documents into corpus
	 * 
	 * @param filename
	 *            Location of the text file
	 */
	public void insertDocument(String filename) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filename));

			String fileName = filename.substring(filename.lastIndexOf('/') + 1);

			Document doc = new Document(br, fileName, this);
			documents.put(fileName, doc);

			if (corpusUpdated == false)
				updateCorpus();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Increments the occurence count of a word by 1
	 * 
	 * @param word
	 *            String of the word
	 */
	public void addWordOccurence(String word) {
		Double[] tempdata;
		if (allwords.get(word) == null) {
			tempdata = new Double[] { 1.0, 0.0 };
			allwords.put(word, tempdata);
		} else {
			tempdata = allwords.get(word);
			tempdata[0]++;
			allwords.put(word, tempdata);
		}

		// debug
		System.out.println();
		System.out.println("Word add => " + word + "\t dc: " + tempdata[0] + "\t tf: " + tempdata[1]);
		System.out.println("--------------------------------------------------------");
		System.out.println("All words");
		TreeMap<String, Double[]> copiedWords = allwords;
		int i = 1;
		for (Iterator<String> it = copiedWords.keySet().iterator(); it.hasNext();) {
			String str = it.next();
			System.out.println(i++ + ": word => " + str + "\t wc => " + copiedWords.get(str)[0] + "\t tf => "
					+ copiedWords.get(str)[1]);
		}

	}

	public void addWordOccurence2(String word, Double[] tempdata2) {
		Double[] tempdata;
		if (allwords.get(word) == null) {
			// tempdata = new Double[] { 1.0, 0.0, 0.0 };
			allwords.put(word, tempdata2);
		} else {
			tempdata = allwords.get(word);
			tempdata[0]++;
			allwords.put(word, tempdata);
		}

		System.out.println("Word add in all bags => " + word);
	}

	/**
	 * Calculates cosine similarity between two documents
	 * 
	 * @param doc1
	 *            Document 1
	 * @param doc2
	 *            Document 2
	 * @return the cosine similarity
	 */
	public double cosSimilarity(Document doc1, Document doc2) {
		String word;
		double similarity = 0;
		for (Iterator<String> it = doc1.words.keySet().iterator(); it.hasNext();) {
			word = it.next();
			if (doc2.words.containsKey(word)) {
				Double docVector1 = doc1.words.get(word)[2];
				Double docVector2 = doc2.words.get(word)[2];
				similarity += docVector1 * docVector2;
			}
		}
		similarity = similarity / (doc1.vectorlength * doc2.vectorlength);
		return similarity;
	}

	/**
	 * Returns a sorted instance of documents where first one is the closest to given document name
	 * 
	 * @param docName
	 *            Name of the document for comparison
	 * @return Names of all documents, closest first, furthest last
	 */
	public String[] similarDocuments(String docName) {
		TreeMap<String, Double> similarDocs = new TreeMap<String, Double>();
		String otherDoc;
		for (Iterator<String> it = documents.keySet().iterator(); it.hasNext();) {
			otherDoc = it.next();
			if (docName.equals(otherDoc))
				continue;
			similarDocs.put(otherDoc, cosSimilarity(documents.get(docName), documents.get(otherDoc)));
		}
		TreeMap<String, Double> sortedSimilars = new TreeMap<String, Double>(new ValueComparer(similarDocs));
		sortedSimilars.putAll(similarDocs);
		String[] array = sortedSimilars.keySet().toArray(new String[1]);
		return array;
	}

	public TreeMap<String, Double> similarDocuments2(String docName) {
		TreeMap<String, Double> similarDocs = new TreeMap<String, Double>();
		String otherDoc;
		for (Iterator<String> it = documents.keySet().iterator(); it.hasNext();) {
			otherDoc = it.next();
			if (docName.equals(otherDoc))
				continue;
			similarDocs.put(otherDoc, cosSimilarity(documents.get(docName), documents.get(otherDoc)));
		}
		TreeMap<String, Double> sortedSimilars = new TreeMap<String, Double>(new ValueComparer(similarDocs));
		sortedSimilars.putAll(similarDocs);

		return sortedSimilars;
	}

	/**
	 * Returns the best words in the document
	 * 
	 * @param docName
	 *            Name of the document
	 * @param numWords
	 *            Number of words expected
	 * @return String array of words
	 */
	public String[] bestWords(String docName, int numWords) {
		return documents.get(docName).bestWordList(numWords);
	}

	/**
	 * Override for bestWords using default value (refer to Document doc)
	 * 
	 * @param docName
	 *            Name of the document
	 * @return String array of words
	 */
	public String[] bestWords(String docName) {
		return documents.get(docName).bestWordList();
	}

	/**
	 * Returns the String array of all document names
	 * 
	 * @return array of Strings
	 */
	public String[] documentNames() {
		return documents.keySet().toArray(new String[1]);
	}

	/**
	 * Test code, might have to change the data path
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Test code for TfIdf
		// Tfidf tf = new Tfidf("D:/Workspace/Data/Tf-Idf/data.back");
		Tfidf tf = new Tfidf("D:/Workspace/Data/Tf-Idf/data.selfintro");
		// Tfidf tf = new Tfidf("D:/Workspace/Data/Tf-Idf/data");

		String word;
		Double[] corpusdata;
		System.out.println();
		System.out.println("-------------------------------------------------------------------");
		for (Iterator<String> it = tf.allwords.keySet().iterator(); it.hasNext();) {
			word = it.next();
			corpusdata = tf.allwords.get(word);
			System.out.println(word + "\t wc: " + corpusdata[0] + "\t idf: " + corpusdata[1]);
		}
		System.out.println();

		tf.buildAllDocuments();
		String[] bwords;
		String[] bdocs;
		for (Iterator<String> it = tf.documents.keySet().iterator(); it.hasNext();) {
			word = it.next();
			System.out.println(word);
			System.out.println("------------------------------------------");

			bwords = tf.documents.get(word).bestWordList(5);
			bdocs = tf.similarDocuments(word);

			// for (String bword : bwords) {
			// System.out.println(bword + "\t");
			// }

			System.out.println();
			for (String bdoc : bdocs) {
				System.out.println(bdoc + "\t");
			}

			System.out.println("\n\n");
		}
		
		TreeMap<String, Double> similarDocuments2 = null;
		for (Iterator<String> it = tf.documents.keySet().iterator(); it.hasNext();) {
			word = it.next();
			System.out.println(word);
			System.out.println("------------------------------------------");
			
			similarDocuments2 = tf.similarDocuments2(word);
			
			Set<String> keySet = similarDocuments2.keySet();
			for (String doc : keySet){
				Double value = similarDocuments2.get(doc);
				System.out.println(doc  + ":" + value);
			}
			
			// for (String bword : bwords) {
			// System.out.println(bword + "\t");
			// }
			
//			System.out.println();
//			for (String bdoc : bdocs) {
//				System.out.println(bdoc + "\t");
//			}
//			
			System.out.println("\n\n");
		}
		
		

	}

}
