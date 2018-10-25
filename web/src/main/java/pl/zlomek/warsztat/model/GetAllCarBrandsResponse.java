package pl.zlomek.warsztat.model;

import lombok.Getter;

@Getter
public class GetAllCarBrandsResponse {
    private String [] brandNames;

    public GetAllCarBrandsResponse( String[] brandNames) {
        this.brandNames = brandNames;
    }
}
