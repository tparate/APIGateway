package net.tuparate.gateway.jwt;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import com.nimbusds.jose.util.Base64;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Component;


@Log4j2
@Component
public class JwtAccessTokenGeneratorFilter extends ZuulFilter {


  @Value("${jwsKey}")
  private String jwsKey;

  @Value("${jwsMacAlgorithm}")
  private String jwsMacAlgorithm;

  @Value("${lifespan}")
  private String lifespan;


  @Value("${issuer}")
  private String issuer;

  @Override
  public String filterType() {
    return "pre";
  }

  @Override
  public int filterOrder() {
    return 1;
  }

  @Override
  public boolean shouldFilter() {
    return true;
  }

  /**
   * Zuul Filter method.
   */
  @Override
  public Object run() throws ZuulException {
    SignedJWT jwt = null;
    RequestContext ctx = RequestContext.getCurrentContext();
    HttpServletRequest request = ctx.getRequest();

    try {
      jwt = generateJwt();

      if (jwt != null) {
        ctx.addZuulRequestHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.serialize());
        log.debug("Passing JWT Access Token to audience, token = Bearer {}", jwt.serialize());
      } else {
        log.error("JWT is null. Can not set in header.");
      }
    } catch (Exception e) {
      throw new ZuulException(e,
          "Failed to construct authorization bearer token for forwarded request",
          HttpStatus.INTERNAL_SERVER_ERROR.value(),
          HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }
    log.info(String.format("%s request to %s", request.getMethod(),
        request.getRequestURL().toString()));
    return null;
  }

  /**
   * This method will generate JWT and signed it
   * 
   * @param identity
   * @return
   * @throws JOSEException
   */
  private SignedJWT generateJwt() throws JOSEException {
    log.debug("Inside generateJwt() method.");
    SignedJWT signedJWT = null;
    JWSSigner jwsSigner = null;
    JWTClaimsSet claimsSet = null;
    String orgSubOrgId = null;
    byte[] keyBytes = null;
    String orgId = null;
    String subOrgId = null;

    Date now = new Date();
    Date expiration = new Date(
        now.getTime() + Duration.ofMinutes(new Long("1")).toMillis());

    try {
      keyBytes = new Base64(this.jwsKey).decode();
      jwsSigner = new MACSigner(keyBytes);

      UserCustomClaims customUserClaims = new UserCustomClaims("tuparate", "Tushar", "Parate",
          "Bangalore");

      log.debug("User Custom Claims = {}", customUserClaims.toString());

      claimsSet = new JWTClaimsSet.Builder().issuer(this.issuer)
          .issueTime(now)
          .expirationTime(expiration)
          .subject(customUserClaims.getUserName())
          .claim("aud", Constants.aud)
          .claim("isu", Constants.issuer)
          .claim("scp", "read write")
          .claim("user", customUserClaims)
          .build();

      log.debug("ClaimsSet = {}", claimsSet);

      if (claimsSet != null) {
        MacAlgorithm macAlg = MacAlgorithm.from(this.jwsMacAlgorithm);

        JWSAlgorithm jwsMacAlgorithm = JWSAlgorithm.parse(macAlg.getName());
        if (jwsMacAlgorithm != null) {
          signedJWT = new SignedJWT(new JWSHeader(jwsMacAlgorithm), claimsSet);
          signedJWT.sign(jwsSigner);
        }
      }
    } catch (KeyLengthException e) {
      throw new IllegalArgumentException("Invalid length for JWT signing key.", e);
    } catch (Exception ex) {
      throw new IllegalArgumentException("Error while generating JWT.", ex);
    }
    log.debug("Returning Jwt Access Token, JWT= {}", signedJWT);
    return signedJWT;
  }
}
