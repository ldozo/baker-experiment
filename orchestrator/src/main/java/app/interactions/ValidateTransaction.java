package app.interactions;

import java.math.BigDecimal;

import com.ing.baker.recipe.annotations.FiresEvent;
import com.ing.baker.recipe.annotations.RequiresIngredient;

import app.interactions.ValidateSourceAccount.SourceAccountFailed;

public interface ValidateTransaction {
    interface TransactionValidationResult {}
    record TransactionValidated() implements TransactionValidationResult {};
    record TransactionFailed(String reason) implements TransactionValidationResult {};

    @FiresEvent(oneOf = { TransactionValidated.class, SourceAccountFailed.class })
    TransactionValidationResult apply(@RequiresIngredient("amount") BigDecimal amount,
                                  @RequiresIngredient("currency") String currency);
}
