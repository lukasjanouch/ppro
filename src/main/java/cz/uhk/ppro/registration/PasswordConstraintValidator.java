package cz.uhk.ppro.registration;

import cz.uhk.ppro.validation.ValidPassword;
import org.passay.*;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class PasswordConstraintValidator
        implements ConstraintValidator<ValidPassword, String> {
    private static final String PASSAY_MESSAGE_FILE_PATH = "src/main/resources/passay_%s.properties";

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        /*UserDto user = (UserDto) obj;
        boolean isValid = user.getPassword().equals(user.getMatchingPassword());
        if(!isValid){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode( "matchingPassword" ).addConstraintViolation();
        }
        return isValid;*/
        PasswordValidator validator = new PasswordValidator(generateMessageResolver(context),
                Arrays.asList(
                // at least 8 characters
                new LengthRule(8, 30),

                // at least one upper-case character
                new CharacterRule(EnglishCharacterData.UpperCase, 1),

                // at least one lower-case character
                new CharacterRule(EnglishCharacterData.LowerCase, 1),

                // at least one digit character
                new CharacterRule(EnglishCharacterData.Digit, 1),

                // at least one symbol (special character)
                new CharacterRule(EnglishCharacterData.Special, 1),

                // no whitespace
                new WhitespaceRule()

        ));
        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        List<String> messages = validator.getMessages(result);

        String messageTemplate = messages.stream()
                .collect(Collectors.joining(","));
        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        return false;
    }

    private MessageResolver generateMessageResolver(ConstraintValidatorContext context) {

        Properties props = new Properties();

        String lang = "cz";
        try (FileInputStream fileInputStream = new FileInputStream(String.format(PASSAY_MESSAGE_FILE_PATH, lang))) {

            props.load(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));
            return new PropertiesMessageResolver(props);

        } catch (IOException exception) {
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode( "fileLoadingFailure" ).addConstraintViolation();
        }
        return new PropertiesMessageResolver();
    }



}
