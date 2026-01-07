package app.interactions;

import com.ing.baker.recipe.annotations.FiresEvent;
import com.ing.baker.recipe.annotations.RequiresIngredient;
import com.ing.baker.recipe.javadsl.Interaction;

import app.ingredients.MoneyTransferDTO;

public interface CreditAccount extends Interaction {
    interface CreditAccountResult {}
    record CreditSucceed(String creditTransactionId) implements CreditAccountResult {}
    record CreditFailed(String reason) implements CreditAccountResult {}

    @FiresEvent(oneOf = { CreditSucceed.class, CreditFailed.class })
    CreditAccountResult apply(@RequiresIngredient("transfer") MoneyTransferDTO transfer);
}
