package app.interactions;

import com.ing.baker.recipe.annotations.FiresEvent;
import com.ing.baker.recipe.annotations.RequiresIngredient;
import com.ing.baker.recipe.javadsl.Interaction;

import app.ingredients.MoneyTransferDTO;

public interface DebitAccount extends Interaction {
    interface DebitAccountResult {}
    record DebitSucceed(String debitTransactionId) implements DebitAccountResult {}
    record DebitFailed(String reason) implements DebitAccountResult {}

    @FiresEvent(oneOf = { DebitSucceed.class, DebitFailed.class })
    DebitAccountResult apply(@RequiresIngredient("transfer") MoneyTransferDTO transfer);
}