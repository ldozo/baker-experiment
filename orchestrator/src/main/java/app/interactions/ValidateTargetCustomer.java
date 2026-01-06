package app.interactions;

import com.ing.baker.recipe.annotations.FiresEvent;
import com.ing.baker.recipe.annotations.RequiresIngredient;

public interface ValidateTargetCustomer {
    interface CustomerValidationResult {}

    record SourceCustomerValidated() implements CustomerValidationResult {};

    record SourceCustomerFailed(String reason) implements CustomerValidationResult {};

    @FiresEvent(oneOf = { SourceCustomerValidated.class, SourceCustomerFailed.class })
    CustomerValidationResult apply(@RequiresIngredient("targetCustomerId") String accountId);
}
