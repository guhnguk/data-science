package tfidf._1;

import java.util.List;

public class Tfidf {
	/**
	 * Calculated the tf of term termToCheck
	 * 
	 * @param totalterms
	 *            : Array of all the words under processing document
	 * @param termToCheck
	 *            : term of which tf is to be calculated.
	 * @return tf(term frequency) of term termToCheck
	 */
	public double tfCalculator(List<String> totalterms, String termToCheck) {
		double count = 0;
		for (String s : totalterms) {
			if (s.equalsIgnoreCase(termToCheck))
				count++;
		}
		return count / totalterms.size();
	}

	/**
	 * Calculated idf of term termToCheck
	 * 
	 * @param allTerms
	 *            : all the terms of all the documents
	 * @param termToCheck
	 * @return idf(inverse document frequency) score
	 */
	public double idfCalculator(List<String[]> allTerms, String termToCheck) {
		double count = 0;
		for (String[] ss : allTerms) {
			for (String s : ss) {
				if (s.equalsIgnoreCase(termToCheck)) {
					count++;
					break;
				}
			}
		}
		return Math.log(allTerms.size() / count);
	}
}




//
// public class Tf_Idf
// {
// /**
// * Calculated the tf of term termToCheck
// * @param totalterms : Array of all the words under processing document
// * @param termToCheck : term of which tf is to be calculated.
// * @return tf(term frequency) of term termToCheck
// */
// public double tfCalculator(List totalterms, String termToCheck)
// {
// double count = 0;
// for (String s : totalterms)
// {
// if (s.equalsIgnoreCase(termToCheck))
// count++;
// }
// return count / totalterms.length;
// }
//
// /**
// * Calculated idf of term termToCheck
// * @param allTerms : all the terms of all the documents
// * @param termToCheck
// * @return idf(inverse document frequency) score
// */
// public double idfCalculator(List<> allTerms, String termToCheck)
// {
// double count = 0;
// for (String[] ss : allTerms)
// {
// for (String s : ss)
// {
// if (s.equalsIgnoreCase(termToCheck))
// {
// count++;
// break;
// }
// }
// }
// return Math.log(allTerms.size() / count);
// }
//
// }
