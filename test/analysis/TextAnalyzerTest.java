package analysis;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class TextAnalyzerTest {

//	@Test
//	public void testGetVins() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testGetMoneyValues() {
		String moneyBags = "$10,000 &ndash; <span>$19,999 </span> data-id=$item.uuid (s|$),$1$2)+ $8000 $8000.74 $8,000 $8000.74 $800 $800.74 $5.7409 $8000.7 $100,589.74 8000.74";
		List<Double> moneyValues = TextAnalyzer.getMoneyValues(moneyBags);
		assertEquals(moneyValues.size(), 8);
		assertEquals(moneyValues.get(0), 10000, 0);
		assertEquals(moneyValues.get(1), 19999, 0);
		assertEquals(moneyValues.get(2), 8000, 0);
		assertEquals(moneyValues.get(3), 8000.74, 0);
		assertEquals(moneyValues.get(4), 8000, 0);
		assertEquals(moneyValues.get(5), 8000.74, 0);
		assertEquals(moneyValues.get(6), 8000, 0);
		assertEquals(moneyValues.get(7), 100589.74, 0);
		
	}

//	@Test
//	public void testContainsCity() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetSchedulers() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetGeneralMatches() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetCurrentTestMatches() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetMatchesStringCollectionOfT() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetMatchesStringTArray() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetMatchesStringPattern() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testCountOccurrences() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testHasOccurrenceStringPattern() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testHasOccurrenceStringCollectionOfPattern() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testHasOemOccurrence() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testExtractStringsFile() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testExtractStringsString() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testExtractUrls() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testMatchesPattern() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testContainsMake() {
//		fail("Not yet implemented");
//	}

}
