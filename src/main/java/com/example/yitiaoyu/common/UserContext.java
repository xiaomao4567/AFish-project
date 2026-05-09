package com.example.yitiaoyu.common;

import com.example.yitiaoyu.pojo.entity.Employee;

public class UserContext {

    private static final ThreadLocal<Employee> userThreadLocal = new ThreadLocal<>();

    public static void setUser(Employee employee) {
        userThreadLocal.set(employee);
    }

    public static Employee getUser() {
        return userThreadLocal.get();
    }

    public static Long getUserId() {
        Employee employee = userThreadLocal.get();
        return employee != null ? employee.getId() : null;
    }

    public static String getUsername() {
        Employee employee = userThreadLocal.get();
        return employee != null ? employee.getUsername() : null;
    }

    public static String getRole() {
        Employee employee = userThreadLocal.get();
        return employee != null ? employee.getRole() : null;
    }

    public static void clear() {
        userThreadLocal.remove();
    }
}
