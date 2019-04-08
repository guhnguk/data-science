package algorithms.tfidf._5;

import java.util.Comparator;
import java.util.Map;

public class ScoreCompare implements Comparator {
	private Map<String, Double> scores;

	public ScoreCompare(Map<String, Double> scores) {
		this.scores = scores;
	}

	// public int compare(Object o1, Object o2) {
	// double e1 = this.scores.get(o1);
	// double e2 = this.scores.get(o2);
	//
	// // if (e1 > e2)
	// // return -1;
	// // if (e1 == e2)
	// // return 0;
	// // if (e1 < e2)
	// // return 1;
	// // return 0;
	//
	// if (e1 >= e2) {
	// return -1;
	// } else {
	// return 1;
	// } // returning 0 would merge keys
	// }

	// note if you want decending in stead of ascending, turn around 1 and -1
	public int compare(Object a, Object b) {
		if ((Double) scores.get(a) == (Double) scores.get(b)) {
			return ((String) a).compareTo((String) b);
		} else if ((Double) scores.get(a) < (Double) scores.get(b)) {
			return 1;
		} else {
			return -1;
		}
	}

	// @Override
	// public int compare(String o1, String o2) {
	// double e1 = this.scores.get(o1);
	// double e2 = this.scores.get(o2);
	//
	// //if (e1 == e2) {
	// //return 0;
	// //} else
	// if (e1 <= e2) {
	// return -1;
	// } else {
	// return 1;
	// }
	// }
}
