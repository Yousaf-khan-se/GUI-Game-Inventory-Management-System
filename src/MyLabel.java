import javax.swing.*;
import java.awt.*;

public class MyLabel extends JLabel {

    MyLabel(String text)
    {
        this.setText(text);
        this.setFont(new Font("Monospaced", Font.BOLD, 18));
        this.setForeground(new Color(253, 254, 255));
        this.setOpaque(true);
        this.setBackground(Color.DARK_GRAY);
    }
}
