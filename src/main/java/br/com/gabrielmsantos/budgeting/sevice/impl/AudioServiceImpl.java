package br.com.gabrielmsantos.budgeting.sevice.impl;

import br.com.gabrielmsantos.budgeting.controller.dto.FunctionalitiesDto;
import br.com.gabrielmsantos.budgeting.sevice.AudioService;
import br.com.gabrielmsantos.budgeting.sevice.FunctionalitiesService;
import br.com.gabrielmsantos.budgeting.sevice.exception.BusinessException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AudioServiceImpl implements AudioService {
    public static Path originalFile = null;
    public static Path wavFile = null;
    private final FunctionalitiesService functionalitiesService;
    private final ChatClient chatClient;

    public AudioServiceImpl(FunctionalitiesService functionalitiesService, ChatClient.Builder chatClientBuilder) {
        this.functionalitiesService = functionalitiesService;
        this.chatClient = chatClientBuilder.build();
    }

    public String audioTranscriptAI(MultipartFile file) throws IOException, InterruptedException {
        System.out.println("Recebendo áudio...");

        Path uploadDir = Paths.get("audios");

        if (!Files.exists(uploadDir))
            Files.createDirectories(uploadDir);

        // EXTENSÃO
        String originalName = file.getOriginalFilename();
        String extension = ".webm";

        if (originalName != null && originalName.contains("."))
            extension = originalName
                    .substring(originalName.lastIndexOf("."));

        // NOME DO ARQUIVO
        String fileName = "audio-" + System.currentTimeMillis();

        // SALVA ARQUIVO ORIGINAL
        originalFile = uploadDir.resolve(
                fileName + extension
        );

        Files.copy(
                file.getInputStream(),
                originalFile,
                StandardCopyOption.REPLACE_EXISTING
        );

        System.out.println("Áudio salvo em:");
        System.out.println(originalFile.toAbsolutePath());

        wavFile = uploadDir.resolve(fileName + ".wav");

        Process ffmpeg = new ProcessBuilder(
                "/opt/homebrew/bin/ffmpeg",
                "-y",
                "-i",
                originalFile.toString(),
                "-af",
                "highpass=f=200,lowpass=f=3000",
                "-ar",
                "16000",
                "-ac",
                "1",
                "-c:a",
                "pcm_s16le",
                wavFile.toString()
        ).start();

        String ffmpegOutput =
                new String(ffmpeg.getInputStream().readAllBytes());

        int ffmpegExit = ffmpeg.waitFor();
        System.out.println(ffmpegOutput);

        if (ffmpegExit != 0) {
            throw new BusinessException("Erro ao converter áudio");
        }

        // EXECUTA WHISPER
        Process whisper = new ProcessBuilder(
                "/opt/homebrew/bin/whisper-cli",
                "-m",
                "/Users/gabrielmarinho/whisper-models/ggml-base.bin",
                "-l",
                "pt",
                "--prompt",
                "abrir modal, fechar modal, gerar relatório, criar alerta, orçamento, despesa, receita",
                "-nt",
                "-f",
                wavFile.toString()
        ).redirectErrorStream(true).start();

        String output =
                new String(whisper.getInputStream().readAllBytes());

        int exitCode = whisper.waitFor();
        if (exitCode != 0)
            throw new BusinessException("error: Erro ao executar whisper");

        // LIMPA SAÍDA DO WHISPER
        StringBuilder transcription = new StringBuilder();
        for (String line : output.split("\n")) {
            line = line.trim();

            if (
                    line.isBlank()
                            || line.startsWith("whisper_")
                            || line.startsWith("load_backend")
                            || line.startsWith("ggml_")
                            || line.startsWith("system_info")
                            || line.startsWith("main:")
                            || line.startsWith("ffmpeg")
                            || line.startsWith("Input #")
                            || line.startsWith("Output #")
                            || line.startsWith("Stream")
                            || line.startsWith("size=")
                            || line.startsWith("[")
            ) {
                continue;
            }
            transcription.append(line).append(" ");
        }

        return transcription.toString().trim();
    }

    public String searchFuncionalitiesIA(String finalText) {
        System.out.println("TRANSCRIÇÃO:");
        System.out.println(finalText);

        var functionalities = functionalitiesService.findAllWithParameters();

        var functionalitiesDto = functionalities.stream()
                .map(FunctionalitiesDto::new)
                .toList();

        String functionalitiesContext = functionalitiesDto.stream()
                .map(f -> """
                        {
                          "name":"%s",
                          "description":"%s",
                          "parameters":[%s]
                        }
                        """.formatted(
                        f.name(),
                        f.description(),
                        f.parameters().stream()
                                .map(p -> """
                                        {
                                          "type":"%s"
                                        }
                                        """.formatted(
                                        p.getType()
                                ))
                                .collect(Collectors.joining(","))
                ))
                .collect(Collectors.joining(",", "[", "]"));

        System.out.println("FUNCIONALIDADES:");
        System.out.println(functionalitiesContext);

        String prompt = """
                Você é um interpretador de comandos.
                
                Escolha apenas uma funcionalidade da lista.
                
                Funcionalidades disponíveis:
                
                %s
                
                Solicitação do usuário:
                
                %s
                
                Regras:
                - Retorne APENAS JSON.
                - Não utilize markdown.
                - Não utilize ```json.
                - Não escreva explicações.
                - Preserve a ordem dos parâmetros definida na funcionalidade.
                - O campo "parameters" deve conter APENAS os valores extraídos.
                - NÃO retorne objetos dentro de "parameters".
                - NÃO retorne nome dos parâmetros.
                - NÃO retorne tipo dos parâmetros.
                - Strings devem ser retornadas como texto.
                - Inteiros devem ser retornados como número.
                - Se não entender a solicitação use a funcionalidade alerta.
                
                Exemplo correto:
                
                {
                  "execute": {
                    "name": "alerta",
                    "parameters": [
                      "teste"
                    ]
                  }
                }
                
                Formato obrigatório:
                
                {
                  "execute": {
                    "name": "nome_da_funcionalidade",
                    "parameters": []
                  }
                }
                """
                .formatted(
                        functionalitiesContext,
                        finalText
                );

        try {

            System.out.println("\n===== PROMPT =====");
            System.out.println(prompt);

            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            System.out.println("\n===== RESPOSTA BRUTA =====");
            System.out.println("[" + response + "]");
            System.out.println("É null? " + (response == null));

            if (response == null || response.isBlank()) {
                throw new RuntimeException("Modelo retornou resposta vazia");
            }

            response = response
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            if ("null".equalsIgnoreCase(response)) {
                throw new RuntimeException("Modelo retornou literal 'null'");
            }

            ObjectMapper mapper = new ObjectMapper();

            JsonNode json = mapper.readTree(response);

            JsonNode executeNode = json.path("execute");
            JsonNode parametersNode = executeNode.path("parameters");

            if (parametersNode.isArray() && executeNode instanceof ObjectNode executeObject) {

                ArrayNode normalizedParameters = mapper.createArrayNode();

                for (JsonNode parameter : parametersNode) {

                    if (parameter.isObject()) {

                        Iterator<Map.Entry<String, JsonNode>> fields = parameter.fields();

                        while (fields.hasNext()) {
                            Map.Entry<String, JsonNode> field = fields.next();

                            JsonNode value = field.getValue();

                            if (value.isInt() || value.isLong()) {
                                normalizedParameters.add(value.asLong());
                            } else if (value.isFloat() || value.isDouble() || value.isBigDecimal()) {
                                normalizedParameters.add(value.asDouble());
                            } else if (value.isBoolean()) {
                                normalizedParameters.add(value.asBoolean());
                            } else {
                                normalizedParameters.add(value.asText());
                            }
                        }

                    } else {
                        normalizedParameters.add(parameter);
                    }
                }

                executeObject.set("parameters", normalizedParameters);
            }

            String jsonVal = mapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(json);

            System.out.println("\n===== JSON VALIDADO =====");
            System.out.println(jsonVal);

            return jsonVal;

        } catch (Exception e) {
            System.err.println("\n===== ERRO AO PROCESSAR IA =====");
            e.printStackTrace();

            return """
                    {
                      "execute": {
                        "name": "alerta",
                        "parameters": [
                          "Não foi possível processar sua solicitação."
                        ]
                      }
                    }
                    """;
        }
    }
}
