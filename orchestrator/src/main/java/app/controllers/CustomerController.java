package app.controllers;

import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.ing.baker.runtime.javadsl.Baker;
import com.ing.baker.runtime.javadsl.EventInstance;

import app.ingredients.RegisterCustomerRequested;
import app.recipes.RegisterCustomerRecipe;

@RestController
public class CustomerController {
    @Autowired Baker _baker;
    @Autowired RegisterCustomerRecipe _recipe;

    public Map<String, Object> register(RegisterCustomerRequested request) {
        var recipeInstanceId = UUID.randomUUID().toString();
        var sensoryEvent = EventInstance.from(request);
        _baker.bake(_recipe.ID(), recipeInstanceId);
        _baker.fireEvent(recipeInstanceId, sensoryEvent);
        return null;
    }

}
