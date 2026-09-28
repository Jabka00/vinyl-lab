package dev.vinyllab.repository.projection;

import dev.vinyllab.model.RecordCondition;

public interface ConditionCount {

  RecordCondition getCondition();

  Long getTotal();
}
