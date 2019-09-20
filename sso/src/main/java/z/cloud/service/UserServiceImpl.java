package z.cloud.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import z.cloud.entity.User;
import z.cloud.mapper.UserMapper;

/**
 * @author zming
 * @version 1.0
 * @date 2019/9/18
 */
@Component
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User save(User user) {
        if(user.getId() == null){
            userMapper.insert(user);
        }else {
            userMapper.updateById(user);
        }
        return user;
    }

    @Override
    public User findByName(String phone) {
        QueryWrapper wrapper = new QueryWrapper<User>();
        wrapper.select("name");
        User user = userMapper.selectOne(wrapper);
        return user;
    }
}
