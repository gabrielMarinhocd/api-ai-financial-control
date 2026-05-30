package br.com.gabrielmsantos.budgeting.sevice.impl;

import br.com.gabrielmsantos.budgeting.controller.dto.FunctionalitiesDto;
import br.com.gabrielmsantos.budgeting.sevice.AudioService;
import br.com.gabrielmsantos.budgeting.sevice.FunctionalitiesService;
import br.com.gabrielmsantos.budgeting.sevice.exception.BusinessException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

        // FUNCIONALIDADES
        var functionalities = functionalitiesService.findAll();
        var functionalitiesDto = functionalities.stream()
                .map(FunctionalitiesDto::new)
                .toList();

        String functionalitiesContext = functionalitiesDto.stream()
                .map(f -> """
                        {
                          "name": "%s",
                          "description": "%s"
                        }
                        """.formatted(
                        f.name(),
                        f.description()
                ))
                .collect(Collectors.joining(",\n"));
        // PROMPT
        String prompt = """
                Você é um interpretador de comandos.
                
                Sua função é analisar o texto do usuário e identificar qual funcionalidade executar.
                
                Funcionalidades disponíveis:
                
                [
                %s
                ]
                
                REGRAS:
                
                1 - Retorne SOMENTE JSON.
                2 - Não explique nada.
                3 - Não use markdown.
                4 - Não use ```json.
                5 - Escolha apenas UMA funcionalidade.
                6 - Extraia os parâmetros necessários da frase.
                
                Estrutura obrigatória:
                
                {
                  "execute": {
                    "name": "nome_da_funcionalidade",
                    "parameters": {}
                  }
                }
                
                Exemplo:
                
                Entrada:
                Gere um alerta com a mensagem Bem vindo
                
                Saída:
                {
                  "execute": {
                    "name": "alerta",
                    "parameters": {
                      "message": "Bem vindo"
                    }
                  }
                }
                
                Texto do usuário:
                
                %s
                """
                .formatted(
                        functionalitiesContext,
                        finalText
                );

        // CHAMA IA
        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        System.out.println("JSON GERADO:");
        System.out.println(response);


        return response;
    }
}
