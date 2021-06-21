package com.glivion.backend.service;

import com.glivion.backend.domain.model.Role;
import com.glivion.backend.domain.model.User;
import com.glivion.backend.domain.model.UserProfile;
import com.glivion.backend.domain.repository.UserProfileRepository;
import com.glivion.backend.domain.repository.UserRepository;
import com.glivion.backend.exception.BadRequestException;
import com.glivion.backend.payload.dto.auth.AuthDto;
import com.glivion.backend.payload.dto.auth.TokenDto;
import com.glivion.backend.payload.request.SignInRequest;
import com.glivion.backend.payload.request.SignUpRequest;
import com.glivion.backend.payload.dto.auth.UserDto;
import com.glivion.backend.security.JWTTokenUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;
    private final JWTTokenUtil tokenUtil;

    public AuthDto createCustomer(SignUpRequest request) {
        validateRequest(request);
        return createUser(request, Role.CUSTOMER);
    }

    public AuthDto createAffiliate(SignUpRequest request) {
        validateRequest(request);
        return createUser(request, Role.AFFILIATE);
    }

    public AuthDto createEmployee(SignUpRequest request) {
        validateRequest(request);
        return createUser(request, Role.EMPLOYEE);
    }

    public AuthDto signInUser(SignInRequest request){

        TokenDto tokenDto = signInUser(request.getUsername(), request.getPassword());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Optional<User> optionalUser = userRepository.findById(userDetails.getId());
        if (optionalUser.isEmpty()){
            throw new EntityNotFoundException();
        }
        User user = optionalUser.get();
        UserProfile profile = user.getUserProfile();
        UserDto userDto = UserDto.of(user.getId(), profile.getName(), profile.getEmailAddress(), profile.getPhoneNumber(), user.getRole());

        return AuthDto.of(tokenDto, userDto);
    }

    private void validateRequest(SignUpRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("A user with the same username exists");
        }

        if (userProfileRepository.existsByEmailAddress(request.getEmail())) {
            throw new BadRequestException("A user with the same email address exists");
        }

        if (request.getPhoneNumber() != null && userProfileRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BadRequestException("A user with the same phone number exists");
        }

    }

    private AuthDto createUser(SignUpRequest request, Role role) {

        UserProfile profile = new UserProfile();
        profile.setEmailAddress(request.getEmail());
        profile.setName(request.getName());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile = userProfileRepository.save(profile);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(role);
        user.setUserProfile(profile);
        user = userRepository.save(user);

        UserDto userDto = UserDto.of(user.getId(), profile.getName(), profile.getEmailAddress(), profile.getPhoneNumber(), user.getRole());

        return AuthDto.of(signInUser(request.getUsername(), request.getPassword()), userDto);
    }

    private TokenDto signInUser(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = tokenUtil.generateToken(userDetails);

        return new TokenDto(jwt);
    }
}
