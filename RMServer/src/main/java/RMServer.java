/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */


import com.share.Mouse;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import java.util.Base64;
import javax.swing.ImageIcon;

public class RMServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Server dang khoi chay...");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client dang ket noi: " + socket.getInetAddress().getHostAddress());
                Thread thread = new Thread(
                        () -> clientRequest(socket)
                );
                thread.start();
            }
        } catch (Exception e) {
        }
    }

    public static void clientRequest(Socket socket) {
        try {
            while (true) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                String request = reader.readLine();

                if (request.equals("Shutdown")) {
                    handleShutdown(reader, writer);
                } else if (request.equals("Restart")) {
                    handleRestart(reader, writer);
                } else if (request.equals("Cancel")) {
                    handleCancel(writer);
                } else if (request.equals("ScreenShot")) {
                    handleScreenshot(writer, socket);
                } else if (request.equals("UploadFile")) {
                    handleUploadFile(reader, writer, socket);
                } else if (request.equals("Remote")){
                    Thread thread = new Thread(()-> startScreenSharing(socket));
                    thread.start();
                } else if(request.equals("SendMouse")){
                    handleMouseEvent(socket);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleShutdown(BufferedReader reader, PrintWriter writer) throws IOException {
        int time = Integer.parseInt(reader.readLine());
        Runtime.getRuntime().exec("shutdown -s -t " + time);
        writer.println("May tinh se tat sau " + time + "s");
        writer.flush();
    }

    private static void handleRestart(BufferedReader reader, PrintWriter writer) throws IOException {
        int time = Integer.parseInt(reader.readLine());
        Runtime.getRuntime().exec("shutdown -r -t " + time);
        writer.println("May tinh se khoi dong lai sau " + time + "s");
        writer.flush();
    }

    private static void handleCancel(PrintWriter writer) throws IOException {
        Runtime.getRuntime().exec("shutdown -a");
        writer.println("Da huy hen gio");
        writer.flush();
    }

    private static void handleScreenshot(PrintWriter writer, Socket socket) throws IOException, AWTException {
        BufferedImage screen = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(screen, "png", baos);
        byte[] imageBytes = baos.toByteArray();
        baos.close();
        writer.println(imageBytes.length);
        writer.flush();
        socket.getOutputStream().write(imageBytes);
        socket.getOutputStream().flush();
        writer.println("Da chup man hinh.....");
        writer.flush();
    }

    private static void handleUploadFile(BufferedReader reader, PrintWriter writer, Socket socket) throws IOException {
        try {
            String fileName = reader.readLine();
            long fileSize = Long.parseLong(reader.readLine());

            String savePath = "C:\\Users\\HI\\Desktop\\HocKy1Nam4\\Laptrinhmang\\File\\" + fileName;
            FileOutputStream fileOutputStream = new FileOutputStream(savePath);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            byte[] buffer = new byte[8192];
            int bytesRead;

            InputStream inputStream = socket.getInputStream();
            while (fileSize > 0 && (bytesRead = inputStream.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
                bufferedOutputStream.write(buffer, 0, bytesRead);
                fileSize -= bytesRead;
            }

            bufferedOutputStream.close();
            fileOutputStream.close();

            writer.println("File uploaded thanh cong!");
            writer.flush();
        } catch (IOException e) {
            System.out.println("Error receiving file: " + e.getMessage());
        }
    }
    public static void  startScreenSharing(Socket socket) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            while (true) {
                BufferedImage screen = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                ImageIcon imageIcon = new ImageIcon(screen);
                outputStream.writeObject(imageIcon);
                outputStream.flush();
                try{
                    Thread.sleep(100);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
//                    outputStream.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void handleMouseEvent(Socket socket){
        System.out.println("1");
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Mouse mouse = (Mouse) ois.readObject();
            Robot robot = new Robot();
            robot.mouseMove((int)mouse.getSceneX(), (int)mouse.getSceneY());
            if(mouse.getEventType().equals("MOUSE_PRESSED")){
                if(mouse.getButton().equals("PRIMARY")){
                    robot.mousePress(InputEvent.getMaskForButton(MouseEvent.BUTTON1));
                } else if(mouse.getButton().equals("SECONDARY")){
                    robot.mousePress(InputEvent.getMaskForButton(MouseEvent.BUTTON3));
                }
            } else if(mouse.getEventType().equals("MOUSE_RELEASED")){
                if(mouse.getButton().equals("PRIMARY")){
                    robot.mouseRelease(InputEvent.getMaskForButton(MouseEvent.BUTTON1));
                } else if(mouse.getButton().equals("SECONDARY")){
                    robot.mouseRelease(InputEvent.getMaskForButton(MouseEvent.BUTTON3));
                }
            }
            System.out.println("Da nhan");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
