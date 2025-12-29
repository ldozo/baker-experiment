package app.controllers;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ing.baker.runtime.akka.actor.protobuf.SensoryEventResult;
import com.ing.baker.runtime.javadsl.EventInstance;
import com.ing.baker.types.Value;

import app.components.BakerComponent;
import app.ingredients.Customer;
import app.ingredients.RegisterCustomerRequested;
import app.recipes.RegisterCustomerRecipe;

@RestController
public class CustomerController {
    private BakerComponent _baker;
    private RegisterCustomerRecipe _recipe;

    public CustomerController(BakerComponent baker, RegisterCustomerRecipe recipe) {
        _baker = baker;
        _recipe = recipe;
    }

    @PostMapping("/register")
    public Map<String, Value> register(@RequestBody Customer request) {
        var recipeInstanceId = UUID.randomUUID().toString();
        RegisterCustomerRequested event = new RegisterCustomerRequested(request);
        var sensoryEvent = EventInstance.from(event);
        try {
            _baker.instance().bake(_recipe.ID(), recipeInstanceId).get();
            var result = _baker.instance().fireEvent(recipeInstanceId, sensoryEvent).getResolveWhenCompleted().get();
        
            return result.getIngredients();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println(e);
        }
    
        return null;
    }

}
