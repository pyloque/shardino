package codehole.shardino.sample;

import java.util.Date;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PostMySQLTest extends AppTestBase {

    @Autowired
    private PostMySQL pm;

    @Before
    public void setUp() {
        pm.dropTables();
        pm.createTables();
    }

    private static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
        }
    }

    @Test
    public void savePost() throws Exception {
        Post p = pm.getPostFromMaster("2222", "1111");
        Assertions.assertThat(p).isNull();
        pm.savePost(new Post("1111", "2222", "test-title", "test-content", new Date()));
        p = pm.getPostFromMaster("2222", "1111");
        Assertions.assertThat(p).isNotNull();
        Assertions.assertThat(p.getTitle()).isEqualTo("test-title");
        // waiting for replication
        sleep(2);
        p = pm.getPostFromSlave("2222", "1111");
        Assertions.assertThat(p).isNotNull();
        Assertions.assertThat(p.getTitle()).isEqualTo("test-title");
    }

    @Test
    public void saveMultiPost() throws Exception {
        for (int i = 0; i <= 1000; i++) {
            int j = i / 10;
            Post p = pm.getPostFromMaster("" + j, "" + i);
            Assertions.assertThat(p).isNull();
            pm.savePost(new Post("" + i, "" + j, "test-title", "test-content", new Date()));
            p = pm.getPostFromMaster("" + j, "" + i);
            Assertions.assertThat(p).isNotNull();
            Assertions.assertThat(p.getTitle()).isEqualTo("test-title");
        }
        sleep(2);
        for (int i = 0; i <= 1000; i++) {
            int j = i / 10;
            Post p = pm.getPostFromSlave("" + j, "" + i);
            Assertions.assertThat(p).isNotNull();
            Assertions.assertThat(p.getTitle()).isEqualTo("test-title");
        }
    }

}
