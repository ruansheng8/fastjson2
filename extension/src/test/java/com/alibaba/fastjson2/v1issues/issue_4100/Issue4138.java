package com.alibaba.fastjson2.v1issues.issue_4100;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.annotation.JSONCreator;
import com.alibaba.fastjson2.annotation.JSONField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Issue4138 {
    // 配置AutoTypeFilter
    static JSONReader.Filter filter = JSONReader.autoTypeFilter(
            // 这里可以配置多个前缀
            "org.springframework.security.core.authority.SimpleGrantedAuthority",
            "org.springframework.util.LinkedCaseInsensitiveMap"
    );

    @BeforeEach
    private void init() {
        JSON.mixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixin.class);
    }

    static class SimpleGrantedAuthorityMixin {
        @JSONCreator
        public SimpleGrantedAuthorityMixin(@JSONField(name = "authority", alternateNames = "role") String role) {

        }
    }

    public Object deserialize(byte[] bytes) {
        return JSON.parseObject(bytes, Object.class, filter);
    }

    public Object deserialize(String str) {
        return JSON.parseObject(str, Object.class, filter);
    }

    public <T> T deserialize(String str, Class<T> objectClass) {
        return JSON.parseObject(str, objectClass, filter);
    }

    @Test
    public void test() {
        String json = "{\"@type\":\"org.springframework.security.core.authority.SimpleGrantedAuthority\",\"role\":\"abc\"}";
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        Object object = deserialize(bytes);
        assertEquals(org.springframework.security.core.authority.SimpleGrantedAuthority.class, object.getClass());
    }

    @Test
    public void testAuthority() {
        String json = "{\"@type\":\"org.springframework.security.core.authority.SimpleGrantedAuthority\",\"authority\":\"abc\"}";
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        Object object = deserialize(bytes);
        assertEquals(org.springframework.security.core.authority.SimpleGrantedAuthority.class, object.getClass());
    }

    @Test
    public void test1() {
        String json = "{\"@type\":\"org.springframework.util.LinkedCaseInsensitiveMap\"}";
        Object object = deserialize(json);
        assertEquals(org.springframework.util.LinkedCaseInsensitiveMap.class, object.getClass());
    }

    @Test
    public void test2() {
        String json = "{\"value\":{\"@type\":\"org.springframework.util.LinkedCaseInsensitiveMap\"}}";
        Bean bean = deserialize(json, Bean.class);
        assertEquals(org.springframework.util.LinkedCaseInsensitiveMap.class, bean.value.getClass());
    }

    public static class Bean {
        public Object value;
    }
}
