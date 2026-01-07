package app.ingredients;

import lombok.Data;

@Data
public class CustomerDTO {
    private String firstname, lastname, nationality, email;
    private int age;
}
