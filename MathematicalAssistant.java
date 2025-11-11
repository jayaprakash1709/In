import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class MathematicalAssistant extends JFrame {

    public MathematicalAssistant() {
        setTitle("Mathematical Assistant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Calculator", createCalculatorPanel());
        tabs.addTab("Logarithmic Spiral", createLogSpiralPanel());
        add(tabs);
        setVisible(true);
    }

    private JPanel createCalculatorPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);

        JTextField t1 = new JTextField(12);
        JTextField t2 = new JTextField(12);
        JLabel res = new JLabel("Result: ");

        JButton[] btns = {
            new JButton("+"), new JButton("-"),
            new JButton("×"), new JButton("÷")
        };

        for (JButton b : btns) {
            b.setFont(new Font("Arial", Font.BOLD, 16));
        }

        btns[0].addActionListener(e -> calc(t1,t2,res, '+'));
        btns[1].addActionListener(e -> calc(t1,t2,res, '-'));
        btns[2].addActionListener(e -> calc(t1,t2,res, '×'));
        btns[3].addActionListener(e -> calc(t1,t2,res, '÷'));

        c.gridx=0; c.gridy=0; p.add(new JLabel("Number 1:"), c);
        c.gridx=1; p.add(t1, c);
        c.gridx=0; c.gridy=1; p.add(new JLabel("Number 2:"), c);
        c.gridx=1; p.add(t2, c);

        JPanel bp = new JPanel(new GridLayout(2,2,10,10));
        for (JButton b : btns) bp.add(b);
        c.gridx=0; c.gridy=2; c.gridwidth=2; p.add(bp, c);

        c.gridy=3; p.add(res, c);
        return p;
    }

    private void calc(JTextField a, JTextField b, JLabel r, char op) {
        try {
            double x = Double.parseDouble(a.getText());
            double y = Double.parseDouble(b.getText());
            double ans = switch(op) {
                case '+' -> x + y;
                case '-' -> x - y;
                case '×' -> x * y;
                case '÷' -> y != 0 ? x/y : Double.NaN;
                default -> 0;
            };
            r.setText("Result: " + (Double.isNaN(ans) ? "Division by zero" : String.format("%.6f", ans)));
        } catch (Exception e) {
            r.setText("Result: Invalid input");
        }
    }

    private JPanel createLogSpiralPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 25));
        top.setBackground(new Color(248,249,251));

        top.add(new JLabel("<html><b>Enter the number of turns (N):</b></html>"));
        JTextField tfTurns = new JTextField("5,6", 6);
        tfTurns.setFont(new Font("Arial", Font.PLAIN, 16));
        JButton btn = new JButton("Calculate");
        btn.setFont(new Font("Arial", Font.BOLD, 14));

        top.add(tfTurns);
        top.add(btn);

        LogSpiralPanel spiralPanel = new LogSpiralPanel();
        btn.addActionListener(e -> {
            try {
                double turns = Double.parseDouble(tfTurns.getText().replace(",", "."));
                if (turns > 0 && turns <= 15) {
                    spiralPanel.setTurns(turns);
                    spiralPanel.repaint();
                }
            } catch (Exception ex) {}
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(spiralPanel, BorderLayout.CENTER);

        // Default: 6 turns
        spiralPanel.setTurns(6.0);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MathematicalAssistant::new);
    }
}

class LogSpiralPanel extends JPanel {
    private double turns = 6.0;

    public void setTurns(double t) { this.turns = t; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        setBackground(Color.WHITE);

        // Draw light grid
        g2.setColor(new Color(240,240,240));
        for (int i = 0; i < getWidth(); i += 50) g2.drawLine(i, 0, i, getHeight());
        for (int i = 0; i < getHeight(); i += 50) g2.drawLine(0, i, getWidth(), i);

        // Axes
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        int cx = getWidth()/2, cy = getHeight()/2;
        g2.drawLine(0, cy, getWidth(), cy);
        g2.drawLine(cx, 0, cx, getHeight());

        // === True Golden Logarithmic Spiral ===
        double phi = (1 + Math.sqrt(5)) / 2;                    // φ ≈ 1.618
        double b   = Math.log(phi) / (Math.PI / 2);            // growth per 90°

        Path2D spiral = new Path2D.Double();
        int points = 8000;
        double maxTheta = turns * 2 * Math.PI;

        for (int i = 0; i <= points; i++) {
            double t = i / (double)points * maxTheta;
            double r = Math.exp(b * t);
            double x = r * Math.cos(t);
            double y = r * Math.sin(t);

            if (i == 0) spiral.moveTo(x, y);
            else        spiral.lineTo(x, y);
        }

        // Scale and center
        Rectangle2D bounds = spiral.getBounds2D();
        double scale = Math.min(
            getWidth() * 0.80 / bounds.getWidth(),
            getHeight() * 0.80 / bounds.getHeight()
        );

        AffineTransform at = new AffineTransform();
        at.translate(getWidth()/2.0, getHeight()/2.0);
        at.scale(scale, scale);
        at.translate(-bounds.getCenterX(), -bounds.getCenterY());

        g2.setStroke(new BasicStroke(4.5f));
        g2.setColor(new Color(0, 75, 160));  // Deep beautiful blue
        g2.draw(at.createTransformedShape(spiral));

        // Center dot
        g2.setColor(Color.RED);
        g2.fillOval(getWidth()/2 - 6, getHeight()/2 - 6, 12, 12);
    }
}