package algorithms.tfidf._6;

import org.junit.Before;
import org.junit.Test;


public class TfCalculationTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void cmdTest() throws Exception {
		String[] args = { "D:/Workspace/Data/Tf-Idf/self_intro", "D:/Workspace/Data/Tf-Idf/result.csv" };
		TfIdfMainDriver_6.main(args);
	}

}
