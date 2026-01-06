package app.interactions;

import java.math.BigDecimal;

import com.ing.baker.recipe.annotations.FiresEvent;
import com.ing.baker.recipe.annotations.RequiresIngredient;
import com.ing.baker.recipe.javadsl.Interaction;

public interface ValidateSourceAccount extends Interaction {
    interface AccountValidationResult {}
    record SourceAccountValidated(String sourceCustomerId) implements AccountValidationResult {};
    record SourceAccountFailed(String reason) implements AccountValidationResult {};

    @FiresEvent(oneOf = { SourceAccountValidated.class, SourceAccountFailed.class })
    AccountValidationResult apply(@RequiresIngredient("sourceAccountId") String accountId,
                                  @RequiresIngredient("amount") BigDecimal amount,
                                  @RequiresIngredient("currency") String currency);
}