package io.holunda.spike.camunda.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.jobexecutor.JobHandler;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.JobEntity;

public abstract class JacksonJobHandlerAdapter<T extends JacksonJobHandlerConfiguration> implements JobHandler<T> {

    private final JacksonJobHandlerConfigurationSerializer<T> serializer;

    protected JacksonJobHandlerAdapter(final ObjectMapper mapper, final Class<T> clazz) {
        this.serializer = new JacksonJobHandlerConfigurationSerializer<>(mapper, clazz);
    }

    @Override
    public String getType() {
        return getClass().getName();
    }

    @Override
    public void execute(final T configuration, final ExecutionEntity execution, final CommandContext commandContext, final String tenantId) {
        // no implementation
    }

    @Override
    public T newConfiguration(final String canonicalString) {
        return this.serializer.fromCanonicalString(canonicalString);
    }

    @Override
    public void onDelete(final T configuration, final JobEntity jobEntity) {
        // empty implementation
    }
}
