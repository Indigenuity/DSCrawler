package datadefinitions.inventory.implementations;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import datadefinitions.inventory.InvType;
import datadefinitions.inventory.InventoryTool;
import datadefinitions.inventory.InventoryToolTest;
import sites.persistence.Vehicle;

public class Auction123Test {
	
	private File rootPage = new File("./test/resources/InventoryTools/Auction123/rootPage.html");
	private File lastPage = new File("./test/resources/InventoryTools/Auction123/lastPage.html");
	private URI rootUri;
	private URI lastUri;
	private URI nextPageUri;

	private InventoryTool tool = new Auction123();
	
	private Document rootDoc;
	private Document lastDoc;
	
	
	List<URI> badUris = new ArrayList<URI>();
	List<URI> rootUris = new ArrayList<URI>();
	List<URI> pathUris = new ArrayList<URI>();

	@Before
	public void setup() throws URISyntaxException, FileNotFoundException, IOException {
		badUris.add(new URI("http://showroom.auction123.com/american_european_autos/widget.html?targetURL=http://americaneuropeanautos.web.auction123.com/inventory"));
		badUris.add(new URI("http://showroom.auction123.com/"));

		rootUris.add(new URI("http://showroom.auction123.com/american_european_autos/index.html?hidenew=&hideused=&hidecertified=&type=&make=&model=&minYear=&maxYear=&minPrice=&maxPrice=&filter="));
		
		pathUris.addAll(rootUris);
		pathUris.add(new URI("http://showroom.auction123.com/american_european_autos/page-2.html?hidenew=&hideused=&hidecertified=&type=&make=&model=&minYear=&maxYear=&minPrice=&maxPrice=&filter="));
		
		try(FileInputStream inputStream = new FileInputStream(rootPage)){
			rootDoc = Jsoup.parse(IOUtils.toString(inputStream, "UTF-8"));	
		}
		try(FileInputStream inputStream = new FileInputStream(lastPage)){
			lastDoc = Jsoup.parse(IOUtils.toString(inputStream, "UTF-8"));	
		}
		
		rootUri = new URI("http://showroom.auction123.com/american_european_autos/index.html?hidenew=&hideused=&hidecertified=&type=&make=&model=&minYear=&maxYear=&minPrice=&maxPrice=&filter=");
		nextPageUri = new URI("http://showroom.auction123.com/american_european_autos/page-2.html?hidenew=&hideused=&hidecertified=&type=&make=&model=&minYear=&maxYear=&minPrice=&maxPrice=&filter=");
		lastUri = new URI("http://showroom.auction123.com/american_european_autos/page-2.html?hidenew=&hideused=&hidecertified=&type=&make=&model=&minYear=&maxYear=&minPrice=&maxPrice=&filter=");
	}
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testIsNewPath() {
		assertFalse(tool.isNewPath(null));
		assertFalse(tool.isNewPath(rootUris.get(0)));
	}

	@Test
	public void testIsNewRoot() {
		assertFalse(tool.isNewRoot(null));
		assertFalse(tool.isNewRoot(rootUris.get(0)));
	}

	@Test
	public void testIsUsedPath() {
		assertFalse(tool.isUsedPath(null));
		assertFalse(tool.isUsedPath(rootUris.get(0)));
	}

	@Test
	public void testIsUsedRoot() {
		assertFalse(tool.isUsedRoot(null));
		assertFalse(tool.isUsedRoot(rootUris.get(0)));
	}

	@Test
	public void testIsGeneralPath() {
		assertFalse(tool.isGeneralPath(null));
		for(URI uri : badUris){
			assertFalse(tool.isGeneralPath(uri));
		}
		for(URI uri : pathUris){
			assertTrue(tool.isGeneralPath(uri));
		}
	}

	@Test
	public void testIsGeneralRoot() {
		assertFalse(tool.isGeneralRoot(null));
		for(URI uri : badUris){
			assertFalse(tool.isGeneralRoot(uri));
		}
		for(URI uri : rootUris){
			assertTrue(tool.isGeneralRoot(uri));
		}
	}

	@Test
	public void testGetCount() {
		assertEquals(tool.getCount(rootDoc), 35);
		assertEquals(tool.getCount(lastDoc), 35);
	}

	@Test
	public void testGetNextPageLink() {
		URI link = tool.getNextPageLink(rootDoc, rootUri);
		assertNotNull(link);
		assertEquals(link, nextPageUri);
		
		link = tool.getNextPageLink(lastDoc, lastUri);
		assertNull(link);
	}

	@Test
	public void testGetVehicles() {
		List<Vehicle> vehicles = new ArrayList<Vehicle>(tool.getVehicles(rootDoc));
		assertEquals(vehicles.size(), 30);
		Vehicle first = vehicles.get(0);
		assertEquals("SCBZK14C2VCX61147", first.getVin());
		assertEquals(21854.0, first.getMileage(), 0);
		assertEquals(54995.0, first.getOfferedPrice(), 0);
	}

	@Test
	public void testGetInvTypes() {
		Set<InventoryTool> invTypes = Auction123.getInvTypes();
		invTypes.contains(InvType.AUCTION_123);
	}

}
