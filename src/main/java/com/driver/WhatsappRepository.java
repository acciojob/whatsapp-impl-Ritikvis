package com.driver;

import org.apache.logging.log4j.message.Message;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class WhatsappRepository {

    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int messageId;
    private int groupCount;

    public WhatsappRepository() {
        this.groupMessageMap = new HashMap<>();
        this.groupUserMap = new HashMap<>();
        this.senderMap = new HashMap<>();
        this.adminMap = new HashMap<>();
        this.userMobile = new HashSet<>();
        this.messageId = 0;
    }

    public String createUser(String name, String mobile) throws Exception {
        if (userMobile.contains(mobile)) {
            throw new Exception("User with mobile number already exists.");
        }
        userMobile.add(mobile);
        return "SUCCESS";
    }

    public Group createGroup(Group group, List<User> users) {
        groupUserMap.put(group, users);
        adminMap.put(group, users.get(0));
        return group;
    }

    public int createMessage(String content) {
        messageId++;
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        if (!groupMessageMap.containsKey(group)) {
            throw new Exception("Group does not exist.");
        }
        if (!groupUserMap.get(group).contains(sender)) {
            throw new Exception("You are not a member of this group.");
        }
        List<Message> messages = groupMessageMap.get(group);
        messages.add(message);
        senderMap.put(message, sender);
        groupMessageMap.put(group, messages);
        return messages.size();
    }

    public String changeAdmin(User newAdmin, Group group) throws Exception {
        if (!groupMessageMap.containsKey(group)) {
            throw new Exception("Group does not exist.");
        }
        if (!adminMap.get(group).equals(newAdmin)) {
            throw new Exception("Current user is not the admin of the group.");
        }
        adminMap.put(group, newAdmin);
        return "SUCCESS";
    }

    public int removeUser(User user) throws Exception {
        for (Map.Entry<Group, List<User>> entry : groupUserMap.entrySet()) {
            Group group = entry.getKey();
            List<User> users = entry.getValue();
            if (users.contains(user)) {
                if (adminMap.get(group).equals(user)) {
                    throw new Exception("Cannot remove admin.");
                }
                users.remove(user);
                groupUserMap.put(group, users);
                List<Message> messages = groupMessageMap.get(group);
                messages.removeIf(message -> senderMap.get(message).equals(user));
                groupMessageMap.put(group, messages);
                return users.size();
            }
        }
        throw new Exception("User not found in any group.");
    }

    // Implementing this method according to your requirements
    public String findMessage(Date start, Date end, int K) throws Exception {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    public int getNextMessageId() {
        return messageId + 1;
    }

    public void saveMessage(Message message) {
        // Save the message
    }

    public boolean groupExists(Group group) {
        return groupMessageMap.containsKey(group);
    }

    public boolean isUserMemberOfGroup(User sender, Group group) {
        List<User> users = groupUserMap.getOrDefault(group, new ArrayList<>());
        return users.contains(sender);
    }

    public boolean isAdmin(User approver) {
        for (User admin : adminMap.values()) {
            if (admin.equals(approver)) {
                return true;
            }
        }
        return false;
    }

    public boolean isUserInAnyGroup(User user) {
        for (List<User> users : groupUserMap.values()) {
            if (users.contains(user)) {
                return true;
            }
        }
        return false;
    }

    public String findMessagesInRange(Date start, Date end) {
        return ""; // Placeholder
    }

    public int removeUserFromGroup(User user) {
        return 0; // Placeholder
    }

    public int removeUserMessages(User user) {
        return 0; // Placeholder
    }

    public int removeUserOverallMessages(User user) {
        return 0; // Placeholder
    }

    public List<User> getGroupUsers(Group group) {
        return groupUserMap.getOrDefault(group, new ArrayList<>());
    }

    public String findKthLatestMessage(Date start, Date end, int k) {
        return ""; // Placeholder
    }

    public static User getUserById(String userId) {
        return null; // Placeholder
    }

    public boolean isUserMobileExists(String mobile) {
        return false; // Placeholder
    }

    public Group getGroupById(String groupId) {
        for (Group group : groupUserMap.keySet()) {
            if (group.getName().equals(groupId)) {
                return group;
            }
        }
        // If the group with the given ID is not found, return null
        return null;
    }

    public void setGroupCount(int groupCount) {
        this.groupCount = groupCount;
    }

    public int getGroupCount() {
        return groupCount;
    }

}