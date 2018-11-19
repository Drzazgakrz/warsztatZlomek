package pl.zlomek.warsztat.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceModel {
    private String serviceName;
    private String price;
    private int count;
    private long id;
    private int tax;
    public ServiceModel(Service service) {
        this.serviceName = service.getName();
        this.id = service.getId();
        this.tax = service.getTax();
    }
}
