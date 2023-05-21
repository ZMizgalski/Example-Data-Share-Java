package example.data.share.ExampleDataShareJava.controller;

import example.data.share.ExampleDataShareJava.model.token.Token;
import example.data.share.ExampleDataShareJava.model.user.User;
import example.data.share.ExampleDataShareJava.payload.requests.forgotPassword.ForgotPasswordRequestModel;
import example.data.share.ExampleDataShareJava.repositories.TokenRepo;
import example.data.share.ExampleDataShareJava.repositories.UserRepo;
import example.data.share.ExampleDataShareJava.security.Argon2PasswordEncoder;
import jakarta.validation.Valid;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reset-password")
public class ResetPasswordController {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private TokenRepo tokenRepo;

    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder();
    }

    @ModelAttribute("passwordResetForm")
    public ForgotPasswordRequestModel passwordReset() {
        return new ForgotPasswordRequestModel();
    }

    @GetMapping
    public String displayResetPassword(@RequestParam(required = false) String token, Model model) {

        Token resetToken = tokenRepo.findByToken(token);
        if (resetToken == null) {
            model.addAttribute("error", "Token wygasł lub nie został znaleziony, poproś o ponowne zresetowanie hasła.");
        } else if (resetToken.isExpired()) {
            model.addAttribute("error", "Token wygasł lub nie został znaleziony, poproś o ponowne zresetowanie hasła.");
        } else {
            model.addAttribute("token", resetToken.getToken());
        }

        return "ResetPasswordPage";
    }

    @PostMapping
    @Transactional
    public String handlePasswordReset(@ModelAttribute("passwordResetForm") @Valid ForgotPasswordRequestModel form, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute(BindingResult.class.getName() + ".passwordResetForm", result);
            redirectAttributes.addFlashAttribute("passwordResetForm", form);
            return "redirect:/reset-password?token=" + form.getToken();
        }

        Token token = tokenRepo.findByToken(form.getToken());

        if (token == null) {
            return "redirect:/login";
        }

        User userData = userRepo.findByEmail(token.getEmail());
        String updatedPassword = passwordEncoder().encode(form.getPassword());
        val id = userData.getId();
        userRepo.findById(id).map(newUser -> {
            newUser.setPassword(updatedPassword);
            return userRepo.save(newUser);
        });
        tokenRepo.deleteByEmail(token.getEmail());
        return "redirect:/login";
    }
}
