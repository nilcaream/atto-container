package com.nilcaream.atto.example.case002;

import com.nilcaream.atto.Atto;
import lombok.Getter;

import javax.inject.Inject;

@Getter
public class AttoHolder {

    @Inject
    private Atto atto;
}
