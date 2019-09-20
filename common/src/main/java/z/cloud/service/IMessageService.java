package z.cloud.service;

import org.springframework.stereotype.Service;
import z.cloud.enums.MsgType;

@Service
public interface IMessageService {

    void sendMsg(String phone, MsgType type);

}
