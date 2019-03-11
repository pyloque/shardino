package codehole.shardino;

import java.util.Properties;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Intercepts({
        @Signature(method = "query", type = Executor.class,
                        args = {MappedStatement.class, Object.class, RowBounds.class,
                                ResultHandler.class}),
        @Signature(method = "update", type = Executor.class,
                        args = {MappedStatement.class, Object.class})})
public class DebugSQLInterceptor implements Interceptor {
    private final static Logger LOG = LoggerFactory.getLogger(DebugSQLInterceptor.class);

    private boolean showSQL;

    public DebugSQLInterceptor(boolean showSQL) {
        this.showSQL = showSQL;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (showSQL) {
            Object[] args = invocation.getArgs();
            MappedStatement stmt = (MappedStatement) args[0];
            if (args.length > 2) {
                RowBounds bounds = (RowBounds) args[2];
                LOG.info("SQL:{}; Using:{}; Offset:{}; Limit:{}",
                                stmt.getBoundSql(args[1]).getSql(), args[1], bounds.getOffset(),
                                bounds.getLimit());
            } else {
                LOG.info("SQL:{}; Using:{}", stmt.getBoundSql(args[1]).getSql(), args[1]);
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
