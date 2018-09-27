package tfidf._5;

import java.util.Map;
import java.util.TreeMap;

public class Document {
	private Map<String, double[]> terms; // count, tf, idf, tfidf
	private String name;
	private double[] vectors;
	private Map<String, String[]> commonTerms;

	public Document(String docName, Map<String, double[]> terms) {
		this.name = docName;
		this.terms = terms;
		commonTerms = new TreeMap<String, String[]>();
	}

	public Document(String keyName, String[] words, int length) {
		// TODO Auto-generated constructor stub
	}

	public Map<String, double[]> getTerms() {
		return terms;
	}

	public String getName() {
		return name;
	}

	public void setVector(double[] vectors) {
		this.vectors = vectors;
	}

	public double[] getVectors() {
		return vectors;
	}

	public void setCommonTerms(Map<String, String[]> commonTermsMap) {
		this.commonTerms.putAll(commonTermsMap);
	}

	public Map<String, String[]> getCommonTerms() {
		return this.commonTerms;
	}
}
