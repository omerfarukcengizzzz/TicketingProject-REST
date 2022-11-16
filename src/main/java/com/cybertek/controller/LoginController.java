package com.cybertek.controller;

import com.cybertek.annotation.DefaultExceptionMessage;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.ResponseWrapper;
import com.cybertek.entity.User;
import com.cybertek.entity.common.AuthenticationRequest;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.util.MapperUtil;
import com.cybertek.service.UserService;
import com.cybertek.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Authentication Controller", description = "Authenticate API")
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private MapperUtil mapperUtil;
    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/authenticate")
    @DefaultExceptionMessage(defaultMessage = "Bad Credentials")
    @Operation(summary = "Login to application")
    public ResponseEntity<ResponseWrapper> doLogin(@RequestBody AuthenticationRequest authenticationRequest) throws TicketingProjectException {

        String username = authenticationRequest.getUsername();
        String password = authenticationRequest.getPassword();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        authenticationManager.authenticate(authenticationToken);

        UserDTO foundUser = userService.findByUserName(username);
        User convertedUser = mapperUtil.convert(foundUser, new User());

        if (!foundUser.isEnabled()) {
            throw new TicketingProjectException("Please verify your email");
        }

        String jwtToken = jwtUtil.generateToken(convertedUser);

        return ResponseEntity
                .ok(new ResponseWrapper("Successfully logged in", jwtToken));
    }

    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @PostMapping("/create-user")
    @Operation(summary = "Create new account")
    private ResponseEntity<ResponseWrapper> doRegister(@RequestBody UserDTO userDTO) {

        UserDTO createdUser = userService.save(userDTO);



    }

}