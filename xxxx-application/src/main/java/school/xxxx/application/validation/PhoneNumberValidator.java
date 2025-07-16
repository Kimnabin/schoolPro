package school.xxxx.application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import school.xxxx.application.util.PhoneNumberUtil;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    private PhoneNumber.PhoneNumberType type;

    @Override
    public void initialize(PhoneNumber constraintAnnotation) {
        this.type = constraintAnnotation.type();
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return true; // Let @NotBlank handle null/empty validation
        }

        return switch (type) {
            case VIETNAM -> PhoneNumberUtil.isValidVietnamPhone(phoneNumber);
            case INTERNATIONAL -> PhoneNumberUtil.isValidInternationalPhone(phoneNumber);
            case FLEXIBLE -> PhoneNumberUtil.isValidPhone(phoneNumber);
        };
    }
}