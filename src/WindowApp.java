import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;

public class WindowApp extends JFrame {

    JTextField city_field;
    String weather_data;
    String weatherApiKey = System.getenv("WEATHER_API_KEY");

    public WindowApp() {
        super("Weather app");
        setSize(300, 230);
        setLocation(200, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        weatherApiKey = (weatherApiKey != null) ? weatherApiKey : "f6d6c79aadeb13a2487b73442d186c75";

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

                        SwingUtilities.invokeLater(() -> {
                            Container container = getContentPane();
                            container.setLayout(new GridLayout(3, 2, 2, 10));

                            container.removeAll();

                            JLabel temp = new JLabel("Температура" + weatherData.getMain().getTemp());
                            JLabel pressure = new JLabel("Тиск" + weatherData.getMain().getPressure());
                            JLabel humidity = new JLabel("Вологість" + weatherData.getMain().getHumidity());

                            container.add(temp);
                            container.add(pressure);
                            container.add(humidity);

                            revalidate();
                            repaint();
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
}
