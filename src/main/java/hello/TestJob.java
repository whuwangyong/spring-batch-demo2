package hello;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;

/**
 * @Author WangYong
 * @Date 2019/04/26
 * @Time 17:45
 */
@Component
public class TestJob implements InitializingBean {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobRegistry jobRegistry;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private Step step1;

    @Autowired
    private JobCompletionNotificationListener listener;

    @Override
    public void afterPropertiesSet() throws Exception {
        Job job = jobBuilderFactory.get("importUserJob")
//            .incrementer(new RunIdIncrementer())
            .listener(listener)
            .flow(step1)
            .end()
            .build();


        JobParameters jobParameters1 = new JobParametersBuilder()
                .addDouble("device",0.07).toJobParameters();

        JobParameters jobParameters2 = new JobParametersBuilder()
                .addDouble("device",0.08).toJobParameters();


        Executors.newSingleThreadExecutor().execute(()-> {
            try {
                jobLauncher.run(job,jobParameters1);
            } catch (JobExecutionAlreadyRunningException e) {
                e.printStackTrace();
            } catch (JobRestartException e) {
                e.printStackTrace();
            } catch (JobInstanceAlreadyCompleteException e) {
                e.printStackTrace();
            } catch (JobParametersInvalidException e) {
                e.printStackTrace();
            }
        });

        Executors.newSingleThreadExecutor().execute(()-> {
            try {
                jobLauncher.run(job,jobParameters2);
            } catch (JobExecutionAlreadyRunningException e) {
                e.printStackTrace();
            } catch (JobRestartException e) {
                e.printStackTrace();
            } catch (JobInstanceAlreadyCompleteException e) {
                e.printStackTrace();
            } catch (JobParametersInvalidException e) {
                e.printStackTrace();
            }
        });

    }




}
