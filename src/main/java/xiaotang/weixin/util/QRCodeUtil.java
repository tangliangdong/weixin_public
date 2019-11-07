package xiaotang.weixin.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/16.
 */
public class QRCodeUtil {
    private static final String PNGIMAGE = "png";

    public QRCodeUtil() {
    }

    public static <T extends OutputStream> T writeEncodeQRCodePngImageByEncodeHit(String content, int width, int height, Map<EncodeHintType, Object> encodeHintTypeObjectMap, T t) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix e = writer.encode(content, BarcodeFormat.QR_CODE, width, height, encodeHintTypeObjectMap);
            e = updateBit(e,10);
            MatrixToImageWriter.writeToStream(e, "png", t);
            return t;
        } catch (WriterException var7) {
            throw new RuntimeException(var7);
        } catch (IOException var8) {
            throw new RuntimeException(var8);
        }
    }

    public static <T extends OutputStream> T writeEncodeQRCodePngImageByUTF8(String content, int width, int height, T t) {
        HashMap hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        return (T) writeEncodeQRCodePngImageByEncodeHit(content, width, height, hints, t);
    }

    public static InputStream EncodeRCodePngImageInputStream(String context, int width, int height) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream = (ByteArrayOutputStream)writeEncodeQRCodePngImageByUTF8(context, width, height, outputStream);

        try {
            return outPutStreamToInputStream(outputStream);
        } catch (IOException var5) {
            throw new RuntimeException(var5);
        }
    }

    private static InputStream outPutStreamToInputStream(ByteArrayOutputStream outputStream) throws IOException {
        byte[] bytes = outputStream.toByteArray();
        outputStream.close();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        return inputStream;
    }

    public static String encodeQRCode(InputStream inputStream) {
        QRCodeReader reader = new QRCodeReader();
        BufferedImage image = null;
        Result result = null;

        try {
            image = ImageIO.read(inputStream);
            BufferedImageLuminanceSource e = new BufferedImageLuminanceSource(image);
            HybridBinarizer binarizer = new HybridBinarizer(e);
            BinaryBitmap imageBinaryBitmap = new BinaryBitmap(binarizer);
            result = reader.decode(imageBinaryBitmap);
        } catch (IOException var7) {
            throw new RuntimeException(var7);
        } catch (ChecksumException var8) {
            throw new RuntimeException(var8);
        } catch (NotFoundException var9) {
            throw new RuntimeException(var9);
        } catch (FormatException var10) {
            throw new RuntimeException(var10);
        }

        return result.getText();
    }

    public static String encodeQRCode(File QRCodeFile) {
        try {
            FileInputStream e = new FileInputStream(QRCodeFile);
            return encodeQRCode((InputStream)e);
        } catch (FileNotFoundException var2) {
            throw new RuntimeException(var2);
        }
    }

    private static BitMatrix updateBit(BitMatrix matrix, int margin){
        int tempM = margin*2;
        int[] rec = matrix.getEnclosingRectangle();   //获取二维码图案的属性
        int resWidth = rec[2] + tempM;
        int resHeight = rec[3] + tempM;
        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight); // 按照自定义边框生成新的BitMatrix
        resMatrix.clear();
        for(int i= margin; i < resWidth- margin; i++){   //循环，将二维码图案绘制到新的bitMatrix中
            for(int j=margin; j < resHeight-margin; j++){
                if(matrix.get(i-margin + rec[0], j-margin + rec[1])){
                    resMatrix.set(i,j);
                }

            }

        }

        return resMatrix;

    }
}
