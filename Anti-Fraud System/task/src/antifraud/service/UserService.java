package antifraud.service;

import antifraud.exeption.UserExistException;
import antifraud.model.User;
import antifraud.model.util.Operation;
import antifraud.model.util.Roles;
import antifraud.model.util.Status;
import antifraud.model.util.UserStatus;
import antifraud.repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author Ilya Grishin
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void addUser(User user) {
        if (userNotExist(user.getUsername())) {
            autoSetRole(user);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            if (!user.getRole().equals(Roles.ADMINISTRATOR)) {
                user.setNonLock(false);
            } else user.setNonLock(true);
            userRepository.save(user);
        } else throw new UserExistException();
    }

    public UserStatus deleteUser(String username) {
        User byUsername = userRepository.findByUsername(username);
        if (byUsername == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        UserStatus userStatus = new UserStatus();
        userRepository.delete(byUsername);
        userStatus.setUsername(username);
        userStatus.setStatus("Deleted successfully!");
        return userStatus;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    //Только админ
    public Status changeLockStatus(String username, Operation operation) {
        Status status = new Status();
        if (!userNotExist(username)) {
            User byUsername = findByUsername(username);
            if (byUsername.getRole().equals(Roles.ADMINISTRATOR)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            if (operation.equals(Operation.LOCK)) {
                byUsername.setNonLock(false);
                userRepository.save(byUsername);
                status.setStatus("User " + username + " locked!");
                return status;
            }
            if (operation.equals(Operation.UNLOCK)) {
                byUsername.setNonLock(true);
                userRepository.save(byUsername);
                status.setStatus("User " + username + " unlocked!");
                return status;
            } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }


    /**
     * Если пользователь не найден, ответьте HTTP Not Foundстатусом ( 404);
     * Если роль не SUPPORT или MERCHANT, ответьте HTTP Bad Requestстатусом ( 400);
     * Если вы хотите назначить роль, которая уже предоставлена ​​пользователю, ответьте HTTP Conflictстатусом ( 409);
     */
    public User updateRole(String username, Roles role) {
        if (role.equals(Roles.ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (userNotExist(username)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        User byUsername = findByUsername(username);
        if (byUsername.getRole().equals(role)) {
            throw new UserExistException();
        }
        byUsername.setRole(role);
        return userRepository.save(byUsername);
    }

    /**
     * true если польователя не существует
     */
    private boolean userNotExist(String username) {
        return userRepository.findByUsername(username) == null;
    }

    private User autoSetRole(User user) {
        if (getAll().isEmpty()) {
            user.setRole(Roles.ADMINISTRATOR);
            return user;
        } else user.setRole(Roles.MERCHANT);
        return user;
    }


}

