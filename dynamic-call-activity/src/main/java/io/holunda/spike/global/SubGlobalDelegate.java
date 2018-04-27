package io.holunda.spike.global;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SubGlobalDelegate implements JavaDelegate{

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    log.info("executing subProcess[global] - {}", execution.getProcessInstanceId());
  }
}
