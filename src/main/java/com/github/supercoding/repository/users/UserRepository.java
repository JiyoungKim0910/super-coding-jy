package com.github.supercoding.repository.users;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface UserRepository {
    Optional<UserEntity> getUserInfoById(Integer userId);
}
