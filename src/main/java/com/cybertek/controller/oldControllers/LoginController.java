package com.cybertek.controller.oldControllers;

import com.cybertek.annotation.DefaultExceptionMessage;
import com.cybertek.dto.MailDTO;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.ConfirmationToken;
import com.cybertek.entity.ResponseWrapper;
import com.cybertek.entity.User;
import com.cybertek.entity.common.AuthenticationRequest;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.service.ConfirmationTokenService;
import com.cybertek.util.MapperUtil;
import com.cybertek.service.UserService;
import com.cybertek.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Authentication Controller", description = "Authenticate API")
public class LoginController {

    @Value("${app.local-url}")
    private String BASE_URL;


    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private MapperUtil mapperUtil;
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private ConfirmationTokenService confirmationTokenService;

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
    private ResponseEntity<ResponseWrapper> doRegister(@RequestBody UserDTO userDTO) throws TicketingProjectException {

        UserDTO createdUser = userService.save(userDTO);

        sendEmail(createEmail(createdUser));

        return ResponseEntity.ok(new ResponseWrapper("User has been created!", createdUser));
    }

    // a custom method to create an email
    private MailDTO createEmail(UserDTO userDTO) {

        User user = mapperUtil.convert(userDTO, new User());
        ConfirmationToken confirmationToken = new ConfirmationToken(user);

        confirmationToken.setIsDeleted(false);

        ConfirmationToken createdConfirmationToken = confirmationTokenService.save(confirmationToken);

        return MailDTO.builder()
                .emailTo(user.getUserName())
                .token(createdConfirmationToken.getToken())
                .subject("Confirm Registration")
                .message("To confirm your account, please click here:")
                .url(BASE_URL + "/confirmation?token=")
                .build();
    }

    // a custom method to send the email
    private void sendEmail(MailDTO mailDTO) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(mailDTO.getEmailTo());
        mailMessage.setSubject(mailDTO.getSubject());
        mailMessage.setText(mailDTO.getMessage() + mailDTO.getUrl() + mailDTO.getToken());

        confirmationTokenService.sendEmail(mailMessage);
    }

    @DefaultExceptionMessage(defaultMessage = "Failed to confirm email, please try again!")
    @GetMapping("/confirmation")
    @Operation(summary = "Confirm Account")
    public ResponseEntity<ResponseWrapper> confirmEmail(@RequestParam("token") String token) throws TicketingProjectException {

        ConfirmationToken confirmationToken = confirmationTokenService.readByToken(token);
        UserDTO confirmUser = userService.confirm(confirmationToken.getUser());
        confirmationTokenService.delete(confirmationToken);

        return ResponseEntity.ok(new ResponseWrapper("User has been confirmed successfully!", confirmUser));
    }

}