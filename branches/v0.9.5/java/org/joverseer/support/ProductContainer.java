package org.joverseer.support;

import java.io.Serializable;
import java.util.HashMap;

import org.joverseer.domain.ProductEnum;


public class ProductContainer implements Serializable {
    HashMap<ProductEnum, Integer> products = new HashMap<ProductEnum, Integer>();
    
    public ProductContainer() {
        for (ProductEnum p : ProductEnum.values()) {
            products.put(p, null);
        }
    }
    
    public void setProduct(ProductEnum pe, Integer amount) {
        products.put(pe, amount);
    }
    
    public Integer getProduct(ProductEnum pe) {
        Integer amount = products.get(pe);
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
