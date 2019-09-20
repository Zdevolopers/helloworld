package z.cloud.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import z.cloud.entity.Message;
import z.cloud.enums.MsgType;
import z.cloud.service.IMessageService;

/**
 * @author zming
 * @version 1.0
 * @date 2019/9/19
 */
@Api(value = "短信接口")
@RestController
@RequestMapping("/user")
public class MessageController {

    @Autowired
    private IMessageService messageService;

    @ApiOperation("发送短信")
    @PostMapping("/sendMsg")
    public Message sendMsg(String phone, MsgType type){
        messageService.sendMsg(phone, type);
        return Message.success();
    }

}
