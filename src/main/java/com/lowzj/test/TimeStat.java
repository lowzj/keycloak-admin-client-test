package com.lowzj.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright 2015, Easemob.
 * All rights reserved.
 * Author: zhangjin@easemob.com
 */
public class TimeStat {
    private static final Logger logger = LoggerFactory.getLogger(TimeStat.class);

    private long preTime;
    private long totalTime;
    private int stepCount;

    public static long timeMilli() {
        return System.currentTimeMillis();
    }

    public static int timeSecond() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public TimeStat start() {
        preTime = timeMilli();
        totalTime = 0;
        stepCount = 0;
        return this;
    }

    public long step(String msg) {
        return step(msg, true);
    }
    public long step(String msg, boolean increaseStepCount) {
        if (increaseStepCount)
            ++stepCount;
        long usedTime = timeMilli() - preTime;
        logger.info(msg + " step " + stepCount + " usedTime=" + usedTime + " ms");
        preTime = timeMilli();
        totalTime += usedTime;
        return usedTime;
    }

    public void stat(String msg) {
        long averageTime = stepCount > 0 ? totalTime / stepCount : totalTime;
        logger.info(msg + " statistic(ms):" +
                " count=" + stepCount +
                " totalTime=" + totalTime +
                " averageTime=" + averageTime);
    }
}
