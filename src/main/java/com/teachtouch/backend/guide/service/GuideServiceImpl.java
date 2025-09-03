package com.teachtouch.backend.guide.service;

import com.teachtouch.backend.example.dto.ExampleRequestDTO;
import com.teachtouch.backend.guide.dto.GuideRequestDTO;
import com.teachtouch.backend.guide.dto.StepRequestDTO;
import com.teachtouch.backend.guide.entity.*;
import com.teachtouch.backend.guide.repository.GuideRepository;
import com.teachtouch.backend.guide.repository.StepProgressRepository;
import com.teachtouch.backend.guide.repository.StepRepository;
import com.teachtouch.backend.product.entity.Product;
import com.teachtouch.backend.product.repository.ProductRepository;
import com.teachtouch.backend.user.entity.User;
import com.teachtouch.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class GuideServiceImpl implements GuideService {

    private final GuideRepository guideRepository;
    private final ProductRepository productRepository;
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


        guide.getProducts().clear();
        guide.getExamples().clear();
        guide.getSteps().clear();


        if (dto.productIds() != null) {
            for (Long productId : dto.productIds()) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않음: " + productId));

                GuideProduct gp = new GuideProduct();
                gp.setProduct(product);
                gp.setGuide(guide);
                guide.getProducts().add(gp);
            }
        }


        if (dto.examples() != null) {
            for (ExampleRequestDTO exDto : dto.examples()) {
                Product product = productRepository.findById(exDto.productId())
                        .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않음: " + exDto.productId()));

                GuideExample example = new GuideExample();
                example.setProduct(product);
                example.setQuantity(exDto.quantity());
                example.setGuide(guide);
                guide.getExamples().add(example);
            }
        }


        if (dto.steps() != null) {
            for (StepRequestDTO stepDto : dto.steps()) {
                Step step = mapStepDtoToEntity(stepDto, guide, null);
                guide.getSteps().add(step);
            }
        }

        return guideRepository.save(guide);
    }


    private Step mapStepDtoToEntity(StepRequestDTO dto, Guide guide, Step parent) {
        Step step = new Step();
        step.setStepCode(dto.stepCode());
        step.setTitle(dto.title());
        step.setType(dto.type());
        step.setContent(dto.content());
        step.setMetadata(dto.metadata());
        step.setGuide(guide);
        step.setParent(parent);

        if (dto.subSteps() != null) {
            for (StepRequestDTO subStepDto : dto.subSteps()) {
                Step subStep = mapStepDtoToEntity(subStepDto, guide, step);
                step.getSubSteps().add(subStep);
            }
        }

        return step;
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

    @Override
    public List<String> getCompletedStepCodes(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음: " + userId));
        return stepProgressRepository.findByUser(user).stream()
                .filter(StepProgress::isCompleted)
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
