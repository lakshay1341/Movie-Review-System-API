package in.lakshay.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * simple wrapper for message source
 * helps with i18n stuff
 */
@Component
public class MessageUtil {
    private final MessageSource messageSource;

    // constructor injection ftw
    public MessageUtil(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // get message with no args
    public String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    // get message with args for formatting
    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}