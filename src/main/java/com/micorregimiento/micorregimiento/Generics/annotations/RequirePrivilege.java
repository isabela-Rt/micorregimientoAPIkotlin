package com.micorregimiento.micorregimiento.Generics.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePrivilege {
    String value();
    String message() default "No tienes permisos para acceder a este recurso";
}