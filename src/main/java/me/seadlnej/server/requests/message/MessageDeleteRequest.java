package me.seadlnej.server.requests.message;


public class MessageDeleteRequest {

    private String token;
    private Long messageId;
    private Long conversationId;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

}