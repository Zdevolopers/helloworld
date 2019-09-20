package z.cloud.thread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import z.cloud.utils.HuyiUtils;

/**
 * @author zming
 * @version 1.0
 * @date 2019/9/19
 */
@Component
public class MessageThread extends Thread {

    private Logger log = LogManager.getLogger(MessageThread.class);

    @Autowired
    private HuyiUtils msgUtils;

    private String phone;
    private int code;

    public MessageThread(){ }

    public MessageThread(String phone, int code){
        this.phone = phone;
        this.code = code;
    }

    @Override
    public void run() {
        /* 开启新线程发送短信 */
        String message = msgUtils.sendMsg(phone,code);
        log.info("给[{}]发送短信返回的消息为[{}]",phone,message);
    }

}
