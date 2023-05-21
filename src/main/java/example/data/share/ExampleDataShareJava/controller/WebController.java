package example.data.share.ExampleDataShareJava.controller;

import example.data.share.ExampleDataShareJava.model.content.Content;
import example.data.share.ExampleDataShareJava.model.file.File;
import example.data.share.ExampleDataShareJava.model.role.ERole;
import example.data.share.ExampleDataShareJava.model.role.Role;
import example.data.share.ExampleDataShareJava.model.token.Token;
import example.data.share.ExampleDataShareJava.model.user.User;
import example.data.share.ExampleDataShareJava.model.user.UserResponse;
import example.data.share.ExampleDataShareJava.payload.requests.content.ContentRequest;
import example.data.share.ExampleDataShareJava.payload.requests.forgotPassword.ForgotPasswordRequest;
import example.data.share.ExampleDataShareJava.payload.requests.mail.MailRequest;
import example.data.share.ExampleDataShareJava.payload.requests.mail.MailService;
import example.data.share.ExampleDataShareJava.payload.responses.MessageResponse;
import example.data.share.ExampleDataShareJava.payload.responses.file.FileModel;
import example.data.share.ExampleDataShareJava.payload.responses.file.FileResponse;
import example.data.share.ExampleDataShareJava.payload.responses.teacherResponse.TeacherResponse;
import example.data.share.ExampleDataShareJava.repositories.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/staff")
public class WebController {

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FileRepo fileRepo;

    @Autowired
    private ContentRepo contentRepo;

    @Autowired
    private TokenRepo tokenRepo;

