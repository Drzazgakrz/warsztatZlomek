package pl.zlomek.warsztat.model;

@lombok.Getter
@lombok.Setter

public class AddCarPartsForm extends AccessTokenForm{
    protected String name;
    protected int tax;
    protected String producer;
}
