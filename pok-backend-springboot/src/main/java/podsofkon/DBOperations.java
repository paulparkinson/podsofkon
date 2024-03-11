package podsofkon;

import oracle.AQ.AQQueueTable;
import oracle.AQ.AQQueueTableProperty;
import oracle.jms.AQjmsDestination;
import oracle.jms.AQjmsDestinationProperty;
import oracle.jms.AQjmsFactory;
import oracle.jms.AQjmsSession;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import javax.jms.*;
import javax.sql.DataSource;
import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DBOperations {
    private static Logger logger = LoggerFactory.getLogger(DBOperations.class);
    private static PoolDataSource oracleSpringDB;
//
//    public DataSource dataSource() throws SQLException {
//
////		PoolDataSource atpInventoryPDB = PoolDataSourceFactory.getPoolDataSource();
////		atpInventoryPDB.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
////		atpInventoryPDB.setURL(environment.getProperty("db_url"));
////		atpInventoryPDB.setUser(environment.getProperty("db_user"));
////		atpInventoryPDB.setPassword(environment.getProperty("db_password"));
////        OracleDataSource ds = new OracleDataSource();
////
////        ds.setUser(environment.getProperty("spring.datasource.username"));
//////        logger.info("USER: " + environment.getProperty("spring.datasource.username"));
////        ds.setPassword(environment.getProperty("spring.datasource.password"));
////        ds.setURL(environment.getProperty("spring.datasource.url"));
////        logger.info("OracleAQConfiguration: dataSource :" + ds);
//        return ds;
//    }
    public DataSource dataSource() throws Exception {
        if (oracleSpringDB != null) return oracleSpringDB;
        oracleSpringDB = PoolDataSourceFactory.getPoolDataSource();
        oracleSpringDB.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
        oracleSpringDB.setUser("admin");
        oracleSpringDB.setPassword("1XRWelcome123");
//        oracleSpringDB.setURL("jdbc:oracle:thin:@xr_tp?TNS_ADMIN=/Users/pparkins/Downloads/xr/Wallet_xrdb");
        oracleSpringDB.setURL("jdbc:oracle:thin:@xr_tp?TNS_ADMIN=/podsofkon/creds");
        return oracleSpringDB;
    }

    String GRANT_QUEUE_PRIVILEGE_SQL = "BEGIN " +
            "DBMS_AQADM.grant_queue_privilege (" +
            "    privilege     =>     ? ," +
            "    queue_name    =>     ? ," +
            "    grantee       =>     ? ," +
            "    grant_option  =>      FALSE);" +
            "END;";


    /**

     CREATE USER $INVENTORY_USER IDENTIFIED BY "$INVENTORY_PASSWORD";
     GRANT unlimited tablespace to $INVENTORY_USER;
     GRANT connect, resource TO $INVENTORY_USER;
     GRANT aq_user_role TO $INVENTORY_USER;
     GRANT EXECUTE ON sys.dbms_aq TO $INVENTORY_USER;
     -- For inventory-springboot deployment
     GRANT aq_administrator_role TO $INVENTORY_USER;
     GRANT EXECUTE ON sys.dbms_aqadm TO $INVENTORY_USER;
     -- For inventory-plsql deployment
     GRANT CREATE JOB to $INVENTORY_USER;
     GRANT EXECUTE ON sys.DBMS_SCHEDULER TO $INVENTORY_USER;
     --This is all we want but table hasn't been created yet... GRANT select on AQ.inventoryqueuetable to $INVENTORY_USER;
     GRANT SELECT ANY TABLE TO $INVENTORY_USER;
     GRANT select on gv\$session to $INVENTORY_USER;
     GRANT select on v\$diag_alert_ext to $INVENTORY_USER;
     GRANT select on DBA_QUEUE_SCHEDULES to $INVENTORY_USER;
     */
    public String createSchema(String serviceName, String servicePassword) throws Exception {
        try {
            dataSource().getConnection().createStatement().execute(
                    "create user " + serviceName + " identified by " + servicePassword);
            dataSource().getConnection().createStatement().execute(
                    "GRANT UNLIMITED TABLESPACE TO " + serviceName);
            dataSource().getConnection().createStatement().execute(
                    "GRANT create session TO " + serviceName);
            dataSource().getConnection().createStatement().execute(
                    "GRANT connect, resource TO " + serviceName);
            dataSource().getConnection().createStatement().execute(
                    "GRANT SELECT ANY TABLE TO " + serviceName);
        } catch (java.sql.SQLSyntaxErrorException ex ) {
            ex.printStackTrace();
            return "createSchema failed due to " + ex.getMessage();
        }
        return "schema created successfully for " + serviceName;
    }
    public String deleteSchema(String serviceName)  {
        try {
            dataSource().getConnection().createStatement().execute(
                    "delete user " + serviceName );
        } catch (Exception e) {
            return "schema delete failed for " + serviceName + " Exception:" + e;
        }
        return "schema deleted successfully for " + serviceName;
    }
    public String createTable(String userName, String password, String tableName)  {
        try {
            dataSource().getConnection(userName, password).createStatement().execute(
                    "create table " + tableName + " ( id varchar(64) )" );
        } catch (Exception e) {
            return "createTable Exception:" + e;
        }
        return "createTable successfully for " + tableName;
    }

    public String selectFromTable(String userName, String password, String tableName) {
        int i = 0;
        try {
            Statement statement = dataSource().getConnection(userName, password).createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
            while (resultSet.next()) {
//                System.out.println(resultSet.getInt(1) + "  " +
//                        resultSet.getString(2) + "  " + resultSet.getString(3));
                i++;
            }
        } catch (Exception e) {
            return "selectFromTable Exception:" + e;
        }
        return "selectFromTable successfully for  " + tableName + " i  " + i;
    }

    public String createQueue(String queueOwner, String queueName) throws Exception {
        boolean isAQ = true;
        if (isAQ) createAQ(false, queueOwner, queueName);
        else  {
            createTEQQueue(queueOwner, queueName);
        }
        java.sql.Connection connection = dataSource().getConnection();
        PreparedStatement pstmt = connection.prepareStatement(GRANT_QUEUE_PRIVILEGE_SQL);
        pstmt.setString(1, "ENQUEUE");
        pstmt.setString(2, queueOwner + "." + queueName);
        pstmt.setString(3, queueOwner);
        pstmt.execute();
        pstmt.setString(1, "DEQUEUE");
        pstmt.setString(2, queueOwner + "." + queueName);
        pstmt.setString(3, queueOwner);
        pstmt.execute();
        logger.debug("create queue successful for queue:" + queueName);
        return " successfully created queue:" + queueName;
    }

    private void createTEQQueue(String queueOwner, String queueName)
            throws Exception {
        TopicConnectionFactory tcf = AQjmsFactory.getTopicConnectionFactory(dataSource());
        TopicConnection conn = tcf.createTopicConnection();
        conn.start();
        TopicSession session = (AQjmsSession) conn.createSession(true, Session.AUTO_ACKNOWLEDGE);
        AQQueueTableProperty props = new AQQueueTableProperty("SYS.AQ$_JMS_TEXT_MESAGE");
        props.setMultiConsumer(true);
        props.setPayloadType("SYS.AQ$_JMS_TEXT_MESSAGE");
        Destination myTeq = ((AQjmsSession) session).createJMSTransactionalEventQueue(queueName, true);
        ((AQjmsDestination) myTeq).start(session, true, true);
    }
//    @Bean
    public String createAQ(boolean isTopic, String queueOwner, String queueName) throws Exception {
        System.out.println("create queue dataSource:" + dataSource());
        DataSource dataSource = dataSource();
//        DataSource dataSource = getPoolDataSource();
        System.out.println("create queue dataSource:" + dataSource );
        System.out.println("create queue queueOwner:" + queueOwner);
        System.out.println("create queue queueName:" + queueName);
        QueueConnectionFactory q_cf = AQjmsFactory.getQueueConnectionFactory(dataSource);
        QueueConnection q_conn = q_cf.createQueueConnection();
        Session session = q_conn.createQueueSession(true, Session.CLIENT_ACKNOWLEDGE);
        AQQueueTable q_table = null;
        AQQueueTableProperty qt_prop =
                new AQQueueTableProperty("SYS.AQ$_JMS_TEXT_MESSAGE" )  ;
        q_table = ((AQjmsSession) session).createQueueTable(queueOwner, queueName, qt_prop);
        if(isTopic) {
            Topic topic = ((AQjmsSession) session).createTopic(q_table, queueName, new AQjmsDestinationProperty());
            ((AQjmsDestination) topic).start(session, true, true);
            System.out.println("create topic successful for queue:" + topic);
            return "createAQ successful for topic:" + topic;
        } else {
            Queue queue = ((AQjmsSession) session).createQueue(q_table, queueName, new AQjmsDestinationProperty());
            ((AQjmsDestination) queue).start(session, true, true);
            System.out.println("create queue successful for queue:" + queue);
            return "createAQ successful for queue:" + queue;
        }
    }

}
