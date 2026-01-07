package app.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ing.baker.runtime.javadsl.EventInstance;
import app.components.BakerComponent;
import app.ingredients.MoneyTransferEvent;
import app.ingredients.MoneyTransferDTO;
import app.interactions.CreditAccount;
import app.interactions.DebitAccount;
import app.interactions.ValidateSourceAccount;
import app.interactions.ValidateSourceCustomer;
import app.interactions.ValidateTargetAccount;
import app.interactions.ValidateTargetCustomer;
import app.interactions.ValidateTransaction;
import app.recipes.MoneyTransferRecipe;

@RestController
public class TransferController {
    private BakerComponent _baker;
    private MoneyTransferRecipe _recipe;

    public TransferController(BakerComponent baker, MoneyTransferRecipe recipe) {
        _baker = baker;
        _recipe = recipe;
    }
        
    @PostMapping("/transfer")
    public ResponseEntity<Map<String, Object>> register(@RequestBody MoneyTransferDTO request) throws InterruptedException, ExecutionException {
        final var recipeInstanceId = UUID.randomUUID().toString();

        final var event = new MoneyTransferEvent(request);
        final var sensoryEvent = EventInstance.from(event);

        final Map<String, Object> responseBody = new HashMap<>(); 
        _baker.instance().bake(_recipe.ID(), recipeInstanceId).get();
        final var result = _baker.instance()
                                .fireEventAndResolveWhenCompleted(recipeInstanceId, sensoryEvent).join();

        responseBody.put("recipeInstanceId", recipeInstanceId);
        responseBody.put("eventList", result.getEventNames());

        final var state = _baker.instance()
                                .getRecipeInstanceState(recipeInstanceId)
                                .orTimeout(3, TimeUnit.SECONDS)
                                .join();
        responseBody.put("ingredients", state.getIngredients());
        responseBody.put("visual", _recipe.compiled().getRecipeVisualization());

        final var events = result.getEventNames();
        List<String> failures = List.of(
            ValidateSourceAccount.SourceAccountFailed.class.getSimpleName(),
            ValidateSourceCustomer.SourceCustomerFailed.class.getSimpleName(),
            ValidateTargetAccount.TargetAccountFailed.class.getSimpleName(),
            ValidateTargetCustomer.TargetCustomerFailed.class.getSimpleName(),
            ValidateTransaction.TransactionFailed.class.getSimpleName(),
            DebitAccount.DebitFailed.class.getSimpleName(),
            CreditAccount.CreditFailed.class.getSimpleName()
        );
        final boolean hasFailure =
            events.stream().anyMatch(failures::contains);

        if (hasFailure) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(responseBody);
        }
        return ResponseEntity.ok(responseBody);
    }
}
