package com.rubnikovich.riverferry.state.ferryimpl;

import com.rubnikovich.riverferry.state.StateFerry;

public class Load implements StateFerry {

    public void doAction() {
        System.out.println("Ferry is  loading");
    }
}
