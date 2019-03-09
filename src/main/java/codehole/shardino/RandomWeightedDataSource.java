package codehole.shardino;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.ibatis.datasource.pooled.PooledDataSource;

public class RandomWeightedDataSource extends DataSourceAdapter {

    private int totalWeight;
    private Set<PooledDataSource> sources;
    private Map<Integer, PooledDataSource> sourceMap;

    public RandomWeightedDataSource(Map<PooledDataSource, Integer> srcs) {
        this.sources = new HashSet<>();
        this.sourceMap = new HashMap<>();
        for (Entry<PooledDataSource, Integer> entry : srcs.entrySet()) {
            // 权重值不宜过大
            int weight = Math.min(10000, entry.getValue());
            for (int i = 0; i < weight; i++) {
                sourceMap.put(totalWeight, entry.getKey());
                totalWeight++;
            }
            this.sources.add(entry.getKey());
        }
    }

    private PooledDataSource getDataSource() {
        return this.sourceMap.get(ThreadLocalRandom.current().nextInt(totalWeight));
    }

    public void close() {
        for (PooledDataSource ds : sources) {
            ds.forceCloseAll();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getDataSource().getConnection(username, password);
    }

}
