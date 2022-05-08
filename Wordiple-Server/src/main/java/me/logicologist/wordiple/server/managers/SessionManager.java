package me.logicologist.wordiple.server.managers;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketHolder;
import me.logicologist.wordiple.server.WordipleServer;
import me.logicologist.wordiple.server.packets.auth.LogoutPacket;
import me.logicologist.wordiple.server.user.WordipleUser;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SessionManager {

    private static SessionManager instance;
    private final HashMap<UUID, WordipleUser> sessions;
    private final HashMap<String, PacketArguments> signupSessions;
    private final HashMap<String, String> resetPasswords;

    public SessionManager() {
        this.sessions = new HashMap<>();
        this.signupSessions = new HashMap<>();
        this.resetPasswords = new HashMap<>();
        instance = this;
    }

    public WordipleUser getSessionFromToken(UUID token) {
        return sessions.get(token);
    }

    public WordipleUser getSessionFromUsername(String username) {
        return sessions.values().stream().filter(user -> user.getUsername().equals(username)).findFirst().orElse(null);
    }

    public void removeSession(UUID session) {
        sessions.remove(session);
    }

    public UUID createSession(String username, String passwordHash, PacketHolder socket) {
        if (!DatabaseManager.instance.validateLogin(username, passwordHash)) {
            return null;
        }
        WordipleUser user = DatabaseManager.instance.constructWordipleUser(username, socket);
        logoutMatchingUsers(user.getId());
        UUID newSessionId;
        do {
            newSessionId = UUID.randomUUID();
        } while (this.sessions.containsKey(newSessionId));

        this.sessions.put(newSessionId, user);
        return newSessionId;
    }

    public String createSignupSession(PacketArguments packetArguments) {
        // Username and password for the email
        final String username = "wordiple@gmail.com";
        final String password = "LK8!MUY'^{-qW%es";

        // Server sided checking for valid email, username, and password salt.
        Pattern validEmail = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Pattern usernamePattern = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");
        Pattern validSalt = Pattern.compile("^[a-zA-Z0-9]{16}$");

        if (!validEmail.matcher(packetArguments.get("email", String.class)).matches())
            return "Invalid email address.";

        if (!DatabaseManager.instance.emailAvailable(packetArguments.get("email", String.class)))
            return "Email address already in use.";

        if (!usernamePattern.matcher(packetArguments.get("username", String.class)).matches())
            return "Invalid username.";

        if (!DatabaseManager.instance.usernameAvailable(packetArguments.get("username", String.class)))
            return "Username already taken.";

        if (!validSalt.matcher(packetArguments.get("salt", String.class)).matches()) {
            return "Security feature violation. Please re-install the OFFICIAL client.";
        }

        // Mail properties to set for sending an email
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Create a new mail session to send recipient email
        Session session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                }
        );

        try {
            // Create a new email message and set content
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("wordiple@gmail.com", "Wordiple System"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(packetArguments.get("email", String.class))
            );
            message.setSubject("Wordiple Email Verification Code");

            // Create a verification code and add it to the email
            StringBuilder stringBuilder = new StringBuilder();
            String possibleCodePart = "0123456789";

            for (int i = 0; i < 6; i++) {
                stringBuilder.append(possibleCodePart.charAt(new Random().nextInt(10)));
            }

            // Set email content
            message.setText("Hello, " + packetArguments.get("username", String.class) + "!\n" +
                    "You have signed up for a Wordiple account! In order to finish registration, please type in this verification code below:\n" +
                    stringBuilder.toString() + " (This code will expire in 10 minutes.)\n" +
                    "\n" +
                    "If you have not signed up or do not recognise this action, simply ignore this email.\n" +
                    "\n" +
                    "We hope you enjoy Wordiple!\n" +
                    "\n" +
                    "[Do not reply to this email. You will not be given a response.]"
            );
            // Log the email sending
            WordipleServer.getLogger().info("Sending email to " + packetArguments.get("email", String.class));

            // Send the email
            WordipleServer.getExecutor().submit(() -> {
                try {
                    Transport.send(message);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            // Log the email sent
            WordipleServer.getLogger().info("Email sent!");

            // Remove previous signup sessions for the email
            this.signupSessions.keySet().stream().filter(x -> x.contains(packetArguments.get("email", String.class))).collect(Collectors.toList()).forEach(this.signupSessions::remove);

            // Add the new signup session
            this.signupSessions.put(stringBuilder + ":" + packetArguments.get("email", String.class), packetArguments);

            // Remove verification code if not used in 10 minutes
            WordipleServer.getExecutor().schedule(() -> signupSessions.remove(stringBuilder.toString()), 10 * 60 * 1000, TimeUnit.MILLISECONDS);
            return "Success";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "There was an error processing your request. Please try again!";
    }

    public void resetPassword(String email, UUID code, String name) {
        final String username = "wordiple@gmail.com";
        final String password = "LK8!MUY'^{-qW%es";

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("wordiple@gmail.com", "Wordiple System"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(email)
            );
            message.setSubject("Wordiple Reset Password");
            message.setText("Hello, " + name + "!\n" +
                    "You have requested to reset your password for your Wordiple account! In order to reset your password, please type in this verification code below:\n" + code + " (This code will expire in 10 minutes.)\n" +
                    "\n" +
                    "If you have not requested to reset your password or do not recognise this action, simply ignore this email.\n" +
                    "\n" +
                    "We hope you enjoy Wordiple!\n" +
                    "\n" +
                    "[Do not reply to this email. You will not be given a response.]"
            );
            WordipleServer.getLogger().info("Sending email to " + email);

            WordipleServer.getExecutor().submit(() -> {
                try {
                    Transport.send(message);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            WordipleServer.getLogger().info("Email sent!");
            this.resetPasswords.put(email, code.toString());
            WordipleServer.getExecutor().schedule(() -> resetPasswords.remove(email), 10 * 60 * 1000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UUID createNewAccount(String verificationCode, String email) {
        PacketArguments packetArguments = signupSessions.remove(verificationCode + ":" + email);
        if (packetArguments == null) return null;

        WordipleUser wordipleUser = new WordipleUser(packetArguments.get("email", String.class), packetArguments.get("username", String.class));
        DatabaseManager.instance.createUser(wordipleUser, packetArguments.get("password_hash", String.class), packetArguments.get("salt", String.class));
        return createSession(packetArguments.get("username", String.class), packetArguments.get("password_hash", String.class), packetArguments.getPacketHolder());
    }

    public void logoutMatchingUsers(UUID uuid) {
        List<UUID> invalidSessionIds = new ArrayList<>();
        sessions.forEach((k, v) -> {
            if (v.getId().equals(uuid)) invalidSessionIds.add(k);
        });
        LogoutPacket logoutPacket = PacketManager.getInstance().getSocket().getPacket(LogoutPacket.class);
        for (UUID sessionId : invalidSessionIds) {
            WordipleUser wordipleUser = sessions.get(sessionId);
            logoutPacket.sendPacket(packet -> packet.getPacketType().getArguments().setValues("reason", "You have been logged out."), wordipleUser.getOutputStream());
            DatabaseManager.instance.saveUser(wordipleUser);
            this.sessions.remove(sessionId);
        }
    }

    public static SessionManager getInstance() {
        return instance;
    }

    public void close() {
        this.sessions.forEach((k, v) -> DatabaseManager.instance.saveUser(v));
    }

    public boolean isResetCodeValid(String email, String code) {
        boolean success = resetPasswords.get(email) != null && resetPasswords.get(email).equals(code);
        if (success) resetPasswords.remove(email);

        return success;
    }
}
