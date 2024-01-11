package catalogue;

import java.io.Serializable;
import java.util.Collections;

/**
 * Write a description of class BetterBasket here.
 * 
 * @author  Your Name 
 * @version 1.0
 */
public class BetterBasket extends Basket implements Serializable
{
  private static final long serialVersionUID = 1L;
  

  
  
  @Override
  public boolean add(Product pr) {
      for (Product a : this) {
          if (a.getProductNum().equals(pr.getProductNum())){
              a.setQuantity(a.getQuantity()+1);
              return true;
          }
      }

      basketSort(pr, this);
          return true;

  }

  private void basketSort(Product pr, Basket bskt) {
      int aResult;
      int prResult = Integer.parseInt(pr.getProductNum());
      int aIndex;
      boolean found = false;

      for (Product a : bskt) {
          aResult = Integer.parseInt(a.getProductNum());
          if(prResult < aResult) {
              aIndex = bskt.indexOf(a);
              super.add(aIndex, pr);
              found = true;
              break;
          }
      }
      if(!found) {
          super.add(pr);
      }
  }
}
  
  
  
  
  
  
  
  
  

