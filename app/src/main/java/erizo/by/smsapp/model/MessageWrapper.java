package erizo.by.smsapp.model;

import java.util.List;

/**
 * Created by valera on 14.7.17.
 */

public class MessageWrapper {
    private List<Message> messages;

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "MessageWrapper{" +
                "messages=" + messages +
                '}';
    }
}
