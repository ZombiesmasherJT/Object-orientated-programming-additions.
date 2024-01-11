package clients.cashier;

import catalogue.Basket;
import catalogue.Product;
import debug.DEBUG;
import middle.*;

import java.util.Observable;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Implements the Model of the cashier client
 * @author  Mike Smith University of Brighton
 * @version 1.0
 */
public class CashierModel extends Observable
{
  private enum State { process, checked }

  private State       theState   = State.process;   // Current state
  private Product     theProduct = null;            // Current product
  private Basket      theBasket  = null;    // Bought items
  private Product     Previous   = null;
  
  
  private String      pn = "";                      // Product being processed

  private StockReadWriter theStock     = null;
  private OrderProcessing theOrder     = null;

  /**
   * Construct the model of the Cashier
   * @param mf The factory to create the connection objects
   */

  public CashierModel(MiddleFactory mf)
  {
    try                                           // 
    {      
      theStock = mf.makeStockReadWriter();        // Database access
      theOrder = mf.makeOrderProcessing();        // Process order
    } catch ( Exception e )
    {
      DEBUG.error("CashierModel.constructor\n%s", e.getMessage() );
    }
    theState   = State.process;                  // Current state
  }
  
  /**
   * Get the Basket of products
   */
  public Basket getBasket()
  {
    return theBasket;
  }

  /**
   * Check if the product is in Stock
   * @param productNum The product number
   */
  public void doCheck(String productNum )
  {
    String theAction = "";
    theState  = State.process;                  // State process
    pn  = productNum.trim();                    // Product no.
    int    amount  = 1;                         //  & quantity
    try
    {
      if ( theStock.exists( pn ) )              // Stock Exists?
      {                                         // T
        Product pr = theStock.getDetails(pn);   //  Get details
        if ( pr.getQuantity() >= amount )       //  In stock?
        {                                       //  T
          theAction =                           //   Display 
            String.format( "%s : %7.2f (%2d) ", //
              pr.getDescription(),              //    description
              pr.getPrice(),                    //    price
              pr.getQuantity() );               //    quantity     
          theProduct = pr;                      //   Remember prod.
          theProduct.setQuantity( amount );     //    & quantity
          theState = State.checked;             //   OK await BUY 
        } else {                                //  F
          theAction =                           //   Not in Stock
            pr.getDescription() +" not in stock";
        }
      } else {                                  // F Stock exists
        theAction =                             //  Unknown
          "Unknown product number " + pn;       //  product no.
      }
    } catch( StockException e )
    {
      DEBUG.error( "%s\n%s", 
            "CashierModel.doCheck", e.getMessage() );
      theAction = e.getMessage();
    }
    setChanged(); notifyObservers(theAction);
  }

  
  
  
  
  
  
  
  
  /**
	 * Delete the last item in the basket
	 * @throws StockException 
	 */
	
  public void doDelete() throws StockException {
	    if (Previous != null) {
	        deletePreviousProduct();
	    } else {
	        deleteByUserInput();
	    }
	}

	private void deletePreviousProduct() throws StockException {
	    for (Product product : theBasket) {
	        if (product.getProductNum().equals(Previous.getProductNum())) {
	            decreaseProductQuantity(product);
	            resetPreviousProduct();
	            notifyDeletionOfProduct();
	            return;
	        }
	    }
	}

	private void decreaseProductQuantity(Product product) throws StockException {
	    product.setQuantity(Math.max(0, product.getQuantity() - 1));
	    theStock.addStock(product.getProductNum(), 1);
	}

	private void resetPreviousProduct() {
	    Previous = null;
	}

	private void notifyDeletionOfProduct() {
	    setChanged();
	    notifyObservers("Deleted");
	}

	private void deleteByUserInput() {
	    String userInput = getUserInput();
	    if (userInput != null && !userInput.isEmpty()) {
	        boolean found = deleteProductByInput(userInput);
	        if (!found) {
	            displayNotFoundMessageToUser();
	        }
	    }
	}

