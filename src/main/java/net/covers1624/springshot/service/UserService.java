package net.covers1624.springshot.service;

import net.covers1624.springshot.entity.User;
import net.covers1624.springshot.form.UserForm;
import net.covers1624.springshot.repo.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.text.MessageFormat;
import java.util.Optional;

/**
 * Created by covers1624 on 5/8/20.
 */
@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LogManager.getLogger();

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, @Lazy BCryptPasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepo.findByUsername(username);

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        throw new UsernameNotFoundException(MessageFormat.format("User with username {0} cannot be found.", username));
    }

    public void signUpUser(UserForm userForm, BindingResult result) {
        logger.info("User {}:{} attempting to register.", userForm.getUsername(), userForm.getEmail());
        Optional<User> userOpt = userRepo.findByUsername(userForm.getUsername());
        logger.info(userOpt.isPresent());
        if (userOpt.isPresent() && !userOpt.get().isPlaceholder()) {
            result.addError(new FieldError(result.getObjectName(), "username", "User with name already exists."));
        }

        userOpt = userRepo.findByEmail(userForm.getEmail());
        logger.info(userOpt.isPresent());
        if (userOpt.isPresent() && !userOpt.get().isPlaceholder()) {
            result.addError(new FieldError(result.getObjectName(), "email", "User with email already exists."));
        }
        if (result.hasErrors()) {
            logger.info("Failed to register, errors exist.");
            return;
        }
        logger.info("Saving registered user.");
        User user = userOpt.orElseGet(User::new);
        user.setUsername(userForm.getUsername());
        user.setEmail(userForm.getEmail());
        user.setPassword(passwordEncoder.encode(userForm.getPassword()));
        user.setPlaceholder(false);
        userRepo.save(user);
    }
}
