package codehole.shardino.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import codehole.shardino.DebugSQLInterceptor;
import codehole.shardino.MySQLGroupBuilder;
import codehole.shardino.MySQLGroupStore;

@Configuration
public class RepoConfig {

    @Autowired
    private Environment env;

    private MySQLGroupBuilder mysqlGroupBuilder = new MySQLGroupBuilder();

    @Bean
    @Qualifier("post")
    public MySQLGroupStore replyMySQLGroupStore() {
        MySQLGroupStore store = mysqlGroupBuilder.buildStore(env, "post");
        store.prepare(factory -> {
            factory.getConfiguration().addMapper(PostMapper.class);
            factory.getConfiguration().addInterceptor(new DebugSQLInterceptor(true));
        });
        return store;
    }

}
