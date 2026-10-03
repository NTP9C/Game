import java.awt.*;
import java.util.Random;
import javax.swing.*;


public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.setVisible(true);

        createMeteor meteor = new createMeteor(frame);
    }
}

class createMeteor {
    int countMeteor;
    JLabel count;
    createMeteor(JFrame frame) {

        String input = JOptionPane.showInputDialog(frame, "Input meteor :");
        int amountMeteor = Integer.parseInt(input);

        if (amountMeteor <= 0) 
        {amountMeteor = 1;}
        
        //int amountMeteor = 30;  // <-- จำนวนอุกกาบาต
        int meteorSize = 50;    // <-- size ของอุกกาบาต

        JLabel[] meteor = new JLabel[amountMeteor];
        int[] DirectionX = new int[amountMeteor];
        int[] DirectionY = new int[amountMeteor];
        int[] meteorSpeed = new int[amountMeteor];

        Random rand = new Random();

        countMeteor = amountMeteor;

        count = new JLabel("Meteor : "+countMeteor);
        count.setSize(150, 30);
        count.setFont(new Font("Arial" , Font.BOLD , 20));
        count.setForeground(new Color(188, 0, 0));
        count.setLocation(10 , 10);
        frame.add(count);

        Thread[] movementTrade = new Thread[amountMeteor];

        String[] meteorFiles = {"meteor1.png", "meteor2.png", "meteor3.png", "meteor4.png", "meteor5.png", "meteor6.png", "meteor7.png", "meteor8.png", "meteor9.png", "meteor510ng"};
        ImageIcon[] meteorPics = new ImageIcon[meteorFiles.length];

        ImageIcon pathBoom = new ImageIcon("src/bomb.gif");
        Image BoomScale = pathBoom.getImage().getScaledInstance(50, 50, Image.SCALE_FAST);
        ImageIcon BoomPic = new ImageIcon(BoomScale);

        for (int k = 0; k < meteorFiles.length; k++) {
            ImageIcon path = new ImageIcon("src/" + meteorFiles[k]);
            Image scale = path.getImage().getScaledInstance(meteorSize, meteorSize, Image.SCALE_SMOOTH);

            meteorPics[k] = new ImageIcon(scale);
        }


        for (int i = 0; i < meteor.length; i++) {
            meteor[i] = new JLabel();

            int randMeteorType = rand.nextInt(meteorFiles.length); // <-- สุ่มรูป
            meteor[i].setIcon(meteorPics[randMeteorType]);   // <-- set รูปตามที่กำหนดไว้

            int x = rand.nextInt(400);
            int y = rand.nextInt(400);

            meteor[i].setBounds(x, y, meteorSize, meteorSize);
            frame.add(meteor[i]);

            DirectionX[i] = rand.nextInt(3) - 1;
            DirectionY[i] = rand.nextInt(3) - 1;

            meteorSpeed[i] = rand.nextInt(8) + 1; // <-- สุ่มความไวอุกกาบาต

            if (DirectionX[i] == 0 && DirectionY[i] == 0) {
                DirectionX[i] = 1;
                DirectionY[i] = 1;
            }

            int meteorIndex = i;

            movementTrade[i] = new Thread() {
                public void run() {
                    while (true) {
                        meteorMovement(meteor, meteorIndex, meteorSize, DirectionX, DirectionY, meteorSpeed, frame);

                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            };

        }
        Thread hitCheckThread = new Thread() {
            public void run() {
                while (true) {

                    meteteorHit(meteor, meteorSpeed, meteorSize, BoomPic, frame);

                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };
        for (int i = 0; i < movementTrade.length; i++) {
            movementTrade[i].start();
        }
        hitCheckThread.start();

    }


    private void meteorMovement(JLabel[] meteor, int meteorIndex, int meteorSize, int[] DirectionX, int[] DirectionY, int[] meteorSpeed, JFrame frame) {

        int maxX = frame.getContentPane().getWidth() - meteorSize;
        int maxY = frame.getContentPane().getHeight() - meteorSize;

        int newX = meteor[meteorIndex].getX() + DirectionX[meteorIndex] * meteorSpeed[meteorIndex];
        int newY = meteor[meteorIndex].getY() + DirectionY[meteorIndex] * meteorSpeed[meteorIndex];


        if (newX > maxX || newX < 0) {
            DirectionX[meteorIndex] = -DirectionX[meteorIndex];
            meteorSpeed[meteorIndex]++;
        }
        if (newY > maxY || newY < 0) {
            DirectionY[meteorIndex] = -DirectionY[meteorIndex];
            meteorSpeed[meteorIndex]++;
        }

        meteor[meteorIndex].setLocation(newX, newY);

    }

    private void meteteorHit(JLabel[] meteor, int[] meteorSpeed, int meteorSize, ImageIcon BoomPic, JFrame frame) {

        for (int i = 0; i < meteor.length; i++) {
            for (int j = i + 1; j < meteor.length; j++) {

                if (!meteor[i].isVisible() || !meteor[j].isVisible()) continue;

                int x1 = meteor[i].getX();
                int y1 = meteor[i].getY();

                int x2 = meteor[j].getX();
                int y2 = meteor[j].getY();

                if (Math.abs(x1 - x2) < (meteorSize / 1.2) && Math.abs(y1 - y2) < (meteorSize / 1.2)) {
                    JLabel boomLabel = new JLabel(BoomPic);

                    if (meteorSpeed[i] < meteorSpeed[j]) {
                        meteor[i].setVisible(false);
                        boomLabel.setBounds(x1, y1, 50, 50);
                        meteorSpeed[j] = (meteorSpeed[j] / 2);
                        countMeteor--;
                        if (meteorSpeed[j] == 0) meteorSpeed[j] = 1;

                    } else {
                        meteor[j].setVisible(false);
                        boomLabel.setBounds(x2, y2, 50, 50);
                        meteorSpeed[i] = meteorSpeed[i] / 2;
                        countMeteor--;
                        if (meteorSpeed[i] == 0) meteorSpeed[i] = 1;
                    }
                    count.setText("Meteor : "+countMeteor);

                    frame.add(boomLabel);
                    frame.repaint();

                    Thread thread = new Thread() {
                        public void run() {
                            try {
                                Thread.sleep(200); // แสดง BOOM 0.2 วิ
                                frame.remove(boomLabel);
                                frame.repaint();
                            } catch (InterruptedException e) {

                            }
                        }
                    };
                    thread.start();
                }
            }
        }
    }
}

