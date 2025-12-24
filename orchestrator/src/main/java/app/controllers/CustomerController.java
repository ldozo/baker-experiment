package app.controllers;

import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.ing.baker.runtime.javadsl.EventInstance;

import app.components.BakerComponent;
import app.ingredients.Customer;
import app.recipes.RegisterCustomerRecipe;

@RestController
public class CustomerController {
    private BakerComponent _baker;
    private RegisterCustomerRecipe _recipe;

    public CustomerController(BakerComponent baker, RegisterCustomerRecipe recipe) {
        _baker = baker;
        _recipe = recipe;
    }

    public Map<String, Object> register(Customer request) {
        var recipeInstanceId = UUID.randomUUID().toString();
        var sensoryEvent = EventInstance.from(request);
        _baker.instance().bake(_recipe.ID(), recipeInstanceId);
        _baker.instance().fireEvent(recipeInstanceId, sensoryEvent);
        return null;
    }

}
