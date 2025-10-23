package gui.login.forms;


import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class Home extends JPanel {

    private List<ModelLocation> locations;
    private int index = 0;
    private HomeOverlay homeOverlay;

    private MediaPlayerFactory factory;
    private EmbeddedMediaPlayer mediaPlayer;

    public Home() {
        init();
        testData();
    }

    private void init() {
        factory = new MediaPlayerFactory();
        mediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer();
        Canvas canvas = new Canvas();
        mediaPlayer.videoSurface().set(factory.videoSurfaces().newVideoSurface(canvas));
        mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
                if (newTime >= mediaPlayer.status().length() - 1000) {
                    mediaPlayer.controls().setPosition(0);
                }
            }
        });
        setLayout(new BorderLayout());
        add(canvas);
    }

    private void testData() {
        locations = new ArrayList<>();
        locations.add(new ModelLocation("KHÁCH SẠN MIMOSA",
                "Khách sạn MIMOSA tọa lạc tại vị trí lý tưởng ven biển, mang đến cho du khách không gian nghỉ dưỡng sang trọng cùng tầm nhìn hướng ra bãi biển tuyệt đẹp, nơi bạn có thể tận hưởng trọn vẹn vẻ đẹp của biển xanh và nắng vàng.",
                "/video/video 1.mp4"));

        locations.add(new ModelLocation("Lời thì thầm từ những con sóng",
                "Chỉ cách bãi biển vài bước chân, MIMOSA Hotel là điểm dừng chân lý tưởng cho những ai muốn đắm mình trong làn gió biển mát lành và chiêm ngưỡng khung cảnh hoàng hôn lãng mạn ngay từ ban công phòng nghỉ.",
                "/video/video 2.mp4"));

        locations.add(new ModelLocation("Khung cảnh nên thơ",
                "Đến với MIMOSA Hotel, bạn sẽ được thư giãn trong không gian ấm cúng, tận hưởng view biển thơ mộng và trải nghiệm kỳ nghỉ đáng nhớ bên bờ sóng xanh.",
                "/video/video 3.mp4"));
    }

    public void initOverlay(JFrame frame) {
        homeOverlay = new HomeOverlay(frame, locations);
        homeOverlay.getOverlay().setEventHomeOverlay(index1 -> {
            play(index1);
        });
        mediaPlayer.overlay().set(homeOverlay);
        mediaPlayer.overlay().enable(true);
    }

    public void play(int index) {
        this.index = index;
        ModelLocation location = locations.get(index);
        if (mediaPlayer.status().isPlaying()) {
            mediaPlayer.controls().stop();
        }
        String mrl = resolveMediaFromResources(location.getVideoPath());
        if (mrl == null) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy video: " + location.getVideoPath(),
                    "Lỗi media", JOptionPane.ERROR_MESSAGE);
            return;
        }

        mediaPlayer.media().play(mrl);
        mediaPlayer.controls().play();
        homeOverlay.getOverlay().setIndex(index);
    }

    public void stop() {
        mediaPlayer.controls().stop();
        mediaPlayer.release();
        factory.release();
    }

    // Trả về đường dẫn file hệ thống cho VLC từ resource (classpath).
    private String resolveMediaFromResources(String resourcePath) {
        try {
            URL url = getClass().getResource(resourcePath);
            if (url == null) return null;

            // Trường hợp chạy từ IDE: resources là file trên ổ đĩa (protocol=file)
            if ("file".equalsIgnoreCase(url.getProtocol())) {
                try {
                    return Paths.get(url.toURI()).toString();
                } catch (Exception ex) {
                    // fallback: dùng URL dạng file:/...
                    return url.toExternalForm();
                }
            }

            // Trường hợp chạy trong JAR: trích ra file tạm rồi phát
            String suffix = ".mp4";
            int dot = resourcePath.lastIndexOf('.');
            if (dot >= 0) suffix = resourcePath.substring(dot);

            try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
                if (in == null) return null;
                Path tmp = Files.createTempFile("vlc-media-", suffix);
                Files.copy(in, tmp, StandardCopyOption.REPLACE_EXISTING);
                tmp.toFile().deleteOnExit();
                return tmp.toString();
            }
        } catch (IOException ex) {
            return null;
        }
    }
}
