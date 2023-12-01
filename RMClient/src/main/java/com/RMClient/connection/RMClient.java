package com.RMClient.connection;

import com.share.Mouse;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

public class RMClient {
    private static Socket socket;
    private static String serverIp;
    private static int serverPort;
    private static PrintWriter printWriter;
    private static BufferedReader reader;
    private static InputStream inputStream;
    private static ObjectInputStream objectInputStream;

    public static boolean connect(String ip, int port){
        serverIp = ip;
        serverPort = port;
        try {
            socket = new Socket(serverIp, serverPort);
            printWriter = new PrintWriter(socket.getOutputStream(),true);
            inputStream = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            return true;
        } catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean shutdownRequest(int timeInSeconds){
        System.out.println(socket);
        try {
            if (socket != null && !socket.isClosed()) {
                // Gửi yêu cầu Shutdown lên máy chủ
                printWriter.println("Shutdown");

                // Gửi thời gian cần tắt máy
                printWriter.println(String.valueOf(timeInSeconds));
                return true;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean rebootRequest(int timeInSeconds){
        System.out.println(socket);
        try {
            if (socket != null && !socket.isClosed()) {
                // Gửi yêu cầu Restart lên máy chủ
                printWriter.println("Restart");

                // Gửi thời gian cần khởi động lại
                printWriter.println(String.valueOf(timeInSeconds));
                return true;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean cancelSchedule(){
        try {
            if (socket != null && !socket.isClosed()) {
                printWriter.println("Cancel");
                return true;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public static BufferedImage captureScreenShot(){
        try {
            if (socket != null && !socket.isClosed()) {
                printWriter.println("ScreenShot");
                reader.readLine();
                BufferedImage image = ImageIO.read(inputStream);
                reader.readLine();
                return image;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static String receiveResponse(){
        try{
            if(socket != null && !socket.isClosed()) {
                socket.setSoTimeout(10000);
                StringBuilder response = new StringBuilder();
                String line= reader.readLine();
                response.append(line).append("\n");
                return response.toString();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
    public static boolean remote(){
        try {
            printWriter.println("Remote");
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public static void startScreenSharing(ImageView imageView){
        Thread thread = new Thread(() -> {
            try {
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                while (true){
                    ImageIcon icon = (ImageIcon) objectInputStream.readObject();
                    if(icon != null){
                        Image image = icon.getImage();
                        BufferedImage bufferedImage = new BufferedImage(
                                image.getWidth(null),
                                image.getHeight(null),
                                BufferedImage.TYPE_INT_ARGB);
                        bufferedImage.getGraphics().drawImage(image, 0, 0, null);

                        javafx.scene.image.Image image1 = SwingFXUtils.toFXImage(bufferedImage,null);
                        imageView.setImage(image1);
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public static void sendKey (KeyEvent event){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(event);
            oos.flush();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendMouse (MouseEvent event){
        try {
            printWriter.println("SendMouse");
            Mouse mouseEvent = new Mouse(event.getSceneX(),event.getSceneY(),event.getEventType().toString(),event.getButton().toString());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(mouseEvent);
            oos.flush();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public static boolean upload(String path){
        printWriter.println("UploadFile");
        String fileName = new File(path).getName();
        printWriter.println(fileName);
        try {
            // Đọc dữ liệu từ file
            byte[] fileData = readFile(path);

            // Nén dữ liệu
            byte[] compressedData = compressData(fileData);

            // Gửi dữ liệu đến server
            try (OutputStream outputStream = socket.getOutputStream();
                 GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(gzipOutputStream)) {

                objectOutputStream.writeObject(compressedData);
                objectOutputStream.flush();
                System.out.println("File sent successfully.");
                return true;
            } catch (Exception e){
                e.printStackTrace();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    private static byte[] readFile(String filePath) throws IOException {
        try (InputStream inputStream = new FileInputStream(filePath)) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        }
    }
    private static byte[] compressData(byte[] data) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream)) {
            gzipOutputStream.write(data);
            gzipOutputStream.finish();
            return outputStream.toByteArray();
        }
    }
    public static void scheduleOpenApp(String app, Integer hour, Integer minute) {
        try {
            printWriter.println("ScheduleOpenApp");
            printWriter.println(app);
            printWriter.println(hour);
            printWriter.println(minute);

            String response = reader.readLine();
            System.out.println(response);

            if (response.equals("RequestScreenShot")) {
                String base64Image = reader.readLine();
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);

                if (imageBytes.length > 0) {
                    // Tạo tên ảnh dựa trên thời gian
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                    String formattedDate = sdf.format(new Date());
                    String imageName = "screenshot_" + formattedDate;

                    // Lưu ảnh vào thư mục ảnh trên máy client
                    String clientImagePath = "C:\\Users\\admin\\Downloads";
                    Path imagePath = Paths.get(clientImagePath + "\\" + imageName + ".png");
                    Files.write(imagePath, imageBytes);
                    System.out.println("Đã lưu ảnh màn hình vào " + imagePath);
                }
            }
        } catch (Exception e){

        }
    }


    public static void stopSharing() {
        printWriter.println("StopSharing");
    }

    public static void zipFile(String sourceFilePath, String zipFilePath, String password) throws ZipException, IOException {
        try {
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setCompressionMethod(CompressionMethod.DEFLATE); // Phương pháp nén
            zipParameters.setEncryptFiles(true);  // Bật mã hóa cho tệp
            zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);  // Phương pháp mã hóa

            ZipFile zipFile = new ZipFile(zipFilePath);
            zipFile.setPassword(password.toCharArray());

            // Thêm file vào tệp zip
            zipFile.addFile(new File(sourceFilePath), zipParameters);
        } catch (ZipException e) {
            throw e;  // Rethrow ZipException separately if needed
        } catch (IOException e) {
            throw e;  // Rethrow IOException separately if needed
        }
    }
}
