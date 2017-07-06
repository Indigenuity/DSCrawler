package utilities;

import static org.junit.Assert.*;

import org.junit.Test;

public class UrlUtilsTest {

	@Test
	public void testRemoveLanguage() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveHttp() {
		assertEquals( UrlUtils.removeHttp("http://www.kengarffvw.com"), "www.kengarffvw.com");
		assertEquals( UrlUtils.removeHttp("https://www.kengarffvw.com"), "www.kengarffvw.com");
		assertEquals( UrlUtils.removeHttp("www.kengarffvw.com"), "www.kengarffvw.com");
		assertEquals( UrlUtils.removeHttp("ftp://www.kengarffvw.com"), "ftp://www.kengarffvw.com");
		assertEquals( UrlUtils.removeHttp("http://www.keᖀ ᖁngarffvw.com"), "www.keᖀ ᖁngarffvw.com");
		assertEquals( UrlUtils.removeHttp(""), "");
		assertNull(UrlUtils.removeHttp(null));
	}

	@Test
	public void testToCom() {
		assertEquals( UrlUtils.toCom("http://www.kengarffvw.com"), "http://www.kengarffvw.com");
		assertEquals( UrlUtils.toCom("http://www.kengarffvw.net"), "http://www.kengarffvw.com");
		assertEquals( UrlUtils.toCom("http://www.kengarffvw.biz"), "http://www.kengarffvw.com");
		assertEquals( UrlUtils.toCom("http://www.kengarffvw.us"), "http://www.kengarffvw.com");
		assertEquals( UrlUtils.toCom("http://www.kengarffvw.cc"), "http://www.kengarffvw.com");
		assertEquals( UrlUtils.toCom("http://www.kengarffvw.org"), "http://www.kengarffvw.com");
		assertEquals( UrlUtils.toCom("http://www.kengarffvw.info"), "http://www.kengarffvw.com");
		assertEquals( UrlUtils.toCom("http://www.kengarffvw.ca"), "http://www.kengarffvw.com");
		assertEquals( UrlUtils.toCom("http://www.kengarffvw.me"), "http://www.kengarffvw.com");
		assertEquals( UrlUtils.toCom("http://www.kengarffvw.car"), "http://www.kengarffvw.com");
		
		assertEquals( UrlUtils.toCom("http://www.kengarffvw.com/"), "http://www.kengarffvw.com/");
		assertEquals( UrlUtils.toCom("http://www.kengarffvw.net/"), "http://www.kengarffvw.com/");
		
		assertEquals( UrlUtils.toCom("http://www.kengarffvw.net/index.html"), "http://www.kengarffvw.com/index.html");
		assertEquals( UrlUtils.toCom("http://www.kengarffvw.net/index.html?query=wtf"), "http://www.kengarffvw.com/index.html?query=wtf");
		
		assertEquals( UrlUtils.toCom("http://www.networthykengarffvw.com"), "http://www.networthykengarffvw.com");
		
		assertEquals( UrlUtils.toCom("http://www.kengarffvw.com?ref=www.example.net"), "http://www.kengarffvw.com?ref=www.example.net");
		
		assertEquals( UrlUtils.toCom("http://www.kᖀ ᖁengarffvw.net"), "http://www.kᖀ ᖁengarffvw.com");
		
		assertEquals( UrlUtils.toCom(""), "");
		assertNull(UrlUtils.removeHttp(null));
	}

	@Test
	public void testRemoveWww() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveQueryString() {
		fail("Not yet implemented");
	}

	@Test
	public void testToHttp() {
		fail("Not yet implemented");
	}

}
