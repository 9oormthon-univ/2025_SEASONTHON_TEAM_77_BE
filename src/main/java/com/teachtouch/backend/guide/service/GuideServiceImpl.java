package com.teachtouch.backend.guide.service;

import com.teachtouch.backend.example.dto.ExampleRequestDTO;
import com.teachtouch.backend.guide.dto.GuideRequestDTO;
import com.teachtouch.backend.guide.dto.StepRequestDTO;
import com.teachtouch.backend.guide.entity.*;
import com.teachtouch.backend.guide.repository.GuideRepository;
import com.teachtouch.backend.guide.repository.StepProgressRepository;
import com.teachtouch.backend.guide.repository.StepRepository;
import com.teachtouch.backend.product.entity.Product;
import com.teachtouch.backend.product.service.ProductService;
import com.teachtouch.backend.user.entity.User;
import com.teachtouch.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GuideServiceImpl implements GuideService {

    private final GuideRepository guideRepository;
    private final ProductService productService;
    private final StepProgressRepository stepProgressRepository;
    private final StepRepository stepRepository;
    private final UserRepository userRepository;

    @Override
    public Guide upsertGuide(GuideRequestDTO dto) {
        Guide guide = (dto.id() != null)
                ? guideRepository.findById(dto.id())
                .orElseThrow(() -> new IllegalArgumentException("가이드가 존재하지 않음: " + dto.id()))
                : new Guide();

        guide.setTitle(dto.title());
        guide.setCategory(dto.category());
        guide.setDescription(dto.description());

        if (dto.productIds() != null) {
            updateProducts(guide, dto.productIds());
        }
        if (dto.examples() != null) {
            updateExamples(guide, dto.examples());
        }
        if (dto.steps() != null) {
            updateSteps(guide, dto.steps(), null);
        }

        return guideRepository.save(guide);
    }


    private void updateProducts(Guide guide, List<Long> productIds) {
        Set<Long> incomingIds = new HashSet<>(productIds);

        guide.getProducts().removeIf(gp -> !incomingIds.contains(gp.getProduct().getId()));


        for (Long productId : productIds.stream().distinct().toList()) {
            boolean exists = guide.getProducts().stream()
                    .anyMatch(gp -> gp.getProduct().getId().equals(productId));
            if (!exists) {
                Product product = productService.findById(productId);
                GuideProduct gp = new GuideProduct();
                gp.setGuide(guide);
                gp.setProduct(product);
                guide.getProducts().add(gp);
            }
        }
    }

    private void updateExamples(Guide guide, List<ExampleRequestDTO> exampleDtos) {
        Map<Long, GuideExample> existingMap = guide.getExamples().stream()
                .filter(ex -> ex.getId() != null)
                .collect(Collectors.toMap(GuideExample::getId, ex -> ex));

        Set<Long> incomingIds = exampleDtos.stream()
                .map(ExampleRequestDTO::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());


        guide.getExamples().removeIf(ex -> ex.getId() != null && !incomingIds.contains(ex.getId()));

        for (ExampleRequestDTO dto : exampleDtos) {
            GuideExample example;
            if (dto.id() != null && existingMap.containsKey(dto.id())) {
                // 기존 example 수정
                example = existingMap.get(dto.id());
                example.setQuantity(dto.quantity());
                if (!example.getProduct().getId().equals(dto.productId())) {
                    Product product = productService.findById(dto.productId());
                    example.setProduct(product);
                }
            } else {
                // 신규 example
                Product product = productService.findById(dto.productId());
                example = new GuideExample();
                example.setGuide(guide);
                example.setProduct(product);
                example.setQuantity(dto.quantity());
                guide.getExamples().add(example);
            }
        }
    }


    private void updateSteps(Guide guide, List<StepRequestDTO> stepDtos, Step parent) {
        List<Step> existingSteps = (parent == null)
                ? guide.getSteps().stream().filter(s -> s.getParent() == null).toList()
                : parent.getSubSteps();


        Map<String, Step> existingMap = existingSteps.stream()
                .collect(Collectors.toMap(Step::getStepCode, s -> s));

        Set<String> incomingCodes = stepDtos.stream()
                .map(StepRequestDTO::stepCode)
                .collect(Collectors.toSet());


        if (parent == null) {
            guide.getSteps().removeIf(s -> !incomingCodes.contains(s.getStepCode()));
        } else {
            parent.getSubSteps().removeIf(s -> !incomingCodes.contains(s.getStepCode()));
        }

        for (StepRequestDTO dto : stepDtos) {
            Step step = existingMap.getOrDefault(dto.stepCode(), new Step());
            step.setGuide(guide);
            step.setParent(parent);
            step.setStepCode(dto.stepCode());
            step.setTitle(dto.title());
            step.setType(dto.type());
            step.setContent(dto.content());
            step.setMetadata(dto.metadata());

            if (!existingMap.containsKey(dto.stepCode())) {
                if (parent == null) guide.getSteps().add(step);
                else parent.getSubSteps().add(step);
            }

            if (dto.subSteps() != null) {
                updateSteps(guide, dto.subSteps(), step);
            }
        }
    }

    @Override
    public List<Guide> findAll() {
        return guideRepository.findAll();
    }

    @Override
    public Optional<Guide> findById(Long id) {
        return guideRepository.findById(id);
    }

    @Override
    public List<Guide> searchByKeyword(String keyword) {
        return guideRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(keyword, keyword);
    }

    @Override
    public void markStepAsCompleted(Long stepId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음: " + userId));
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new IllegalArgumentException("단계 없음: " + stepId));

        StepProgress progress = stepProgressRepository.findByUserAndStep(user, step)
                .orElse(StepProgress.builder()
                        .user(user)
                        .step(step)
                        .completed(true)
                        .build());

        progress.setCompleted(true);
        stepProgressRepository.save(progress);
    }

    public List<String> getCompletedStepCodes(Long userId, Long guideId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음: " + userId));

        return stepProgressRepository
                .findByUserAndStep_Guide_IdAndCompletedTrue(user, guideId)
                .stream()
                .map(progress -> progress.getStep().getStepCode())
                .toList();
    }

    @Override
    public void deleteGuide(Long guideId) {
        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가이드를 찾을 수 없습니다: " + guideId));
        guideRepository.delete(guide);
    }
}
