package org.example;

import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import javax.imageio.ImageIO;

public class SimpleGame extends JFrame implements KeyListener {
    private int medkits = 0;
    private int bullets = 0;
    private int health = 100;
    private String message = "Вы очнулись в заброшенном городе, вам нужно найти выход.";
    private int location = 0; // Текущая локация (0 - стартовая, 1 - патроны, 2 - враг)
    private BufferedImage playerImage;
    private BufferedImage currentMapImage; // Текущая карта
    private Font font;
    private Clip backgroundMusic;
    private Random random;

    public SimpleGame() {
        setTitle("Заброшенный Город");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        addKeyListener(this);
        random = new Random();
        loadResources();
        playBackgroundMusic("background.wav");
        setVisible(true);
    }

    private void loadResources() {
        loadPlayerImage();
        loadFont();
        loadMapImage();
    }

    private void loadPlayerImage() {
        try {
            playerImage = ImageIO.read(getClass().getResourceAsStream("/player.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFont() {
        try {
            InputStream fontStream = getClass().getResourceAsStream("/font/your-font.ttf");
            if (fontStream != null) {
                font = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(20f);
            } else {
                System.err.println("Font file not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMapImage() {
        String[] mapNames = {"map.jpg", "map1.jpg", "map2.jpg"};
        try {
            currentMapImage = ImageIO.read(getClass().getResourceAsStream("/" + mapNames[location]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playBackgroundMusic(String filePath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResourceAsStream("/" + filePath));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioInputStream);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkForItems() {
        if (location == 1) { // Локация с патронами
            bullets += 10;
            message = "Вы нашли патроны, добавляем в инвентарь.";
        } else if (location == 0) { // Локация с аптечками
            if (health < 100) {
                health = 100;
                message = "Вы нашли аптечку, восстанавливаем здоровье.";
            } else {
                medkits++;
                message = "Вы нашли аптечку! Добавляем в инвентарь.";
            }
        } else if (location == 2) { // Локация с врагом
            startBattle();
        }
        repaint();
    }

    private void startBattle() {
        if (bullets > 0) {
            int chance = random.nextInt(100);
            int damage = random.nextInt(16) + 5; // Урон от 5 до 20

            if (chance < 50) {
                message = "Начинается бой! Вы победили врага.";
                bullets -= 1; // Используем патроны
            } else {
                health -= damage;
                message = "Вы проиграли! Вы получили " + damage + " урона.";
            }
        } else {
            message = "Патронов нет, вы решили сбежать.";
            bullets += 10; // Находим патроны при сбеге
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            location++;
            if (location > 2) location = 0; // Переход назад на первую локацию если вышли за предел
            loadMapImage();
            checkForItems();
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            location--;
            if (location < 0) location = 2; // Переход на последнюю локацию если вышли за предел
            loadMapImage();
            checkForItems();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setFont(font);
        g.setColor(Color.BLACK);
        g.fillRect(0, 300, 600, 100); // Чёрная рамка для текста
        g.setColor(Color.WHITE);
        g.drawString("Аптечки: " + medkits, 10, 320);
        g.drawString("Патроны: " + bullets, 500, 320);
        g.drawString(message, 10, 310);

        if (currentMapImage != null) {
            g.drawImage(currentMapImage, 0, 0, 600, 300, null);
        }

        if (playerImage != null) {
            g.drawImage(playerImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH), 250, 150, null);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimpleGame::new);
    }
}
