package app.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ing.baker.runtime.javadsl.EventInstance;
import app.components.BakerComponent;
import app.ingredients.Customer;
import app.ingredients.RegisterCustomerRequested;
import app.interactions.OpenAccount;
import app.interactions.RegisterCustomer;
import app.interactions.SendRegisterEmail;
import app.recipes.RegisterCustomerRecipe;

@RestController
public class OnboardingController {
    private BakerComponent _baker;
    private RegisterCustomerRecipe _recipe;

    public OnboardingController(BakerComponent baker, RegisterCustomerRecipe recipe) {
        _baker = baker;
        _recipe = recipe;
    }

     
@PostMapping("/register")
public ResponseEntity<Map<String, Object>> register(@RequestBody Customer request) {
    // Idempotency için domain tabanlı instanceId önerilir:
    // final var recipeInstanceId = "cust-" + request.getCustomerId();
    final var recipeInstanceId = UUID.randomUUID().toString();

    final var event = new RegisterCustomerRequested(request);
    final var sensoryEvent = EventInstance.from(event);

    final Map<String, Object> body = new HashMap<>();
    try { 
        _baker.instance().bake(_recipe.ID(), recipeInstanceId).get();
        final var result = _baker.instance()
                                 .fireEventAndResolveWhenCompleted(recipeInstanceId, sensoryEvent).join();

        body.put("recipeInstanceId", recipeInstanceId);
        body.put("eventList", result.getEventNames());

        final var state = _baker.instance()
                                .getRecipeInstanceState(recipeInstanceId)
                                .orTimeout(3, TimeUnit.SECONDS)
                                .join();
        body.put("ingredients", state.getIngredients());
        body.put("visual", _recipe.compiled().getRecipeVisualization());

        final var events = result.getEventNames();
        final boolean hasFailure =
            events.contains(RegisterCustomer.CustomerRejected.class.getSimpleName()) ||
            events.contains(OpenAccount.AccountFailed.class.getSimpleName()) ||
            events.contains(SendRegisterEmail.EmailFailed.class.getSimpleName());

        if (hasFailure) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(body);
        }
        return ResponseEntity.ok(body);

    } catch (Exception e) { 
        body.put("error", "unexpected_error");
        body.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}


}
