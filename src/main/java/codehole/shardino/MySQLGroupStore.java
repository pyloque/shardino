package codehole.shardino;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.apache.ibatis.session.SqlSessionFactory;

public class MySQLGroupStore {

    static class Pair {
        MySQLStore master;
        MySQLStore slave;

        public Pair(MySQLStore master, MySQLStore slave) {
            this.master = master;
            this.slave = slave;
        }
    }

    private List<Pair> pairs = new ArrayList<Pair>();

    public MySQLGroupStore append(MySQLStore master, MySQLStore slave) {
        this.pairs.add(new Pair(master, slave));
        return this;
    }

    public MySQLStore master(int partition) {
        return pairs.get(partition % pairs.size()).master;
    }

    public MySQLStore slave(int partition) {
        return pairs.get(partition % pairs.size()).slave;
    }

    public MySQLStore master() {
        return pairs.get(0).master;
    }

    public MySQLStore slave() {
        return pairs.get(0).slave;
    }

    public MySQLStore db() {
        return master();
    }

    public void prepare(Consumer<SqlSessionFactory> consumer) {
        for (Pair pair : pairs) {
            pair.master.prepare(consumer);
            if (pair.slave != null)
                pair.slave.prepare(consumer);
        }
    }

}
