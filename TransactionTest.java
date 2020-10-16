import static org.junit.Assert.*;

import org.junit.Test;

public class TransactionTest {

	@Test
	public void testFormatDeliveryAddress() {
		Transaction transaction1 = new Transaction();
		transaction1.setRecipientFirstName("Sarah");
		transaction1.setRecipientLastName("McDougal");
		transaction1.setRecipientAddress1("23 Tree Street");
		transaction1.setRecipientAddress2("Apt. #1");
		transaction1.setRecipientCity("Boulder");
		transaction1.setRecipientState("NV");
		transaction1.setRecipientZipcode("54921");
		
		Transaction transaction2 = new Transaction();
		transaction2.setRecipientFirstName("Betty");
		transaction2.setRecipientLastName("Lee");
		transaction2.setRecipientAddress1("5 N. 750 E.");
		transaction2.setRecipientCity("Somewhere");
		transaction2.setRecipientState("UT");
		transaction2.setRecipientZipcode("84200");
		
		assertEquals(transaction1.formatDeliveryAddress(), "Sarah McDougal\n23 Tree Street\nApt. #1\nBoulder, NV 54921");
		assertEquals(transaction2.formatDeliveryAddress(), "Betty Lee\n5 N. 750 E.\nSomewhere, UT 84200");
	}

}
