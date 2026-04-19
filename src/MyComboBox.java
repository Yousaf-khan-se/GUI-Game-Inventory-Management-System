import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;

class MyComboBox<E> extends JComboBox<E> {
    private final Color backgroundColor = Color.darkGray;
    private final Color foregroundColor = new Color(0, 250, 250);
    private final Color focusColor = Color.black;
    private final Color arrowColor = new Color(52, 152, 219);

    public MyComboBox(E[] items) {
        super(items);
        setFont(new Font("Monospaced", Font.PLAIN, 14));
        setBackground(backgroundColor);
        setForeground(foregroundColor);
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        setUI(new MyComboBoxUI());
        setRenderer(new MyListCellRenderer());
    }

    private class MyComboBoxUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            return new JButton() {
                @Override
                public void paint(Graphics g) {
                    int width = getWidth();
                    int height = getHeight();
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(backgroundColor);
                    g2.fillRect(0, 0, width, height);
                    g2.setColor(arrowColor);
                    int[] xPoints = {width / 4, width / 2, 3 * width / 4};
                    int[] yPoints = {height / 3, 2 * height / 3, height / 3};
                    g2.fillPolygon(xPoints, yPoints, 3);
                    g2.dispose();
                }
            };
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            g.setColor(hasFocus ? focusColor : backgroundColor);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    private class MyListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setBackground(isSelected ? focusColor : backgroundColor);
            setForeground(foregroundColor);
            return this;
        }
    }
}