package z.cloud.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zming
 * @version 1.0
 * @date 2019/9/18
 */
@Data
public class Message<T> implements Serializable {

    /**
     *  返回的状态码
     */
    private Integer code;

    /**
     *  响应的信息
     */
    private String msg;

    /**
     *  响应的数据
     */
    private T data;

    Message(){}

    public Message(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Message success(){
        return new Message(0,"操作成功",null);
    }

    public static <T> Message success(T data){
        return new Message(0,"操作成功",data);
    }

    public static <T> Message error(){
        return new Message(1,"操作失败",null);
    }

    public static <T> Message error(String msg){
        return new Message(1,msg,null);
    }

}
