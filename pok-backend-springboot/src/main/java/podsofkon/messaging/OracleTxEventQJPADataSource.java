package podsofkon.messaging;

import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.logging.Logger;

public class OracleTxEventQJPADataSource implements DataSource
{
    static org.slf4j.Logger logger = LoggerFactory.getLogger(OracleTxEventQJPADataSource.class);
    DataSource datasource;
    private static Connection aqConnection;

    public OracleTxEventQJPADataSource(DataSource datasource) {
        this.datasource = datasource;
    }

    public static void setConnection(Connection connectionFromAQSessions) throws SQLException {
        ThreadLocal threadLocalValue = new ThreadLocal<>();
        logger.info("setConnection connectionFromAQSessions:" + connectionFromAQSessions);
        logger.info("setConnection threadLocalValue:" + threadLocalValue);
        aqConnection = new OracleTxEventQJPAConnection((oracle.jdbc.internal.OracleConnection)connectionFromAQSessions);
    }

    @Override
    public Connection getConnection() throws SQLException {
        ThreadLocal threadLocalValue = new ThreadLocal<>();
        logger.info("getConnection threadLocalValue:" + threadLocalValue);
        if(aqConnection!=null) {
            Connection qasconnection = aqConnection;
            aqConnection = null;
            logger.info("getConnection" + qasconnection);
            return qasconnection;
        }
        Connection connection = new OracleTxEventQJPAConnection((oracle.jdbc.internal.OracleConnection)datasource.getConnection());
        logger.info("getConnection" + connection);
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        logger.info("getConnection username = " + username + ", password = " + password);
        return datasource.getConnection(username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return datasource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        datasource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        datasource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return datasource.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return datasource.getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return datasource.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return datasource.isWrapperFor(iface);
    }
}
