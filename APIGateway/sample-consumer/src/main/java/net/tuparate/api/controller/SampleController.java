package net.tuparate.api.controller;


import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@Log4j2
@RestController
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class SampleController {



  @GetMapping(path = "/sample", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getSample(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String jwtAccessToken) {
    log.info("Sample API called. Access token received: {}", jwtAccessToken);
    
    return new ResponseEntity<>("Success: true" + jwtAccessToken, HttpStatus.OK);
  }
}
