package com.driver;

import java.util.*;

import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.SimpleMessage;
import org.springframework.stereotype.Service;

@Service
public class WhatsappService {

    WhatsappRepository whatsappRepository = new WhatsappRepository();

    public String createUser(String name, String mobile) throws Exception {
        return whatsappRepository.createUser(name, mobile);
    }

    public Group createGroup(List<User> users) {
        // Ensure there are at least 2 users in the list
        if (users == null || users.size() < 2) {
            throw new IllegalArgumentException("At least 2 users are required to create a group.");
        }

        // Extract the admin user
        User admin = users.get(0);

        // If there are only 2 users, it's a personal chat
        if (users.size() == 2) {
            // Set the group name as the name of the second user (other than admin)
            User otherUser = users.get(1);
            return new Group(otherUser.getName(), 2);
        } else {
            // Increment the group count
            int groupCount = whatsappRepository.getGroupCount() + 1;
            // Set the group name as "Group #count"
            String groupName = "Group " + groupCount;
            // Update the group count in the repository
            whatsappRepository.setGroupCount(groupCount);
            // Create the group using the repository
            return whatsappRepository.createGroup(new Group(groupName, users.size()), users);
        }
    }

    public int createMessage(String content) {
        // The 'i^th' created message has message id 'i'.
        // Increment the message ID counter and use it as the message ID
        int messageId = whatsappRepository.getNextMessageId();

        // Create the message with the given content
        SimpleMessage message = new SimpleMessage(content);

        // Delegate the creation of the message to the repository
        whatsappRepository.saveMessage(message);

        // Return the message ID
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "You are not allowed to send message" if the sender is not a member of the group
        if (!whatsappRepository.groupExists(group)) {
            throw new Exception("Group does not exist");
        }

        if (!whatsappRepository.isUserMemberOfGroup(sender, group)) {
            throw new Exception("You are not allowed to send message");
        }

        return whatsappRepository.sendMessage(message, sender, group);
    }

    public String changeAdmin(User approver, Group group) throws Exception {
        // Check if the group exists
        if (!whatsappRepository.groupExists(group)) {
            throw new Exception("Group does not exist");
        }

        // Check if the approver is the current admin of the group
        if (!whatsappRepository.isAdmin(approver)) {
            throw new Exception("Approver does not have rights");
        }

        // Get the users of the group
        List<User> users = whatsappRepository.getGroupUsers(group);

        // Check if there are participants in the group
        if (users.isEmpty()) {
            throw new Exception("No participants in the group");
        }

        // Select the new admin (for simplicity, selecting the first user)
        User newAdmin = users.get(0);

        // Call the repository method to change the admin
        return whatsappRepository.changeAdmin(newAdmin, group);
    }

    public int removeUser(User user) throws Exception {
        // Check if the user exists in any group
        if (!whatsappRepository.isUserInAnyGroup(user)) {
            throw new Exception("User not found");
        }

        // Check if the user is an admin of any group
        if (whatsappRepository.isAdmin(user)) {
            throw new Exception("Cannot remove admin");
        }

        // Remove the user from the group and delete their messages
        int updatedUserCount = whatsappRepository.removeUserFromGroup(user);
        int updatedGroupMessageCount = whatsappRepository.removeUserMessages(user);
        int updatedOverallMessageCount = whatsappRepository.removeUserOverallMessages(user);

        // Return the combined count of users in the group and messages in the group and overall
        return updatedUserCount + updatedGroupMessageCount + updatedOverallMessageCount;
    }

    public String findMessage(Date start, Date end, int K) throws Exception {
        // Find the Kth latest message between start and end dates
        return whatsappRepository.findMessage(start, end, K);
    }

    public boolean isUserMemberOfGroup(User user, Group group) {
        // Check if the user is a member of the provided group
        return whatsappRepository.isUserMemberOfGroup(user, group);
    }

    public boolean isAdmin(User user) {
        // Check if the user is an admin
        return whatsappRepository.isAdmin(user);
    }

    public int removeUserFromGroup(User user) {
        // Remove the user from all groups and return the count of affected groups
        return whatsappRepository.removeUserFromGroup(user);
    }

    public int removeUserMessages(User user) {
        // Remove all messages sent by the user and return the count of removed messages
        return whatsappRepository.removeUserMessages(user);
    }

    public int removeUserOverallMessages(User user) {
        // Remove all messages associated with the user from all groups and return the count of removed messages
        return whatsappRepository.removeUserOverallMessages(user);
    }

    public boolean isUserMobileExists(String mobile) {
        // Check if the mobile number exists in the repository
        return whatsappRepository.isUserMobileExists(mobile);
    }

    public String findKthLatestMessage(Date start, Date end, int k) throws Exception {
        // Find the Kth latest message between start and end dates
        return whatsappRepository.findKthLatestMessage(start, end, k);
    }

    public User getUserById(String userId) {
        return whatsappRepository.getUserById(userId);
    }

    public Group getGroupById(String groupId) {
        return whatsappRepository.getGroupById(groupId);
    }
}