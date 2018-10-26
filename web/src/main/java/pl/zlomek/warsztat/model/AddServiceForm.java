package pl.zlomek.warsztat.model;


import lombok.Getter;

@Getter
public class AddServiceForm extends AccessTokenForm{
    protected String serviceName;
    protected int tax;
}
