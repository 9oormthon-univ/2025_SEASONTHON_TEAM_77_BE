package com.teachtouch.backend.ocr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/v1.0/ocr")
public class OcrToOpenAiController {

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @PostMapping("/ai")
    public String generateGuideFromOcr(@org.springframework.web.bind.annotation.RequestBody String ocrJson) throws IOException {
        // OCR JSON 문자열을 리스트로 변환
        List<Map<String, Object>> ocrComponents = objectMapper.readValue(
                ocrJson, new TypeReference<>() {}
        );

        //프롬프트 구성
        String prompt =
                "다음은 키오스크 화면에서 감지된 구성 요소 목록입니다. 각 항목은 label과 화면 위치 좌표(x, y)를 포함하며, 이 정보를 바탕으로 현재 화면의 목적과 구성, 조작 방법을 유추할 수 있습니다.\n\n" +
                        "당신은 이 화면을 처음 사용하는 60대 이상 사용자에게, 지금 어떤 화면인지, 어떤 요소가 어디에 있고, 무엇을 눌러야 하는지를 간결하고 자연스럽게 설명해야 합니다.\n\n" +
                        "다음 조건을 반드시 지켜서 안내 문장을 하나의 content 키 안에 담긴 JSON 형식으로 출력하세요:\n\n" +
                        "1. 인사말, 감사 표현, 의인화는 절대 사용하지 말 것.\n" +
                        "2. 설명은 하나의 문단으로만 구성하고, 문장 사이 줄바꿈 없이 부드럽게 연결할 것.\n" +
                        "3. x, y 좌표를 기준으로 위치를 설명할 것. 예: '왼쪽 위', '가운데 아래', '오른쪽 위' 등.\n" +
                        "4. 동일한 성격의 항목은 그룹으로 묶어 설명할 것. 항목들을 하나하나 나열하지 말 것.\n" +
                        "5. 가격 정보는 절대 설명하지 말 것.\n" +
                        "6. 화면의 목적을 먼저 설명하고, 그 후에 사용자 조작 방법을 안내할 것.\n" +
                        "7. '선택한 항목', '결제 버튼', '시간 표시' 등이 포함된 경우, 위치와 역할을 함께 설명할 것.\n\n" +
                        "출력 형식 예시는 다음과 같습니다:\n" +
                        "{ \"content\": \"...\" }\n\n" +
                        "아래는 현재 화면에서 감지된 구성 요소입니다:\n\n" +
                        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(ocrComponents);


        // 요청 본문 구성
        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("model", "gpt-3.5-turbo"); //
        requestBodyMap.put("temperature", 0.5);
        requestBodyMap.put("max_tokens", 250); //
        requestBodyMap.put("messages", List.of(
                Map.of("role", "system", "content", "너는 디지털 약자를 도와주는 친절한 안내자야. 응답은 간결하고 핵심적으로 작성해."),
                Map.of("role", "user", "content", prompt)
        ));

        RequestBody requestBody = RequestBody.create(
                objectMapper.writeValueAsString(requestBodyMap),
                Objects.requireNonNull(MediaType.parse("application/json"))
        );

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + openAiApiKey)
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("OpenAI API 호출 실패: " + response);
            }
            Map<String, Object> responseMap = objectMapper.readValue(response.body().string(), new TypeReference<>() {});
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return message.get("content").toString();
        }
    }
}
