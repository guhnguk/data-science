package tfidf._4;

import java.util.Comparator;
import java.util.Map;

public class ScoreCompare implements Comparator<String> {
	private Map<String, Double> documentScores = null;

	public ScoreCompare(Map<String, Double> documentScores) {
		super();
		this.documentScores = documentScores;
	}

	public int compare(String o1, String o2) {
		double e1 = this.documentScores.get(o1);
		double e2 = this.documentScores.get(o2);
		if (e1 > e2)
			return -1;
		if (e1 == e2)
			return 0;
		if (e1 < e2)
			return 1;
		return 0;
	}
}
