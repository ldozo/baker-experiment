package app.interactions;

import com.ing.baker.recipe.annotations.FiresEvent;
import com.ing.baker.recipe.annotations.RequiresIngredient;
import com.ing.baker.recipe.javadsl.Interaction;

public interface ValidateTargetCustomer extends Interaction {

    record TargetCustomerValidated() implements CustomerValidationResult {};

    record TargetCustomerFailed(String targetCustomerFailReason) implements CustomerValidationResult {};

    @FiresEvent(oneOf = { TargetCustomerValidated.class, TargetCustomerFailed.class })
    CustomerValidationResult apply(@RequiresIngredient("targetCustomerId") String accountId);
}
