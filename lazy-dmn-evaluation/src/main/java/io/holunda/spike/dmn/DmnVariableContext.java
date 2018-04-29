package io.holunda.spike.dmn;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.context.VariableContext;
import org.camunda.bpm.engine.variable.type.ValueType;
import org.camunda.bpm.engine.variable.value.TypedValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class DmnVariableContext implements VariableContext {

  private final Map<String, ValueType> keyTypes = new HashMap<>();

  private final DataService dataService;

  @Override
  public TypedValue resolve(final String key) {
    return new TypedValue() {
      @Override
      public Object getValue() {
        return dataService.apply(key);
      }

      @Override
      public ValueType getType() {
        return keyTypes.get(key);
      }
    };
  }

  @Override
  public boolean containsVariable(String key) {
    return keySet().contains(key);
  }

  @Override
  public Set<String> keySet() {
    return keyTypes.keySet();
  }


  public DmnVariableContext register(String key, ValueType type) {
    keyTypes.put(key, type);
    return this;
  }

  public VariableMap toVariables() {
    final VariableMap variableMap = Variables.createVariables();

    for (final String variable : keySet()) {
      // use of typed values to ensure lazy load of variables
      variableMap.putValueTyped(variable, resolve(variable));
    }
    return variableMap;
  }
}
