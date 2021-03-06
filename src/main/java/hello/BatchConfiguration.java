package hello;

import javax.sql.DataSource;
import javax.swing.event.DocumentEvent;

import hello.device.DeviceCommand;
import hello.device.DeviceItemProcessor;
import hello.device.DeviceLineAggregator;
import hello.device.DeviceLineMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    // tag::readerwriterprocessor[]
    @Bean
    public FlatFileItemReader<Person> reader() {
        return new FlatFileItemReaderBuilder<Person>()
            .name("personItemReader")
            .resource(new ClassPathResource("sample-data.csv"))
            .delimited()
            .names(new String[]{"firstName", "lastName"})
            .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                setTargetType(Person.class);
            }})
            .build();
    }

    @Bean
    public FlatFileItemReader<DeviceCommand> deviceReader() {
        return new FlatFileItemReaderBuilder<DeviceCommand>()
                .name("deviceItemReader")
                .resource(new ClassPathResource("batch-data-source.csv"))
                .lineMapper(new DeviceLineMapper())
                .build();
    }

    @Bean
    public PersonProcessor processor() {
        return new PersonProcessor();
    }

    @Bean
    public DeviceItemProcessor deviceItemProcessor() {return  new DeviceItemProcessor();}

    @Bean
    public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
            .dataSource(dataSource)
            .build();
    }

    @Bean
    public FlatFileItemWriter<DeviceCommand> deviceItemWriter() {
        return new FlatFileItemWriterBuilder<DeviceCommand>()
                .name("deviceItemWriter")
                .resource(new ClassPathResource("batch-data-target.csv"))
                .lineAggregator(new DeviceLineAggregator())
                .build();
    }

    // end::readerwriterprocessor[]

    // tag::jobstep[]
//    @Bean
//    public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
//        return jobBuilderFactory.get("importUserJob")
//            .incrementer(new RunIdIncrementer())
//            .listener(listener)
//            .flow(step1)
//            .end()
//            .build();
//    }
//
    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .<DeviceCommand, DeviceCommand> chunk(5)
            .reader(deviceReader())
            .processor(deviceItemProcessor())
            .writer(deviceItemWriter())
            .build();
    }
    // end::jobstep[]





    @Value("org/springframework/batch/core/schema-drop-mysql.sql")
    private Resource dropReopsitoryTables;

    @Value("org/springframework/batch/core/schema-mysql.sql")
    private Resource dataReopsitorySchema;

    @Value("schema-all.sql")
    private Resource peopleTables;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/test?useUnicode=false&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false");
        dataSource.setUsername("admin");
        dataSource.setPassword("admin");
        return dataSource;
    }


    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        ResourceDatabasePopulator databasePopulator =
                new ResourceDatabasePopulator();

//        databasePopulator.addScript(dropReopsitoryTables);
//        databasePopulator.addScript(dataReopsitorySchema);
//        databasePopulator.addScript(peopleTables);
        databasePopulator.setIgnoreFailedDrops(true);

        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator);

        return initializer;
    }



//    @Bean
//    public JobRepository getJobRepository() throws Exception {
//        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
//        factory.setDataSource(dataSource());
//        factory.setTransactionManager(getTransactionManager());
//        factory.afterPropertiesSet();
//        return factory.getObject();
//    }
//
//    @Bean
//    public PlatformTransactionManager getTransactionManager() {
//        return new DataSourceTransactionManager();
//    }
//
//    @Bean
//    public JobLauncher getJobLauncher() throws Exception {
//        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
//        jobLauncher.setJobRepository(getJobRepository());
//        jobLauncher.afterPropertiesSet();
//        return jobLauncher;
//    }
}
