package gui.menu;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;

import com.formdev.flatlaf.util.Animator;

public class MenuAnimation {

    private static final HashMap<MenuItem, Animator> hash = new HashMap<>();
    private static final Random random = new Random();

    public static void animate(MenuItem menu, boolean show) {
        // Dừng animator hiện tại nếu đang chạy
        if (hash.containsKey(menu) && hash.get(menu).isRunning()) {
            hash.get(menu).stop();
        }

        Animator lightningAnimator = new Animator(200, new Animator.TimingTarget() {
            private int flashCount = 0;
            private final int maxFlashes = 3;
            private Color originalBackground = menu.getBackground();
            private boolean isFlashOn = false;

            @Override
            public void timingEvent(float f) {
                // Nhấp nháy màu nền
                if (f >= (flashCount + 1) / (float) maxFlashes) {
                    flashCount++;
                    isFlashOn = !isFlashOn;
                    menu.setBackground(isFlashOn ? new Color(211, 211, 211) : originalBackground); // Trắng xám hoặc gốc
                }

                int shakeX = random.nextInt(3) - 1; // -1, 0, 1
                int shakeY = random.nextInt(3) - 1;
                menu.setLocation(menu.getX() + shakeX, menu.getY() + shakeY);
                menu.revalidate();
                menu.repaint();
            }

            @Override
            public void end() {
                menu.setBackground(originalBackground);
                menu.setLocation(menu.getX() - (menu.getX() % 2), menu.getY() - (menu.getY() % 2));
                menu.revalidate();

                // Chạy animation mở rộng submenu
                runMenuAnimation(menu, show);
            }
        });
        lightningAnimator.setResolution(1);
        lightningAnimator.start();
        hash.put(menu, lightningAnimator);
    }

    private static void runMenuAnimation(MenuItem menu, boolean show) {
        menu.setMenuShow(show);
        Animator animator = new Animator(400, new Animator.TimingTarget() {
            @Override
            public void timingEvent(float f) {
                if (show) {
                    menu.setAnimate(f);
                } else {
                    menu.setAnimate(1f - f);
                }
                menu.revalidate();
            }

            @Override
            public void end() {
                hash.remove(menu);
            }
        });
        animator.setResolution(1);
        animator.setInterpolator((float f) -> (float) (1 - Math.pow(1 - f, 3)));
        animator.start();
        hash.put(menu, animator);
    }
}
