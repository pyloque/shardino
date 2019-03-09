Principles
--
Explicit is better than Implicit!


```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import codehole.shardino.Holder;
import codehole.shardino.MySQLGroupStore;

@Repository
public class PostMySQL {

    @Autowired
    private PartitionConfig partitions;

    @Autowired
    @Qualifier("post")
    private MySQLGroupStore mysql;

    public void createTables() {
        for (int i = 0; i < partitions.post(); i++) {
            int k = i;
            mysql.master(k).execute(session -> {
                PostMapper mapper = session.getMapper(PostMapper.class);
                mapper.createTable(k);
            });
        }
    }

    public void dropTables() {
        for (int i = 0; i < partitions.post(); i++) {
            int k = i;
            mysql.master(k).execute(session -> {
                PostMapper mapper = session.getMapper(PostMapper.class);
                mapper.dropTable(k);
            });
        }
    }

    public Post getPostFromMaster(String userId, String id) {
        Holder<Post> holder = new Holder<>();
        int partition = this.partitionFor(userId);
        mysql.master(partition).execute(session -> {
            PostMapper mapper = session.getMapper(PostMapper.class);
            holder.value(mapper.getPost(partition, id));
        });
        return holder.value();
    }

    public Post getPostFromSlave(String userId, String id) {
        Holder<Post> holder = new Holder<>();
        int partition = this.partitionFor(userId);
        mysql.slave(partition).execute(session -> {
            PostMapper mapper = session.getMapper(PostMapper.class);
            holder.value(mapper.getPost(partition, id));
        });
        return holder.value();
    }

    public void savePost(Post post) {
        int partition = this.partitionFor(post);
        mysql.master(partition).execute(session -> {
            PostMapper mapper = session.getMapper(PostMapper.class);
            Post curPost = mapper.getPost(partition, post.getId());
            if (curPost != null) {
                mapper.updatePost(partition, post);
            } else {
                mapper.insertPost(partition, post);
            }
        });
    }

    public void deletePost(String userId, String id) {
        int partition = this.partitionFor(userId);
        mysql.master(partition).execute(session -> {
            PostMapper mapper = session.getMapper(PostMapper.class);
            mapper.deletePost(partition, id);
        });
    }

    private int partitionFor(Post post) {
        return Post.partitionFor(post.getUserId(), partitions.post());
    }

    private int partitionFor(String userId) {
        return Post.partitionFor(userId, partitions.post());
    }

}
```
