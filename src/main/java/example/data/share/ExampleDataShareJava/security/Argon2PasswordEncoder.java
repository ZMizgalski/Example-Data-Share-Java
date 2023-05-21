package example.data.share.ExampleDataShareJava.security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.security.crypto.password.PasswordEncoder;

import static de.mkammerer.argon2.Argon2Factory.Argon2Types.ARGON2id;

@Slf4j
public class Argon2PasswordEncoder implements PasswordEncoder {
    private static final int ITERATIONS_COUNT = 10;
    private static final int MEMORY_TO_USE = 4049;
    private static final int PARALLELISM = 4;

    private static final Argon2 argon2idHasher = Argon2Factory.create(ARGON2id);

    private static String argon2idHash(CharSequence plaintextPassword) {
        log.debug("Hashing user password with sha512");
        char[] sha512HashedPassword = sha512Hash(plaintextPassword.toString());

        String hash = "";

        try {
            log.debug("Hashing sha512 encoded user password with Argon2id");
            hash = argon2idHasher.hash(ITERATIONS_COUNT, MEMORY_TO_USE, PARALLELISM, sha512HashedPassword);
        } finally {
            log.debug("Wiping array with sha512 hashed user password");
            argon2idHasher.wipeArray(sha512HashedPassword);
        }

        log.debug("Password encoding done");
        return hash;
    }

    private static boolean argon2idVerifyHash(CharSequence plaintextPassword, CharSequence hashedPassword) {
        log.debug("Verifying Argon2id hash");
        log.debug("Hashing user password with sha512");
        char[] sha512HashedPassword = sha512Hash(plaintextPassword.toString());

        boolean isMatched;

        try {
            log.debug("Hashing sha512 encoded user password with Argon2id and verifying if it is correct");
            isMatched = argon2idHasher.verify(hashedPassword.toString(), sha512HashedPassword);
        } finally {
            log.debug("Wiping array with sha512 hashed user password");
            argon2idHasher.wipeArray(sha512HashedPassword);
        }

        log.debug("Password verification finished. Is password matching: {}", isMatched);
        return isMatched;
    }

    private static char[] sha512Hash(String textToHash) {
        SHA3.Digest512 digest512 = new SHA3.Digest512();
        byte[] digest = digest512.digest(textToHash.getBytes());
        String hashed = Hex.toHexString(digest);
        return hashed.toCharArray();
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return argon2idHash(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return argon2idVerifyHash(rawPassword, encodedPassword);
    }
}
