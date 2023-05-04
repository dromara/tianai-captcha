package cloud.tianai.captcha.generator.impl;

import cloud.tianai.captcha.common.exception.ImageCaptchaException;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.ImageCaptchaPostProcessor;
import cloud.tianai.captcha.generator.common.model.dto.CaptchaTransferData;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: 天爱有情
 * @date 2023/4/24 15:23
 * @Description 验证码后处理器管理
 */
public class StaticCaptchaPostProcessorManager {

    @Getter
    private static LinkedList<ImageCaptchaPostProcessor> processors = new LinkedList<>();

    static {

    }

    public static void add(ImageCaptchaPostProcessor processor) {
        processors.add(processor);
    }

    public static void add(Integer index, ImageCaptchaPostProcessor processor) {
        processors.add(index, processor);
    }

    public static void addFirst(ImageCaptchaPostProcessor processor) {
        processors.addFirst(processor);
    }

    public static void addLast(ImageCaptchaPostProcessor processor) {
        processors.addLast(processor);
    }

    public static void clear() {
        processors.clear();
    }

    public static void add(List<ImageCaptchaPostProcessor> addPostProcessors) {
        processors.addAll(addPostProcessors);
    }


    public static ImageCaptchaInfo applyPostProcessorBeforeGenerate(CaptchaTransferData transferData, ImageCaptchaGenerator context) {
        for (ImageCaptchaPostProcessor processor : processors) {
            try {
                ImageCaptchaInfo imageCaptchaInfo = processor.beforeGenerateCaptchaImage(transferData, context);
                if (imageCaptchaInfo != null) {
                    return imageCaptchaInfo;
                }
            } catch (Exception e) {
                throw new ImageCaptchaException("apply ImageCaptchaPostProcessor.beforeGenerateCaptchaImage error, [" + processor.getClass() + "]", e);
            }
        }
        return null;
    }

    public static void applyPostProcessorBeforeWrapImageCaptchaInfo(CaptchaTransferData transferData, ImageCaptchaGenerator context) {
        for (ImageCaptchaPostProcessor processor : processors) {
            try {
                processor.beforeWrapImageCaptchaInfo(transferData, context);
            } catch (Exception e) {
                throw new ImageCaptchaException("apply ImageCaptchaPostProcessor.beforeWrapImageCaptchaInfo error, [" + processor.getClass() + "]", e);
            }
        }
    }


    public static void applyPostProcessorAfterGenerateCaptchaImage(CaptchaTransferData transferData, ImageCaptchaInfo imageCaptchaInfo, ImageCaptchaGenerator context) {
        for (ImageCaptchaPostProcessor processor : processors) {
            try {
                processor.afterGenerateCaptchaImage(transferData, imageCaptchaInfo, context);
            } catch (Exception e) {
                throw new ImageCaptchaException("apply ImageCaptchaPostProcessor.afterGenerateCaptchaImage error, [" + processor.getClass() + "]", e);
            }
        }
    }

}
