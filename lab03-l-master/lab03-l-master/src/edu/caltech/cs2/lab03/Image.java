package edu.caltech.cs2.lab03;

import edu.caltech.cs2.libraries.Pixel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Image {
    private Pixel[][] pixels;

    public Image(File imageFile) throws IOException {
        BufferedImage img = ImageIO.read(imageFile);
        this.pixels = new Pixel[img.getWidth()][img.getHeight()];
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                this.pixels[i][j] = Pixel.fromInt(img.getRGB(i, j));
            }
        }
    }

    private Image(Pixel[][] pixels) {
        this.pixels = pixels;
    }

    public Image transpose() {
        Pixel[][] r = new Pixel[pixels[0].length][pixels.length];
        Image img = new Image(r);
        for(int i = 0; i < pixels.length; i++){
            for(int j = 0; j < pixels[i].length; j++){
                img.pixels[j][i] = pixels[i][j];
            }
        }
        return img;
    }

    public String decodeText() {
        String text = "";
        int bitCount = 0;
        int byteInts = 0;
        for(int i = 0; i < pixels.length; i++){
            for(int j = 0; j < pixels[i].length; j++){
                bitCount++;
                byteInts += pixels[i][j].getLowestBitOfR() * Math.pow(2, bitCount - 1);
                if(bitCount == 8){
                    if(byteInts == 0){
                        return text;
                    }
                    text += (char)byteInts;
                    bitCount = 0;
                    byteInts = 0;
                }
            }
        }
        return text;
    }

    public Image hideText(String text) { //Image
        Pixel[][] r = new Pixel[pixels.length][pixels[0].length];
        Image img = new Image(r);
        char letter;
        ArrayList<Integer> fullList = new ArrayList<>();
        for (int q = 0; q < text.length(); q++) {
            letter = text.charAt(q);
            int letterByte = (int) letter;
            int[] temp = new int[8];
            for (int k = 7; k >= 0; k--) {
                temp[7 - k] = (int) (letterByte / Math.pow(2, k));
                letterByte = (int) (letterByte - ((Math.pow(2, k) * temp[7 - k])));
            }
            int[] tempReversed = new int[8];
            for (int o = 0; o < temp.length; o++) {
                tempReversed[o] = temp[7 - o];
            }
            for (int l = 0; l < 8; l++) {
                fullList.add(tempReversed[l]);
            }
        }

        System.out.println();
        int p = 0;
        for(int i = 0; i < pixels.length; i++){
            for(int j = 0; j < pixels[i].length; j++) {
                img.pixels[i][j] = pixels[i][j];
                if(p < fullList.size()) {
                    img.pixels[i][j] = img.pixels[i][j].fixLowestBitOfR(fullList.get(i * pixels[i].length + j));
                }
                else {
                    img.pixels[i][j] = img.pixels[i][j].fixLowestBitOfR(0);
                }
                p++;
            }
        }
        System.out.println(img.decodeText());
        return img;
    }

    public BufferedImage toBufferedImage() {
        BufferedImage b = new BufferedImage(this.pixels.length, this.pixels[0].length, BufferedImage.TYPE_4BYTE_ABGR);
        for (int i = 0; i < this.pixels.length; i++) {
            for (int j = 0; j < this.pixels[0].length; j++) {
                b.setRGB(i, j, this.pixels[i][j].toInt());
            }
        }
        return b;
    }

    public void save(String filename) {
        File out = new File(filename);
        try {
            ImageIO.write(this.toBufferedImage(), filename.substring(filename.lastIndexOf(".") + 1, filename.length()), out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
