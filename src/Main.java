import chess.game.Game;
import chess.ui.Controller;
import chess.ui.view.BoardUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main {
    public static void main(String[] args) {   java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
            createAndShowUI();
        }
    });
    }

    public static void createAndShowUI() {
        //Game game = new Game("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1");
        Game game = new Game();
        Controller controller = new Controller(game);
        BoardUI boardUI = new BoardUI(controller);
        controller.setBoardUI(boardUI);
        JFrame frame = new JFrame();
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_LEFT:
                        controller.undo();
                        break;
                    case KeyEvent.VK_RIGHT:
                        controller.redo();
                        break;
                }
            }
        });
        frame.setLayout(new GridBagLayout());
        frame.setSize(boardUI.getMainComponent().getSize());
        frame.setSize(526, 549);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(boardUI.getMainComponent());
        frame.setVisible(true);
    }
}