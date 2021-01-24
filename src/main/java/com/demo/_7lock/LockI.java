package com.demo._7lock;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁接口
 *
 * @author jerome
 * @date 2016/8/26 22:49
 */
public interface LockI {

    /*
     * 获取锁，如果没有得到就等待
     */
    void getLock() throws Exception;

    /*
     * 获取锁，直到超时
     */
    boolean getLock(long timeOut, TimeUnit unit) throws Exception;

    /*
     * 释放锁
     */
    void releaseLock() throws Exception;


}
