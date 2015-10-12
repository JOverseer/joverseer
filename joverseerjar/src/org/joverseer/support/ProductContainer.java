package org.joverseer.support;

import java.io.Serializable;
import java.util.HashMap;

import org.joverseer.domain.ProductEnum;

/**
 * Utility class that stores product-value pairs.
 * 
 * It also provides the ability to add product containers together
 * 
 * Useful for storing pop center production (and not only that)
 * 
 * @author Marios Skounakis
 */
public class ProductContainer implements Serializable {
    HashMap<ProductEnum, Integer> products = new HashMap<ProductEnum, Integer>();
    
    public ProductContainer() {
        for (ProductEnum p : ProductEnum.values()) {
            this.products.put(p, null);
        }
    }
    
    public void setProduct(ProductEnum pe, Integer amount) {
        this.products.put(pe, amount);
    }
    
    public Integer getProduct(ProductEnum pe) {
        Integer amount = this.products.get(pe);
        return amount;
    }
    
    public void add(ProductContainer pc) {
        for (ProductEnum p : ProductEnum.values()) {
            Integer amount = getProduct(p);
            if (amount == null) {
                amount = pc.getProduct(p);
            } else if (pc.getProduct(p) == null) {
                // do nothing
            } else {
                amount += pc.getProduct(p);
            }
            setProduct(p, amount);
        }
    }
    
    public boolean hasData() {
        for (ProductEnum p : ProductEnum.values()) {
            Integer amount = getProduct(p);
            if (amount != null && amount > 0) {
                return true;
            }
        }
        return false;
    }
}
