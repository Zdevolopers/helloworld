package z.cloud.dto;

import lombok.Data;
import z.cloud.enums.UserStatus;

import java.util.Date;

/**
 * @author zming
 * @version 1.0
 * @date 2019/9/19
 */
@Data
public class UserDto {

    /**
     *  id
     */
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
     *  是否设置交易密码
     */
    private Boolean hasTransationPwd;

    /**
     *   盐值
     */
    private String pwdKey;

    /**
     *  创建时间
     */
    private Date createDate;

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
     *  电话号码
     */
    private String phone;

}
