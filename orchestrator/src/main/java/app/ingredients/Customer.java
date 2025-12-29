package app.ingredients;

import lombok.Data;

@Data
public class Customer {
    private String firstname, lastname, nationality, email;
    private int age;
}
