package codehole.shardino.sample;

import org.springframework.context.annotation.Configuration;

@Configuration
public class PartitionConfig {

    private int post = 64;

    public int post() {
        return post;
    }

    public void post(int post) {
        this.post = post;
    }

}
