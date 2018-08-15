package io.holunda.spike.camunda.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.annotate.JsonIgnore;
import org.camunda.bpm.engine.impl.jobexecutor.JobHandlerConfiguration;

/**
 * Base class for any job handler configuration which should be subclassed in order to get serialized.
 */
public abstract class JacksonJobHandlerConfiguration implements JobHandlerConfiguration {

    @JsonIgnore
    private final JacksonJobHandlerConfigurationSerializer<JacksonJobHandlerConfiguration> serializer;

    protected JacksonJobHandlerConfiguration(final ObjectMapper mapper) {
        this.serializer = new JacksonJobHandlerConfigurationSerializer<>(mapper, JacksonJobHandlerConfiguration.class);
    }

    public String toCanonicalString() {
        return serializer.toCanonicalString(this);
    }
}
