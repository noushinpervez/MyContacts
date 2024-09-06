package edu.ewubd.mycontacts;

public class Contact {

    String id = "";
    String name = "";
    String email = "";
    String homePhone = "";
    String officePhone = "";
    String image = "";

    public Contact(String id, String name, String email, String homePhone, String officePhone, String image) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.homePhone = homePhone;
        this.officePhone = officePhone;
        this.image = image;
    }

    public String getId() {
        return id;
    }
}