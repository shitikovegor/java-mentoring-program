package com.jmp.advanced.multithreading.task7;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

import javax.imageio.ImageIO;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static com.jmp.advanced.multithreading.task7.ForkBlur.sThreshold;

class ForkBlurTests {

    @SneakyThrows
    @Test
    void compute() {
        final var srcName = "task7/red-tulip.jpg";
        final var srcFile = new File(
                Objects.requireNonNull(getClass().getClassLoader().getResource(srcName)).getFile());
        final var image = ImageIO.read(srcFile);

        System.out.println("Source image: " + srcName);
        final var blurredImage = blur(image);
        final var dstName = Paths.get("src/test/resources/task7/blurred-tulip.png");
        Files.deleteIfExists(dstName);

        final var dstFile = Files.createFile(dstName).toFile();
        ImageIO.write(blurredImage, "png", dstFile);
        System.out.println("Output image: " + dstName);
    }

    public static BufferedImage blur(final BufferedImage srcImage) {
        final var w = srcImage.getWidth();
        final var h = srcImage.getHeight();

        final var src = srcImage.getRGB(0, 0, w, h, null, 0, w);
        final var dst = new int[src.length];

        System.out.println("Array size is " + src.length);
        System.out.println("Threshold is " + sThreshold);

        final var processors = Runtime.getRuntime().availableProcessors();
        System.out.println(processors + " processor"
                + (processors != 1 ? "s are " : " is ")
                + "available");

        final var fb = new ForkBlur(src, 0, src.length, dst);
        final var pool = new ForkJoinPool();
        final var startTime = System.currentTimeMillis();
        pool.invoke(fb);
        final var endTime = System.currentTimeMillis();

        System.out.println("Image blur took " + (endTime - startTime) +
                " milliseconds.");

        final var dstImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        dstImage.setRGB(0, 0, w, h, dst, 0, w);

        return dstImage;
    }

}
