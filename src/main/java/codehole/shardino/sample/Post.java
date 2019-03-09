package codehole.shardino.sample;

import java.util.Date;
import java.util.zip.CRC32;

public class Post {
    private String id;

    private String userId;

    private String title;

    private String content;

    private Date createTime;

    public Post() {}

    public Post(String id, String userId, String title, String content, Date createTime) {
        super();
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int partitionFor(int num) {
        return partitionFor(userId, num);
    }

    public static int partitionFor(String userId, int num) {
        CRC32 crc = new CRC32();
        crc.update(userId.getBytes(Charsets.UTF8));
        return (int) (Math.abs(crc.getValue()) % num);
    }

}
