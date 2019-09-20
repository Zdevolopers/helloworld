package z.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import z.cloud.dto.UserDto;
import z.cloud.enums.UserStatus;
import z.cloud.enums.UserType;

import java.util.Date;

/**
 * @author zming
 * @version 1.0
 * @date 2019/9/18
 */
@Data
@TableName(value = "zc_user")
public class User {

    /**
     *  id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *  用户名(电话号码作为用户名称，并且唯一)
     */
    private String name;

    /**
     *  真实名称
     */
    private String chinaName;

    /**
     *  密码
     */
    private String password;

    /**
     *  是否设置交易密码
     */
    private Boolean hasTransationPwd;

    /**
     *  交易密码
     */
    private String transactionPwd;

    /**
     *   盐值
     */
    private String pwdKey;

    /**
     *  创建时间
     */
    private Date createDate;

    /**
     *  最后登录时间
     */
    private Date lastLoginDate;

    /**
     *  认证后的生日
     */
    private Date birthday;

    /**
     *  认证后的性别
     */
    private Integer sex;

    /**
     *  认证后的身份证号码
     */
    private String cardNo;

    /**
     *  是否认证
     */
    private Boolean isValidate;

    /**
     *  认证的次数
     */
    private Integer validateCount;

    /**
     *  认证后的年龄
     */
    private Integer age;

    /**
     *  用户状态
     */
    private UserStatus status;

    /**
     *   用户类型
     */
    private UserType userType;

    /**
     *  电话号码
     */
    private String phone;

    public UserDto toDto(){
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(this,dto);
        return dto;
    }

}
