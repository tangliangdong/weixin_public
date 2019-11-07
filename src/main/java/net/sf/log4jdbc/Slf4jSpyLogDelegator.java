package net.sf.log4jdbc;

/**
 * Created by Administrator on 2017/12/7.
 */

import net.sf.log4jdbc.DriverSpy;
import net.sf.log4jdbc.ResultSetCollector;
import net.sf.log4jdbc.Spy;
import net.sf.log4jdbc.SpyLogDelegator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

public class Slf4jSpyLogDelegator implements SpyLogDelegator {
    private final Logger jdbcLogger = LoggerFactory.getLogger("jdbc.audit");
    private final Logger resultSetLogger = LoggerFactory.getLogger("jdbc.resultset");
    private final Logger resultSetTableLogger = LoggerFactory.getLogger("jdbc.resultsettable");
    private final Logger sqlOnlyLogger = LoggerFactory.getLogger("jdbc.sqlonly");
    private final Logger sqlTimingLogger = LoggerFactory.getLogger("jdbc.sqltiming");
    private final Logger connectionLogger = LoggerFactory.getLogger("jdbc.connection");
    private final Logger debugLogger = LoggerFactory.getLogger("log4jdbc.debug");
    private static String nl = System.getProperty("line.separator");

    public Slf4jSpyLogDelegator() {
    }

    public Logger getSqlOnlyLogger() {
        return this.sqlOnlyLogger;
    }

    public boolean isJdbcLoggingEnabled() {
        return this.jdbcLogger.isErrorEnabled() || this.resultSetLogger.isErrorEnabled() || this.sqlOnlyLogger.isErrorEnabled() || this.sqlTimingLogger.isErrorEnabled() || this.connectionLogger.isErrorEnabled() || this.resultSetTableLogger.isErrorEnabled();
    }

    @Override
    public void exceptionOccured(Spy spy, String s, Exception e, String s1, long l) {

    }

    @Override
    public void methodReturned(Spy spy, String s, Object o, Object o1, Object... objects) {

    }

    @Override
    public void constructorReturned(Spy spy, String s) {

    }

    @Override
    public String sqlOccured(Spy spy, String s, String s1) {
        return null;
    }

    @Override
    public String sqlOccured(Spy spy, String s, String[] strings) {
        return null;
    }

    public void sqlTimingOccured(Spy spy, long execTime, String methodCall, String sql) {
        if(this.sqlTimingLogger.isErrorEnabled() && (!DriverSpy.DumpSqlFilteringOn || this.shouldSqlBeLogged(sql))) {
            if(DriverSpy.SqlTimingErrorThresholdEnabled && execTime >= DriverSpy.SqlTimingErrorThresholdMsec) {
                this.sqlTimingLogger.error(this.buildSqlTimingDump(spy, execTime, methodCall, sql, true));
            } else if(this.sqlTimingLogger.isWarnEnabled()) {
                if(DriverSpy.SqlTimingWarnThresholdEnabled && execTime >= DriverSpy.SqlTimingWarnThresholdMsec) {
                    this.sqlTimingLogger.warn(this.buildSqlTimingDump(spy, execTime, methodCall, sql, true));
                } else if(this.sqlTimingLogger.isDebugEnabled()) {
                    this.sqlTimingLogger.debug(this.buildSqlTimingDump(spy, execTime, methodCall, sql, true));
                } else if(this.sqlTimingLogger.isInfoEnabled()) {
                    this.sqlTimingLogger.info(this.buildSqlTimingDump(spy, execTime, methodCall, sql, false));
                }
            }
        }

    }

    @Override
    public void connectionOpened(Spy spy) {

    }

    @Override
    public void connectionClosed(Spy spy) {

    }

    @Override
    public void debug(String s) {

    }

    @Override
    public boolean isResultSetCollectionEnabled() {
        return false;
    }

    @Override
    public boolean isResultSetCollectionEnabledWithUnreadValueFillIn() {
        return false;
    }

