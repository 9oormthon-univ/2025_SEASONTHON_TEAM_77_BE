package com.teachtouch.backend.ocr.serivce;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class VisionService {

    public String extractUiComponentsFromImageUrl(String imageUrl) throws Exception {
        ByteString imgBytes = ByteString.readFrom(new URL(imageUrl).openStream());
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feat).setImage(img).build();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            AnnotateImageResponse res = client.batchAnnotateImages(List.of(request)).getResponses(0);
            if (res.hasError()) return "Error: " + res.getError().getMessage();

            List<Map<String, Object>> components = new ArrayList<>();

            for (var page : res.getFullTextAnnotation().getPagesList()) {
                for (var block : page.getBlocksList()) {
                    for (var para : block.getParagraphsList()) {
                        for (var word : para.getWordsList()) {
                            StringBuilder wordText = new StringBuilder();
                            for (var symbol : word.getSymbolsList()) {
                                wordText.append(symbol.getText());
                            }
                            String text = wordText.toString();

                            var vertices = word.getBoundingBox().getVerticesList();
                            int x = (vertices.get(0).getX() + vertices.get(2).getX()) / 2;
                            int y = (vertices.get(0).getY() + vertices.get(2).getY()) / 2;

                            if (text.length() <= 1 && text.matches("[()0-9]+")) continue;

                            Map<String, Object> item = new LinkedHashMap<>();
                            item.put("label", text);
                            item.put("x", x);
                            item.put("y", y);
                            components.add(item);
                        }
                    }
                }
            }

            List<Map<String, Object>> merged = mergeByLine(components);

            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(merged);
        }
    }

    private List<Map<String, Object>> mergeByLine(List<Map<String, Object>> items) {
        List<Map<String, Object>> merged = new ArrayList<>();

        items.sort(Comparator
                .comparingInt((Map<String, Object> c) -> ((Number) c.get("y")).intValue())
                .thenComparingInt(c -> ((Number) c.get("x")).intValue()));


        List<Map<String, Object>> currentLine = new ArrayList<>();
        int yThreshold = 12;
        int prevY = -9999;

        for (Map<String, Object> comp : items) {
            int currY = ((Number) comp.get("y")).intValue();
            if (Math.abs(currY - prevY) <= yThreshold) {
                currentLine.add(comp);
            } else {
                if (!currentLine.isEmpty()) {
                    merged.add(buildMergedItem(currentLine));
                    currentLine.clear();
                }
                currentLine.add(comp);
            }
            prevY = currY;
        }
        if (!currentLine.isEmpty()) merged.add(buildMergedItem(currentLine));

        return merged;
    }

    private Map<String, Object> buildMergedItem(List<Map<String, Object>> line) {
        line.sort(Comparator.comparingInt(c -> ((Number) c.get("x")).intValue()));

        StringBuilder label = new StringBuilder();
        int x = ((Number) line.get(0).get("x")).intValue();
        int y = ((Number) line.get(0).get("y")).intValue();

        for (Map<String, Object> word : line) {
            label.append(word.get("label")).append(" ");
        }

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("label", label.toString().trim());
        item.put("x", x);
        item.put("y", y);
        return item;
    }

}

