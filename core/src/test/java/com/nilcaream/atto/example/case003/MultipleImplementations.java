package com.nilcaream.atto.example.case003;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Named;

@Getter
public class MultipleImplementations {

    @Inject
    @Named("Blue")
    private ExampleInterface blue1;

    @Inject
    @Named("Blue")
    private ExampleInterface blue2;

    @Inject
    @GreenQualifier
    private ExampleInterface green1;

    @Inject
    @GreenQualifier
    private ExampleInterface green2;

}
