package com.hy.util;


import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

//@WebServlet(urlPatterns = "/servlet/ImageCode",name ="ImageCode" )
public class ImageCode {

    public static final String[] partSymbolArray = new String[]{"0","1", "3", "4", "5",
            "6", "7", "8","9", "a", "b", "c", "d", "e", "f","g", "h", "i", "j", "k","l",
            "m", "n","o", "p","q", "r", "s", "t", "u", "v", "w", "x", "y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    public static final String[] partUpperArray = new String[]{"0","1", "3", "4", "5",
            "6", "7", "8","9","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    public static final String[] digitArray=new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    public static String getSymbol(int lenth, String[] symbolArray) {
        String tempStr = "";
        Random rand = new Random();
        for (int i = 0; i < lenth; i++) {
            int j = rand.nextInt(symbolArray.length);
            tempStr += symbolArray[j];
        }
        return tempStr;
    }

    public static String getPartSymbol(int length) {
        return getSymbol(length, partSymbolArray);
    }
    public static String getPartDigit(int length) {
        return getSymbol(length, digitArray);
    }

    private static Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    /**
     * 生成验证码
     * @param request
     * @param response
     * @throws ServletException
     */
    public static void generatorImg(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
//            System.out.println(request.getParameter("time"));
        request.getSession(true).setAttribute("imgTime", new Date().getTime());
        response.setContentType("image/jpeg; charset=GBK");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        try {
            int width = 90, height = 38;
            BufferedImage image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = image.getGraphics();
            Random random = new Random();
            g.setColor(getRandColor(245, 250));
            g.fillRect(0, 0, width, height);
            g.setFont(new Font("Arial", Font.PLAIN, 30));
            g.setColor(getRandColor(150, 250));
            for (int i = 0; i < 300; i++) {
                int x = random.nextInt(width);
                int y = random.nextInt(height);
                int xl = random.nextInt(12);
                int yl = random.nextInt(12);
                g.drawLine(x, y, (x + xl), (y + yl));
            }
            String sRand = getPartSymbol(4);
            for (int i = 0; i < sRand.length(); i++) {
                g.setColor(new Color(10 + new Random().nextInt(100),
                        10 + new Random().nextInt(100), 10 + new Random()
                        .nextInt(100)));
/*                if (i == 0)
                    g.drawString(" ", 1, 30);*/
                g.drawString(sRand.substring(i, i + 1), (20 * i + (i==0?5:9)), 30);
            }
            request.getSession(true).setAttribute("imageCode", sRand);
            g.dispose();
            ImageIO.write(image, "JPEG", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*    public static Random random = new Random();

    public static String getRandom(int length) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < length; i++) {
            boolean isChar = (random.nextInt(2) % 2 == 0);// 输出字母还是数字
            if (isChar) { // 字符串
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; // 取得大写字母还是小写字母
                ret.append((char) (choice + random.nextInt(26)));
            } else { // 数字
                ret.append(Integer.toString(random.nextInt(10)));
            }
        }
        return ret.toString();
    }*/
}
