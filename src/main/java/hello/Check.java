package hello;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Author WangYong
 * @Date 2019/04/27
 * @Time 11:57
 */
@Slf4j
//@Component
public class Check implements InitializingBean {

    @Autowired
    JobExplorer jobExplorer;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    JobRegistry jobRegistry;

    @Override
    public void afterPropertiesSet() throws Exception {

        Executors.newSingleThreadExecutor().execute(()->{
            while (true) {
                Collection<String> jobNames = jobRegistry.getJobNames();
                log.info("jobNames:"+jobNames);

                for (String jobName : jobNames) {
                    try {
                        int jobInstanceCount = jobExplorer.getJobInstanceCount(jobName);
                        log.info("jobInstanceCount:" + jobInstanceCount);
                    } catch (NoSuchJobException e) {
                        e.printStackTrace();
                    }
                }

                JobParameters jobParameters1 = new JobParametersBuilder()
                        .addDouble("device",0.03).toJobParameters();
                JobParameters jobParameters2 = new JobParametersBuilder()
                        .addDouble("device",0.04).toJobParameters();
                JobExecution jobExecution1 = jobRepository.getLastJobExecution("importUserJob", jobParameters1);
                JobExecution jobExecution2 = jobRepository.getLastJobExecution("importUserJob", jobParameters2);
                log.info(jobExecution1.toString());
                log.info(jobExecution2.toString());


                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
