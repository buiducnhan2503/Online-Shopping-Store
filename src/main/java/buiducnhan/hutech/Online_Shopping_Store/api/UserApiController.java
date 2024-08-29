package buiducnhan.hutech.Online_Shopping_Store.api;

import buiducnhan.hutech.Online_Shopping_Store.entities.User;
import buiducnhan.hutech.Online_Shopping_Store.service.UserService;
import buiducnhan.hutech.Online_Shopping_Store.viewmodels.user.UserGetVm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/api/users")
public class UserApiController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserGetVm>> getAllUser() {
        List<User> listUser = userService.getAllUsers();
        List<UserGetVm> userGetVmList = listUser.stream().map(UserGetVm::from).toList();
        return ResponseEntity.ok(userGetVmList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserGetVm> findUserById(@PathVariable Long id) {
        Optional<User> user = userService.findUserById(id);
        Optional<UserGetVm> userGetVm = user.map(UserGetVm::from);
        return userGetVm.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (userService.checkUsernameExists(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        User createdUser = userService.createUser(user);
        userService.setDefaultRole(createdUser.getUsername());
        return ResponseEntity.ok(createdUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok().build();
    }
}
