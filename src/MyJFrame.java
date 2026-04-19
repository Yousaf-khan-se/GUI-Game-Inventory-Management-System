import javax.swing.*;
import java.awt.*;

public class MyJFrame extends JFrame {

    public MyJFrame(String title)
    {
        this.setTitle(title);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 700);
        this.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        Container c = this.getContentPane();
        c.setBackground(Color.gray);
    }
}
