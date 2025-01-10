package zerheri.fatima.springbatch.listener;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import zerheri.fatima.springbatch.model.Order;


import java.util.List;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {
    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
    private final JdbcTemplate jdbcTemplate;

    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("Le job s'est terminé avec succès.");

            // Récupérer les données depuis la base de données
            List<Order> orders = jdbcTemplate.query(
                    "SELECT * FROM orders",
                    new DataClassRowMapper<>(Order.class)
            );

            if (orders.isEmpty()) {
                log.info("Aucune commande n'a été trouvée dans la base de données.");
            } else {
                orders.forEach(order -> log.info("Commande insérée : {}",order.toString()));
            }
        } else {
            log.error("Le job s'est terminé avec des erreurs. Statut : {}", jobExecution.getStatus());
        }
    }
}