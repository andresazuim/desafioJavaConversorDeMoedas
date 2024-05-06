import com.google.gson.Gson;
import models.CurrencyRate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main {
    private static final String API_KEY = "06659ad554891a6cb9c91b3a";
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bem-vindo ao conversor de moedas!");

        System.out.println("Informe a moeda base (ex: USD, EUR, BRL): ");
        String baseCurrency = scanner.nextLine().toUpperCase();

        System.out.println("Informe a moeda para conversão (ex: JPY, CAD): ");
        String targetCurrency = scanner.nextLine().toUpperCase();

        System.out.println("Informe o valor a ser convertido: ");
        while (!scanner.hasNextDouble()) {
            System.out.println("Por favor, digite um número válido.");
            scanner.next();
        }
        double amount = scanner.nextDouble();

        try {
            double convertedAmount = convertCurrency(baseCurrency, targetCurrency, amount);
            System.out.printf("O valor %.2f %s é equivalente a %.2f %s\n", amount, baseCurrency, convertedAmount, targetCurrency);
        } catch (Exception e) {
            System.err.println("Erro durante a conversão: " + e.getMessage());
        }

        scanner.close();
    }

    private static double convertCurrency(String baseCurrency, String targetCurrency, double amount) {
        String responseBody = fetchCurrencyRates(baseCurrency);
        if (responseBody == null) {
            throw new RuntimeException("Failed to fetch currency rates.");
        }

        Gson gson = new Gson();
        CurrencyRate currencyRate = gson.fromJson(responseBody, CurrencyRate.class);

        if (currencyRate == null || !currencyRate.getConversionRates().containsKey(targetCurrency)) {
            throw new RuntimeException("Invalid currency or conversion rate not found.");
        }

        return amount * currencyRate.getConversionRates().get(targetCurrency);
    }

    private static String fetchCurrencyRates(String baseCurrency) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + API_KEY + "/latest/" + baseCurrency))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.out.println("Erro na API: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
