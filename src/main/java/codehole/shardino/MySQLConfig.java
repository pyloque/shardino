package codehole.shardino;

import java.util.ArrayList;
import java.util.List;

public class MySQLConfig {

    public final static String DEFAULT_DB = "test";
    public final static String DEFAULT_USER = "root";
    public final static String DEFAULT_PASSWD = "";
    public final static int DEFAULT_POOL_SIZE = 16;

    private List<WeightedAddr> addrWeights = new ArrayList<WeightedAddr>();

    private String db = "test";
    private String user = DEFAULT_USER;
    private String passwd = DEFAULT_PASSWD;
    private int poolSize = DEFAULT_POOL_SIZE;

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public List<WeightedAddr> getAddrWeights() {
        return addrWeights;
    }

    public void setAddrWeights(List<WeightedAddr> addrWeights) {
        this.addrWeights = addrWeights;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

}
