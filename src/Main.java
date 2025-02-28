import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.google.gson.Gson;

// Clase para serie de television
class TVSeries {
    String name;
    String genre;
    double imdb_rating;
}

// Clase de la respuesta de la API
class TVSeriesResponse {
    int total_pages;
    List<TVSeries> data;
}

public class Main {
    private static final String API_URL = "https://jsonmock.hackerrank.com/api/tvseries/?page=";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            // Solicitar al usuario que ingrese el género
            System.out.println("Ingrese el género de las series que desea buscar: ");
            String targetGenre = scanner.nextLine().trim().toLowerCase();

            List<TVSeries> allSeries = fetchAllSeries();

            // Filtrar las series por el género especificado
            TVSeries bestSeries = null;
            for (TVSeries series : allSeries) {
                String[] genres = series.genre.toLowerCase().split(",\\s*");
                for (String genre : genres) {
                    if (genre.equals(targetGenre)) {
                        if (bestSeries == null || series.imdb_rating > bestSeries.imdb_rating ||
                                (series.imdb_rating == bestSeries.imdb_rating && series.name.compareTo(bestSeries.name) > 0)) {
                            bestSeries = series;
                        }
                    }
                }
            }

            // Mostrar los resultados
            if (bestSeries != null) {
                System.out.println("Nombre: " + bestSeries.name);
                System.out.println("Géneros: " + bestSeries.genre);
                System.out.println("Rating: " + bestSeries.imdb_rating);
            } else {
                System.out.println("No se encontró ninguna serie en el género " + targetGenre);
            }
        } catch (Exception e) {
            System.err.println("Error al procesar la solicitud: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para obtener todas las series de la API
    private static List<TVSeries> fetchAllSeries() throws Exception {
        List<TVSeries> allSeries = new ArrayList<>();
        Gson gson = new Gson();
        int currentPage = 1;
        int totalPages;

        do {
            String response = getApiResponse(API_URL + currentPage);
            TVSeriesResponse tvSeriesResponse = gson.fromJson(response, TVSeriesResponse.class);
            totalPages = tvSeriesResponse.total_pages;
            allSeries.addAll(tvSeriesResponse.data);
            currentPage++;
        } while (currentPage <= totalPages);

        return allSeries;
    }

    // Método para obtener la respuesta de la API
    private static String getApiResponse(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            return content.toString();
        }
    }
}