package z.cloud.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import z.cloud.utils.RedisUtils;
import z.cloud.entity.User;
import z.cloud.enums.MsgType;
import z.cloud.thread.MessageThread;

import java.util.concurrent.TimeUnit;

/**
 * @author zming
 * @version 1.0
 * @date 2019/9/19
 */
@Component
public class MessageServiceImpl implements IMessageService {

    private Logger log = LogManager.getLogger(MessageServiceImpl.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void sendMsg(String phone,MsgType type) {
        User user = userService.findByName(phone);
        if(user != null){
            //接收  发送短信返回的成功或者失败消息
            int code = 999999; //(int) ((Math.random() * 9 + 1) * 100000);
            //开启新线程
            new MessageThread(phone,code).start();

            String key = type.toString() + "_" + user.getId() + "_" + code;
            redisUtils.set(key,code+"",0, TimeUnit.SECONDS);
            log.info("发送短信储存的key为：[{}]",key);
            log.info("发送的验证码为：[{}]",code);
            log.info("发送短信的类型为：[{}]",type);
        }
    }

}
