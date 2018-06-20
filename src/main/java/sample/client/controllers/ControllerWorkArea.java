package sample.client.controllers;

import sample.client.Client;

public class ControllerWorkArea{
    private Client client;

    public void initControllerWorkArea (Client client){
        this.client = client;
        client.setControllerWorkArea(this);
    }

}
