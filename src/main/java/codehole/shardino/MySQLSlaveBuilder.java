package codehole.shardino;

public class MySQLSlaveBuilder extends MySQLBuilder {

    @Override
    public String mode() {
        return "slave";
    }

}
