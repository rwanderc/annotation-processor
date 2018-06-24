package com.wandercosta.annotation.example;

import com.wandercosta.annotation.ACustomAnnotation;
import org.aeonbits.owner.Config;

@ACustomAnnotation
public interface PersonConfig extends Config {

    int getAge();

    String getName();

}
