package org.camunda.bpm.extension.cmmn.helper;

import org.camunda.bpm.engine.delegate.DelegateCaseExecution;

import java.util.function.BiPredicate;

public interface EntryCriterionPredicate extends BiPredicate<DelegateCaseExecution, String> {
}
