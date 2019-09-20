package z.cloud.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import z.cloud.utils.EncryptUtils;
import z.cloud.utils.RedisUtils;
import z.cloud.dto.UserDto;
import z.cloud.entity.Message;
import z.cloud.entity.User;
import z.cloud.enums.MsgType;
import z.cloud.enums.UserType;
import z.cloud.service.IUserService;

import java.util.Date;

/**
 * @author zming
 * @version 1.0
 * @date 2019/9/18
 */
@Api(value = "用户接口")
@RestController
@RequestMapping("/sso")
public class SSOController {

    private Logger log = LogManager.getLogger(SSOController.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisUtils redisUtils;

    /**
     *  注册
     */
    @ApiOperation(value = "用户注册")
    @RequestMapping(value = "/register",method = {RequestMethod.POST})
    public Message register(String phone, String pwd, String validateCode){

        //判断用户名称是否已经被注册
        User user1 = userService.findByName(phone);
        if(user1 != null){
            return Message.error("用户已存在");
        }

        //判断验证码
        String key = MsgType.REGISTER.toString() + "_" + user1.getId() + "_" + validateCode;
        Object code = redisUtils.get(key);
        if(code == null){
            return Message.error("请发送短信,接收验证码注册");
        }
        if(!key.equals(code)){
            return Message.error("验证码错误");
        }

        User user = new User();
        user.setName(phone);

        //给password加密,颜值为当前的电话号码
        String pwdKey = phone;
        String password = EncryptUtils.getSaltMD5(pwd,pwdKey);
        log.info("用户加密后的密码为：[{}]",password);

        user.setPwdKey(pwdKey);
        user.setPassword(password);
        user.setCreateDate(new Date());
        userService.save(user);

        return Message.success("注册成功");
    }

    /**
     *  app用户登录
     * @return
     */
    @ApiOperation(value = "app用户登录",response = UserDto.class)
    @RequestMapping(value = "/appLogin",method = RequestMethod.POST)
    public Message appLogin(String name,String pwd){
        User user = userService.findByName(name);
        if(user == null){
            return Message.error("账号不存在");
        }
        if(user.getUserType() != null && user.getUserType() == UserType.web){
            return Message.error("管理员类型不能登录app");
        }

        //加密
        String password = EncryptUtils.getSaltMD5(pwd, user.getPwdKey());

        //String password;
        if(!user.getPassword().equals(password)){
            return Message.error("密码输入错误");
        }
        UserDto dto = user.toDto();
        user.setLastLoginDate(new Date());
        userService.save(user);
        return Message.success(dto);
    }

    /**
     *  验证码登录
     * @return
     */
    @ApiOperation(value = "验证码登录",response = UserDto.class)
    @RequestMapping(value = "/validateLogin",method = RequestMethod.POST)
    public Message validateLogin(String phone,String validateCode){
        User user = userService.findByName(phone);
        if(user == null){
            return Message.error("账号不存在");
        }

        //判断验证码
        String key = MsgType.LOGIN.toString() + "_" + user.getId() + "_" + validateCode;
        Object code = redisUtils.get(key);
        if(code == null){
            return Message.error("请发送短信,接收验证码注册");
        }
        if(!key.equals(code.toString())){
            return Message.error("验证码错误");
        }

        if(user.getUserType() != null && user.getUserType() == UserType.web){
            return Message.error("管理员类型不能登录app");
        }
        UserDto dto = user.toDto();
        user.setLastLoginDate(new Date());
        userService.save(user);
        return Message.success(dto);
    }

    /**
     *  web用户登录
     * @return
     */
    @ApiOperation(value = "web用户登录",response = UserDto.class)
    @RequestMapping(value = "/webLogin",method = RequestMethod.POST)
    public Message webLogin(String name,String pwd){
        User user = userService.findByName(name);
        if(user == null){
            return Message.error("账号不存在");
        }
        if(user.getUserType() != null && user.getUserType() == UserType.app){
            return Message.error("客户端类型不能登录web");
        }
        //加密
        String password = EncryptUtils.getSaltMD5(pwd,user.getPwdKey());
        if(!user.getPassword().equals(password)){
            return Message.error("密码输入错误");
        }
        UserDto dto = user.toDto();
        user.setLastLoginDate(new Date());
        userService.save(user);
        return Message.success(dto);
    }


}
