import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WindowApp extends JFrame {

    JTextField city_field;

    public WindowApp() {
        super("Weather app");
        setSize(300, 230);
        setLocation(200, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container container = getContentPane();
        container.setLayout(new GridLayout(3, 2, 2, 10));

        JLabel city = new JLabel("Enter the name of the city");
        city_field = new JTextField("", 1);

        container.add(city);
        container.add(city_field);

        JButton send_button = new JButton("Відправити!");

        container.add(send_button);

        send_button.addActionListener(new ButtonEventManager());
    }

    class ButtonEventManager implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(WindowApp.this, "Data sent", "Success", JOptionPane.PLAIN_MESSAGE);
            System.out.println(city_field.getText());
        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WindowApp app = new WindowApp();
            app.setVisible(true);
        });
    }
}
