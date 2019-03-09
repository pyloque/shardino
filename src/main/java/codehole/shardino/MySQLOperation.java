package codehole.shardino;

import java.sql.SQLException;

@FunctionalInterface
public interface MySQLOperation<T> {

    void accept(T t) throws SQLException;

}
