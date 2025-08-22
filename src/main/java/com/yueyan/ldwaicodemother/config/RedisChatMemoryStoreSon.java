//package com.yueyan.ldwaicodemother.config;
//
//import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStoreException;
//import dev.langchain4j.data.message.ChatMessage;
//import dev.langchain4j.data.message.ChatMessageDeserializer;
//import dev.langchain4j.data.message.ChatMessageSerializer;
//import dev.langchain4j.internal.ValidationUtils;
//import dev.langchain4j.store.memory.chat.ChatMemoryStore;
//import lombok.extern.slf4j.Slf4j;
//import redis.clients.jedis.JedisPooled;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Slf4j
//public class RedisChatMemoryStoreSon implements ChatMemoryStore {
//
//
//    private final JedisPooled client;
//    private final String keyPrefix;
//    private final Long ttl;
//
////    public RedisChatMemoryStore(String host, Integer port, String user, String password) {
////        this(host, port, user, password, "", 0L);
////    }
////
////    public RedisChatMemoryStore(String host, Integer port, String user, String password, String prefix, Long ttl) {
////        String finalHost = ValidationUtils.ensureNotBlank(host, "host");
////        int finalPort = (Integer)ValidationUtils.ensureNotNull(port, "port");
////        if (user != null) {
////            String finalUser = ValidationUtils.ensureNotBlank(user, "user");
////            String finalPassword = ValidationUtils.ensureNotBlank(password, "password");
////            this.client = new JedisPooled(finalHost, finalPort, finalUser, finalPassword);
////        } else {
////            this.client = new JedisPooled(finalHost, finalPort);
////        }
////
////        this.keyPrefix = (String)ValidationUtils.ensureNotNull(prefix, "prefix");
////        this.ttl = (Long)ValidationUtils.ensureNotNull(ttl, "ttl");
////    }
////    public RedisChatMemoryStoreSon(String host, Integer port, String user, String password) {
////        this(host, port, user, password, "", 0L);
////    }
//
//    public RedisChatMemoryStoreSon(String host, Integer port, String password, String prefix, String user, Long ttl) {
//        JedisPooled client1;
//        String finalHost = ValidationUtils.ensureNotBlank(host, "host");
//        int finalPort = (Integer)ValidationUtils.ensureNotNull(port, "port");
//
//        try {
//            // 尝试使用URI方式连接
//            if (password != null && !password.trim().isEmpty()) {
//                String redisUri = "redis://:" + password + "@" + finalHost + ":" + finalPort;
//                client1 = new JedisPooled(redisUri);
//            } else {
//                String redisUri = "redis://" + finalHost + ":" + finalPort;
//                client1 = new JedisPooled(redisUri);
//            }
//        } catch (Exception e) {
//            if (password != null && !password.trim().isEmpty()) {
//                client1 = new JedisPooled(finalHost, finalPort, Boolean.parseBoolean(password));
//            } else {
//                client1 = new JedisPooled(finalHost, finalPort);
//            }
//            log.info("Failed to connect to Redis using URI, trying to connect using host and port");
//        }
//
//        this.client = client1;
//        this.keyPrefix = (String)ValidationUtils.ensureNotNull(prefix, "prefix");
//        this.ttl = (Long)ValidationUtils.ensureNotNull(ttl, "ttl");
//    }
//
//
//
//    public List<ChatMessage> getMessages(Object memoryId) {
//        String json = this.client.get(this.toRedisKey(memoryId));
//        return (List)(json == null ? new ArrayList() : ChatMessageDeserializer.messagesFromJson(json));
//    }
//
//    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
//        String json = ChatMessageSerializer.messagesToJson((List)ValidationUtils.ensureNotEmpty(messages, "messages"));
//        String key = this.toRedisKey(memoryId);
//        String res;
//        if (this.ttl > 0L) {
//            res = this.client.setex(key, this.ttl, json);
//        } else {
//            res = this.client.set(key, json);
//        }
//
//        if (!"OK".equals(res)) {
//            throw new RedisChatMemoryStoreException("Set memory error, msg=" + res);
//        }
//    }
//
//    public void deleteMessages(Object memoryId) {
//        this.client.del(this.toRedisKey(memoryId));
//    }
//
//    private String toMemoryIdString(Object memoryId) {
//        boolean isNullOrEmpty = memoryId == null || memoryId.toString().trim().isEmpty();
//        if (isNullOrEmpty) {
//            throw new IllegalArgumentException("memoryId cannot be null or empty");
//        } else {
//            return memoryId.toString();
//        }
//    }
//
//    private String toRedisKey(Object memoryId) {
//        String var10000 = this.keyPrefix;
//        return var10000 + this.toMemoryIdString(memoryId);
//    }
//
//    public static RedisChatMemoryStoreSon.Builder builder() {
//        return new RedisChatMemoryStoreSon.Builder();
//    }
//
//    public static class Builder {
//        private String host;
//        private Integer port;
//        private String user;
//        private String password;
//        private Long ttl = 0L;
//        private String prefix = "";
//
//        public Builder() {
//        }
//
//        public RedisChatMemoryStoreSon.Builder host(String host) {
//            this.host = host;
//            return this;
//        }
//
//        public RedisChatMemoryStoreSon.Builder port(Integer port) {
//            this.port = port;
//            return this;
//        }
//
//        public RedisChatMemoryStoreSon.Builder user(String user) {
//            this.user = user;
//            return this;
//        }
//
//        public RedisChatMemoryStoreSon.Builder password(String password) {
//            this.password = password;
//            return this;
//        }
//
//        public RedisChatMemoryStoreSon.Builder ttl(Long ttl) {
//            this.ttl = ttl;
//            return this;
//        }
//
//        public RedisChatMemoryStoreSon.Builder prefix(String prefix) {
//            this.prefix = prefix;
//            return this;
//        }
//
//        public RedisChatMemoryStoreSon build() {
//            return new RedisChatMemoryStoreSon(this.host, this.port, this.user, this.password, this.prefix, this.ttl);
//        }
//    }
//
//}
