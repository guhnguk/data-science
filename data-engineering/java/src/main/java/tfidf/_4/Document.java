package tfidf._4;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Document {
	private String documentName;

	private String[] rawWords;
	private double rawWc;
	private Map<String, Double[]> tfIdfs;

	private String[] terms;
	private double[] vectors;
	private double magnitude;

	public Document(String documentName, String[] words, double wc) {
		this.documentName = documentName;
		this.rawWords = words;
		this.rawWc = wc;
	}

	public String getDocumentName() {
		return documentName;
	}

	public String[] getWords() {
		return this.rawWords;
	}

	public double getWc() {
		return this.rawWc;
	}

	public void setTf(Map<String, Double[]> tfs) {
		this.tfIdfs = tfs;

		Set<String> termSet = this.tfIdfs.keySet();
		this.terms = new String[termSet.size()];

		int i = 0;
		for (String term : termSet) {
			this.terms[i++] = term;
		}
	}

	public Map<String, Double[]> getTfIdfs() {
		return tfIdfs;
	}

	public String[] getTerms() {
		return terms;
	}

	public void setVectors(double[] vertors) {
		this.vectors = vertors;

		double tempMagnitude = 0.0;
		for (int i = 0; i < this.vectors.length; i++) {
			tempMagnitude += Math.pow(this.vectors[i], 2);
		}

		this.magnitude = Math.sqrt(tempMagnitude);
	}

	public double[] getVectors() {
		return this.vectors;
	}

	public double getMagnitude() {
		return magnitude;
	}
}
