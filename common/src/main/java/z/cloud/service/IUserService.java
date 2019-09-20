package z.cloud.service;

import org.springframework.stereotype.Service;
import z.cloud.entity.User;

/**
 * @author zming
 * @version 1.0
 * @date 2019/9/18
 */
@Service
public interface IUserService {

    User save(User user);


    User findByName(String phone);
}
