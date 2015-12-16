package com.lowzj.test;

import lombok.Data;

/**
 * Copyright 2015, Easemob.
 * All rights reserved.
 * Author: zhangjin@easemob.com
 */
@Data
public class RunContext {
    private String prefix;

    private int realmsCount;
    private int realmsSucceed;

    private int usersCount;
    private int usersSucceed;

    private int clientsCount;
    private int clientsSucceed;

    private int rolesCount;
    private int rolesSucceed;

    private int readUserOneTime;
}
