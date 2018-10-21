package pl.zlomek.warsztat.model;

@lombok.Getter
@lombok.Setter

public class AddCarPartsForm {
    protected String name;
    protected String accessToken;
    protected int tax;
    protected String producer;
}
