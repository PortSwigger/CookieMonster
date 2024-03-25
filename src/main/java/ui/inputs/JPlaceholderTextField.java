package ui.inputs;

import javax.swing.*;
import java.awt.*;

public class JPlaceholderTextField extends JTextField {

    private final String placeholder;
    private final Insets textInsets = new Insets(0, 5, 0, 0);

    public JPlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;

        this.setMargin(new Insets(0, 50, 0, 0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getText().isEmpty()) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(Color.GRAY);
            g2d.drawString(placeholder, textInsets.left, (getHeight() - g2d.getFontMetrics().getHeight()) / 2 + g2d.getFontMetrics().getAscent());
            g2d.dispose();
        }
    }
}