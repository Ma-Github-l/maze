package com.maze.gamePanel;

import com.maze.gameFrame.GameFrame;
import com.maze.gameOrigin.Rect;
import com.maze.gameUnit.Block;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * 画布类
 * create is time：2021/8/31 15:00
 *
 * @author maMaster
 */
public class GamePanel extends JPanel implements ActionListener {
    private JMenuBar jMenuBar = null;
    private GameFrame gameFrame = null;
    private GamePanel gamePanel = null;
    private String gameFlag = "status"; //游戏状态

    //初始化相关参数
    public final int ROWS = 20; //行
    public final int COLS = 20; //列
    public final int height = 20;   //每块宽高
    public Block[][] blocks = null;    //存储每个单元的二维数组

    public Rect start ;//开始方形
    public Rect end ;//终点方形

    //构造方法
    public GamePanel(GameFrame gameFrame) {
        this.setLayout(null);
        this.setOpaque(false);
        this.gameFrame = gameFrame;
        this.gamePanel = this;

        //创建菜单项
        createMenu();

        //绘制迷宫表格
        createBlocks();
        //计算处理线路
        computed();

        //创建开始结束的方形
        createRects();
        //添加键盘事件监听
        createKeyListener();
    }

    //创建开始结束的方形
    private void createRects() {
        start = new Rect(0, 0, height, "start") ;
        end = new Rect(ROWS-1, COLS-1, height, "end") ;
    }

    //设置字体
    private Font createFont() {
        return new Font("微软雅黑", Font.BOLD, 18);
    }

