package app.interactions;

import java.math.BigDecimal;

import com.ing.baker.recipe.annotations.FiresEvent;
import com.ing.baker.recipe.annotations.RequiresIngredient;

public interface ValidateTargetAccount {
    interface AccountValidationResult {}
    record TargetAccountValidated(String targetCustomerId) implements AccountValidationResult {};
    record TargetAccountFailed(String reason) implements AccountValidationResult {};

    @FiresEvent(oneOf = { TargetAccountValidated.class, TargetAccountFailed.class })
    AccountValidationResult apply(@RequiresIngredient("targetAccountId") String accountId,
                                  @RequiresIngredient("amount") BigDecimal amount,
                                  @RequiresIngredient("currency") String currency);
}
