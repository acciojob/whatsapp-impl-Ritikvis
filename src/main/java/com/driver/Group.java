package com.driver;

import org.apache.catalina.User;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private static String name;
    private List<User> participants;

    public Group(String name, int size) {
        this.name = name;
        this.participants = new ArrayList<>();
    }

    public static String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public void addParticipant(User user) {
        participants.add(user);
    }

    public void removeParticipant(User user) {
        participants.remove(user);
    }

    public int getNumberOfParticipants() {
        return participants.size();
    }
}
