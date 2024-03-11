package podsofkon.messaging;

import oracle.jdbc.*;
import oracle.jdbc.aq.*;
import oracle.jdbc.dcn.DatabaseChangeRegistration;
import oracle.jdbc.diagnostics.SecuredLogger;
import oracle.jdbc.driver.HAManager;
import oracle.jdbc.internal.*;
import oracle.jdbc.internal.OracleArray;
import oracle.jdbc.internal.OracleBfile;
import oracle.jdbc.internal.OracleStatement;
import oracle.jdbc.oracore.OracleTypeADT;
import oracle.jdbc.oracore.OracleTypeCLOB;
import oracle.jdbc.pool.OracleConnectionCacheCallback;
import oracle.jdbc.pool.OraclePooledConnection;
import oracle.sql.*;

import javax.transaction.xa.XAResource;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.sql.Date;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.Executor;

public class OracleTxEventQJPAConnection implements oracle.jdbc.internal.OracleConnection{

    oracle.jdbc.internal.OracleConnection connection;

    public  OracleTxEventQJPAConnection(oracle.jdbc.internal.OracleConnection connection) {
        this.connection = connection;
    }
    @Override
    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return connection.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return connection.nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
//        new Throwable("instentional from setAutoCommit:" + autoCommit).printStackTrace();
        if(!autoCommit) connection.setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return connection.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        for (StackTraceElement instentional_from_rollback : new Throwable("instentional from commit").getStackTrace()) {
//            if (instentional_from_rollback.getClassName().equals("oracle.jms.AQjmsSession")) {
            //more specifically org.hibernate.engine.transaction.internal.TransactionImpl.commit or org.springframework.data.jpa.repository.support
            if (instentional_from_rollback.getClassName().indexOf("org.springframework.data.jpa") > -1 ||
                    instentional_from_rollback.getClassName().indexOf("org.springframework.orm.jpa") > -1) {
//                System.out.println("OracleTxEventQJPAConnection.commit is not org.springframework.data.jpa.repository.support so commit");
                return;
//                connection.close();
//                return;
            }
        }
        connection.commit();
    }

    @Override
    public void rollback() throws SQLException {
        connection.rollback();
    }

    @Override
    public void close() throws SQLException {
        for (StackTraceElement instentional_from_rollback : new Throwable("instentional from close").getStackTrace()) {
            //more specifically org.hibernate.engine.transaction.internal.TransactionImpl.commit or org.springframework.data.jpa.repository.support
            if (instentional_from_rollback.getClassName().indexOf("org.springframework.data.jpa") > -1 ||
                    instentional_from_rollback.getClassName().indexOf("org.springframework.orm.jpa") > -1) {
                return;
            }
        }
        connection.close();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return connection.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return connection.getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        connection.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return connection.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        connection.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return connection.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        connection.setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return connection.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return connection.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        connection.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.prepareStatement(sql, resultSetType,resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return connection.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        connection.setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        connection.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return connection.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return connection.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return connection.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        connection.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        connection.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return connection.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return connection.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return connection.prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return connection.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return connection.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return connection.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return connection.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return connection.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        connection.setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        connection.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return connection.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return connection.getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return connection.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return connection.createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        connection.setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return connection.getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        connection.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        connection.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return connection.getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return connection.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return connection.isWrapperFor(iface);
    }

    @Override
    public int getVarTypeMaxLenCompat() throws SQLException {
        return connection.getVarTypeMaxLenCompat();
    }

    @Override
    public short getStructAttrNCsId() throws SQLException {
        return connection.getStructAttrNCsId();
    }

    @Override
    public Properties getDBAccessProperties() throws SQLException {
        return connection.getDBAccessProperties();
    }

    @Override
    public Properties getOCIHandles() throws SQLException {
        return connection.getOCIHandles();
    }

    @Override
    public String getDatabaseProductVersion() throws SQLException {
        return connection.getDatabaseProductVersion();
    }

    @Override
    public String getURL() throws SQLException {
        return connection.getURL();
    }

    @Override
    public short getVersionNumber() throws SQLException {
        return connection.getVersionNumber();
    }

    @Override
    public Map<String, Class<?>> getJavaObjectTypeMap() {
        return connection.getJavaObjectTypeMap();
    }

    @Override
    public void setJavaObjectTypeMap(Map<String, Class<?>> map) {
        connection.setJavaObjectTypeMap(map);
    }

    @Override
    public byte getInstanceProperty(InstanceProperty instanceProperty) throws SQLException {
        return connection.getInstanceProperty(instanceProperty);
    }

    @Override
    public BfileDBAccess createBfileDBAccess() throws SQLException {
        return connection.createBfileDBAccess();
    }

    @Override
    public BlobDBAccess createBlobDBAccess() throws SQLException {
        return connection.createBlobDBAccess();
    }

    @Override
    public ClobDBAccess createClobDBAccess() throws SQLException {
        return connection.createClobDBAccess();
    }

    @Override
    public void setDefaultFixedString(boolean b) {
        connection.setDefaultFixedString(b);
    }

    @Override
    public boolean getDefaultFixedString() {
        return connection.getDefaultFixedString();
    }

    @Override
    public oracle.jdbc.OracleConnection getWrapper() {
        return connection.getWrapper();
    }

    @Override
    public Class classForNameAndSchema(String s, String s1) throws ClassNotFoundException {
        return connection.classForNameAndSchema(s, s1);
    }

    @Override
    public void setFDO(byte[] bytes) throws SQLException {
        connection.setFDO(bytes);
    }

    @Override
    public byte[] getFDO(boolean b) throws SQLException {
        return connection.getFDO(b);
    }

    @Override
    public boolean getBigEndian() throws SQLException {
        return connection.getBigEndian();
    }

    @Override
    public Object getDescriptor(byte[] bytes) {
        return connection.getDescriptor(bytes);
    }

    @Override
    public void putDescriptor(byte[] bytes, Object o) throws SQLException {
        connection.putDescriptor(bytes, o);
    }

    @Override
    public oracle.jdbc.internal.OracleConnection getPhysicalConnection() {
        return connection.getPhysicalConnection();
    }

    @Override
    public void removeDescriptor(String s) {
        connection.removeDescriptor(s);
    }

    @Override
    public void removeAllDescriptor() {
        connection.removeAllDescriptor();
    }

    @Override
    public int numberOfDescriptorCacheEntries() {
        return connection.numberOfDescriptorCacheEntries();
    }

    @Override
    public Enumeration<String> descriptorCacheKeys() {
        return connection.descriptorCacheKeys();
    }

    @Override
    public long getTdoCState(String s, String s1) throws SQLException {
        return connection.getTdoCState(s, s1);
    }

    @Override
    public long getTdoCState(String s) throws SQLException {
        return connection.getTdoCState(s);
    }

    @Override
    public BufferCacheStatistics getByteBufferCacheStatistics() {
        return connection.getByteBufferCacheStatistics();
    }

    @Override
    public BufferCacheStatistics getCharBufferCacheStatistics() {
        return connection.getCharBufferCacheStatistics();
    }

    @Override
    public Datum toDatum(CustomDatum customDatum) throws SQLException {
        return connection.toDatum(customDatum);
    }

    @Override
    public short getDbCsId() throws SQLException {
        return connection.getDbCsId();
    }

    @Override
    public short getJdbcCsId() throws SQLException {
        return connection.getJdbcCsId();
    }

    @Override
    public short getNCharSet() {
        return connection.getNCharSet();
    }

    @Override
    public ResultSet newArrayDataResultSet(Datum[] data, long l, int i, Map<String, Class<?>> map) throws SQLException {
        return connection.newArrayDataResultSet(data, l, i, map);
    }

    @Override
    public ResultSet newArrayDataResultSet(OracleArray oracleArray, long l, int i, Map<String, Class<?>> map) throws SQLException {
        return connection.newArrayDataResultSet(oracleArray, l, i, map);
    }

    @Override
    public ResultSet newArrayLocatorResultSet(ArrayDescriptor arrayDescriptor, byte[] bytes, long l, int i, Map<String, Class<?>> map) throws SQLException {
        return connection.newArrayLocatorResultSet(arrayDescriptor, bytes, l, i, map);
    }

    @Override
    public ResultSetMetaData newStructMetaData(StructDescriptor structDescriptor) throws SQLException {
        return connection.newStructMetaData(structDescriptor);
    }

    @Override
    public void getForm(OracleTypeADT oracleTypeADT, OracleTypeCLOB oracleTypeCLOB, int i) throws SQLException {
        connection.getForm(oracleTypeADT, oracleTypeCLOB, i);
    }

    @Override
    public int CHARBytesToJavaChars(byte[] bytes, int i, char[] chars) throws SQLException {
        return connection.CHARBytesToJavaChars(bytes, i, chars);
    }

    @Override
    public int NCHARBytesToJavaChars(byte[] bytes, int i, char[] chars) throws SQLException {
        return connection.NCHARBytesToJavaChars(bytes, i, chars);
    }

    @Override
    public boolean IsNCharFixedWith() {
        return connection.IsNCharFixedWith();
    }

    @Override
    public short getDriverCharSet() {
        return connection.getDriverCharSet();
    }

    @Override
    public int getC2SNlsRatio() {
        return connection.getC2SNlsRatio();
    }

    @Override
    public int getMaxCharSize() throws SQLException {
        return connection.getMaxCharSize();
    }

    @Override
    public int getMaxCharbyteSize() {
        return connection.getMaxCharbyteSize();
    }

    @Override
    public int getMaxNCharbyteSize() {
        return connection.getMaxNCharbyteSize();
    }

    @Override
    public boolean isCharSetMultibyte(short i) {
        return connection.isCharSetMultibyte(i);
    }

    @Override
    public int javaCharsToCHARBytes(char[] chars, int i, byte[] bytes) throws SQLException {
        return connection.javaCharsToCHARBytes(chars, i, bytes);
    }

    @Override
    public int javaCharsToNCHARBytes(char[] chars, int i, byte[] bytes) throws SQLException {
        return connection.javaCharsToNCHARBytes(chars, i, bytes);
    }

    @Override
    @Deprecated
    public void setStartTime(long l) throws SQLException {
        connection.setStartTime(l);
    }

    @Override
    @Deprecated
    public long getStartTime() throws SQLException {
        return connection.getStartTime();
    }

    @Override
    public boolean isStatementCacheInitialized() {
        return connection.isStatementCacheInitialized();
    }

    @Override
    public void getPropertyForPooledConnection(OraclePooledConnection oraclePooledConnection) throws SQLException {
        connection.getPropertyForPooledConnection(oraclePooledConnection);
    }

    @Override
    public String getProtocolType() {
        return connection.getProtocolType();
    }

    @Override
    public Connection getLogicalConnection(OraclePooledConnection oraclePooledConnection, boolean b) throws SQLException {
        return connection.getLogicalConnection(oraclePooledConnection, b);
    }

    @Override
    public void setTxnMode(int i) {
        connection.setTxnMode(i);
    }

    @Override
    public int getTxnMode() {
        return connection.getTxnMode();
    }

    @Override
    public int getHeapAllocSize() throws SQLException {
        return connection.getHeapAllocSize();
    }

    @Override
    public int getOCIEnvHeapAllocSize() throws SQLException {
        return connection.getOCIEnvHeapAllocSize();
    }

    @Override
    @Deprecated
    public void setAbandonedTimeoutEnabled(boolean b) throws SQLException {
        connection.setAbandonedTimeoutEnabled(b);
    }

    @Override
    @Deprecated
    public int getHeartbeatNoChangeCount() throws SQLException {
        return connection.getHeartbeatNoChangeCount();
    }

    @Override
    public void closeInternal(boolean b) throws SQLException {
        connection.closeInternal(b);
    }

    @Override
    public void cleanupAndClose(boolean b) throws SQLException {
        connection.cleanupAndClose(b);
    }

    @Override
    @Deprecated
    public OracleConnectionCacheCallback getConnectionCacheCallbackObj() throws SQLException {
        return connection.getConnectionCacheCallbackObj();
    }

    @Override
    @Deprecated
    public Object getConnectionCacheCallbackPrivObj() throws SQLException {
        return connection.getConnectionCacheCallbackPrivObj();
    }

    @Override
    @Deprecated
    public int getConnectionCacheCallbackFlag() throws SQLException {
        return connection.getConnectionCacheCallbackFlag();
    }

    @Override
    public Properties getServerSessionInfo() throws SQLException {
        return connection.getServerSessionInfo();
    }

    @Override
    public CLOB createClob(byte[] bytes) throws SQLException {
        return connection.createClob(bytes);
    }

    @Override
    public CLOB createClobWithUnpickledBytes(byte[] bytes) throws SQLException {
        return connection.createClobWithUnpickledBytes(bytes);
    }

    @Override
    public CLOB createClob(byte[] bytes, short i) throws SQLException {
        return connection.createClob(bytes, i);
    }

    @Override
    public BLOB createBlob(byte[] bytes) throws SQLException {
        return connection.createBlob(bytes);
    }

    @Override
    public BLOB createBlobWithUnpickledBytes(byte[] bytes) throws SQLException {
        return connection.createBlobWithUnpickledBytes(bytes);
    }

    @Override
    public BFILE createBfile(byte[] bytes) throws SQLException {
        return connection.createBfile(bytes);
    }

    @Override
    public boolean isDescriptorSharable(oracle.jdbc.internal.OracleConnection oracleConnection) throws SQLException {
        return connection.isDescriptorSharable(oracleConnection);
    }

    @Override
    public OracleStatement refCursorCursorToStatement(int i) throws SQLException {
        return connection.refCursorCursorToStatement(i);
    }

    @Override
    public XAResource getXAResource() throws SQLException {
        return connection.getXAResource();
    }

    @Override
    public boolean isV8Compatible() throws SQLException {
        return connection.isV8Compatible();
    }

    @Override
    public boolean getMapDateToTimestamp() {
        return connection.getMapDateToTimestamp();
    }

    @Override
    public boolean getJDBCStandardBehavior() {
        return connection.getJDBCStandardBehavior();
    }

    @Override
    public byte[] createLightweightSession(String s, KeywordValueLong[] keywordValueLongs, int i, KeywordValueLong[][] keywordValueLongs1, int[] ints) throws SQLException {
        return connection.createLightweightSession(s, keywordValueLongs, i, keywordValueLongs1, ints);
    }

    @Override
    public void executeLightweightSessionPiggyback(int i, byte[] bytes, KeywordValueLong[] keywordValueLongs, int i1) throws SQLException {
        connection.executeLightweightSessionPiggyback(i, bytes, keywordValueLongs, i1);
    }

    @Override
    public void doXSNamespaceOp(XSOperationCode xsOperationCode, byte[] bytes, XSNamespace[] xsNamespaces, XSNamespace[][] xsNamespaces1, XSSecureId xsSecureId) throws SQLException {
        connection.doXSNamespaceOp(xsOperationCode, bytes, xsNamespaces, xsNamespaces1, xsSecureId);
    }

    @Override
    public void doXSNamespaceOp(XSOperationCode xsOperationCode, byte[] bytes, XSNamespace[] xsNamespaces, XSSecureId xsSecureId) throws SQLException {
        connection.doXSNamespaceOp(xsOperationCode, bytes, xsNamespaces, xsSecureId);
    }

    @Override
    public byte[] doXSSessionCreateOp(XSSessionOperationCode xsSessionOperationCode, XSSecureId xsSecureId, byte[] bytes, XSPrincipal xsPrincipal, String s, XSNamespace[] xsNamespaces, XSSessionModeFlag xsSessionModeFlag, XSKeyval xsKeyval) throws SQLException {
        return connection.doXSSessionCreateOp(xsSessionOperationCode, xsSecureId, bytes, xsPrincipal, s, xsNamespaces, xsSessionModeFlag, xsKeyval);
    }

    @Override
    public void doXSSessionDestroyOp(byte[] bytes, XSSecureId xsSecureId, byte[] bytes1) throws SQLException {
        connection.doXSSessionDestroyOp(bytes, xsSecureId, bytes1);
    }

    @Override
    public void doXSSessionAttachOp(int i, byte[] bytes, XSSecureId xsSecureId, byte[] bytes1, XSPrincipal xsPrincipal, String[] strings, String[] strings1, String[] strings2, XSNamespace[] xsNamespaces, XSNamespace[] xsNamespaces1, XSNamespace[] xsNamespaces2, TIMESTAMPTZ timestamptz, TIMESTAMPTZ timestamptz1, int i1, long l, XSKeyval xsKeyval, int[] ints) throws SQLException {
        connection.doXSSessionAttachOp(i, bytes, xsSecureId, bytes1, xsPrincipal, strings, strings1, strings2, xsNamespaces, xsNamespaces1, xsNamespaces2, timestamptz, timestamptz1, i1, l, xsKeyval, ints);
    }

    @Override
    public void doXSSessionDetachOp(int i, byte[] bytes, XSSecureId xsSecureId, boolean b) throws SQLException {
        connection.doXSSessionDetachOp(i, bytes, xsSecureId, b);
    }

    @Override
    public void doXSSessionChangeOp(XSSessionSetOperationCode xsSessionSetOperationCode, byte[] bytes, XSSecureId xsSecureId, XSSessionParameters xsSessionParameters) throws SQLException {
        connection.doXSSessionChangeOp(xsSessionSetOperationCode, bytes, xsSecureId, xsSessionParameters);
    }

    @Override
    public String getDefaultSchemaNameForNamedTypes() throws SQLException {
        return connection.getDefaultSchemaNameForNamedTypes();
    }

    @Override
    public void setUsable(boolean b) {
        connection.setUsable(b);
    }

    @Override
    public Class getClassForType(String s, Map<String, Class<?>> map) {
        return connection.getClassForType(s, map);
    }

    @Override
    public void addXSEventListener(XSEventListener xsEventListener) throws SQLException {
        connection.addXSEventListener(xsEventListener);
    }

    @Override
    public void addXSEventListener(XSEventListener xsEventListener, Executor executor) throws SQLException {
        connection.addXSEventListener(xsEventListener, executor);
    }

    @Override
    public void removeXSEventListener(XSEventListener xsEventListener) throws SQLException {
        connection.removeXSEventListener(xsEventListener);
    }

    @Override
    public int getTimezoneVersionNumber() throws SQLException {
        return connection.getTimezoneVersionNumber();
    }

    @Override
    public void removeAllXSEventListener() throws SQLException {
        connection.removeAllXSEventListener();
    }

    @Override
    public TIMEZONETAB getTIMEZONETAB() throws SQLException {
        return connection.getTIMEZONETAB();
    }

    @Override
    public String getDatabaseTimeZone() throws SQLException {
        return connection.getDatabaseTimeZone();
    }

    @Override
    public boolean getTimestamptzInGmt() {
        return connection.getTimestamptzInGmt();
    }

    @Override
    public boolean getUse1900AsYearForTime() {
        return connection.getUse1900AsYearForTime();
    }

    @Override
    public boolean isDataInLocatorEnabled() throws SQLException {
        return connection.isDataInLocatorEnabled();
    }

    @Override
    public boolean isLobStreamPosStandardCompliant() throws SQLException {
        return connection.isLobStreamPosStandardCompliant();
    }

    @Override
    public long getCurrentSCN() throws SQLException {
        return connection.getCurrentSCN();
    }

    @Override
    public EnumSet<TransactionState> getTransactionState() throws SQLException {
        return connection.getTransactionState();
    }

    @Override
    public boolean isConnectionSocketKeepAlive() throws SocketException, SQLException {
        return connection.isConnectionSocketKeepAlive();
    }

    @Override
    public boolean isConnectionBigTZTC() throws SQLException {
        return connection.isConnectionBigTZTC();
    }

    @Override
    public void setReplayOperations(EnumSet<ReplayOperation> enumSet) throws SQLException {
        connection.setReplayOperations(enumSet);
    }

    @Override
    public void setReplayingMode(boolean b) throws SQLException {
        connection.setReplayingMode(b);
    }

    @Override
    public void beginNonRequestCalls() throws SQLException {
        connection.beginNonRequestCalls();
    }

    @Override
    public void endNonRequestCalls() throws SQLException {
        connection.endNonRequestCalls();
    }

    @Override
    public void setReplayContext(ReplayContext[] replayContexts) throws SQLException {
        connection.setReplayContext(replayContexts);
    }

    @Override
    public ReplayContext[] getReplayContext() throws SQLException {
        return connection.getReplayContext();
    }

    @Override
    public ReplayContext getLastReplayContext() throws SQLException {
        return connection.getLastReplayContext();
    }

    @Override
    public void setLastReplayContext(ReplayContext replayContext) throws SQLException {
        connection.setLastReplayContext(replayContext);
    }

    @Override
    public void registerEndReplayCallback(EndReplayCallback endReplayCallback) throws SQLException {
        connection.registerEndReplayCallback(endReplayCallback);
    }

    @Override
    public int getEOC() throws SQLException {
        return connection.getEOC();
    }

    @Override
    public byte[] getDerivedKeyInternal(byte[] bytes, int i) throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException {
        return connection.getDerivedKeyInternal(bytes, i);
    }

    @Override
    public short getExecutingRPCFunctionCode() {
        return connection.getExecutingRPCFunctionCode();
    }

    @Override
    public String getExecutingRPCSQL() {
        return connection.getExecutingRPCSQL();
    }

    @Override
    public void jmsEnqueue(String s, JMSEnqueueOptions jmsEnqueueOptions, JMSMessage jmsMessage, AQMessageProperties aqMessageProperties) throws SQLException {
        connection.jmsEnqueue(s, jmsEnqueueOptions, jmsMessage, aqMessageProperties);
    }

    @Override
    public void jmsEnqueue(String s, JMSEnqueueOptions jmsEnqueueOptions, JMSMessage[] jmsMessages, AQMessageProperties[] aqMessageProperties) throws SQLException {
        connection.jmsEnqueue(s, jmsEnqueueOptions, jmsMessages, aqMessageProperties);
    }

    @Override
    public JMSMessage jmsDequeue(String s, JMSDequeueOptions jmsDequeueOptions) throws SQLException {
        return connection.jmsDequeue(s, jmsDequeueOptions);
    }

    @Override
    public JMSMessage jmsDequeue(String s, JMSDequeueOptions jmsDequeueOptions, OutputStream outputStream) throws SQLException {
        return connection.jmsDequeue(s, jmsDequeueOptions, outputStream);
    }

    @Override
    public JMSMessage jmsDequeue(String s, JMSDequeueOptions jmsDequeueOptions, String s1) throws SQLException {
        return connection.jmsDequeue(s, jmsDequeueOptions, s1);
    }

    @Override
    public JMSMessage[] jmsDequeue(String s, JMSDequeueOptions jmsDequeueOptions, int i) throws SQLException {
        return connection.jmsDequeue(s, jmsDequeueOptions, i);
    }

    @Override
    public Map<String, JMSNotificationRegistration> registerJMSNotification(String[] strings, Map<String, Properties> map) throws SQLException {
        return connection.registerJMSNotification(strings, map);
    }

    @Override
    public Map<String, JMSNotificationRegistration> registerJMSNotification(String[] strings, Map<String, Properties> map, String s) throws SQLException {
        return connection.registerJMSNotification(strings, map, s);
    }

    @Override
    public void unregisterJMSNotification(JMSNotificationRegistration jmsNotificationRegistration) throws SQLException {
        connection.unregisterJMSNotification(jmsNotificationRegistration);
    }

    @Override
    public void startJMSNotification(JMSNotificationRegistration jmsNotificationRegistration) throws SQLException {
        connection.startJMSNotification(jmsNotificationRegistration);
    }

    @Override
    public void stopJMSNotification(JMSNotificationRegistration jmsNotificationRegistration) throws SQLException {
        connection.stopJMSNotification(jmsNotificationRegistration);
    }

    @Override
    public void ackJMSNotification(JMSNotificationRegistration jmsNotificationRegistration, byte[] bytes, JMSNotificationRegistration.Directive directive) throws SQLException {
        connection.ackJMSNotification(jmsNotificationRegistration, bytes, directive);
    }

    @Override
    public void ackJMSNotification(ArrayList<JMSNotificationRegistration> arrayList, byte[][] bytes, JMSNotificationRegistration.Directive directive) throws SQLException {
        connection.ackJMSNotification(arrayList, bytes, directive);
    }

    @Override
    public int getNegotiatedSDU() throws SQLException {
        return connection.getNegotiatedSDU();
    }

    @Override
    public byte getNegotiatedTTCVersion() throws SQLException {
        return connection.getNegotiatedTTCVersion();
    }

    @Override
    public void setPDBChangeEventListener(PDBChangeEventListener pdbChangeEventListener) throws SQLException {
        connection.setPDBChangeEventListener(pdbChangeEventListener);
    }

    @Override
    public void setPDBChangeEventListener(PDBChangeEventListener pdbChangeEventListener, Executor executor) throws SQLException {
        connection.setPDBChangeEventListener(pdbChangeEventListener, executor);
    }

    @Override
    public void setChecksumMode(ChecksumMode checksumMode) throws SQLException {
        connection.setChecksumMode(checksumMode);
    }

    @Override
    public ResultSetCache getResultSetCache() throws SQLException {
        return connection.getResultSetCache();
    }

    @Override
    public void closeLogicalConnection() throws SQLException {
        connection.closeLogicalConnection();
    }

    @Override
    public void cleanupAndClose() throws SQLException {
        connection.cleanupAndClose();
    }

    @Override
    public boolean isLifecycleOpen() throws SQLException {
        return connection.isLifecycleOpen();
    }

    @Override
    public void clearDrcpTagName() throws SQLException {
        connection.clearDrcpTagName();
    }

    @Override
    public void setClientIdentifier(String s) throws SQLException {
        connection.setClientIdentifier(s);
    }

    @Override
    public void clearClientIdentifier(String s) throws SQLException {
        connection.clearClientIdentifier(s);
    }

    @Override
    public int freeTemporaryBlobsAndClobs() throws SQLException {
        return connection.freeTemporaryBlobsAndClobs();
    }

    @Override
    public void setChunkInfo(OracleShardingKey oracleShardingKey, OracleShardingKey oracleShardingKey1, String s) throws SQLException {
        connection.setChunkInfo(oracleShardingKey, oracleShardingKey1, s);
    }

    @Override
    public HAManager getHAManager() {
        return connection.getHAManager();
    }

    @Override
    public void setHAManager(HAManager haManager) throws SQLException {
        connection.setHAManager(haManager);
    }

    @Override
    public boolean isUsable(boolean b) {
        return connection.isUsable(b);
    }

    @Override
    public boolean isNetworkCompressionEnabled() {
        return connection.isNetworkCompressionEnabled();
    }

    @Override
    public int getOutboundConnectTimeout() {
        return connection.getOutboundConnectTimeout();
    }

    @Override
    public NetStat getNetworkStat() {
        return connection.getNetworkStat();
    }

    @Override
    public void sendRequestFlags() throws SQLException {
        connection.sendRequestFlags();
    }

    @Override
    public boolean hasNoOpenHandles() throws SQLException {
        return connection.hasNoOpenHandles();
    }

    @Override
    public void endRequest(boolean b) throws SQLException {
        connection.endRequest(b);
    }

    @Override
    public DatabaseSessionState getDatabaseSessionState() throws SQLException {
        return connection.getDatabaseSessionState();
    }

    @Override
    public void setDatabaseSessionState(DatabaseSessionState databaseSessionState) throws SQLException {
        connection.setDatabaseSessionState(databaseSessionState);
    }

    @Override
    public boolean isSafelyClosed() throws SQLException {
        return connection.isSafelyClosed();
    }

    @Override
    public void setSafelyClosed(boolean b) throws SQLException {
        connection.setSafelyClosed(b);
    }

    @Override
    public void addLargeObject(OracleLargeObject oracleLargeObject) throws SQLException {
        connection.addLargeObject(oracleLargeObject);
    }

    @Override
    public void removeLargeObject(OracleLargeObject oracleLargeObject) throws SQLException {
        connection.removeLargeObject(oracleLargeObject);
    }

    @Override
    public void addBfile(OracleBfile oracleBfile) throws SQLException {
        connection.addBfile(oracleBfile);
    }

    @Override
    public void removeBfile(OracleBfile oracleBfile) throws SQLException {
        connection.removeBfile(oracleBfile);
    }

    @Override
    public PreparedStatement prepareStatement(String s, Properties properties) throws SQLException {
        return connection.prepareStatement(s, properties);
    }

    @Override
    public CallableStatement prepareCall(String s, Properties properties) throws SQLException {
        return connection.prepareCall(s, properties);
    }

    @Override
    public Properties getClientInfoInternal() throws SQLException {
        return connection.getClientInfoInternal();
    }

    @Override
    public boolean getAutoCommitInternal() throws SQLException {
        return connection.getAutoCommitInternal();
    }

    @Override
    public boolean serverSupportsRequestBoundaries() throws SQLException {
        return connection.serverSupportsRequestBoundaries();
    }

    @Override
    public boolean serverSupportsExplicitBoundaryBit() throws SQLException {
        return connection.serverSupportsExplicitBoundaryBit();
    }

    @Override
    public PreparedStatement prepareDirectPath(String s, String s1, String[] strings) throws SQLException {
        return connection.prepareDirectPath(s, s1, strings);
    }

    @Override
    public PreparedStatement prepareDirectPath(String s, String s1, String[] strings, Properties properties) throws SQLException {
        return connection.prepareDirectPath(s, s1, strings, properties);
    }

    @Override
    public PreparedStatement prepareDirectPath(String s, String s1, String[] strings, String s2) throws SQLException {
        return connection.prepareDirectPath(s, s1, strings, s2);
    }

    @Override
    public PreparedStatement prepareDirectPath(String s, String s1, String[] strings, String s2, Properties properties) throws SQLException {
        return connection.prepareDirectPath(s, s1, strings, s2, properties);
    }

    @Override
    public Properties getJavaNetProperties() throws SQLException {
        return connection.getJavaNetProperties();
    }

    @Override
    public double getPercentageQueryExecutionOnDirectShard() {
        return connection.getPercentageQueryExecutionOnDirectShard();
    }

    @Override
    public void addFeature(ClientFeature clientFeature) throws SQLException {
        connection.addFeature(clientFeature);
    }

    @Override
    public Executor createUserCodeExecutor() {
        return connection.createUserCodeExecutor();
    }

    @Override
    public void commit(EnumSet<CommitOption> enumSet) throws SQLException {
        connection.commit(enumSet);
    }

    @Override
    public void archive(int i, int i1, String s) throws SQLException {
        connection.archive(i, i1, s);
    }

    @Override
    public void openProxySession(int i, Properties properties) throws SQLException {
        connection.openProxySession(i, properties);
    }

    @Override
    public boolean getAutoClose() throws SQLException {
        return connection.getAutoClose();
    }

    @Override
    public int getDefaultExecuteBatch() {
        return connection.getDefaultExecuteBatch();
    }

    @Override
    public int getDefaultRowPrefetch() {
        return connection.getDefaultRowPrefetch();
    }

    @Override
    public Object getDescriptor(String s) {
        return connection.getDescriptor(s);
    }

    @Override
    public String[] getEndToEndMetrics() throws SQLException {
        return connection.getEndToEndMetrics();
    }

    @Override
    public short getEndToEndECIDSequenceNumber() throws SQLException {
        return connection.getEndToEndECIDSequenceNumber();
    }

    @Override
    public boolean getIncludeSynonyms() {
        return connection.getIncludeSynonyms();
    }

    @Override
    public boolean getRestrictGetTables() {
        return connection.getRestrictGetTables();
    }

    @Override
    public Object getJavaObject(String s) throws SQLException {
        return connection.getJavaObject(s);
    }

    @Override
    public boolean getRemarksReporting() {
        return connection.getRemarksReporting();
    }

    @Override
    public String getSQLType(Object o) throws SQLException {
        return connection.getSQLType(o);
    }

    @Override
    public int getStmtCacheSize() {
        return connection.getStmtCacheSize();
    }

    @Override
    public short getStructAttrCsId() throws SQLException {
        return connection.getStructAttrCsId();
    }

    @Override
    public String getUserName() throws SQLException {
        return connection.getUserName();
    }

    @Override
    public String getCurrentSchema() throws SQLException {
        return connection.getCurrentSchema();
    }

    @Override
    public boolean getUsingXAFlag() {
        return connection.getUsingXAFlag();
    }

    @Override
    public boolean getXAErrorFlag() {
        return connection.getXAErrorFlag();
    }

    @Override
    public int pingDatabase() throws SQLException {
        return connection.pingDatabase();
    }

    @Override
    public int pingDatabase(int i) throws SQLException {
        return connection.pingDatabase(i);
    }

    @Override
    public void putDescriptor(String s, Object o) throws SQLException {
        connection.putDescriptor(s, o);
    }

    @Override
    public void registerSQLType(String s, Class<?> aClass) throws SQLException {
        connection.registerSQLType(s, aClass);
    }

    @Override
    public void registerSQLType(String s, String s1) throws SQLException {
        connection.registerSQLType(s, s1);
    }

    @Override
    public void setAutoClose(boolean b) throws SQLException {
        connection.setAutoClose(b);
    }

    @Override
    public void setDefaultExecuteBatch(int i) throws SQLException {
        connection.setDefaultExecuteBatch(i);
    }

    @Override
    public void setDefaultRowPrefetch(int i) throws SQLException {
        connection.setDefaultRowPrefetch(i);
    }

    @Override
    public void setEndToEndMetrics(String[] strings, short i) throws SQLException {
        connection.setEndToEndMetrics(strings, i);
    }

    @Override
    public void setIncludeSynonyms(boolean b) {
        connection.setIncludeSynonyms(b);
    }

    @Override
    public void setRemarksReporting(boolean b) {
        connection.setRemarksReporting(b);
    }

    @Override
    public void setRestrictGetTables(boolean b) {
        connection.setRestrictGetTables(b);
    }

    @Override
    public void setStmtCacheSize(int i) throws SQLException {
        connection.setStmtCacheSize(i);
    }

    @Override
    public void setStmtCacheSize(int i, boolean b) throws SQLException {
        connection.setStmtCacheSize(i, b);
    }

    @Override
    public void setStatementCacheSize(int i) throws SQLException {
        connection.setStatementCacheSize(i);
    }

    @Override
    public int getStatementCacheSize() throws SQLException {
        return connection.getStatementCacheSize();
    }

    @Override
    public void setImplicitCachingEnabled(boolean b) throws SQLException {
        connection.setImplicitCachingEnabled(b);
    }

    @Override
    public boolean getImplicitCachingEnabled() throws SQLException {
        return connection.getImplicitCachingEnabled();
    }

    @Override
    public void setExplicitCachingEnabled(boolean b) throws SQLException {
        connection.setExplicitCachingEnabled(b);
    }

    @Override
    public boolean getExplicitCachingEnabled() throws SQLException {
        return connection.getExplicitCachingEnabled();
    }

    @Override
    public void purgeImplicitCache() throws SQLException {
        connection.purgeImplicitCache();
    }

    @Override
    public void purgeExplicitCache() throws SQLException {
        connection.purgeExplicitCache();
    }

    @Override
    public PreparedStatement getStatementWithKey(String s) throws SQLException {
        return connection.getStatementWithKey(s);
    }

    @Override
    public CallableStatement getCallWithKey(String s) throws SQLException {
        return connection.getCallWithKey(s);
    }

    @Override
    public void setUsingXAFlag(boolean b) {
        connection.setUsingXAFlag(b);
    }

    @Override
    public void setXAErrorFlag(boolean b) {
        connection.setXAErrorFlag(b);
    }

    @Override
    public void shutdown(DatabaseShutdownMode databaseShutdownMode) throws SQLException {
        connection.shutdown(databaseShutdownMode);
    }

    @Override
    public void startup(String s, int i) throws SQLException {
        connection.startup(s, i);
    }

    @Override
    public void startup(DatabaseStartupMode databaseStartupMode) throws SQLException {
        connection.startup(databaseStartupMode);
    }

    @Override
    public void startup(DatabaseStartupMode databaseStartupMode, String s) throws SQLException {
        connection.startup(databaseStartupMode, s);
    }

    @Override
    public PreparedStatement prepareStatementWithKey(String s) throws SQLException {
        return connection.prepareStatementWithKey(s);
    }

    @Override
    public CallableStatement prepareCallWithKey(String s) throws SQLException {
        return connection.prepareCallWithKey(s);
    }

    @Override
    public void setCreateStatementAsRefCursor(boolean b) {
        connection.setCreateStatementAsRefCursor(b);
    }

    @Override
    public boolean getCreateStatementAsRefCursor() {
        return connection.getCreateStatementAsRefCursor();
    }

    @Override
    public void setSessionTimeZone(String s) throws SQLException {
        connection.setSessionTimeZone(s);
    }

    @Override
    public String getSessionTimeZone() {
        return connection.getSessionTimeZone();
    }

    @Override
    public String getSessionTimeZoneOffset() throws SQLException {
        return connection.getSessionTimeZoneOffset();
    }

    @Override
    public Properties getProperties() {
        return connection.getProperties();
    }

    @Override
    public Connection _getPC() {
        return connection._getPC();
    }

    @Override
    public boolean isLogicalConnection() {
        return connection.isLogicalConnection();
    }

    @Override
    public void registerTAFCallback(OracleOCIFailover oracleOCIFailover, Object o) throws SQLException {
        connection.registerTAFCallback(oracleOCIFailover, o);
    }

    @Override
    public oracle.jdbc.OracleConnection unwrap() {
        return connection.unwrap();
    }

    @Override
    public void setWrapper(oracle.jdbc.OracleConnection oracleConnection) {
        connection.setWrapper(oracleConnection);
    }

    @Override
    public oracle.jdbc.internal.OracleConnection physicalConnectionWithin() {
        return connection.physicalConnectionWithin();
    }

    @Override
    public oracle.jdbc.OracleSavepoint oracleSetSavepoint() throws SQLException {
        return connection.oracleSetSavepoint();
    }

    @Override
    public oracle.jdbc.OracleSavepoint oracleSetSavepoint(String s) throws SQLException {
        return connection.oracleSetSavepoint(s);
    }

    @Override
    public void oracleRollback(oracle.jdbc.OracleSavepoint oracleSavepoint) throws SQLException {
        connection.oracleRollback(oracleSavepoint);
    }

    @Override
    public void oracleReleaseSavepoint(oracle.jdbc.OracleSavepoint oracleSavepoint) throws SQLException {
        connection.oracleReleaseSavepoint(oracleSavepoint);
    }

    @Override
    @Deprecated
    public void close(Properties properties) throws SQLException {
        connection.close(properties);
    }

    @Override
    public void close(int i) throws SQLException {
        connection.close(i);
    }

    @Override
    public boolean isProxySession() {
        return connection.isProxySession();
    }

    @Override
    @Deprecated
    public void applyConnectionAttributes(Properties properties) throws SQLException {
        connection.applyConnectionAttributes(properties);
    }

    @Override
    @Deprecated
    public Properties getConnectionAttributes() throws SQLException {
        return connection.getConnectionAttributes();
    }

    @Override
    @Deprecated
    public Properties getUnMatchedConnectionAttributes() throws SQLException {
        return connection.getUnMatchedConnectionAttributes();
    }

    @Override
    @Deprecated
    public void registerConnectionCacheCallback(OracleConnectionCacheCallback oracleConnectionCacheCallback, Object o, int i) throws SQLException {
        connection.registerConnectionCacheCallback(oracleConnectionCacheCallback, o, i);
    }

    @Override
    @Deprecated
    public void setConnectionReleasePriority(int i) throws SQLException {
        connection.setConnectionReleasePriority(i);
    }

    @Override
    @Deprecated
    public int getConnectionReleasePriority() throws SQLException {
        return connection.getConnectionReleasePriority();
    }

    @Override
    public void setPlsqlWarnings(String s) throws SQLException {
        connection.setPlsqlWarnings(s);
    }

    @Override
    public AQNotificationRegistration[] registerAQNotification(String[] strings, Properties[] properties, Properties properties1) throws SQLException {
        return connection.registerAQNotification(strings, properties, properties1);
    }

    @Override
    public void unregisterAQNotification(AQNotificationRegistration aqNotificationRegistration) throws SQLException {
        connection.unregisterAQNotification(aqNotificationRegistration);
    }

    @Override
    public AQMessage dequeue(String s, AQDequeueOptions aqDequeueOptions, byte[] bytes) throws SQLException {
        return connection.dequeue(s, aqDequeueOptions, bytes);
    }

    @Override
    public AQMessage dequeue(String s, AQDequeueOptions aqDequeueOptions, byte[] bytes, int i) throws SQLException {
        return connection.dequeue(s, aqDequeueOptions, bytes, i);
    }

    @Override
    public AQMessage dequeue(String s, AQDequeueOptions aqDequeueOptions, String s1) throws SQLException {
        return connection.dequeue(s, aqDequeueOptions, s1);
    }

    @Override
    public void enqueue(String s, AQEnqueueOptions aqEnqueueOptions, AQMessage aqMessage) throws SQLException {
        connection.enqueue(s, aqEnqueueOptions, aqMessage);
    }

    @Override
    public int enqueue(String s, AQEnqueueOptions aqEnqueueOptions, AQMessage[] aqMessages) throws SQLException {
        return connection.enqueue(s, aqEnqueueOptions, aqMessages);
    }

    @Override
    public AQMessage[] dequeue(String s, AQDequeueOptions aqDequeueOptions, String s1, int i) throws SQLException {
        return connection.dequeue(s, aqDequeueOptions, s1, i);
    }

    @Override
    public AQMessage[] dequeue(String s, AQDequeueOptions aqDequeueOptions, byte[] bytes, int i, int i1) throws SQLException {
        return connection.dequeue(s, aqDequeueOptions, bytes, i, i1);
    }

    @Override
    public DatabaseChangeRegistration registerDatabaseChangeNotification(Properties properties) throws SQLException {
        return connection.registerDatabaseChangeNotification(properties);
    }

    @Override
    public DatabaseChangeRegistration getDatabaseChangeRegistration(int i) throws SQLException {
        return connection.getDatabaseChangeRegistration(i);
    }

    @Override
    public void unregisterDatabaseChangeNotification(DatabaseChangeRegistration databaseChangeRegistration) throws SQLException {
        connection.unregisterDatabaseChangeNotification(databaseChangeRegistration);
    }

    @Override
    public void unregisterDatabaseChangeNotification(int i, String s, int i1) throws SQLException {
        connection.unregisterDatabaseChangeNotification(i, s, i1);
    }

    @Override
    public void unregisterDatabaseChangeNotification(int i) throws SQLException {
        connection.unregisterDatabaseChangeNotification(i);
    }

    @Override
    public void unregisterDatabaseChangeNotification(long l, String s) throws SQLException {
        connection.unregisterDatabaseChangeNotification(l, s);
    }

    @Override
    public ARRAY createARRAY(String s, Object o) throws SQLException {
        return connection.createARRAY(s, o);
    }

    @Override
    public Array createOracleArray(String s, Object o) throws SQLException {
        return connection.createOracleArray(s, o);
    }

    @Override
    public BINARY_DOUBLE createBINARY_DOUBLE(double v) throws SQLException {
        return connection.createBINARY_DOUBLE(v);
    }

    @Override
    public BINARY_FLOAT createBINARY_FLOAT(float v) throws SQLException {
        return connection.createBINARY_FLOAT(v);
    }

    @Override
    public DATE createDATE(Date date) throws SQLException {
        return connection.createDATE(date);
    }

    @Override
    public DATE createDATE(Time time) throws SQLException {
        return connection.createDATE(time);
    }

    @Override
    public DATE createDATE(Timestamp timestamp) throws SQLException {
        return connection.createDATE(timestamp);
    }

    @Override
    public DATE createDATE(Date date, Calendar calendar) throws SQLException {
        return connection.createDATE(date, calendar);
    }

    @Override
    public DATE createDATE(Time time, Calendar calendar) throws SQLException {
        return connection.createDATE(time, calendar);
    }

    @Override
    public DATE createDATE(Timestamp timestamp, Calendar calendar) throws SQLException {
        return connection.createDATE(timestamp, calendar);
    }

    @Override
    public DATE createDATE(String s) throws SQLException {
        return connection.createDATE(s);
    }

    @Override
    public INTERVALDS createINTERVALDS(String s) throws SQLException {
        return connection.createINTERVALDS(s);
    }

    @Override
    public INTERVALYM createINTERVALYM(String s) throws SQLException {
        return connection.createINTERVALYM(s);
    }

    @Override
    public NUMBER createNUMBER(boolean b) throws SQLException {
        return connection.createNUMBER(b);
    }

    @Override
    public NUMBER createNUMBER(byte b) throws SQLException {
        return connection.createNUMBER(b);
    }

    @Override
    public NUMBER createNUMBER(short i) throws SQLException {
        return connection.createNUMBER(i);
    }

    @Override
    public NUMBER createNUMBER(int i) throws SQLException {
        return connection.createNUMBER(i);
    }

    @Override
    public NUMBER createNUMBER(long l) throws SQLException {
        return connection.createNUMBER(l);
    }

    @Override
    public NUMBER createNUMBER(float v) throws SQLException {
        return connection.createNUMBER(v);
    }

    @Override
    public NUMBER createNUMBER(double v) throws SQLException {
        return connection.createNUMBER(v);
    }

    @Override
    public NUMBER createNUMBER(BigDecimal bigDecimal) throws SQLException {
        return connection.createNUMBER(bigDecimal);
    }

    @Override
    public NUMBER createNUMBER(BigInteger bigInteger) throws SQLException {
        return connection.createNUMBER(bigInteger);
    }

    @Override
    public NUMBER createNUMBER(String s, int i) throws SQLException {
        return connection.createNUMBER(s, i);
    }

    @Override
    public TIMESTAMP createTIMESTAMP(Date date) throws SQLException {
        return connection.createTIMESTAMP(date);
    }

    @Override
    public TIMESTAMP createTIMESTAMP(DATE date) throws SQLException {
        return connection.createTIMESTAMP(date);
    }

    @Override
    public TIMESTAMP createTIMESTAMP(Time time) throws SQLException {
        return connection.createTIMESTAMP(time);
    }

    @Override
    public TIMESTAMP createTIMESTAMP(Timestamp timestamp) throws SQLException {
        return connection.createTIMESTAMP(timestamp);
    }

    @Override
    public TIMESTAMP createTIMESTAMP(Timestamp timestamp, Calendar calendar) throws SQLException {
        return connection.createTIMESTAMP(timestamp, calendar);
    }

    @Override
    public TIMESTAMP createTIMESTAMP(String s) throws SQLException {
        return connection.createTIMESTAMP(s);
    }

    @Override
    public TIMESTAMPTZ createTIMESTAMPTZ(Date date) throws SQLException {
        return connection.createTIMESTAMPTZ(date);
    }

    @Override
    public TIMESTAMPTZ createTIMESTAMPTZ(Date date, Calendar calendar) throws SQLException {
        return connection.createTIMESTAMPTZ(date, calendar);
    }

    @Override
    public TIMESTAMPTZ createTIMESTAMPTZ(Time time) throws SQLException {
        return connection.createTIMESTAMPTZ(time);
    }

    @Override
    public TIMESTAMPTZ createTIMESTAMPTZ(Time time, Calendar calendar) throws SQLException {
        return connection.createTIMESTAMPTZ(time, calendar);
    }

    @Override
    public TIMESTAMPTZ createTIMESTAMPTZ(Timestamp timestamp) throws SQLException {
        return connection.createTIMESTAMPTZ(timestamp);
    }

    @Override
    public TIMESTAMPTZ createTIMESTAMPTZ(Timestamp timestamp, Calendar calendar) throws SQLException {
        return connection.createTIMESTAMPTZ(timestamp, calendar);
    }

    @Override
    public TIMESTAMPTZ createTIMESTAMPTZ(Timestamp timestamp, ZoneId zoneId) throws SQLException {
        return connection.createTIMESTAMPTZ(timestamp, zoneId);
    }

    @Override
    public TIMESTAMPTZ createTIMESTAMPTZ(String s) throws SQLException {
        return connection.createTIMESTAMPTZ(s);
    }

    @Override
    public TIMESTAMPTZ createTIMESTAMPTZ(String s, Calendar calendar) throws SQLException {
        return connection.createTIMESTAMPTZ(s, calendar);
    }

    @Override
    public TIMESTAMPTZ createTIMESTAMPTZ(DATE date) throws SQLException {
        return connection.createTIMESTAMPTZ(date);
    }

    @Override
    public TIMESTAMPLTZ createTIMESTAMPLTZ(Date date, Calendar calendar) throws SQLException {
        return connection.createTIMESTAMPLTZ(date, calendar);
    }

    @Override
    public TIMESTAMPLTZ createTIMESTAMPLTZ(Time time, Calendar calendar) throws SQLException {
        return connection.createTIMESTAMPLTZ(time, calendar);
    }

    @Override
    public TIMESTAMPLTZ createTIMESTAMPLTZ(Timestamp timestamp, Calendar calendar) throws SQLException {
        return connection.createTIMESTAMPLTZ(timestamp, calendar);
    }

    @Override
    public TIMESTAMPLTZ createTIMESTAMPLTZ(String s, Calendar calendar) throws SQLException {
        return connection.createTIMESTAMPLTZ(s, calendar);
    }

    @Override
    public TIMESTAMPLTZ createTIMESTAMPLTZ(DATE date, Calendar calendar) throws SQLException {
        return connection.createTIMESTAMPLTZ(date, calendar);
    }

    @Override
    public void cancel() throws SQLException {
        connection.cancel();
    }

    @Override
    public void abort() throws SQLException {
        connection.abort();
    }

    @Override
    public TypeDescriptor[] getAllTypeDescriptorsInCurrentSchema() throws SQLException {
        return connection.getAllTypeDescriptorsInCurrentSchema();
    }

    @Override
    public TypeDescriptor[] getTypeDescriptorsFromListInCurrentSchema(String[] strings) throws SQLException {
        return connection.getTypeDescriptorsFromListInCurrentSchema(strings);
    }

    @Override
    public TypeDescriptor[] getTypeDescriptorsFromList(String[][] strings) throws SQLException {
        return connection.getTypeDescriptorsFromList(strings);
    }

    @Override
    public String getDataIntegrityAlgorithmName() throws SQLException {
        return connection.getDataIntegrityAlgorithmName();
    }

    @Override
    public String getEncryptionAlgorithmName() throws SQLException {
        return connection.getEncryptionAlgorithmName();
    }

    @Override
    public String getAuthenticationAdaptorName() throws SQLException {
        return connection.getAuthenticationAdaptorName();
    }

    @Override
    public boolean isUsable() {
        return connection.isUsable();
    }

    @Override
    public void setDefaultTimeZone(TimeZone timeZone) throws SQLException {
        connection.setDefaultTimeZone(timeZone);
    }

    @Override
    public TimeZone getDefaultTimeZone() throws SQLException {
        return connection.getDefaultTimeZone();
    }

    @Override
    public void setApplicationContext(String s, String s1, String s2) throws SQLException {
        connection.setApplicationContext(s, s1, s2);
    }

    @Override
    public void clearAllApplicationContext(String s) throws SQLException {
        connection.clearAllApplicationContext(s);
    }

    @Override
    public void addLogicalTransactionIdEventListener(LogicalTransactionIdEventListener logicalTransactionIdEventListener) throws SQLException {
        connection.addLogicalTransactionIdEventListener(logicalTransactionIdEventListener);
    }

    @Override
    public void addLogicalTransactionIdEventListener(LogicalTransactionIdEventListener logicalTransactionIdEventListener, Executor executor) throws SQLException {
        connection.addLogicalTransactionIdEventListener(logicalTransactionIdEventListener, executor);
    }

    @Override
    public void removeLogicalTransactionIdEventListener(LogicalTransactionIdEventListener logicalTransactionIdEventListener) throws SQLException {
        connection.removeLogicalTransactionIdEventListener(logicalTransactionIdEventListener);
    }

    @Override
    public LogicalTransactionId getLogicalTransactionId() throws SQLException {
        return connection.getLogicalTransactionId();
    }

    @Override
    public boolean isDRCPEnabled() throws SQLException {
        return connection.isDRCPEnabled();
    }

    @Override
    public boolean isDRCPMultitagEnabled() throws SQLException {
        return connection.isDRCPMultitagEnabled();
    }

    @Override
    public String getDRCPReturnTag() throws SQLException {
        return connection.getDRCPReturnTag();
    }

    @Override
    public String getDRCPPLSQLCallbackName() throws SQLException {
        return connection.getDRCPPLSQLCallbackName();
    }

    @Override
    public boolean attachServerConnection() throws SQLException {
        return connection.attachServerConnection();
    }

    @Override
    public void detachServerConnection(String s) throws SQLException {
        connection.detachServerConnection(s);
    }

    @Override
    public boolean needToPurgeStatementCache() throws SQLException {
        return connection.needToPurgeStatementCache();
    }

    @Override
    public DRCPState getDRCPState() throws SQLException {
        return connection.getDRCPState();
    }

    @Override
    public void beginRequest() throws SQLException {
        connection.beginRequest();
    }

    @Override
    public void endRequest() throws SQLException {
        connection.endRequest();
    }

    @Override
    public boolean setShardingKeyIfValid(OracleShardingKey oracleShardingKey, OracleShardingKey oracleShardingKey1, int i) throws SQLException {
        return connection.setShardingKeyIfValid(oracleShardingKey, oracleShardingKey1, i);
    }

    @Override
    public void setShardingKey(OracleShardingKey oracleShardingKey, OracleShardingKey oracleShardingKey1) throws SQLException {
        connection.setShardingKey(oracleShardingKey, oracleShardingKey1);
    }

    @Override
    public boolean setShardingKeyIfValid(OracleShardingKey oracleShardingKey, int i) throws SQLException {
        return connection.setShardingKeyIfValid(oracleShardingKey, i);
    }

    @Override
    public void setShardingKey(OracleShardingKey oracleShardingKey) throws SQLException {
        connection.setShardingKey(oracleShardingKey);
    }

    @Override
    public boolean isValid(ConnectionValidation connectionValidation, int i) throws SQLException {
        return connection.isValid(connectionValidation, i);
    }

    @Override
    public String getEncryptionProviderName() throws SQLException {
        return connection.getEncryptionProviderName();
    }

    @Override
    public String getChecksumProviderName() throws SQLException {
        return connection.getChecksumProviderName();
    }

    @Override
    public String getNetConnectionId() throws SQLException {
        return connection.getNetConnectionId();
    }

    @Override
    public void disableLogging() throws SQLException {
        connection.disableLogging();
    }

    @Override
    public void enableLogging() throws SQLException {
        connection.enableLogging();
    }

    @Override
    public void dumpLog() throws SQLException {
        connection.dumpLog();
    }

    @Override
    public SecuredLogger getLogger() throws SQLException {
        return connection.getLogger();
    }

    @Override
    public boolean setShardingKeyIfValid(ShardingKey shardingKey, ShardingKey superShardingKey, int timeout) throws SQLException {
        return connection.setShardingKeyIfValid(shardingKey, superShardingKey, timeout);
    }

    @Override
    public boolean setShardingKeyIfValid(ShardingKey shardingKey, int timeout) throws SQLException {
        return connection.setShardingKeyIfValid(shardingKey, timeout);
    }

    @Override
    public void setShardingKey(ShardingKey shardingKey, ShardingKey superShardingKey) throws SQLException {
        connection.setShardingKey(shardingKey, superShardingKey);
    }

    @Override
    public void setShardingKey(ShardingKey shardingKey) throws SQLException {
        connection.setShardingKey(shardingKey);
    }

    @Override
    public void setACProxy(Object o) {
        connection.setACProxy(o);
    }

    @Override
    public Object getACProxy() {
        return connection.getACProxy();
    }

    @Override
    public CloseableLock getMonitorLock() {
        return connection.getMonitorLock();
    }

    @Override
    public CloseableLock newDefaultLock() {
        return connection.newDefaultLock();
    }

    @Override
    public CloseableLock acquireCloseableLock() {
        return connection.acquireCloseableLock();
    }

    @Override
    public void acquireLock() {
        connection.acquireLock();
    }

    @Override
    public void releaseLock() {
        connection.releaseLock();
    }

    public static Monitor newInstance() {
        return Monitor.newInstance();
    }
}