	private String getUserInput() {
	    JPanel panel = new JPanel();
	    panel.add(new JLabel("Enter product number to delete:"));
	    JTextField delete = new JTextField(6);
	    panel.add(delete);

	    Object[] options = {"Submit", "Cancel"};

	    int reply = JOptionPane.showOptionDialog(
	        null, panel, "Delete", JOptionPane.DEFAULT_OPTION, 
	        JOptionPane.PLAIN_MESSAGE, null, options, null
	    );

	    return (reply == 0) ? delete.getText() : null;
	}

	private boolean deleteProductByInput(String input) {
	    for (Product product : theBasket) {
	        if (product.getProductNum().equals(input)) {
	            try {
					decreaseProductQuantity(product);
				} catch (StockException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            notifyDeletionIfFound();
	            return true;
	        }
	    }
	    return false;
	}

	private void notifyDeletionIfFound() {
	    setChanged();
	    notifyObservers("Deleted");
	}

	private void displayNotFoundMessageToUser() {
	    JOptionPane.showMessageDialog(null, "Product was not found in the basket");
	}
  
  
  
  /**
   * Buy the product
   */
  
  
  
  
  
  
  
  
  
  
  
  
  
  public void doBuy()
  {
    String theAction = "";
    int    amount  = 1;                         //  & quantity
    try
    {
      if ( theState != State.checked )          // Not checked
      {                                         //  with customer
        theAction = "Check if OK with customer first";
      } else {
        boolean stockBought =                   // Buy
          theStock.buyStock(                    //  however
            theProduct.getProductNum(),         //  may fail              
            theProduct.getQuantity() );         //
        if ( stockBought )                      // Stock bought
        {                                       // T
          makeBasketIfReq();                    //  new Basket ?
          theBasket.add( theProduct );          //  Add to bought
          theAction = "Purchased " +            //    details
                  theProduct.getDescription();  //
        } else {                                // F
          theAction = "!!! Not in stock";       //  Now no stock
        }
      }
    } catch( StockException e )
    {
      DEBUG.error( "%s\n%s", 
            "CashierModel.doBuy", e.getMessage() );
      theAction = e.getMessage();
    }
    theState = State.process;                   // All Done
    setChanged(); notifyObservers(theAction);
  }
  
  /**
   * Customer pays for the contents of the basket
   */
  public void doBought()
  {
    String theAction = "";
    int    amount  = 1;                       //  & quantity
    try
    {
      if ( theBasket != null &&
           theBasket.size() >= 1 )            // items > 1
      {                                       // T
        theOrder.newOrder( theBasket );       //  Process order
        theBasket = null;                     //  reset
      }                                       //
      theAction = "Next customer";            // New Customer
      theState = State.process;               // All Done
      theBasket = null;
    } catch( OrderException e )
    {
      DEBUG.error( "%s\n%s", 
            "CashierModel.doCancel", e.getMessage() );
      theAction = e.getMessage();
    }
    theBasket = null;
    setChanged(); notifyObservers(theAction); // Notify
  }

  /**
   * ask for update of view callled at start of day
   * or after system reset
   */
  public void askForUpdate()
  {
    setChanged(); notifyObservers("Welcome");
  }
  
  /**
   * make a Basket when required
   */
  private void makeBasketIfReq()
  {
    if ( theBasket == null )
    {
      try
      {
        int uon   = theOrder.uniqueNumber();     // Unique order num.
        theBasket = makeBasket();                //  basket list
        theBasket.setOrderNum( uon );            // Add an order number
      } catch ( OrderException e )
      {
        DEBUG.error( "Comms failure\n" +
                     "CashierModel.makeBasket()\n%s", e.getMessage() );
      }
    }
  }

  /**
   * return an instance of a new Basket
   * @return an instance of a new Basket
   */
  protected Basket makeBasket()
  {
    return new Basket();
  }
}
  
