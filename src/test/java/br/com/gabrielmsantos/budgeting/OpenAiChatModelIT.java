package br.com.gabrielmsantos.budgeting;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;

@SpringBootTest
class OllamaChatModelIT {

//    @Autowired
//    private OllamaApi ollamaApi;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    void shouldReceiveResponseWhenChatModelIsCalled() {
//
//        var chatModel = OllamaChatModel.builder()
//                .ollamaApi(ollamaApi)
//                .build();
//
//        String response = chatModel.call(
//                "Gere um registro de budgeting com descrição do gasto, valor em reais e local."
//        );
//
//        System.out.println(response);
//
//        assertThat(response)
//                .isNotNull()
//                .isNotBlank();
//    }
//
//    @Test
//    void shouldGenerateTextUsingRoute() throws Exception {
//
//        mockMvc.perform(post("/api/ia/texto")
//                        .contentType("application/json")
//                        .content("""
//                                {
//                                  "prompt":"Responda apenas OK"
//                                }
//                                """))
//                .andExpect(status().isOk())
//                .andExpect(content().string(Matchers.not(Matchers.isEmptyOrNullString())));
//    }
//
//    @Test
//    void shouldUploadAudio() throws Exception {
//
//        MockMultipartFile audio =
//                new MockMultipartFile(
//                        "arquivo",
//                        "audio.mp3",
//                        "audio/mpeg",
//                        "conteudo de teste".getBytes()
//                );
//
//        mockMvc.perform(
//                        multipart("/api/ia/audio")
//                                .file(audio)
//                )
//                .andExpect(status().isOk())
//                .andExpect(content().string(
//                        Matchers.containsString("audio.mp3")
//                ));
//    }
}