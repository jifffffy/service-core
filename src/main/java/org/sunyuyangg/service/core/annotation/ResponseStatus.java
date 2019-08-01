package org.sunyuyangg.service.core.annotation;

import com.ibm.staf.STAFResult;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseStatus {

    @AliasFor("code")
    int value() default STAFResult.UnknownError;

    @AliasFor("value")
    int code() default STAFResult.UnknownError;

    String reason() default "";
}
