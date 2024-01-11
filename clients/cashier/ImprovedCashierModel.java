package clients.cashier;

import catalogue.Basket;
import catalogue.BetterBasket;
import middle.MiddleFactory;



public class ImprovedCashierModel extends CashierModel {



	    public ImprovedCashierModel(MiddleFactory mf) {
	        super(mf);

	    }

	    @Override
	    protected Basket makeBasket()
	    {

	    return new BetterBasket();
	}

	}
	
