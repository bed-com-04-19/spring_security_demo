package com.spring.security.demo.user;

import com.spring.security.demo.commons.ApiResponse;
import com.spring.security.demo.user.hateoas.UserModel;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@PostAuthorize("hasRole('ROLE_ADMIN') and hasRole('ROLE_EXECUTIVE')")
@RequestMapping("/api/v1/users")
@Validated
public class UserController {
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> signUp(@Valid @RequestBody UserSignUpRequest userSignUpRequest){
        return userService.saveUser(userSignUpRequest);
    }


    @GetMapping
    public ResponseEntity<PagedModel<?>> getAllUsers(@PositiveOrZero  @RequestParam(value="page", defaultValue = "0") int page,
                                                      @Positive @RequestParam(value="size", defaultValue = "20") int size,
                                                     PagedResourcesAssembler<User> pagedResourcesAssembler){
        return userService.getAllUsers(page, size, pagedResourcesAssembler);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_EXECUTIVE') or hasRole('ROLE_USER')")
    @GetMapping("/me")
    public ResponseEntity<UserModel> getUserDetails(Authentication authentication){
        return userService.getUser(authentication);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_EXECUTIVE')")
    @PutMapping("update-role/{userName}/{role}")
    public ResponseEntity<ApiResponse> updateUserRole(@PathVariable("userName") String userName,
                                                      @PathVariable("role") String role,
                                                      Authentication currentUser){
        return userService.updateUserRole(userName, role, currentUser);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("disable-account/{userName}")
    public ResponseEntity<ApiResponse> disableAccount(@PathVariable("userName") String userName){
        return userService.disableAccount(userName);
    }
}
