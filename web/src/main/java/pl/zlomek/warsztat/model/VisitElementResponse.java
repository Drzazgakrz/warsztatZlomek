package pl.zlomek.warsztat.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
public class VisitElementResponse implements Serializable {
    private String name;
    private String price;
    private int count;

    public VisitElementResponse(String name, String price, int count) {
        this.name = name;
        this.price = price;
        this.count = count;
    }
}
