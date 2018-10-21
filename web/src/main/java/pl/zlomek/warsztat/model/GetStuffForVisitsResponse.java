package pl.zlomek.warsztat.model;

import lombok.Getter;

import javax.ws.rs.GET;

@Getter
public class GetStuffForVisitsResponse {
    private CarPartModel[] parts;
    private ServiceModel[] services;

    public GetStuffForVisitsResponse(CarPartModel[] parts, ServiceModel[] services) {
        this.parts = parts;
        this.services = services;
    }
}
