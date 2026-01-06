package app.components;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ing.baker.runtime.inmemory.InMemoryBaker;
import com.ing.baker.runtime.javadsl.Baker;

import app.interactions.impl.OpenAccountImpl;
import app.interactions.impl.RegisterCustomerImpl;
import app.interactions.impl.SendRegisterEmailImpl;

@Component
public class BakerComponent {
    private final Baker _baker;

    public BakerComponent (RegisterCustomerImpl rci,
                            OpenAccountImpl oai,
                            SendRegisterEmailImpl srei
    ){
        _baker = InMemoryBaker.java(
            List.of(rci, oai, srei)
        );
    }

    public Baker instance() {
        return _baker;
    }
}
