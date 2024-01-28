import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import javax.swing.border.EmptyBorder;
import java.text.DecimalFormat;

public class WindowApp extends JFrame {

    JTextField city_field;
    JLabel alertInfo = new JLabel("Data sent");
    private Color currentTemperature = new Color(20, 168, 168);
    String iconSrc = "./assets/weather-icon-default.png";
    String weather_data;
    String weatherApiKey = System.getenv("WEATHER_API_KEY");

    public WindowApp() {
        super("Weather app");
        setSize(300, 230);
        setLocation(200, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        weatherApiKey = (weatherApiKey != null) ? weatherApiKey : "f6d6c79aadeb13a2487b73442d186c75";

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));

        ImageIcon icon = new ImageIcon(getClass().getResource(iconSrc));
        Image scaledImage = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(scaledIcon);

        setContentPane(contentPane);
        contentPane.setLayout(new GridLayout(4, 2, 2, 10));
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel city = new JLabel("Enter the name of the city");
        city.setFont(new Font("Montserrat", Font.BOLD, 16));
        city.setHorizontalAlignment(SwingConstants.CENTER);
        city.setForeground(currentTemperature);

        alertInfo.setFont(new Font("Montserrat", Font.BOLD, 16));
        alertInfo.setForeground(currentTemperature);

        city_field = new JTextField("Your city", 1);
        city_field.setFont(new Font("Montserrat", Font.BOLD, 16));
        city_field.setForeground(currentTemperature);
        city_field.setBorder((new EmptyBorder(0, 10, 0, 0)));

        contentPane.add(imageLabel);
        contentPane.add(city);
        contentPane.add(city_field);

        JButton send_button = new JButton("View the forecast!");
        send_button.setBackground(currentTemperature);
        send_button.setForeground(Color.white);
        send_button.setBorder(null);
        send_button.setFont(new Font("Montserrat", Font.BOLD, 16));
        Cursor pointer = new Cursor(Cursor.HAND_CURSOR);
        send_button.setCursor(pointer);

        contentPane.add(send_button);

        send_button.addActionListener(new ButtonEventManager());
    }

    class ButtonEventManager implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(WindowApp.this, alertInfo, "Success!", JOptionPane.PLAIN_MESSAGE);
            System.out.println(city_field.getText());

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openweathermap.org/data/2.5/weather?q=" + city_field.getText() + "&appid=" + weatherApiKey))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(data -> {
                        weather_data = data;
                        Gson gson = new Gson();
                        WeatherData weatherData = gson.fromJson(weather_data, WeatherData.class);

                        setColor((weatherData.getMain().getTemp() - 270));

                        SwingUtilities.invokeLater(() -> {
                            JPanel contentPane = (JPanel) getContentPane();
                            contentPane.setLayout(new GridLayout(5, 2, 2, 10));
                            contentPane.removeAll();

                            ImageIcon icon = new ImageIcon(getClass().getResource(iconSrc));
                            Image scaledImage = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                            ImageIcon scaledIcon = new ImageIcon(scaledImage);
                            JLabel imageLabel = new JLabel(scaledIcon);

                            double tempValue = weatherData.getMain().getTemp() - 270;
                            DecimalFormat df = new DecimalFormat("#.##");
                            JLabel temp = new JLabel("Temperature: " + df.format(tempValue) + ";");
                            JLabel pressure = new JLabel("Pressure: " + weatherData.getMain().getPressure() + ";");
                            JLabel humidity = new JLabel("Humidity: " + weatherData.getMain().getHumidity() + ";");
                            JLabel cityName = new JLabel(weatherData.getName());

                            setTextUIProperties(temp);
                            setTextUIProperties(pressure);
                            setTextUIProperties(humidity);
                            setTextUIProperties(cityName);

                            cityName.setFont(new Font("Montserrat", Font.BOLD, 20));
                            cityName.setHorizontalAlignment(SwingConstants.CENTER);

                            contentPane.add(imageLabel);
                            contentPane.add(cityName);
                            contentPane.add(temp);
                            contentPane.add(pressure);
                            contentPane.add(humidity);

                            contentPane.revalidate();
                            contentPane.repaint();
                        });
                    })
                    .join();
        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WindowApp app = new WindowApp();
            app.setVisible(true);
        });
    }

    private void setColor(Double temperature) {
        currentTemperature = getColorForTemperature(temperature);
    }

    private Color getColorForTemperature(Double temperature) {
        if (temperature > 24) {
            iconSrc = "./assets/weather-icon-warm.png";
            return new Color(227, 34, 69);

        } else if (temperature > 0 ) {
            iconSrc = "./assets/weather-icon-normal.png";
            return new Color(245, 170, 66);
        }
        else {
            iconSrc = "./assets/weather-icon-cold.png";
            return new Color(66, 135, 245);
        }
    }

    private void setTextUIProperties(JComponent component) {
        component.setFont(new Font("Montserrat", Font.BOLD, 16));
        component.setForeground(currentTemperature);
    }
}
