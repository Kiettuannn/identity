package com.kiettuan.identity_service.service;

import com.kiettuan.identity_service.dto.request.AuthenticationRequest;
import com.kiettuan.identity_service.dto.request.IntrospectRequest;
import com.kiettuan.identity_service.dto.request.LogoutRequest;
import com.kiettuan.identity_service.dto.request.RefreshTokenRequest;
import com.kiettuan.identity_service.dto.response.AuthenticationResponse;
import com.kiettuan.identity_service.dto.response.IntrospectResponse;
import com.kiettuan.identity_service.entity.InvalidatedToken;
import com.kiettuan.identity_service.entity.User;
import com.kiettuan.identity_service.exception.AppException;
import com.kiettuan.identity_service.exception.ErrorCode;
import com.kiettuan.identity_service.repository.InvalidatedTokenRepository;
import com.kiettuan.identity_service.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    ///  INTROSPECT
    public IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException {
        var token = request.getToken();
        boolean isValid = true;

        // SIGNED JWT = Verify token
        try{
            verifyToken(token);
        }catch (AppException e){
            isValid = false;
        }
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    /// AUTHENTICATE
    public AuthenticationResponse authenticate(AuthenticationRequest request){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        boolean authenticated =  passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!authenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    ///  LOGOUT METHOD
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var signToken = verifyToken(request.getToken());

        String jti = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jti)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);
    }

    /// Refresh token
    public AuthenticationResponse refreshToken(RefreshTokenRequest request)
            throws ParseException, JOSEException {

        // Step 1: Check validation of the token.
        var signJWT = verifyToken(request.getToken());

        // Step 2: Invalidate old token
        var jti = signJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jti)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        // Step 3: Create a new token base on current user
        var username = signJWT.getJWTClaimsSet().getSubject();

        // Get user
        var user = userRepository.findByUsername(username).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        // Generate token
        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    ///  VERIFY TOKEN
    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);

        if(!(verified && expirationTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        // Case: User logout but token is not expiry
        if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
                throw new AppException(ErrorCode.UNAUTHENTICATED);
        return signedJWT;
    }

    ///  GENERATE TOKEN
    private String generateToken(User user){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("domain_name")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header,payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token",e);
            throw new RuntimeException(e);
        }
    }

    ///  BUILD SCOPE
    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");

        if(!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());

                if(!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> {
                        stringJoiner.add(permission.getName());
                    });
            });

        return stringJoiner.toString();
    }
}
