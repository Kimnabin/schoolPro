package school.xxxx.domain.exception;

public class BusinessRuleViolationException extends DomainException {

    private static final String ERROR_CODE = "BUSINESS_RULE_VIOLATION";

    public BusinessRuleViolationException(String message) {
        super(message, ERROR_CODE);
    }

    public BusinessRuleViolationException(String rule, String context) {
        super("Business rule violation '" + rule + "' in context: " + context, ERROR_CODE);
    }
}