package me.cor_right.codeCounter.gui;

import me.cor_right.codeCounter.service.MainService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/***
 * 主GUI窗口
 *
 * @author master
 *
 */
public class Frame {

    private final String imgdir = "target/classes/static/img/";

    JFrame mainframe;
    JMenuBar bar;
    JMenuItem start, alterpath;
    JMenu run, edit;
    MainFrameListener listener;
    MainService body;
    JLabel label;
    JButton leftbutton, rightbutton, saveconfirmbutton;
    JTextArea textarea;
    JPanel custompanel;
    Box box;
    JTextField oritext, tartext;

    public Frame() {
        setMenuItem();
        setMenu();
        setBar();
        setFrame();
        body = new MainService();
    }

    /***
     * 设置窗口属性;
     */
    private void setFrame() {
        mainframe = new JFrame("How Many Lines Code ?");// 创建窗口并命名
        mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 设置默认关闭操作
        mainframe.setBounds(400, 200, 1200, 700);// 设置窗口大小
        mainframe.setResizable(false);// 设置窗口大小不可变
        mainframe.setJMenuBar(bar);
        label = new JLabel(new ImageIcon(imgdir + "sh_07.png"));
        mainframe.add(label);
        mainframe.setVisible(true);// 最后设置窗口可视
    }

    /***
     * 设置菜单条属性
     */
    private void setBar() {
        bar = new JMenuBar();
        bar.add(run);
        bar.add(edit);
    }

    /***
     * 设置菜单属性
     */
    private void setMenu() {

        Font font = new Font("楷体", Font.BOLD, 30);
        run = new JMenu("运行");
        edit = new JMenu("编辑");
        run.setFont(font);
        edit.setFont(font);
        run.add(start);
        edit.add(alterpath);
    }

    /***
     * 设置菜单项属性
     */
    private void setMenuItem() {
        listener = new MainFrameListener();
        Font font = new Font("楷体", Font.BOLD, 25);
        start = new JMenuItem("开始运行", new ImageIcon(imgdir + "sh_08.png"));
        alterpath = new JMenuItem("修改路径", new ImageIcon(imgdir + "sh_01.png"));
        start.setEnabled(true);
        alterpath.setEnabled(true);
        start.setFont(font);
        alterpath.setFont(font);
        start.addActionListener(listener);
        alterpath.addActionListener(listener);
    }

    /***
     * 用户点击运行后改变主窗口 之后再主窗口中执行操作
     */
    private void alterFrameWhenStart() {
        bar.setVisible(false);//隐藏菜单栏
        Font textfont = new Font("楷体", Font.BOLD, 25);
        Font buttonfont = new Font("楷体", Font.BOLD, 30);
        custompanel = new JPanel();
        // 对文本区设置
        textarea = new JTextArea();
        textarea.setBackground(custompanel.getBackground());
        textarea.setEditable(false);
        textarea.setFont(textfont);
        textarea.setColumns(40);
        textarea.setCaretColor(new Color(255, 0, 255));
        textarea.setLineWrap(true);//自动换行
        // 对下边的两个按钮设置
        // 加了一个中间容器，设好格局
        FlowLayout flow = new FlowLayout();
        flow.setHgap(50);
        custompanel.setLayout(flow);
        mainframe.remove(label);
        mainframe.add(custompanel);
        mainframe.add(textarea, BorderLayout.CENTER);
        mainframe.add(custompanel, BorderLayout.SOUTH);
        leftbutton = new JButton("确认");
        rightbutton = new JButton("确认并保存");
        leftbutton.setFont(buttonfont);
        rightbutton.setFont(buttonfont);
        leftbutton.addActionListener(new startFrameListener());
        rightbutton.addActionListener(new startFrameListener());
        custompanel.add(leftbutton);
        custompanel.add(rightbutton);
        mainframe.validate();
        //对点击“确认并保存”按钮之后出现的页面的按钮进行设置
        saveconfirmbutton = new JButton("确认");
        saveconfirmbutton.setFont(buttonfont);
        saveconfirmbutton.addActionListener(new startFrameListener());


        // 运行方法
        body.startSerch();

        if (body.getFlag() == false) {
            textarea.append("\n\n\n\n\t目标文件或文件夹不存在\n\n\t请创建后重试或重新选择文件夹");
            custompanel.remove(rightbutton);
        } else {
            textarea.append("\n文件检索成功\n文件检索路径为：\n    " + body.getOriPath());
            if (body.getFileType()) {
                textarea.append("\n检索文件类型为：    文件");
            } else {
                textarea.append("\n检索文件类型为：    文件夹");
            }

            textarea.append("\n\n当前日期为：\t" + body.getDate());
            textarea.append("\n\n文件数目为：\t" + body.getFileNumber() + " 个");
            textarea.append("\n文件容量为：\t" + body.getFileSize() + " Byte");
            textarea.append("\n文件中代码行数为：\t" + body.getCodeLines() + " 行");
            textarea.append("\n\n如要保存文件，则保存文件的路径为：\n    " + body.getTarPath());
        }
    }


