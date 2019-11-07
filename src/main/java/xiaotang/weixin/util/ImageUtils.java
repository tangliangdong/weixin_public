package xiaotang.weixin.util;

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by fanzetao on 2018/4/26.
 */
public abstract class ImageUtils {

    static Logger logger = LoggerFactory.getLogger(ImageUtils.class);

    /**
     * 按比例缩小图片
     * @param inputStream 图片输入流
     * @param scale 缩小比例 0到1的浮点数
     * @return
     * @throws IOException
     */
    public static byte[] scale(InputStream inputStream, float scale) throws IOException {
        BufferedImage originalImage = ImageIO.read(inputStream);

        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            Thumbnails.of(originalImage)
                    .scale(scale)
                    .outputFormat("jpg")
                    .outputQuality(0.8)
                    .toOutputStream(outputStream);

            outputStream.flush();
            byte[] ret = outputStream.toByteArray();
            return ret;
        } finally {
            outputStream.close();
        }
    }

    public static byte[] scaleBySize(BufferedImage originalImage, int width, int height) throws IOException {
        logger.info("压缩图片--> " + width + ":" + height);

        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            Thumbnails.of(originalImage)
                    .size(width, height)
                    .outputFormat("jpg")
                    .outputQuality(0.8)
                    .toOutputStream(outputStream);

            outputStream.flush();
            byte[] ret = outputStream.toByteArray();
            return ret;
        } finally {
            outputStream.close();
        }
    }
    public static byte[] scaleBySize90du(BufferedImage originalImage, int width, int height) throws IOException {
        logger.info("压缩图片--> " + width + ":" + height);

        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            Thumbnails.of(originalImage)
                    .size(width, height)
                    .outputFormat("jpg")
                    .outputQuality(0.8)
                    .rotate(90)
                    .toOutputStream(outputStream);

            outputStream.flush();
            byte[] ret = outputStream.toByteArray();
            return ret;
        } finally {
            outputStream.close();
        }
    }
}
