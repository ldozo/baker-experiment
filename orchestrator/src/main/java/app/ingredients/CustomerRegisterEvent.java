package app.ingredients;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerRegisterEvent {
    private CustomerDTO customer;
}
