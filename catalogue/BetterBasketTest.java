package catalogue;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BetterBasketTest {

	@Test
void testMergeAddProduct() {
		
		BetterBasket b = new BetterBasket();
		
		Product p1 = new Product("0001", "Toaster", 10.00, 1);
		Product p2 = new Product("0001", "Toaster", 10.00, 1);
		Product p3 = new Product("0003", "Watch", 10.00, 1);
		
		
		
		b.add(p1);
		b.add(p2);
		assertEquals(1, b.size(), "size incorrect after merge");
		assertEquals(2, b.get(0).getQuantity(), "Quantity incorrect after merge");
		
		
		
		b.add(p3);
		assertEquals(2, b.size(), "size incorrect after non-merge");
		
		
		
		
		
	}
		
		
	}



