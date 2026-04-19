import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

class MyTextField extends JTextField {
    private final Color focusColor = new Color(0, 250, 250);

    public MyTextField() {
        setFont(new Font("Monospaced", Font.BOLD, 14));
        setForeground(focusColor);
        setBackground(Color.black);
        setCaretColor(focusColor);

        // Set initial border
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                setBorder(BorderFactory.createLineBorder(focusColor, 2));
            }

            @Override
            public void focusLost(FocusEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (hasFocus()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(focusColor.getRed(), focusColor.getGreen(), focusColor.getBlue(), 25));
            g2.fillRect(2, 2, getWidth() - 4, getHeight() - 4);
            g2.dispose();
        }
    }
}