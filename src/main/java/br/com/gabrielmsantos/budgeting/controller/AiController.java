package br.com.gabrielmsantos.budgeting.controller;

import br.com.gabrielmsantos.budgeting.dto.PromptRequest;
import br.com.gabrielmsantos.budgeting.sevice.FunctionalitiesService;
import br.com.gabrielmsantos.budgeting.sevice.impl.AudioServiceImpl;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/ai")
@CrossOrigin(origins = "*")
public class AiController {

    private final ChatClient chatClient;
    private final FunctionalitiesService functionalitiesService;
    private final AudioServiceImpl audioService;

    public AiController(ChatClient.Builder chatClientBuilder, FunctionalitiesService functionalitiesService, AudioServiceImpl audioService
    ) {
        this.chatClient = chatClientBuilder.build();
        this.functionalitiesService = functionalitiesService;
        this.audioService = audioService;
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody PromptRequest request) {

        String resposta = chatClient.prompt()
                .user(request.prompt())
                .call()
                .content();

        return ResponseEntity.ok(resposta);
    }

    @PostMapping(
            value = "/transcription",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> transcription(
            @RequestParam("file") MultipartFile file) {

        try {
            String finalText = audioService.audioTranscriptAI(file);
            String response = audioService.searchFuncionalitiesIA(finalText);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body("""
                            {
                              "error":"Erro interno"
                            }
                            """);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI Service Running");
    }
}