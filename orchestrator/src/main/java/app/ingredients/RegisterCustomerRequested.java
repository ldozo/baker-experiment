package app.ingredients;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterCustomerRequested {
    private Customer customer;
}
