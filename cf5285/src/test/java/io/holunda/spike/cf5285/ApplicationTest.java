package io.holunda.spike.cf5285;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.batch.history.HistoricBatch;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static io.holunda.spike.cf5285.Application.JOB_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * use a spring boot test that will run the application "as is" without any inmemory/mock/... stuff. So job execution will work.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

  @Autowired
  private HistoryService historyService;

  @Test
  public void no_failed_jobs_after_batch_run() throws Exception {
    // we know that the batches will be created (see Application#onStart(), so we just have to wait.

    // awaitility used for async testing. every 100ms the tool checks if all assertions are true, if not
    // reached after 10s, the test fails.
    await()
      .untilAsserted(() -> {
        final HistoricBatch historicBatch = historyService.createHistoricBatchQuery().singleResult();
        assertThat(historicBatch).isNotNull();
        assertThat(historicBatch.getType()).isEqualTo(JOB_TYPE);
        // all three jobs ran
        assertThat(historicBatch.getTotalJobs()).isEqualTo(3);
        // the batch is finished
        // FIXME: this does not happen, the runtimeException thrown on fail prevents this batch from closing, although it should not handle the incident.
        assertThat(historicBatch.getEndTime()).isNotNull();
      });
  }
}
