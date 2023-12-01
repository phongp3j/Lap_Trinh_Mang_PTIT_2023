/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package rmserver;

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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import java.util.zip.GZIPInputStream;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class RMServer {

    
    public static ArrayList<ScheduledTask> scheduledTasks = new ArrayList<>();
    public static boolean isSharing;

    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 256;

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
                    handleFileUpload(socket);
                } else if (request.equals("ScheduleOpenApp")) {
                    handleScheduleOpenApp(reader, writer, socket);
                } else if (request.equals("Remote")) {
                    isSharing = true;
                    Thread thread = new Thread(() -> startScreenSharing(socket));
                    thread.start();
                } else if (request.equals("SendMouse")) {
                    handleMouseEvent(socket);
                } else if (request.equals("StopSharing")) {
                    isSharing = false;
                } else if (request.equals("BlackList")) {
                    checkBlackList(writer, reader, socket);
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

   

    private static void handleScheduleOpenApp(BufferedReader reader, PrintWriter writer, Socket socket) throws IOException {
        String appName = reader.readLine();
        int hour = Integer.parseInt(reader.readLine());
        int minute = Integer.parseInt(reader.readLine());

        long scheduleTime = getScheduleTime(hour, minute);

        ScheduledTask task = new ScheduledTask(appName, scheduleTime);
        scheduledTasks.add(task);

        Thread taskThread = new Thread(() -> {
            long currentTime = System.currentTimeMillis();
            long waitTime = scheduleTime - currentTime;

            try {
                if (waitTime >= 0) {
                    Thread.sleep(waitTime);
                }

                // Mở ứng dụng
                openApp(appName, writer, socket);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                scheduledTasks.remove(task);
            }
        });

        taskThread.start();

        writer.println("Đã lên lịch mở ứng dụng " + appName + " vào lúc " + hour + " giờ " + minute + " phút.");
        writer.flush();
    }

    private static long getScheduleTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long scheduleTime = calendar.getTimeInMillis();

        if (scheduleTime <= System.currentTimeMillis()) {
            scheduleTime += 24 * 60 * 60 * 1000;
        }

        return scheduleTime;
    }

    private static void openApp(String appName, PrintWriter writer, Socket socket) throws InterruptedException {
        try {
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                // Đối với Windows
                Runtime.getRuntime().exec("cmd /c start " + appName);
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                // Đối với Linux hoặc MacOS
                Runtime.getRuntime().exec("xdg-open " + appName);
            } else {
                System.out.println("Không hỗ trợ hệ điều hành này.");
                return;
            }
            Thread.sleep(2000);
            // Chụp ảnh màn hình sau khi mở ứng dụng
            BufferedImage screen = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(screen, "png", baos);

            // Chuyển ảnh thành Base64
            byte[] imageBytes = Base64.getEncoder().encode(baos.toByteArray());
            String base64Image = new String(imageBytes);

            // Gửi thông báo cho client để nó biết là cần nhận ảnh màn hình
            writer.println("RequestScreenShot");
            writer.flush();

            // Gửi dữ liệu ảnh dưới dạng Base64
            writer.println(base64Image);
            writer.flush();

        } catch (IOException | AWTException e) {
            e.printStackTrace();
        }
    }

    public static void startScreenSharing(Socket socket) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            while (isSharing) {
                BufferedImage screen = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                ImageIcon imageIcon = new ImageIcon(screen);
                outputStream.writeObject(imageIcon);
                outputStream.flush();
                System.out.println(".");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                    outputStream.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void handleMouseEvent(Socket socket) {
        System.out.println("1");
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Mouse mouse = (Mouse) ois.readObject();
            Robot robot = new Robot();
            robot.mouseMove((int) mouse.getSceneX(), (int) mouse.getSceneY());
            if (mouse.getEventType().equals("MOUSE_PRESSED")) {
                if (mouse.getButton().equals("PRIMARY")) {
                    robot.mousePress(InputEvent.getMaskForButton(MouseEvent.BUTTON1));
                } else if (mouse.getButton().equals("SECONDARY")) {
                    robot.mousePress(InputEvent.getMaskForButton(MouseEvent.BUTTON3));
                }
            } else if (mouse.getEventType().equals("MOUSE_RELEASED")) {
                if (mouse.getButton().equals("PRIMARY")) {
                    robot.mouseRelease(InputEvent.getMaskForButton(MouseEvent.BUTTON1));
                } else if (mouse.getButton().equals("SECONDARY")) {
                    robot.mouseRelease(InputEvent.getMaskForButton(MouseEvent.BUTTON3));
                }
            }
            System.out.println("Da nhan");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    //yêu cầu 1 bắt đầu từ đây
    public static List<String> targetProcesses = new ArrayList<>();
    private static String getOutputFromInputStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        return output.toString();
    }

    private static void checkBlackList(PrintWriter writer, BufferedReader reader, Socket socket) throws AWTException {
        try {
            int blacklistSize = Integer.parseInt(reader.readLine());
            for (int i = 0; i < blacklistSize; i++) {
                String processClient = reader.readLine().toLowerCase();
                if (targetProcesses.size() != 0) {
                    if (!targetProcesses.contains(processClient)) {
                        targetProcesses.add(processClient);
                    }
                } else {
                    targetProcesses.add(processClient);
                }
            }
            String list = "";
            for (String i : targetProcesses) {
                list = list + i + " ";
            }

            int check = 0;
            String res = "";
            // Kiểm tra xem các ứng dụng trong list có đang chạy không
            while (check == 0) {
                // Lệnh tùy thuộc vào hệ điều hành
                String os = System.getProperty("os.name").toLowerCase();
                String command = (os.contains("win")) ? "tasklist" : "ps aux";

                // Thực hiện lệnh hệ thống và lấy output
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                Process process = processBuilder.start();
                InputStream inputStream = process.getInputStream();
                String output = getOutputFromInputStream(inputStream);
                int check1 = 0;
                for (String targetProcess : targetProcesses) {
                    if (output.toLowerCase().contains(targetProcess)) {
                        System.out.println(targetProcess + " is running.");
                        res = res + targetProcess + ", ";
                        check1 = 1;
                    } else {
                        System.out.println(targetProcess + " is not running.");
                    }
                }
                // Đợi quá trình kết thúc
                process.waitFor();
                if(check1 == 1){
                    check = 1;
                }
            }

            res += "is running.";
            writer.println(list);
            System.out.println(list);
            writer.flush();
            writer.println(res);
            writer.flush();
            writer.println(check);
            writer.flush();
            if (check == 1) {
                Thread.sleep(2000);
                handleScreenshot(writer, socket);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
   private static void handleFileUpload(Socket socket) {
    try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Receive the file name from the client
        String fileName = reader.readLine();

        // Receive the file size from the client
        long fileSize = Long.parseLong(reader.readLine());

        // Receive the file content from the client
        byte[] fileContent = new byte[(int) fileSize];
        int bytesRead = socket.getInputStream().read(fileContent);

        if (bytesRead > 0) {
            // Save the file on the server
            String savePath = "C:\\Users\\HI\\Desktop\\HocKy1Nam4\\Laptrinhmang\\File\\" + fileName;
            Path filePath = Paths.get(savePath);
            Files.write(filePath, fileContent);

            System.out.println("File saved: " + fileName);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}
}