    //创建菜单
    private void createMenu() {
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
        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("微软雅黑", Font.ITALIC, 18)));
        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("微软雅黑", Font.ITALIC, 18)));

        if ("exit".equals(command)) {
            Object[] options = {"确认", "取消"};
            int response = JOptionPane.showOptionDialog(this, "您确认要退出吗", "",
                    JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                    options, options[0]);
            if (response == 0)
                System.exit(0);
        } else if ("restart".equals(command)) {
            restart();
        } else if ("help".equals(command)) {
            JOptionPane.showMessageDialog(null, "通过键盘的上下左右来移动",
                    "提示！", JOptionPane.INFORMATION_MESSAGE);
        } else if ("win".equals(command)) {
            JOptionPane.showMessageDialog(null, "移动到终点获得胜利",
                    "提示！", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    //创建数组内容
    private void createBlocks() {
        blocks = new Block[ROWS][COLS];
        Block block;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                block = new Block(i, j, height, this);
                blocks[i][j] = block;
            }
        }
    }

    //绘制迷宫块
    private void drawBlock(Graphics graphics) {
        Block block;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                block = blocks[i][j];
                if (block != null) {
                    block.draw(graphics);
                }
            }
        }
    }

    /**
     * 线路的计算处理:深度优先算法
     * 就是从起点开始走，寻找它的上下左右4个邻居，然后随机一个走，到走不通的时候就返回上一步继续走，直到全部单元都走完
     */
    private void computed(){
	/*
	1.将起点作为当前迷宫单元并标记为已访问
	2.当还存在未标记的迷宫单元，进行循环
		1).如果当前迷宫单元有未被访问过的的相邻的迷宫单元
			(1).随机选择一个未访问的相邻迷宫单元
			(2).将当前迷宫单元入栈
			(3).移除当前迷宫单元与相邻迷宫单元的墙
			(4).标记相邻迷宫单元并用它作为当前迷宫单元
		2).如果当前迷宫单元不存在未访问的相邻迷宫单元，并且栈不空
			(1).栈顶的迷宫单元出栈
			(2).令其成为当前迷宫单元
	 */
        Random random = new Random();
        Stack<Block> stack = new Stack<>();//栈
        Block current = blocks[0][0];//取第一个为当前单元
        current.setVisited(true);//标记为已访问

        int unVisitedCount=ROWS*COLS-1;//因为第一个已经设置为访问了
        List<Block> neighbors ;//定义邻居
        Block next;
        while(unVisitedCount>0){
            neighbors = current.findNeighbors();//查找邻居集合(未被访问的)
            if(neighbors.size()>0){//如果当前迷宫单元有未被访问过的的相邻的迷宫单元
                //随机选择一个未访问的相邻迷宫单元
                int index = random.nextInt(neighbors.size());
                next = neighbors.get(index);
                //将当前迷宫单元入栈
                stack.push(current);
                //移除当前迷宫单元与相邻迷宫单元的墙
                this.removeWall(current,next);
                //标记相邻迷宫单元并用它作为当前迷宫单元
                next.setVisited(true);
                //标记一个为访问，则计数器递减1
                unVisitedCount--;//递减
                current = next;
            }else if(!stack.isEmpty()){//如果当前迷宫单元不存在未访问的相邻迷宫单元，并且栈不空
			/*
				1.栈顶的迷宫单元出栈
				2.令其成为当前迷宫单元
			*/
                Block cell = stack.pop();
                current = cell;
            }
        }
    }

    //移除当前迷宫单元与相邻单元的墙
    private void removeWall(Block current,Block next){
        if(current.getI()==next.getI()){//横向邻居
            if(current.getJ()>next.getJ()){//匹配到的是左边邻居
                //左边邻居的话，要移除自己的左墙和邻居的右墙
                current.walls[3]=false;
                next.walls[1]=false;
            }else{//匹配到的是右边邻居
                //右边邻居的话，要移除自己的右墙和邻居的左墙
                current.walls[1]=false;
                next.walls[3]=false;
            }
        }else if(current.getJ()==next.getJ()){//纵向邻居
            if(current.getI()>next.getI()){//匹配到的是上边邻居
                //上边邻居的话，要移除自己的上墙和邻居的下墙
                current.walls[0]=false;
                next.walls[2]=false;
            }else{//匹配到的是下边邻居
                //下边邻居的话，要移除自己的下墙和邻居的上墙
                current.walls[2]=false;
                next.walls[0]=false;
            }
        }

    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        //绘制网格
        drawBlock(g);
        //绘制开始结束方向
        drawRect(g);
    }
    //绘制开始结束方块
    private void drawRect(Graphics g) {
        end.draw(g);
        start.draw(g);
    }

    //添加键盘监听
    private void createKeyListener() {
        KeyAdapter l = new KeyAdapter() {
            //按下
            @Override
            public void keyPressed(KeyEvent e) {
                if(!"start".equals(gameFlag)) return ;
                int key = e.getKeyCode();
                switch (key) {
                    //向上
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        if(start!=null) start.move(0,blocks,gamePanel);
                        break;

                    //向右
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        if(start!=null) start.move(1,blocks,gamePanel);
                        break;

                    //向下
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        if(start!=null) start.move(2,blocks,gamePanel);
                        break;

                    //向左
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        if(start!=null) start.move(3,blocks,gamePanel);
                        break;
                }

            }
            //松开
            @Override
            public void keyReleased(KeyEvent e) {
            }

        };
        //给主frame添加键盘监听
        gameFrame.addKeyListener(l);
    }

    //游戏胜利
    public void gameWin() {
        gameFlag = "end";
        //弹出结束提示
        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("思源宋体", Font.ITALIC, 18)));
        UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("思源宋体", Font.ITALIC, 18)));
        JOptionPane.showMessageDialog(gameFrame, "你胜利了,太棒了!");
    }
    //重新开始
    void restart() {
		/*参数重置
		1.游戏状态
		2.迷宫单元重置
		3.重新计算线路
		*/

        //1.游戏状态
        gameFlag="start";
        //2.迷宫单元重置
        Block block ;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                block = blocks[i][j];
                if(block!=null){
                    block.setVisited(false);
                    block.walls[0]=true;
                    block.walls[1]=true;
                    block.walls[2]=true;
                    block.walls[3]=true;
                }
            }
        }
        //3.计算处理线路
        computed();
        //开始方块归零
        start.setI(0);
        start.setJ(0);
        //重绘
        repaint();
    }
    //游戏结束
    public void gameOver() {
        gameFlag = "end";
        //弹出结束提示
        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("思源宋体", Font.ITALIC, 18)));
        UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("思源宋体", Font.ITALIC, 18)));
        JOptionPane.showMessageDialog(gameFrame, "你失败了,请再接再厉!");
    }
}