    @Override
    public void resultSetCollected(ResultSetCollector resultSetCollector) {

    }

    private boolean shouldSqlBeLogged(String sql) {
        if(sql == null) {
            return false;
        } else {
            sql = sql.trim();
            if(sql.length() < 6) {
                return false;
            } else {
                sql = sql.substring(0, 6).toLowerCase();
                return DriverSpy.DumpSqlSelect && "select".equals(sql) || DriverSpy.DumpSqlInsert && "insert".equals(sql) || DriverSpy.DumpSqlUpdate && "update".equals(sql) || DriverSpy.DumpSqlDelete && "delete".equals(sql) || DriverSpy.DumpSqlCreate && "create".equals(sql);
            }
        }
    }
    private String buildSqlTimingDump(Spy spy, long execTime, String methodCall, String sql, boolean debugInfo) {
        StringBuffer out = new StringBuffer();
        if(debugInfo) {
            out.append(getDebugInfo());
            out.append(nl);
            out.append(spy.getConnectionNumber());
            out.append(". ");
        }

        sql = this.processSql(sql);
        out.append(sql);
        out.append(" {executed in ");
        out.append(execTime);
        out.append(" msec}");
        return out.toString();
    }
    private static String getDebugInfo() {
        Throwable t = new Throwable();
        t.fillInStackTrace();
        StackTraceElement[] stackTrace = t.getStackTrace();
        if(stackTrace == null) {
            return null;
        } else {
            StringBuffer dump = new StringBuffer();
            String className;
            int lastApplicationCall;
            if(DriverSpy.DumpFullDebugStackTrace) {
                boolean firstLog4jdbcCall = true;

                for(lastApplicationCall = 0; lastApplicationCall < stackTrace.length; ++lastApplicationCall) {
                    className = stackTrace[lastApplicationCall].getClassName();
                    if(!className.startsWith("net.sf.log4jdbc")) {
                        if(firstLog4jdbcCall) {
                            firstLog4jdbcCall = false;
                        } else {
                            dump.append("  ");
                        }

                        dump.append("at ");
                        dump.append(stackTrace[lastApplicationCall]);
                        dump.append(nl);
                    }
                }
            } else {
                dump.append(" ");
                int var7 = 0;
                lastApplicationCall = 0;

                int j;
                for(j = 0; j < stackTrace.length; ++j) {
                    className = stackTrace[j].getClassName();
                    if(className.startsWith("net.sf.log4jdbc")) {
                        var7 = j;
                    } else if(DriverSpy.TraceFromApplication && className.startsWith(DriverSpy.DebugStackPrefix)) {
                        lastApplicationCall = j;
                        break;
                    }
                }

                j = lastApplicationCall;
                if(lastApplicationCall == 0) {
                    j = 1 + var7;
                }

                dump.append(stackTrace[j].getClassName()).append(".").append(stackTrace[j].getMethodName()).append("(").append(stackTrace[j].getFileName()).append(":").append(stackTrace[j].getLineNumber()).append(")");
            }

            return dump.toString();
        }
    }
    private String processSql(String sql) {
        if(sql == null) {
            return null;
        } else {
            if(DriverSpy.TrimSql) {
                sql = sql.trim();
            }

            StringBuffer output = new StringBuffer();
            if(DriverSpy.DumpSqlMaxLineLength <= 0) {
                output.append(sql);
            } else {
                StringTokenizer st = new StringTokenizer(sql);
                int linelength = 0;

                while(st.hasMoreElements()) {
                    String token = (String)st.nextElement();
                    output.append(token);
                    linelength += token.length();
                    output.append(" ");
                    ++linelength;
                    if(linelength > DriverSpy.DumpSqlMaxLineLength) {
                        output.append("\n");
                        linelength = 0;
                    }
                }
            }

            if(DriverSpy.DumpSqlAddSemicolon) {
                output.append(";");
            }

            return output.toString();
        }
    }
}
