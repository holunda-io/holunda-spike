package io.holunda.spike.callactivity.localvar.mapping.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component("determineElements")
public class DetermineElementsDelegate implements JavaDelegate {

    public void execute(DelegateExecution execution) {
        execution.setVariable("elements", Arrays.asList(2,3));
    }
}
