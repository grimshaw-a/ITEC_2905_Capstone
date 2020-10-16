import static org.junit.Assert.*;

import org.junit.Test;

public class CustomerTest {

	@Test
	public void testFormatMailingAddress() {
		Customer customer1 = new Customer();
		customer1.setFirstName("Sherry");
		customer1.setLastName("Higgins");
		customer1.setAddressFirstLine("123 Alphabet Street");
		customer1.setAddressSecondLine("Suite 3C");
		customer1.setCity("Generic Town");
		customer1.setState("SC");
		customer1.setZipcode("09573");
		
		Customer customer2 = new Customer();
		customer2.setFirstName("David");
		customer2.setLastName("Anderson");
		customer2.setAddressFirstLine("747 Flyer Street");
		customer2.setCity("Another City");
		customer2.setState("NC");
		customer2.setZipcode("10382");
		
		assertEquals(customer1.formatMailingAddress(), "Sherry Higgins\n123 Alphabet Street\nSuite 3C\nGeneric Town, SC 09573");
		assertEquals(customer2.formatMailingAddress(), "David Anderson\n747 Flyer Street\nAnother City, NC 10382");
	}

}
