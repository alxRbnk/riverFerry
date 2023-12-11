package com.rubnikovich.riverferry.state.ferryimpl;

import com.rubnikovich.riverferry.state.StateFerry;

public class Sailing implements StateFerry {

    public void doAction() {
        System.out.println("Ferry is sailing");
    }
}
