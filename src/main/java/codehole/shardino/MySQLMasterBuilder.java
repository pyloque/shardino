package codehole.shardino;

public class MySQLMasterBuilder extends MySQLBuilder {

    @Override
    public String mode() {
        return "master";
    }

}
