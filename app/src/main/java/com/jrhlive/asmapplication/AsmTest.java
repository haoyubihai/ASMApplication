package com.jrhlive.asmapplication;

import java.util.Random;

/**
 * **************************************
 * 项目名称:ASMApplication
 *
 * @Author jiaruihua
 * 邮箱：jiaruihua@ksjgs.com
 * 创建时间: 2020/6/8     1:54 PM
 * 用途:
 * **************************************
 */
class AsmTest {

    public void calTime(){
        long startTime = System.currentTimeMillis();
        try {
            Thread.sleep(new Random().nextInt(1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("花费时间="+(endTime-startTime));
    }
}
