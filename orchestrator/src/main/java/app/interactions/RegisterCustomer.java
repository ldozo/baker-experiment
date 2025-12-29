package app.interactions;

import com.ing.baker.recipe.annotations.FiresEvent;
import com.ing.baker.recipe.annotations.RequiresIngredient;
import com.ing.baker.recipe.javadsl.Interaction;

import app.ingredients.Customer;

public interface RegisterCustomer extends Interaction {
    interface RegisterResult {}
    record CustomerRegistered (String customerId, Customer customer) implements RegisterResult {}
    record CustomerRejected(String reason) implements RegisterResult {}

    @FiresEvent(oneOf = { CustomerRegistered.class, CustomerRejected.class })
    RegisterResult apply(@RequiresIngredient("customer") Customer customer);

}
