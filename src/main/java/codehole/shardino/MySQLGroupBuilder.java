package codehole.shardino;

import org.springframework.core.env.Environment;

public class MySQLGroupBuilder {

    private MySQLMasterBuilder masterBuilder = new MySQLMasterBuilder();
    private MySQLSlaveBuilder slaveBuilder = new MySQLSlaveBuilder();

    public MySQLGroupStore buildStore(Environment env, String configPrefix) {
        String nodesProp = String.format("mysqlgroup.%s.nodes", configPrefix);
        String[] nodes = env.getProperty(nodesProp, String[].class);
        String slaveEnabledProp = String.format("mysqlgroup.%s.slaveEnabled", configPrefix);
        boolean slaveEnabled = env.getProperty(slaveEnabledProp, Boolean.class, false);
        MySQLGroupStore store = new MySQLGroupStore();
        for (String node : nodes) {
            MySQLStore master = masterBuilder.buildStore(env, node);
            MySQLStore slave = null;
            if (slaveEnabled) {
                slave = slaveBuilder.buildStore(env, node);
            }
            store.append(master, slave);
        }
        return store;
    }

}
