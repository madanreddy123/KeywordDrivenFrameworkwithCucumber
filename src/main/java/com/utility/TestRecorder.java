package com.utility;

import java.awt.*;
import java.io.File;
import org.monte.media.Format;
import org.monte.media.math.Rational;
import static org.monte.media.VideoFormatKeys.*;
import static org.monte.media.AudioFormatKeys.*;
import org.monte.screenrecorder.ScreenRecorder;

public class TestRecorder extends ScreenRecorder {
    static ScreenRecorder screenRecorder;
    public TestRecorder(GraphicsConfiguration cfg, Rectangle captureArea, Format fileFormat,
                        Format screenFormat, Format mouseFormat, Format audioFormat, File movieFolder)
            throws Exception {
        super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat, movieFolder);
    }

    public static void startRecording() throws Exception {
        File file = new File("./recordings/");
        GraphicsConfiguration gconfig = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getDefaultScreenDevice()
                .getDefaultConfiguration();

         screenRecorder = new ScreenRecorder(gconfig, new Rectangle(0, 0, 1920, 1080),
                new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        CompressorNameKey, COMPRESSOR_NAME_QUICKTIME_ANIMATION,
                        DepthKey, 24, FrameRateKey, Rational.valueOf(15),
                        QualityKey, 1.0f, KeyFrameIntervalKey, 15 * 60),
                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black",
                        FrameRateKey, Rational.valueOf(30)),
                null, file);
        screenRecorder.start();
    }

    public static void stopRecording() throws Exception {
        screenRecorder.stop();
    }
}
