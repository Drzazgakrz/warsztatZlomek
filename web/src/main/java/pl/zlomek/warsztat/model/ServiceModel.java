package pl.zlomek.warsztat.model;

import lombok.Getter;

@Getter
public class ServiceModel {
    private String serviceName;
    private String price;
    private int count;

    public ServiceModel(String serviceName) {
        this.serviceName = serviceName;
    }
}
