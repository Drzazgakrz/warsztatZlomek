package pl.zlomek.warsztat.model;


import lombok.Getter;

@Getter
public class AddServiceForm extends AccessTokenForm{
    protected String name;
    protected int tax;

    public boolean validate(){
        return !name.equals("") && tax >= 0;
    }
}
