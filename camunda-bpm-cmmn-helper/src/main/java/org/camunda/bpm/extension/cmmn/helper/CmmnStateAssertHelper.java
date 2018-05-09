package org.camunda.bpm.extension.cmmn.helper;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.history.HistoricCaseActivityInstanceQuery;
import org.camunda.bpm.engine.impl.cmmn.execution.CaseExecutionState;
import org.camunda.bpm.engine.impl.persistence.entity.HistoricCaseActivityInstanceEntity;

import java.util.List;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class CmmnStateAssertHelper implements Supplier<List<CaseExecutionState>> {

  private final HistoryService historyService;
  private final String activityId;
  private final String caseInstanceId;


  @Override
  public List<CaseExecutionState> get() {
    HistoricCaseActivityInstanceQuery query = historyService.createHistoricCaseActivityInstanceQuery()
      .caseActivityId(activityId);

    if (caseInstanceId != null) {
      query.caseInstanceId(caseInstanceId);
    }

    List<Integer> list = query.list().stream()
      .map(h -> ((HistoricCaseActivityInstanceEntity) h).getCaseActivityInstanceState())
      .collect(toList());

    return list.stream().map(CaseExecutionState.CASE_EXECUTION_STATES::get).collect(toList());
  }
}
