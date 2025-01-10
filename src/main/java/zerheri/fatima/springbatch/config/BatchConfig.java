package zerheri.fatima.springbatch.config;


import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import zerheri.fatima.springbatch.listener.JobCompletionNotificationListener;
import zerheri.fatima.springbatch.model.Order;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import zerheri.fatima.springbatch.processor.OrderProcessor;


import javax.sql.DataSource;

@Configuration
public class BatchConfig {
    @Bean
    public FlatFileItemReader<Order> reader() {
        return new FlatFileItemReaderBuilder<Order>()
                .name("ordersItemReader")
                .resource(new ClassPathResource("orders.csv"))
                .delimited()
                .names("orderId", "customerName", "amount" )
                .linesToSkip(1)
                .targetType(Order.class)
                .build();
    }

    @Bean
    public OrderProcessor processor() {
        return new OrderProcessor();
    }


    @Bean
    public JdbcBatchItemWriter<Order> writer(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<Order>()
                .sql("INSERT INTO orders (orderid, customerName, amount) VALUES (:orderId, :customerName, :amount)")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository,
                      DataSourceTransactionManager transactionManager,
                      FlatFileItemReader<Order> reader,
                      OrderProcessor processor,
                      JdbcBatchItemWriter<Order> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Order, Order>chunk(3, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job importOrderJob(JobRepository jobRepository, Step step1,
                              JobCompletionNotificationListener listener) {
        return new JobBuilder("importOrderJob", jobRepository)
                .listener(listener)
                .start(step1)
                .build();
    }
}