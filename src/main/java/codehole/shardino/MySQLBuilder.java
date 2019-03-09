package codehole.shardino;

import org.springframework.core.env.Environment;

public abstract class MySQLBuilder {

    private MySQLConfig readConfig(Environment env, String configPrefix) {
        MySQLConfig config = new MySQLConfig();
        String addrsProp = String.format("mysql.%s.%s.addrWeights", configPrefix, mode());
        String addrsWeightRaw = env.getRequiredProperty(addrsProp);
        config.setAddrWeights(WeightedAddr.parse(addrsWeightRaw));
        String dbProp = String.format("mysql.%s.%s.db", configPrefix, mode());
        config.setDb(env.getProperty(dbProp, MySQLConfig.DEFAULT_DB));
        String userProp = String.format("mysql.%s.%s.user", configPrefix, mode());
        config.setUser(env.getProperty(userProp, MySQLConfig.DEFAULT_USER));
        String passwdProp = String.format("mysql.%s.%s.password", configPrefix, mode());
        config.setPasswd(env.getProperty(passwdProp, MySQLConfig.DEFAULT_PASSWD));
        String poolSizeProp = String.format("mysql.%s.%s.poolSize", configPrefix, mode());
        config.setPoolSize(env.getProperty(poolSizeProp, Integer.class,
                        MySQLConfig.DEFAULT_POOL_SIZE));
        return config;
    }

    public abstract String mode();

    public MySQLStore buildStore(Environment env, String configPrefix) {
        MySQLConfig config = this.readConfig(env, configPrefix);
        MySQLStore store = new MySQLStore(config);
        return store;
    }

}
