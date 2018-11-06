package pl.zlomek.warsztat.util;

public class Validator {
    public static boolean validateNamesWithUnicode(String name){
        return name != null && name.matches("[A-ZŹĄĘÓŁŻ]{1}+[a-z,ąęółńćźż]{2,}");
    }
    public static boolean validateNames(String name){
        return name != null && name.matches("[A-Z]{1}+[a-z]{2,}");
    }
    public static boolean validateNip(String NIP){
        return NIP != null && NIP.matches("[0-9]{2}+-+[0-9]{3}");
    }
    public static boolean validateEmail(String email){
        return email!=null && email.matches("[A-Za-z0-9._-]{1,}+@+[a-z]{1,6}+.+[a-z]{2,3}");
    }
    public static boolean validateZipCode(String zipCode){
        return zipCode != null && zipCode.matches("[0-9]{2}+-+[0-9]{3}");
    }

    public static boolean validateRegistrationNumber(String registrationNumber){
        return registrationNumber != null && registrationNumber.matches("[A-Z]{1,3}\\s[0-9 A-Z]{4,5}");
    }

    public static boolean validateVin(String vin){
        return vin != null && vin.matches("[0-9]{17}");
    }
    public static boolean validatePassword(String password){
        return password != null && password.matches("[A-Za-z0-9ĄŻŹÓŁĘążźćńłóę!@#%*^]{6,20}");
    }

}
