package me.logicologist.wordiple.server.managers;

import com.olziedev.olziesocket.framework.PacketArguments;
import me.logicologist.wordiple.server.user.WordipleUser;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SessionManager {

    private static SessionManager instance;
    private final HashMap<UUID, WordipleUser> sessions;
    private final HashMap<String, PacketArguments> signupSessions;

    public SessionManager() {
        this.sessions = new HashMap<>();
        this.signupSessions = new HashMap<>();
        instance = this;
    }

    public WordipleUser getSessionFromToken(UUID token) {
        if (sessions.containsKey(token)) {
            return sessions.get(token);
        }
        return null;
    }

    public UUID createSession(String username, String password) {
        if (!DatabaseManager.instance.validateLogin(username, password)) {
            return null;
        }
        WordipleUser user = DatabaseManager.instance.constructWordipleUser(username);
        List<UUID> invalidSessionIds = new ArrayList<>();
        sessions.forEach((k, v) -> {
            if (v.getId().equals(user.getId())) invalidSessionIds.add(k);
        });
        for (UUID sessionId : invalidSessionIds) {
            DatabaseManager.instance.saveUser(sessions.get(sessionId));
            this.sessions.remove(sessionId);
        }
        UUID newSessionId = UUID.randomUUID();
        while (this.sessions.containsKey(newSessionId)) {
            newSessionId = UUID.randomUUID();
        }
        this.sessions.put(newSessionId, user);
        return newSessionId;
    }

    public String createSignupSession(PacketArguments packetArguments) {
        final String username = "wordiple@gmail.com";
        final String password = "LK8!MUY'^{-qW%es";

        Pattern validEmail = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Pattern usernamePattern = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");

        if (!validEmail.matcher(packetArguments.get("email", String.class)).matches())
            return "Invalid email address.";

        if (!DatabaseManager.instance.emailAvailable(packetArguments.get("email", String.class)))
            return "Email address already in use.";

        if (!usernamePattern.matcher(packetArguments.get("username", String.class)).matches())
            return "Invalid username.";

        if (!DatabaseManager.instance.usernameAvailable(packetArguments.get("username", String.class)))
            return "Username already taken.";

        if (packetArguments.get("password", String.class).length() <= 5)
            return "Invalid password.";

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
                    InternetAddress.parse(packetArguments.get("email", String.class))
            );
            message.setSubject("Wordiple Email Verification Code");

            StringBuilder stringBuilder = new StringBuilder();
            String possibleCodePart = "0123456789";

            for (int i = 0; i < 6; i++) {
                stringBuilder.append(possibleCodePart.charAt(new Random().nextInt(10)));
            }

            message.setText("Hello, " + packetArguments.get("username", String.class) + "!\n" +
                    "You have signed up for a wordiple account! In order to finish registration, please type in this verification code below:\n" +
                    stringBuilder.toString() + " (This code will expire in 10 minutes.)\n" +
                    "\n" +
                    "If you have not signed up or do not recognise this action, simply ignore this email.\n" +
                    "\n" +
                    "We hope you enjoy wordiple!\n" +
                    "\n" +
                    "[Do not reply to this email. You will not be given a response.]"
            );
            System.out.println("Sending email...");

            new Thread(() -> {
                try {
                    Transport.send(message);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
            System.out.println("Email sent!");
            this.signupSessions.keySet().stream().filter(x -> x.contains(packetArguments.get("email", String.class))).collect(Collectors.toList()).forEach(this.signupSessions::remove);
            this.signupSessions.put(stringBuilder + ":" + packetArguments.get("email", String.class), packetArguments);
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            signupSessions.remove(stringBuilder.toString());
                        }
                    },
                    10 * 60 * 1000
            );
            return "Success";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "There was an error processing your request. Please try again!";
    }

    public UUID createNewAccount(String verificationCode, String email) {
        PacketArguments packetArguments = signupSessions.remove(verificationCode + ":" + email);
        if (packetArguments == null) return null;
        WordipleUser wordipleUser = new WordipleUser(packetArguments.get("email", String.class), packetArguments.get("username", String.class));
        DatabaseManager.instance.createUser(wordipleUser, packetArguments.get("password", String.class));

        return createSession(packetArguments.get("username", String.class), packetArguments.get("password", String.class));
    }

    public static SessionManager getInstance() {
        return instance;
    }

}