    /***
     * 当用户点击修改路径时，修改窗口的样式
     */
    public void alterFrameWhenPath(int kind) {
        mainframe.remove(label);
        bar.setVisible(false);
        Font textfont = new Font("楷体", Font.BOLD, 25);
        Font buttonfont = new Font("楷体", Font.BOLD, 30);
        //设置盒式布局容器样式
        box = Box.createVerticalBox();
        box.setVisible(true);
        mainframe.add(box, BorderLayout.CENTER);
        //设置盒式容器中插件样式
        oritext = new JTextField();
        tartext = new JTextField();
        JLabel orilabel = new JLabel("文件检索路径：");
        JLabel tarlabel = new JLabel("文件保存路径：");
        oritext.setFont(textfont);
        tartext.setFont(textfont);
        oritext.setText(body.getOriPath());
        tartext.setText(body.getTarPath());
        orilabel.setFont(textfont);
        tarlabel.setFont(textfont);
        //依次加入窗口中
        box.add(Box.createVerticalStrut(20));
        box.add(orilabel);
        box.add(Box.createVerticalStrut(30));
        box.add(oritext);
        box.add(Box.createVerticalStrut(40));
        box.add(tarlabel);
        box.add(Box.createVerticalStrut(30));
        box.add(tartext);
        box.add(Box.createVerticalStrut(20));
        // 加了一个中间容器，设好格局，加入窗口下边
        custompanel = new JPanel();
        leftbutton = new JButton("应用");
        rightbutton = new JButton("取消");
        leftbutton.setFont(buttonfont);
        rightbutton.setFont(buttonfont);
        leftbutton.addActionListener(new AlterPathListener());
        rightbutton.addActionListener(new AlterPathListener());
        FlowLayout flow = new FlowLayout();
        flow.setHgap(50);
        custompanel.setLayout(flow);
        custompanel.add(leftbutton);
        custompanel.add(rightbutton);
        mainframe.add(custompanel, BorderLayout.SOUTH);

    }

    /***
     * <b> 用户点击运行之后出现的窗口中需要的监听器
     *
     */
    class startFrameListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == leftbutton) {
                System.out.println("用户点击了“确认”按钮");
                mainframe.remove(custompanel);//移除组件还原窗口
                mainframe.remove(textarea);
                mainframe.add(label);
                mainframe.repaint();//必须加，用于刷新
            } else if (e.getSource() == rightbutton) {
                System.out.println("用户点击了“确认并保存”按钮");
                body.saveToXLS();
                alterFrameAfterSave();
            } else {//按下是时点击保存之后出现的页面的确认键
                System.out.println("用户点击了保存信息页面“确认”按钮");
                mainframe.remove(custompanel);
                mainframe.remove(textarea);
                mainframe.add(label);
                mainframe.repaint();
            }
            bar.setVisible(true);
        }

        //保存之后的下一个窗口
        private void alterFrameAfterSave() {
            custompanel.remove(leftbutton);
            custompanel.remove(rightbutton);
            custompanel.add(saveconfirmbutton);
            if (body.getFlag() == true) {
                textarea.setText("\n\n\n\t文件保存成功\n\n\t保存的路径为：" + body.getTarPath());
            } else {
                textarea.setText("\n\n\n\t文件失败，请找程序员调试后再运行程序");
            }
            mainframe.repaint();
        }

    }

    /***
     * <b>用户点击修改路径之后需要的监听器
     */
    class AlterPathListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == leftbutton) {
                System.out.println("用户点击了“确认”按钮");
                body.setOriPath(oritext.getText());
                body.setTarPath(tartext.getText());
            } else
                System.out.println("用户点击了“取消”按钮");
            mainframe.remove(box);
            mainframe.remove(custompanel);
            mainframe.add(label);
            mainframe.repaint();
            bar.setVisible(true);
        }

    }

    /***
     * <b> 主窗口中菜单项的监听器<br>
     * 当用户点击“只运行“ ，运行new Progream.onluRun()”<br>
     * 当用户点击“运行并记录” ，运行new Progream.Run()”<br>
     * 当用户点击“修改检索目录路径 ”，运行new Progream.alterOriPath()”<br>
     * 当用户点击“修改表格文件路径“ ，运行new Progream.alterTarPath()”
     */
    class MainFrameListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == start) {
                System.out.println("用户点击了“开始运行”菜单项");
                alterFrameWhenStart();
            }
            if (e.getSource() == alterpath) {
                System.out.println("用户点击了“修改路径”菜单项");
                alterFrameWhenPath(1);
            }
        }
    }

}

