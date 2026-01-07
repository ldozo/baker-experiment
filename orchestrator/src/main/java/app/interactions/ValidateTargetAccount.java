package app.interactions;
 
import com.ing.baker.recipe.annotations.FiresEvent;
import com.ing.baker.recipe.annotations.RequiresIngredient;
import com.ing.baker.recipe.javadsl.Interaction;

import app.ingredients.MoneyTransferDTO;

public interface ValidateTargetAccount extends Interaction {
    record TargetAccountValidated(String targetCustomerId) implements AccountValidationResult {};
    record TargetAccountFailed(String reason) implements AccountValidationResult {};

    @FiresEvent(oneOf = { TargetAccountValidated.class, TargetAccountFailed.class })
    AccountValidationResult apply(@RequiresIngredient("transfer") MoneyTransferDTO transfer);
}
