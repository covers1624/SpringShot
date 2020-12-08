package net.covers1624.springshot.form;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * Created by covers1624 on 6/8/20.
 */
public class UserForm {

    @NotNull (message = "Can't be empty")
    @Size (min = 2, max = 30, message = "Must be more than 2 and less than 30 characters")
    private String username;

    @Pattern (//
            regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])",//
            message = "Invalid Email address"//
    )
    @NotNull (message = "Can't be empty")
    private String email;

    @Pattern (//
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[#$%&@^`~.,:;\"'\\\\/|_\\-<*+!?={\\[()\\]}])[A-Za-z\\d#$%&@^`~.,:;\"'\\\\/|_\\-<*+!?={\\[()\\]}]{8,72}$",//
            message = "Password must be between 8 and 72 characters, contain at least one letter, number and special character"//
    )
    @NotNull (message = "Can't be empty")
    private String password;

    @NotNull (message = "Can't be empty")
    private String passwordConfirm;

    //@formatter:off
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPasswordConfirm() { return passwordConfirm; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPasswordConfirm(String passwordConfirm) { this.passwordConfirm = passwordConfirm; }
    //@formatter:on

    @AssertTrue (message = "Passwords do not match.")
    public boolean isPasswordsEqual() {
        return Objects.equals(password, passwordConfirm);
    }
}
