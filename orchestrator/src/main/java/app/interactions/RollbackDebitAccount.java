package app.interactions;

import com.ing.baker.recipe.annotations.FiresEvent;
import com.ing.baker.recipe.annotations.RequiresIngredient;
import com.ing.baker.recipe.javadsl.Interaction;

public interface RollbackDebitAccount extends Interaction {
    interface DebitRollbackResult {}
    record DebitRollbackSucceed() implements DebitRollbackResult {}
    record DebitRollbackFailed(String debitFailReason) implements DebitRollbackResult {}

    @FiresEvent(oneOf = { DebitRollbackSucceed.class, DebitRollbackFailed.class })
    DebitRollbackResult apply(@RequiresIngredient("debitTransactionId") String transactionId);
}