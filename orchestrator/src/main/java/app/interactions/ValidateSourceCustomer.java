package app.interactions;

import com.ing.baker.recipe.annotations.FiresEvent;
import com.ing.baker.recipe.annotations.RequiresIngredient;
import com.ing.baker.recipe.javadsl.Interaction;

public interface ValidateSourceCustomer extends Interaction {
    interface CustomerValidationResult {}

    record SourceCustomerValidated() implements CustomerValidationResult {};

    record SourceCustomerFailed(String reason) implements CustomerValidationResult {};

    @FiresEvent(oneOf = { SourceCustomerValidated.class, SourceCustomerFailed.class })
    CustomerValidationResult apply(@RequiresIngredient("sourceCustomerId") String accountId);
}
