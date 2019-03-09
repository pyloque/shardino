package codehole.shardino.sample;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface PostMapper {

    @Update("create table if not exists post_#{partition}(id varchar(128) primary key not null, user_id varchar(1024) not null, title varchar(1024) not null, content text, create_time timestamp not null) engine=innodb")
    public void createTable(int partition);

    @Update("drop table if exists post_#{partition}")
    public void dropTable(int partition);

    @Results({@Result(property = "createTime", column = "create_time"),
            @Result(property = "userId", column = "user_id")})
    @Select("select id, user_id, title, content, create_time from post_#{partition} where id=#{id}")
    public Post getPost(@Param("partition") int partition, @Param("id") String id);

    @Insert("insert into post_#{partition}(id, user_id, title, content, create_time) values(#{p.id}, ${p.userId}, #{p.title}, #{p.content}, #{p.createTime})")
    public void insertPost(@Param("partition") int partition, @Param("p") Post post);

    @Update("update post_#{partition} set title=#{p.title}, content=#{p.content}, create_time=#{p.createTime} where id=#{p.id}")
    public void updatePost(@Param("partition") int partition, @Param("p") Post post);

    @Delete("delete from post_#{partition} where id=#{id}")
    public void deletePost(@Param("partition") int partition, @Param("id") String id);

}
