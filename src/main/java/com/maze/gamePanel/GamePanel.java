package com.maze.gamePanel;

import com.maze.gameFrame.GameFrame;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 画布类
 */
public class GamePanel extends JPanel implements ActionListener {
    private JMenuBar jMenuBar = null;
    private GameFrame gameFrame = null;
    private GamePanel gamePanel = null;
    private String gameFlag = "status"; //游戏状态

    //构造方法
    public GamePanel(GameFrame gameFrame) {
        this.setLayout(null);
        this.setOpaque(false);
        this.gameFrame = gameFrame;
        this.gamePanel = this;

        //创建菜单项
        createMenu();
    }

    //设置字体
    private Font createFont(){
        return new Font("微软雅黑",Font.BOLD,18);
    }
    
    //创建菜单
    private void createMenu(){
        /* 创建JMenuBar */
        jMenuBar = new JMenuBar();
        //取得字体
        Font getFont = createFont();

        //创建游戏菜单选项并添加字体
        JMenu jMenu1 = new JMenu("游戏");
        jMenu1.setFont(getFont);
        JMenu jMenu2 = new JMenu("帮助");
        jMenu2.setFont(getFont);
        //添加子菜单
        JMenuItem newGame = new JMenuItem("新游戏");
        newGame.setFont(getFont);
        JMenuItem esc = new JMenuItem("退出");
        esc.setFont(getFont);
        //子菜单添加到主菜单中
        jMenu1.add(newGame);
        jMenu1.add(esc);

        //添加子菜单项
        JMenuItem help = new JMenuItem("操作帮助");
        help.setFont(getFont);
        JMenuItem win = new JMenuItem("胜利条件");
        win.setFont(getFont);
        //子菜单追加到主菜单中
        jMenu2.add(help);
        jMenu2.add(win);

        jMenuBar.add(jMenu1);
        jMenuBar.add(jMenu2);

        gameFrame.setJMenuBar(jMenuBar);
        //添加监听
        newGame.addActionListener(this);
        esc.addActionListener(this);
        help.addActionListener(this);
        win.addActionListener(this);

        //设置指令
        newGame.setActionCommand("restart");
        esc.setActionCommand("exit");
        help.setActionCommand("help");
        win.setActionCommand("win");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        System.out.println(command);
        UIManager.put("OptionPane.buttonFont",new FontUIResource(new Font("微软雅黑",Font.ITALIC,18)));
        UIManager.put("OptionPane.buttonFont",new FontUIResource(new Font("微软雅黑",Font.ITALIC,18)));

        if ("exit".equals(command)){
            Object[] options = {"确认","取消"};
            int response = JOptionPane.showOptionDialog(this, "您确认要退出吗", "",
                    JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                    options, options[0]);
            if (response == 0)
                System.exit(0);
        } else if ("restart".equals(command)){
            JOptionPane.showMessageDialog(null, "通过键盘的上下左右来移动",
                    "提示！", JOptionPane.INFORMATION_MESSAGE);
        } else if ("win".equals(command)){
            JOptionPane.showMessageDialog(null, "移动到终点获得胜利",
                    "提示！", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    void restart(){

    }
}
