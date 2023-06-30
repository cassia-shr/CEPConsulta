package bolsa.cepconsulta;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author cassia
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsultaCEP {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;
        List<String> resultadosSalvos = new ArrayList<>(); // lista para salvar os resultados da api e otimizar a busca

        while (continuar) { 
            try {
                System.out.print("Digite o CEP ou 'sair' para encerrar: ");
                String query = scanner.nextLine(); //recebendo dado do usuário

                if (query.equalsIgnoreCase("sair")) { //tratando "sair" com boolean no if
                    continuar = false;
                    
                    System.out.println("Encerrando sistema...");
                    break;    
                }

                boolean resultadoEncontrado = false; //pegando dados na lista 
                for (String resultado : resultadosSalvos) {
                    if (resultado.contains(query)) {
                        System.out.println("Resultado encontrado na lista de resultados salvos:");
                        System.out.println(resultado);
                        resultadoEncontrado = true;
                        break;
                    }
                }

                if (!resultadoEncontrado) { // conexão HTTP com a API do ViaCEP
                    String url = "https://viacep.com.br/ws/" + query + "/json/";
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("GET");

                    int responseCode = con.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        StringBuilder response;
                        
                        // utiliza a classe BufferedReader para verificar a response da api e retornar para o usuário somente logradouro, localidade e estado
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                            response = new StringBuilder(); // utilizando StringBuilder para manipular string de uma forma dinâmica, deixando o código otimizado
                            String line; // utilizo o metodo String line para inserir quebra de linha entre os objetos.
                            while ((line = reader.readLine()) != null) { 
                                if (line.contains("\"logradouro\"") || line.contains("\"localidade\"") || line.contains("\"uf\"")) {
                                    response.append(line).append(System.lineSeparator());
                                }
                            }
                        }

                        //salvando resposta na lista e imprimindo no terminal.
                        String resultado = response.toString();
                        resultadosSalvos.add(resultado);

                        System.out.println("Resposta da API: ");
                        System.out.println(resultado);
                    } else { //em caso de erro, informa o código da response
                        System.out.println("Ocorreu um erro ao consultar o CEP. Código de resposta: " + responseCode);
                    }
                }
            } catch (IOException e) { //exceção de erro
                System.out.println("Ocorreu um erro: " + e.getMessage());
            }
        }
        scanner.close();
    }
}
