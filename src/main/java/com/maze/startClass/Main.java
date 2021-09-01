package com.maze.startClass;

import com.maze.gameFrame.GameFrame;
import com.maze.gamePanel.GamePanel;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 */
@SpringBootApplication(scanBasePackages = "com.maze")
public class Main {
    public static void main(String[] args) {
        GameFrame gameFrame = new GameFrame();
        GamePanel gamePanel = new GamePanel(gameFrame);
        gameFrame.add(gamePanel);
        gameFrame.setVisible(true);
    }
}
