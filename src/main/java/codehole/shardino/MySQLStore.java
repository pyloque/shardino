package codehole.shardino;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.sql.DataSource;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySQLStore {

    private final static Logger LOG = LoggerFactory.getLogger(MySQLStore.class);

    private MySQLConfig config;
    private RandomWeightedDataSource ds;
    private SqlSessionFactory factory;

    private List<Consumer<SqlSessionFactory>> prepareCallbacks = new ArrayList<>();

    public MySQLStore(MySQLConfig config) {
        this.config = config;
        this.ds = buildDataSource(config);
        this.factory = this.buildFactory(ds);
    }

    private static RandomWeightedDataSource buildDataSource(MySQLConfig config) {
        Map<PooledDataSource, Integer> sources = new HashMap<>();
        for (WeightedAddr aw : config.getAddrWeights()) {
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = String.format(
                            "jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=UTF8",
                            aw.getHost(), aw.getPort(), config.getDb());
            PooledDataSource ds =
                            new PooledDataSource(driver, url, config.getUser(), config.getPasswd());
            ds.setPoolMaximumActiveConnections(config.getPoolSize());
            ds.setPoolPingEnabled(true);
            ds.setPoolPingQuery("select 1");
            ds.setPoolPingConnectionsNotUsedFor(10000);
            sources.put(ds, aw.getWeight());
        }
        return new RandomWeightedDataSource(sources);
    }

    private SqlSessionFactory buildFactory(DataSource ds) {
        TransactionFactory trxFactory = new JdbcTransactionFactory();
        Environment env = new Environment("wtf", trxFactory, ds);
        Configuration c = new Configuration(env);
        c.setCacheEnabled(false);
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(c);
        for (Consumer<SqlSessionFactory> prepare : this.prepareCallbacks) {
            prepare.accept(factory);
        }
        return factory;
    }

    public void prepare(Consumer<SqlSessionFactory> prepareCallback) {
        this.prepareCallbacks.add(prepareCallback);
        prepareCallback.accept(factory);
    }

    public MySQLConfig getConfig() {
        return this.config;
    }

    public void execute(MySQLOperation<SqlSession> consumer) {
        this.execute(consumer, true);
    }

    public <T> void executeWithMapper(Class<T> mapperClass, MySQLOperation<T> consumer) {
        this.executeWithMapper(mapperClass, consumer, true);
    }

    public <T> void executeWithMapper(Class<T> mapperClass, MySQLOperation<T> consumer,
                    boolean autocommit) {
        this.execute(session -> {
            T mapper = session.getMapper(mapperClass);
            consumer.accept(mapper);
        }, autocommit);
    }

    public void execute(MySQLOperation<SqlSession> consumer, boolean autocommit) {
        SqlSession session;
        try {
            session = factory.openSession(autocommit);
        } catch (Exception e) {
            LOG.error("connect mysql error", e);
            throw new RuntimeException("connect mysql error", e);
        }
        try {
            consumer.accept(session);
        } catch (SQLException e) {
            if (!autocommit)
                session.rollback();
            LOG.error("access mysql error", e);
            throw new RuntimeException("access mysql error", e);
        } finally {
            session.close();
        }
    }

    public void close() {
        this.ds.close();
    }

}
