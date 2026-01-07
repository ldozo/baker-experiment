package app.interactions;

import com.ing.baker.recipe.annotations.FiresEvent;
import com.ing.baker.recipe.annotations.RequiresIngredient;
import com.ing.baker.recipe.javadsl.Interaction;

import app.ingredients.MoneyTransferDTO;
import app.interactions.ValidateSourceAccount.SourceAccountFailed;

public interface ValidateTransaction extends Interaction {
    interface TransactionValidationResult {}
    record TransactionValidated() implements TransactionValidationResult {};
    record TransactionFailed(String reason) implements TransactionValidationResult {};

    @FiresEvent(oneOf = { TransactionValidated.class, SourceAccountFailed.class })
    TransactionValidationResult apply(@RequiresIngredient("transfer") MoneyTransferDTO transfer);
}
