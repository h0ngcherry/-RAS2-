package com.example.documentReview.mapper;


import com.example.documentReview.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface UserMapper {

    User selectByNameAndPwd(User user);

    User selectByName(User user);

    int insert(User user);

    int update(User user);

    int selectIsName(User user);

    String selectPasswordByName(User user);

    List<User> findAll();

    void delUser(int id);

    void updateLimit(User user);
}