    @Autowired
    private MailService emailService;


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/upload/{id}")
    public ResponseEntity<?> uploadToDB(@RequestParam("file") MultipartFile file, @PathVariable String id) {
        if (!userRepo.existsById(id)) {
            return ResponseEntity.badRequest().body(null);
        }

        User user = userRepo.findUserById(id);

        File finalFile = new File();
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        finalFile.setFileName(fileName);
        try {
            finalFile.setFile(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        finalFile.setContentType(file.getContentType());
        finalFile.setUsername(user.getUsername());
        fileRepo.save(finalFile);

        return ResponseEntity.ok(new MessageResponse("File uploaded successfully"));
    }

    @GetMapping("/getAllFilesBy/{name}")
    public ResponseEntity<?> getAllFilesBy(@PathVariable String name, @RequestParam(required = false, defaultValue = "0", value = "pageNumber") Integer pageNumber, @RequestParam(required = false, defaultValue = "5", value = "pageSize") Integer pageSize) {
        if (pageNumber < 0 || pageSize <= 0) {
            return ResponseEntity.badRequest().body(new MessageResponse("Wrong page number or page size"));
        }

        if (!userRepo.existsByUsername(name)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Username not exists"));
        }

        Page<FileModel> files = fileRepo.findAllByUsername(name, PageRequest.of(pageNumber, pageSize));

        List<FileResponse> filesWithUri = files.stream().map(file -> new FileResponse(file.getId(), file.getFileName(), ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/staff/download/").path(file.getId()).toUriString())).collect(Collectors.toList());

        Page<FileResponse> fileResponses = new PageImpl<>(filesWithUri, PageRequest.of(pageNumber, pageSize), files.getTotalElements());

        return ResponseEntity.ok().body(fileResponses);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFromDB(@PathVariable String id) {
        Optional<File> file = fileRepo.findById(id);

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse(String.format("File with id: %s not found", id)));
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(file.get().getContentType())).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + UriUtils.encodePath(file.get().getFileName(), "UTF-8") + "\"").body(file.get().getFile());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteTeacherById/{id}")
    public ResponseEntity<?> deleteTeacher(@PathVariable String id) {
        if (!userRepo.existsById(id)) {
            MessageResponse messageResponse = new MessageResponse(String.format("User with id: %s not exists", id));
            return ResponseEntity.ok().body(messageResponse);
        }

        if (contentRepo.existsById(id)) {
            contentRepo.deleteById(id);
        }
        User user = userRepo.findUserById(id);

        if (fileRepo.existsByUsername(user.getUsername())) {
            fileRepo.deleteAllByUsername(user.getUsername());
        }
        userRepo.deleteById(id);
        MessageResponse messageResponse = new MessageResponse("User has been removed");
        return ResponseEntity.ok().body(messageResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllTeachersNames")
    public ResponseEntity<?> getAllTeachers(@RequestParam(required = false, defaultValue = "0", value = "pageNumber") Integer pageNumber, @RequestParam(required = false, defaultValue = "5", value = "pageSize") Integer pageSize) {
        if (pageNumber < 0 || pageSize <= 0) {
            return ResponseEntity.badRequest().body(new MessageResponse("Wrong page number or page size"));
        }

        Page<User> usersWithoutRole = userRepo.findByRolesNotIn(ERole.ROLE_ADMIN, PageRequest.of(pageNumber, pageSize));
        Page<UserResponse> users = usersWithoutRole.map(user -> new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRoles()));

        return ResponseEntity.ok().body(users);
    }

    @GetMapping("/getTeachersNames")
    public ResponseEntity<?> getTeachers() {
        List<TeacherResponse> teachers = new ArrayList<>();
        Role role = roleRepo.findRoleByName(ERole.ROLE_ADMIN);

        for (User user : userRepo.findAll()) {
            if (contentRepo.existsById(user.getId()) && !user.getRoles().contains(role.getName())) {
                TeacherResponse teacherResponse = new TeacherResponse(user.getId(), user.getUsername());
                teachers.add(teacherResponse);
            }
        }

        return ResponseEntity.ok().body(teachers);
    }

    @GetMapping("/getTeacherContent/{id}")
    public ResponseEntity<?> getTeacherContent(@PathVariable String id) {
        if (!contentRepo.existsById(id)) {
            User user = userRepo.findUserById(id);
            Role role = roleRepo.findRoleByName(ERole.ROLE_ADMIN);

            if (!userRepo.existsById(id)) {
                MessageResponse messageResponse = new MessageResponse("User not exists");
                return ResponseEntity.badRequest().body(messageResponse);
            }

            if (!user.getRoles().contains(role.getName())) {
                Content finalContent = new Content(id, user.getUsername(), "Default content");
                contentRepo.save(finalContent);
                return ResponseEntity.ok().body(finalContent);
            } else {
                Content content = new Content(user.getId(), user.getUsername(), "Admin panel enabled");
                return ResponseEntity.ok().body(content);
            }
        }

        Content content = contentRepo.findContentById(id);
        return ResponseEntity.ok().body(content);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/addContent")
    public ResponseEntity<?> addContent(@RequestBody ContentRequest contentRequest) {
        val id = contentRequest.getId();
        val content = contentRequest.getContent();

        if (!userRepo.existsById(id)) {
            return ResponseEntity.badRequest().body(null);
        }

        if (contentRepo.existsById(id)) {
            return ResponseEntity.badRequest().body(null);
        }

        User user = userRepo.findUserById(id);
        Content finalContent = new Content(id, user.getUsername(), content);

        contentRepo.save(finalContent);
        return ResponseEntity.ok().body(finalContent);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/updateDescription")
    public ResponseEntity<?> updateDesc(@RequestBody ContentRequest contentRequest) {
        val id = contentRequest.getId();
        val content = contentRequest.getContent();

        contentRepo.findById(id).map(con -> {
            con.setContent(content);
            return contentRepo.save(con);
        });
        MessageResponse messageResponse = new MessageResponse("Description has been updated");

        return ResponseEntity.ok().body(messageResponse);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/deleteFileById/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable String id) {
        if (!fileRepo.existsById(id)) {
            return ResponseEntity.badRequest().body(String.format("File with id: %s not exists", id));
        }

        fileRepo.deleteById(id);
        MessageResponse messageResponse = new MessageResponse("File has been removed");
        return ResponseEntity.ok().body(messageResponse);
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<?> resetPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest, HttpServletRequest request) {
        val resetPasswordEmail = forgotPasswordRequest.getEmail();

        if (!userRepo.existsByEmail(resetPasswordEmail)) {

            MessageResponse messageResponse = new MessageResponse("User not exists");
            return ResponseEntity.badRequest().body(messageResponse);
        }

        if (tokenRepo.existsByEmail(resetPasswordEmail)) {
            Token token = tokenRepo.findByEmail(resetPasswordEmail);
            tokenRepo.deleteByEmail(token.getEmail());
        }

        User userData = userRepo.findByEmail(resetPasswordEmail);
        Token token = new Token();
        token.setEmail(userData.getEmail());
        token.setUsed(false);
        token.setExpiryDate(30);
        token.setToken(UUID.randomUUID().toString());
        tokenRepo.save(token);

        MailRequest mail = new MailRequest();
        mail.setFrom("info@pepisandbox.com");
        mail.setTo(userData.getEmail());
        mail.setSubject("Password reset request");

        Map<String, Object> model = new HashMap<>();
        model.put("token", token);
        model.put("user", userData);
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        model.put("resetUrl", url + "/reset-password?token=" + token.getToken());
        mail.setModel(model);
        emailService.sendEmail(mail);

        return ResponseEntity.ok().body(new MessageResponse("An email requesting a password reset has been sent"));
    }
}
