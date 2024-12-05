package com.mtsd.repository;

import com.mtsd.model.Product;
import com.mtsd.model.User;

import java.util.List;

public interface UserRepository {
    boolean insert(User user);
    boolean update(User user);
    boolean delete(int userId);
    User getById(int userId);
    List<User> getAll();
    boolean checkUserCredentials(User user);
    User getByUsername(String username);

}
