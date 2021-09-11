package com.maze.gameFrame;

import javax.swing.*;

/**
 * 窗体类
 * create is time：2021/8/30 13:00
 */
public class GameFrame extends JFrame {
    //构造方法
    public GameFrame(){
        setTitle("迷宫");     //设置标题
        setSize(420,470);   //设置窗体大小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);     //关闭后进程退出
        setLocationRelativeTo(null);    //居中
        setResizable(false);            //不允许变大
        setVisible(true);               //设置显示窗体

    }
}
