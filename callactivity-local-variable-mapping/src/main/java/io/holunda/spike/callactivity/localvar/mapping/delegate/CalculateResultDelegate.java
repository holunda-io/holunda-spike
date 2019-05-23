package io.holunda.spike.callactivity.localvar.mapping.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("calculateResult")
public class CalculateResultDelegate implements JavaDelegate {

    public void execute(DelegateExecution execution) {
        Integer i = (Integer) execution.getVariable("element");
        execution.setVariable("result", i * i);
    }
}
