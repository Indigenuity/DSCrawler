package datadefinitions.inventory;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public abstract class InventoryToolTest {
	
	protected InventoryTool tool;
	
	@Before
    public void setUp() {
        tool = makeTool();
    }
	
	protected abstract InventoryTool makeTool();

	public abstract void testIsNewPath();
	public abstract void testIsNewRoot();
	public abstract void testIsUsedPath();
	public abstract void testIsUsedRoot();
	public abstract void testIsGeneralPath();
	public abstract void testIsGeneralRoot();
	public abstract void testGetCount();
	public abstract void testGetNextPageLink();
	public abstract void testGetPaginationLinks();
	public abstract void testGetVehicles();


	
	public abstract void testGetInvTypes();

	@Test
	public void testDoubleString() {
		assertEquals(0, tool.doubleString(null), 0);
		assertEquals(0, tool.doubleString(""), 0);
		assertEquals(0, tool.doubleString("."), 0);
		assertEquals(500, tool.doubleString("500"), 0);
		assertEquals(500, tool.doubleString("500.0"), 0);
		assertEquals(526.54, tool.doubleString("526.54"), 0);
		assertEquals(500, tool.doubleString("$500"), 0);
		assertEquals(500, tool.doubleString("$500.0"), 0);
		assertEquals(1500, tool.doubleString("1500"), 0);
		assertEquals(1500, tool.doubleString("$1500"), 0);
		assertEquals(1500, tool.doubleString("1,500"), 0);
		assertEquals(1500, tool.doubleString("$1,500"), 0);
		assertEquals(1500.56, tool.doubleString("$1,500.56"), 0);
	}
}
