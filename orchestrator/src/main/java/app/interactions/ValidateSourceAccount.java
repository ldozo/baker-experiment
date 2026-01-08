package app.interactions;

import com.ing.baker.recipe.annotations.FiresEvent;
import com.ing.baker.recipe.annotations.RequiresIngredient;
import com.ing.baker.recipe.javadsl.Interaction;

import app.ingredients.MoneyTransferDTO;

public interface ValidateSourceAccount extends Interaction {
    record SourceAccountValidated(String sourceCustomerId) implements AccountValidationResult {};
    record SourceAccountFailed(String sourceAccountId, String sourceReason) implements AccountValidationResult {};

    @FiresEvent(oneOf = { SourceAccountValidated.class, SourceAccountFailed.class })
    AccountValidationResult apply(@RequiresIngredient("transfer") MoneyTransferDTO transfer);
}