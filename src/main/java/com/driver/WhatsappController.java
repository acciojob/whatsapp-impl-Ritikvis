package com.driver;

import java.util.*;

import com.driver.Message;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/whatsapp")
public class WhatsappController<SenderId, GroupId> {

    // Autowiring will not work in this case, no need to add @Autowired annotation
    WhatsappService whatsappService = new WhatsappService();

    @PostMapping("/add-user")
    public String createUser(@RequestParam String name, @RequestParam String mobile) throws Exception {
        if (whatsappService.isUserMobileExists(mobile)) {
            throw new Exception("User already exists.");
        }
        return whatsappService.createUser(name, mobile);
    }

    @PostMapping("/add-group")
    public Group createGroup(@RequestBody List<User> users) {
        return whatsappService.createGroup(users);
    }

    @PostMapping("/add-message")
    public int createMessage(@RequestParam String content) {
        return whatsappService.createMessage(content);
    }

    @PostMapping("/send-message")
    public int sendMessage(@RequestBody Message message,
                           @RequestParam SenderId senderId,
                           @RequestParam GroupId groupId) throws Exception {
        // Retrieve the sender and group using their IDs
        User sender = whatsappService.getUserById(User.getName());
        Group group = whatsappService.getGroupById(Group.getName());

        // Check if the group exists
        if (group == null) {
            throw new IllegalArgumentException("Group does not exist.");
        }

        // Check if the sender is a member of the group
        if (!whatsappService.isUserMemberOfGroup(sender, group)) {
            throw new IllegalArgumentException("You are not allowed to send message.");
        }

        // Call the service layer to send the message
        int numMessages = whatsappService.sendMessage((org.apache.logging.log4j.message.Message) message, sender, group);
        return numMessages;
    }

    @PutMapping("/change-admin")
    public String changeAdmin(@RequestBody User approver,
                              @RequestBody User user,
                              @RequestBody Group group) throws Exception {
        // Check if the group exists
        if (group == null) {
            throw new IllegalArgumentException("Group does not exist.");
        }

        // Check if the approver is an admin
        if (!whatsappService.isAdmin(approver)) {
            throw new IllegalArgumentException("Approver does not have rights.");
        }

        // Check if the user is a participant in the group
        if (!whatsappService.isUserMemberOfGroup(user, group)) {
            throw new IllegalArgumentException("User is not a participant.");
        }

        // Call the service layer to change the admin
        whatsappService.changeAdmin(user, group);
        return "SUCCESS";
    }

    @DeleteMapping("/remove-user")
    public ResponseEntity<String> removeUser(@RequestParam String userId) {
        try {
            User user = whatsappService.getUserById(userId);
            if (user == null) {
                throw new IllegalArgumentException("User not found.");
            }
            if (whatsappService.isAdmin(user)) {
                throw new IllegalArgumentException("Cannot remove admin.");
            }
            int updatedGroupUsers = whatsappService.removeUserFromGroup(user);
            int updatedGroupMessages = whatsappService.removeUserMessages(user);
            int updatedOverallMessages = whatsappService.removeUserOverallMessages(user);
            return ResponseEntity.ok("User removed successfully. Updated counts: Group Users=" + updatedGroupUsers +
                    ", Group Messages=" + updatedGroupMessages + ", Overall Messages=" + updatedOverallMessages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/find-messages")
    public ResponseEntity<String> findMessage(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
                                              @RequestParam int K) {
        try {
            String latestMessage = whatsappService.findKthLatestMessage(start, end, K);
            return ResponseEntity.ok("The " + K + "th latest message between " + start + " and " + end + " is: " + latestMessage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
}

