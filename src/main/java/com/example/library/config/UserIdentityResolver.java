package com.example.library.config;

import com.example.library.domain.UserSnapshot;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserIdentityResolver {
    private final boolean demoEnabled;
    private final UserSnapshot demoUser;

    public UserIdentityResolver(
            @Value("${app.demo-user.enabled:false}") boolean demoEnabled,
            @Value("${app.demo-user.id:demo-user}") String id,
            @Value("${app.demo-user.type:INTERNAL}") String type,
            @Value("${app.demo-user.level:L2}") String level,
            @Value("${app.demo-user.department-id:demo}") String departmentId,
            @Value("${app.demo-user.department-name:Demo Department}") String departmentName) {
        this.demoEnabled = demoEnabled;
        this.demoUser = new UserSnapshot(id, type, level, departmentId, departmentName);
    }

    public UserSnapshot resolve(HttpServletRequest request) {
        String id = request.getHeader("X-User-Id");
        String type = request.getHeader("X-User-Type");
        String level = request.getHeader("X-User-Level");
        String departmentId = request.getHeader("X-Department-Id");
        String departmentName = request.getHeader("X-Department-Name");
        if (present(id) && present(type) && present(level) && present(departmentId) && present(departmentName)) {
            return new UserSnapshot(id, type, level, departmentId, departmentName);
        }
        if (demoEnabled) {
            return demoUser;
        }
        throw new IllegalArgumentException("Missing caller identity headers");
    }

    private boolean present(String value) {
        return value != null && !value.isBlank();
    }
}
