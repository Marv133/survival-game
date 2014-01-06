package survivalgame.map.test_Oberflaeche;

import survivalgame.map.generation.MapGenerator;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: mabr
 * Date: 09.10.13
 * Time: 16:22
 * To change this template use File | Settings | File Templates.
 */
public class UserInterface extends JPanel implements KeyListener {

    private JFrame jFrame;
    private MapGenerator generator = new MapGenerator(500);
    private int x = 0;
    private int y = 0;
    private BufferedImage dirt;
    private BufferedImage stone;
    private BufferedImage air;
    private BufferedImage gold;
    private BufferedImage iron;
    private BufferedImage wood;
    private BufferedImage caveStone;
    private BufferedImage caveDirt;
    private static final String PATH = "/Users/Shared/development/projects_git/applications/terraria-clone/src/main/resources/";

    public UserInterface() {
        try {
            dirt = ImageIO.read(new File(PATH + "Dirt.png"));
            stone = ImageIO.read(new File(PATH + "Stone.png"));
            air = ImageIO.read(new File(PATH + "Air.png"));
            gold = ImageIO.read(new File(PATH + "Gold.png"));
            iron = ImageIO.read(new File(PATH + "Iron.png"));
            wood = ImageIO.read(new File(PATH + "Wood.png"));
            caveStone = ImageIO.read(new File(PATH + "Stone_dark.png"));
            caveDirt = ImageIO.read(new File(PATH + "Dirt_alt.png"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        jFrame = new JFrame();
        jFrame.setSize(500,500);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLocationRelativeTo(null);
        jFrame.setLayout(null);

        setSize(500, 500);
        setLocation(0, 0);
        setLayout(null);
        jFrame.add(this);
        jFrame.addKeyListener(this);
        jFrame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponents(g);
        float[] map = generator.generate();
        for (int x = this.x - 250, paintX = 0; x < this.x + 250; x++, paintX = paintX % 500) {
            for (int y = this.y - 250, paintY = 0; y < this.y + 250; y++, paintY++) {
                int tx = x + 250;
                int ty = y + 250;
                float value = Math.abs(map[tx+ty*500]/MapGenerator.maxHeight);          //MapGenerator.maxHeight
                try{
                    g.setColor(new Color(value,value,value));
                }
                catch (IllegalArgumentException e){
                    System.out.println(map[tx+ty*500]);
                    e.printStackTrace();
                    return;
                }

                g.drawRect(paintX,paintY,1,1);
            }
            paintX++;
        }
    }


    public static void main(String[] args) {
        UserInterface userInterface = new UserInterface();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == 'w') {
            this.y = y - 100;
        }
        else if (e.getKeyChar() == 's') {
            this.y = y + 100;
        }
        else if (e.getKeyChar() == 'a') {
            this.x = x - 100;
        }
        else if (e.getKeyChar() == 'd') {
            this.x = x + 100;
        }
        this.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
