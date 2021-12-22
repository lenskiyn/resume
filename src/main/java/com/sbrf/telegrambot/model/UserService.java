package com.sbrf.telegrambot.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public boolean save(User user) {
        if (!userRepository.existsById(user.getId())) {
            userRepository.save(user);
            return true;
        } else return false;
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public String getUserNameById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) return user.get().getName();
        else return "Пользователь с id: " + id + " отсутствует в базе";
    }

    public String getUserIdByName(String name) {
        Optional<User> user = userRepository.findByName(name);
        if (user.isPresent()) return user.get().getId().toString();
        else return "Пользователь с именем: " + name + " отсутствует в базе";
    }

    public String updateUserNameById(Long id, String name) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User updatedUser = user.get();
            updatedUser.setName(name);
            userRepository.save(updatedUser);
            return "Имя пользователя успешно изменено";
        } else return "Пользователь с id: " + id + " отсутствует в базе";
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public boolean isAdmin(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(User::isAdmin).orElse(false);
    }

    public String setAdmin(Long id, Boolean bool) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User updatedUser = user.get();
            updatedUser.setAdmin(bool);
            userRepository.save(updatedUser);
            return updatedUser.isAdmin() ? "Пользователь успешно получил права администратора" : "Пользователь больше не является администратором";
        } else return "Пользователь с id: " + id + " отсутствует в базе";
    }

    public boolean isNotified(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(User::isNotified).orElse(false);
    }

    public String setNotified(Long id, Boolean bool) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User updatedUser = user.get();
            updatedUser.setNotified(bool);
            userRepository.save(updatedUser);
            return updatedUser.isNotified() ? String.format("Пользователь %s теперь будет получать уведомления", updatedUser.getName())
                    : String.format("Пользователь %s больше не будет получать уведомления", updatedUser.getName());
        } else return "Пользователь с id: " + id + " отсутствует в базе";
    }

    public boolean delete(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.delete(userRepository.getOne(id));
            return true;
        }
        return false;
    }

    public boolean deleteAll () {
        userRepository.deleteAll();
        return userRepository.findAll().size() == 0;
    }
}
