package com.secor.eatnowauthservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
public class MainRestController {

    private static final Logger log = LoggerFactory.getLogger(MainRestController.class);

    @Autowired
    CredentialRepository credentialRepository;

    @Autowired
    TokenService tokenService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    Producer producer;

    @PostMapping("signup")
    public ResponseEntity<?> signup(@RequestBody Credential credential) throws JsonProcessingException {
        log.info("Received request to signup: {}", credential);
        credentialRepository.save(credential);
        log.info("User signed up successfully: {}", credential);
        producer.publishAuthDatum(credential.getUsername(), "SIGNUP");
        return ResponseEntity.ok("User signed up successfully");
    }

    @GetMapping("login")
    public ResponseEntity<?> login(@RequestBody Credential credential) throws JsonProcessingException {
        log.info("Received request to login: {}", credential);
        Credential user = credentialRepository.findById(credential.getUsername()).orElse(null);

        if(user == null)
        {
            log.info("User not found: {}", credential);
            return ResponseEntity.badRequest().body("User not found");
        }
        if(user.getPassword().equals(credential.getPassword()))
        {
            //More to be done here - Token to  be sent to the user
            log.info("User logged in successfully: {}", credential);
            producer.publishAuthDatum(credential.getUsername(), "LOGIN");
            return ResponseEntity.ok().
                    header("Authorization", tokenService.generateToken(user.getUsername()).getTokenid().toString()).
                    body("User logged in successfully");
        }

        log.info("Incorrect password: {}", credential);
        return ResponseEntity.badRequest().body("incorrect password");
    }

    @GetMapping("validate")
    public ResponseEntity<?> validate(@RequestHeader("Authorization") String token) throws JsonProcessingException {
        log.info("Token: " + token);
        String[] tokenArray = token.split(" ");
        log.info("TokenArray: " + tokenArray[1]);
        if(tokenService.validateToken(tokenArray[1]))
        {
            String username = tokenRepository.findById(Integer.valueOf(tokenArray[1])).get().getUsername();
            log.info("Token is valid");
            producer.publishAuthDatum(username, "VALIDATED");
            return ResponseEntity.ok("valid");
        }
        log.info("Token is invalid");
        return ResponseEntity.ok("invalid");
    }

    @PostMapping("logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) throws JsonProcessingException {
        log.info("Logout Request Received for TOKEN: " + token);
        String[] tokenArray = token.split(" ");

        Token token1 = tokenService.tokenRepository.findById(Integer.parseInt(tokenArray[1])).orElse(null);

        if(token1 == null)
        {
            log.info("Token not found: {}", token);
            return ResponseEntity.badRequest().body("invalid");
        }

        log.info("Token found: {}", token);
        tokenRepository.updateStatusByTokenid("invalid", Integer.valueOf(tokenArray[1]));

        log.info("Token updated: {}", token);
        String username = tokenRepository.findById(Integer.valueOf(tokenArray[1])).get().getUsername();
        producer.publishAuthDatum(username, "LOGGEDOUT");
        return ResponseEntity.ok("logged out successfully");
    }



}
