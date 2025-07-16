package school.xxxx.application.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {
    String message() default "Invalid phone number format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    // Optional: specify phone number type
    PhoneNumberType type() default PhoneNumberType.INTERNATIONAL;

    enum PhoneNumberType {
        VIETNAM,           // Only Vietnam numbers
        INTERNATIONAL,     // International format
        FLEXIBLE          // Flexible validation
    }
}