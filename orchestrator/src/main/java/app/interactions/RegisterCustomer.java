package app.interactions;

import com.ing.baker.recipe.annotations.FiresEvent;
import com.ing.baker.recipe.annotations.RequiresIngredient;
import com.ing.baker.recipe.javadsl.Interaction;

import app.ingredients.RegisterCustomerRequested;

public interface RegisterCustomer extends Interaction {
    interface Outcome {}
    record CustomerRegistered (String customerId) implements Outcome {}
    record CustomerRejected(String reason) implements Outcome {}

    @FiresEvent(oneOf = { CustomerRegistered.class, CustomerRejected.class })
    Outcome apply(@RequiresIngredient("customerRequest") RegisterCustomerRequested customer);

}
