package com.teachtouch.backend.guide.service;

import com.teachtouch.backend.example.dto.ExampleRequestDTO;
import com.teachtouch.backend.guide.dto.GuideRequestDTO;
import com.teachtouch.backend.guide.dto.StepRequestDTO;
import com.teachtouch.backend.guide.entity.Guide;
import com.teachtouch.backend.guide.entity.GuideExample;
import com.teachtouch.backend.guide.entity.GuideProduct;
import com.teachtouch.backend.guide.entity.Step;
import com.teachtouch.backend.guide.repository.GuideRepository;
import com.teachtouch.backend.product.entity.Product;
import com.teachtouch.backend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GuideServiceImpl implements GuideService {

    private final GuideRepository guideRepository;
    private final ProductRepository productRepository;

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
}
