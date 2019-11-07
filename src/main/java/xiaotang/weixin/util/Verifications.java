package xiaotang.weixin.util;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by fanzetao on 2018/5/18.
 */
public class Verifications {

    public final static String CODE = "verification-code";

    private BufferedImage image;

    private static final int IMG_WIDTH = 146;

    private static final int IMG_HEIGHT = 30;

    private static final int DISTURB_LINE_SIZE = 50;

    private Random random = new Random();

    private int result;

    private String randomString;

    //零一二三四五六七八九十乘除加减
    private static final String NUMBERS = "\u96F6\u4E00\u4E8C\u4E09\u56DB\u4E94\u516D\u4E03\u516B\u4E5D\u5341\u4E58\u9664\u52A0\u51CF";

    private final Font font = new Font("黑体", Font.BOLD, 18);

    private static final Map<String, Integer> OPMap = new HashMap<String, Integer>();

    static {
        OPMap.put("*", 11);
        OPMap.put("/", 12);
        OPMap.put("+", 13);
        OPMap.put("-", 14);
    }

    private BufferedImage drawVerificationCodeImage() {
        BufferedImage image = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, IMG_WIDTH, IMG_HEIGHT);
        g.setColor(getRandomColor(200, 250));
        g.drawRect(0, 0, IMG_WIDTH - 2, IMG_HEIGHT - 2);

        g.setColor(getRandomColor(110, 133));
        for (int i = 0; i < DISTURB_LINE_SIZE; i++) {
            drawDisturbLine1(g);
            drawDisturbLine2(g);
        }
        getRandomMathString();
        StringBuffer s = new StringBuffer();
        for (int j = 0, k = randomString.length(); j < k; j++) {
            int child = 0;
            if (j == 1) {
                child = OPMap.get(String.valueOf(randomString.charAt(j)));
            } else {
                child = Integer.parseInt(String.valueOf(randomString.charAt(j)));
            }
            String ch = String.valueOf(NUMBERS.charAt(child));
            s.append(ch);
            drawRandomString((Graphics2D) g, ch, j);
        }
        drawRandomString((Graphics2D) g, "\u7B49\u4E8E\uFF1F", 3);
        s.append("\u7B49\u4E8E \uFF1F");
        randomString = s.toString();
        g.dispose();
        return image;
    }

    private void getRandomMathString() {
        int xx = random.nextInt(10);
        int yy = random.nextInt(10);
        StringBuilder suChinese = new StringBuilder();
        int randomOperate = (int) Math.round(Math.random() * 2);
        if (randomOperate == 0) {
            this.result = yy * xx;
            suChinese.append(yy);
            suChinese.append("*");
            suChinese.append(xx);
        } else if (randomOperate == 1) {
            if (!(xx == 0) && yy % xx == 0) {
                this.result = yy / xx;
                suChinese.append(yy);
                suChinese.append("/");
                suChinese.append(xx);
            } else {
                this.result = yy + xx;
                suChinese.append(yy);
                suChinese.append("+");
                suChinese.append(xx);
            }
        } else if (randomOperate == 2) {
            this.result = yy - xx;
            suChinese.append(yy);
            suChinese.append("-");
            suChinese.append(xx);
        } else {
            this.result = yy + xx;
            suChinese.append(yy);
            suChinese.append("+");
            suChinese.append(xx);
        }
        this.randomString = suChinese.toString();
    }

    private void drawRandomString(Graphics2D g, String randomString, int i) {
        g.setFont(font);
        int rc = random.nextInt(255);
        int gc = random.nextInt(255);
        int bc = random.nextInt(255);
        g.setColor(new Color(rc, gc, bc));
        int x = random.nextInt(3);
        int y = random.nextInt(2);
        g.translate(x, y);
        int degree = new Random().nextInt() % 15;
        g.rotate(degree * Math.PI / 180, 5 + i * 25, 20);
        g.drawString(randomString, 5 + i * 25, 20);
        g.rotate(-degree * Math.PI / 180, 5 + i * 25, 20);
    }

    private void drawDisturbLine1(Graphics g) {
        int x1 = random.nextInt(IMG_WIDTH);
        int y1 = random.nextInt(IMG_HEIGHT);
        int x2 = random.nextInt(13);
        int y2 = random.nextInt(15);
        g.drawLine(x1, y1, x1 + x2, y1 + y2);
    }

    private void drawDisturbLine2(Graphics g) {
        int x1 = random.nextInt(IMG_WIDTH);
        int y1 = random.nextInt(IMG_HEIGHT);
        int x2 = random.nextInt(13);
        int y2 = random.nextInt(15);
        g.drawLine(x1, y1, x1 - x2, y1 - y2);
    }

    private Color getRandomColor(int fc, int bc) {
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc - 16);
        int g = fc + random.nextInt(bc - fc - 14);
        int b = fc + random.nextInt(bc - fc - 18);
        return new Color(r, g, b);
    }

    private void createImage(String fileLocation) {
        try {
            FileOutputStream fos = new FileOutputStream(fileLocation);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ImageIO.write(image, "png", bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
        }
    }

    /**
     * 生成 加载中… 图片
     * @param destOutPath
     */
    private void createDefaultVerificationCode(String destOutPath) {
        int lineSize = 15;
        image = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, IMG_WIDTH, IMG_HEIGHT);
        g.setColor(getRandomColor(200, 250));
        g.drawRect(0, 0, IMG_WIDTH - 2, IMG_HEIGHT - 2);
        for (int i = 0; i < lineSize; i++) {
            drawDisturbLine1(g);
            drawDisturbLine2(g);
        }
        //加载中…
        String defaultVCString = "\u52A0\u8F7D\u4E2D\u2026";
        String defaultChar = null;
        for (int i = 0; i < 4; i++) {
            defaultChar = String.valueOf(defaultVCString.charAt(i));
            drawRandomString((Graphics2D) g, defaultChar, i);
        }
        createImage(destOutPath);
    }

    /**
     * 生成验证码
     * @param request
     * @param response
     * @param key
     */
    public void createVerificationCode(HttpServletRequest request, HttpServletResponse response, String key) {
        try {
            request.setCharacterEncoding("utf-8");
            response.setContentType("text/html; charset=utf-8");
            BufferedImage image = drawVerificationCodeImage();
            int result = getResult();
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "No-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/png");
            HttpSession session = request.getSession(true);
            session.setAttribute(key, result);
            ImageIO.write(image, "png", response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("验证码获取异常，请稍后重试");
        }
    }

    /**
     * 生成验证码
     * @param outputStream
     * @return
     */
    public int createVerificationCode(OutputStream outputStream) {
        try {
            BufferedImage image = drawVerificationCodeImage();
            ImageIO.write(image, "png", outputStream);
            return getResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("验证码获取异常，请稍后重试");
        }
    }

    public int getResult() {
        return result;
    }
}
