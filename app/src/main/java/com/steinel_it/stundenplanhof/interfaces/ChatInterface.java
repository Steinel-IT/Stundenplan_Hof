package com.steinel_it.stundenplanhof.interfaces;

import com.steinel_it.stundenplanhof.objects.Message;

import java.util.ArrayList;

public interface ChatInterface {

    void succLogin();

    void errorLogin();

    void onUpdate(ArrayList<Message> newMessages);

    void onCancelUpdate(String errorMessage);

}
