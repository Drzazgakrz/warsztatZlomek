package pl.zlomek.warsztat.model;

@lombok.Getter
@lombok.Setter

public class AddCarPartsForm {
    private String name;
    private String accessToken;
    private int tax;
    private String producer;
}
