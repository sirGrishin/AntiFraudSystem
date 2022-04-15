package antifraud.controller;

import antifraud.model.User;
import antifraud.model.util.Status;
import antifraud.model.util.UserOperation;
import antifraud.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Ilya Grishin
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;


    @PostMapping("/user")
    public ResponseEntity<User> registration(@RequestBody @Valid User user) {
        userService.addUser(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);

    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('SUPPORT')")
    @GetMapping("/list")
    public ResponseEntity<List<User>> userList() {
        List<User> all = userService.getAll();
        return ResponseEntity.ok().body(all);
    }

    /**
     * изменяет роли пользователей. Он должен принимать следующее тело JSON:
     * "username": "<String value, not empty>",
     * "role": "<String value, not empty>"
     * }
     */

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PutMapping("/role")
    public ResponseEntity<User> changeRole(@RequestBody User user) {
        User user1 = userService.updateRole(user.getUsername(), user.getRole());
        return ResponseEntity.ok().body(user1);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PutMapping("/access")
    public ResponseEntity<Status> changeLockStatus(@RequestBody UserOperation userOperation) {
        Status status = userService.changeLockStatus(userOperation.getUsername(), userOperation.getOperation());
        return ResponseEntity.ok().body(status);
    }


    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @DeleteMapping(value = {"/user/", "/user/{username}"})
    public ResponseEntity<?> deleteUser(@PathVariable @Nullable String username) {
        return ResponseEntity.ok().body(userService.deleteUser(username));
    }


}

                                        