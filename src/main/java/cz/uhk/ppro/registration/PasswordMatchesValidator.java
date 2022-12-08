package cz.uhk.ppro.registration;

import cz.uhk.ppro.user.UserDto;
import cz.uhk.ppro.validation.PasswordMatches;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator
        implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        UserDto user = (UserDto) obj;
        boolean isValid = user.getPassword().equals(user.getMatchingPassword());
        if(!isValid){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode( "matchingPassword" ).addConstraintViolation();
        }
        return isValid;
    }
}