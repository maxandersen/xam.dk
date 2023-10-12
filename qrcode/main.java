///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 11+
//DEPS com.google.zxing:core:3.4.0
//DEPS com.google.zxing:javase:3.4.0
//DEPS info.picocli:picocli:4.5.0
//DEPS info.picocli:picocli-codegen:4.5.0
//JAVA_OPTIONS -Dapple.awt.UIElement=true

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class main implements Callable<Integer> {

    @Parameters(index = "0", description = "Text to encode")
    String text; 

    @Option(names = {"-i", "--image"}, description = "Image to overlay", required = true)
    Path imagePath;

    @Option(names = {"-o", "--output"}, description = "Output file", defaultValue = "qrcode.png")
    Path outPath;

    public static void main(String[] args) throws Exception {
        
        new picocli.CommandLine(new main()).execute(args);
    
    }

    public Integer call() {
        writeQrCode(text, imagePath, outPath, 640);

        if(outPath.toFile().exists()) {
            System.out.println("Created QR code at " + outPath);
            System.exit(ExitCode.OK); // hard exit to avoid OSX AWT delay
        } else {
            System.out.println("Could not create QR code at " + outPath);
        }
        return ExitCode.OK;
    }

    private void writeQrCode(String text, Path imagePath, Path outPath, int width) {
        try {
        Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();

        // Specify the error correction, to allow the QR code to tolerate errors, such as
        // a great big picture plunked in the middle
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        // QR codes are square
        int height = width;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width,
                height, hints);
        // Load QR image
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix,
                new MatrixToImageConfig(
                        0xFF000000,
                        0xFFFFFFFF));

        // Initialize combined image
        BufferedImage combined = new BufferedImage(qrImage.getHeight(), qrImage.getWidth(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) combined.getGraphics();

        // Write QR code to new image at position 0/0
        g.drawImage(qrImage, 0, 0, null);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        addOverlayImage(g, qrImage, imagePath);

        ImageIO.write(combined, "png", outPath.toFile());
        
    } catch(IOException io) {
            throw new IllegalStateException("Could not write QR code to " + outPath, io);
        } catch (WriterException e) {
            throw new IllegalStateException("Could not write QR code text", e);
        }
    }


    private static BufferedImage addOverlayImage(Graphics2D g, BufferedImage qrImage,
                                                 Path imagePath) {
        // Load logo image
        BufferedImage overlay;
        try {
            overlay = ImageIO.read(imagePath.toFile());
        } catch (IOException e) {
            throw new IllegalStateException("Could not read overlay image from " + imagePath, e);
        } 

        // Calculate the delta height and width between QR code and the logo
        // Note that we don't do any scaling, so the sizes need to kind of
        // work together without obscuring too much logo
        int deltaHeight = qrImage.getHeight() - overlay.getHeight();
        int deltaWidth = qrImage.getWidth() - overlay.getWidth();

        int woffset = Math.round(deltaWidth / 2);
        int hoffset = Math.round(deltaHeight / 2);

        // Write the logo into the combined image at position (deltaWidth / 2) and
        // (deltaHeight / 2), so that it's centered
        g.drawImage(overlay, woffset, hoffset, null);
        return overlay;
    }
}