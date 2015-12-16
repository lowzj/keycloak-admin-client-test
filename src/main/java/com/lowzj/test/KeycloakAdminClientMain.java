package com.lowzj.test;

/**
 * Copyright 2015, Easemob.
 * All rights reserved.
 * Author: zhangjin@easemob.com
 */
public class KeycloakAdminClientMain {
    public static void main(String[] args) {
        TimeStat timeStat = new TimeStat().start();
        KeycloakPerformanceTest performanceTest = KeycloakPerformanceTest.getInstance();
        RunContext runContext = new RunContext();
        runContext.setRealmsCount(10);
        runContext.setUsersCount(1000);
        runContext.setClientsCount(100);
        runContext.setRolesCount(100);
        runContext.setPrefix("test_" + TimeStat.timeSecond());
        performanceTest.run(runContext);
        timeStat.step("test");
    }
}
