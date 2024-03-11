package podsofkon.messaging;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.jms.*;
import javax.sql.DataSource;

import oracle.AQ.AQQueueTable;
import oracle.AQ.AQQueueTableProperty;
import oracle.jms.AQjmsDestination;
import oracle.jms.AQjmsDestinationProperty;
import oracle.jms.AQjmsSession;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import oracle.jdbc.pool.OracleDataSource;
import oracle.jms.AQjmsFactory;

@Configuration
public class OracleAQConfiguration {
    static Logger logger = LoggerFactory.getLogger(OracleAQConfiguration.class);
    @Autowired
    private Environment environment;
    OracleDataSource ds;

    @Bean
    public DataSource dataSource() throws SQLException {
//		PoolDataSource atpInventoryPDB = PoolDataSourceFactory.getPoolDataSource();
//		atpInventoryPDB.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
//		atpInventoryPDB.setURL(environment.getProperty("db_url"));
//		atpInventoryPDB.setUser(environment.getProperty("db_user"));
//		atpInventoryPDB.setPassword(environment.getProperty("db_password"));
        if( ds != null) return ds;
        ds = new OracleDataSource();
        ds.setUser(environment.getProperty("spring.datasource.username"));
        logger.info("USER: " + environment.getProperty("spring.datasource.username"));
        ds.setPassword(environment.getProperty("spring.datasource.password"));
        ds.setURL(environment.getProperty("spring.datasource.url"));
        logger.info("OracleAQConfiguration: dataSource :" + ds);
        return ds;
//        return new OracleTxEventQJPADataSource(ds);
    }

    @Bean
    public QueueConnectionFactory connectionFactory(DataSource dataSource) throws JMSException, SQLException {
        logger.info("OracleAQConfiguration: connectionFactory success");
        return AQjmsFactory.getQueueConnectionFactory(dataSource);
    }

    @Bean
    public JmsListenerContainerFactory<?> queueConnectionFactory(QueueConnectionFactory connectionFactory,
                                                                 DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setPubSubDomain(false);
        factory.setSessionTransacted(true);
        return factory;
    }

    @Bean
    public JmsListenerContainerFactory<?> topicConnectionFactory(QueueConnectionFactory connectionFactory,
                                                                 DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setPubSubDomain(true);
        factory.setSessionTransacted(true);
        configurer.configure(factory, connectionFactory);
        factory.setClientId("inventory_service");
        return factory;
    }

    @Bean
    public DynamicDestinationResolver destinationResolver() {
        return new DynamicDestinationResolver() {
            @Override
            public Destination resolveDestinationName(Session session, String destinationName, boolean pubSubDomain)
                    throws JMSException {
                return super.resolveDestinationName(session, destinationName, false);//topic or queue
            }
        };
    }





}