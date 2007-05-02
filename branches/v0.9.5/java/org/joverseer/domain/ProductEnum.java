package org.joverseer.domain;


public enum ProductEnum {
    Leather("le"),
    Bronze("br"),
    Steel("st"),
    Mithril("mi"),
    Food("fo"),
    Mounts("mo"),
    Timber("ti"),
    Gold("go");
    
    String code;
    
    private ProductEnum(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
    
    public static ProductEnum getFromCode(String code) {
        for (ProductEnum p : ProductEnum.values()) {
            if (p.getCode().equals(code)) return p;
        }
        return null;
    }
}
