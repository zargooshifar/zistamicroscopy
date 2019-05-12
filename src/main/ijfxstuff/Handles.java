package main.ijfxstuff;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Handles {
    public Class<?> type();
}
