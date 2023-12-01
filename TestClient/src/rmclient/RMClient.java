/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package rmclient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Scanner;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;


/**
 *
 * @author HI
 */
public class RMClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            Socket socket = new Socket("localhost", 5000);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            Scanner sc = new Scanner(System.in);
            while (true) {

                System.out.println("Chon chuc nang:");
                System.out.println("1. Hen thoi gian tat may");
                System.out.println("2. Hen thoi gian khoi dong lai may");
                System.out.println("3. Huy tat/khoi dong may");
                System.out.println("4. Chup anh man hinh");
                System.out.println("5. Dat lich tac vu va chup anh man hinh tu dong");
                System.out.println("6. Upload file len server");
                System.out.println("7. BlackList");
                int choice = sc.nextInt();
                sc.nextLine();
                switch (choice) {
                    case 1:
                        writer.println("Shutdown");
                        writer.flush();
                        System.out.println("Nhap thoi gian tat may (s): ");
                        int time = sc.nextInt();
                        sc.nextLine();
                        writer.println(time);
                        writer.flush();
                        String tmp = reader.readLine();
                        System.out.println(tmp);
                        break;
                    case 2:
                        writer.println("Restart");
                        writer.flush();
                        System.out.println("Nhap thoi gian khoi dong lai may (s): ");
                        int time1 = sc.nextInt();
                        sc.nextLine();
                        writer.println(time1);
                        writer.flush();
                        String tmp1 = reader.readLine();
                        System.out.println(tmp1);
                        break;
                    case 3:
                        writer.println("Cancel");
                        writer.flush();
                        String tmp2 = reader.readLine();
                        System.out.println(tmp2);
                        break;
                    case 4:
                        writer.println("ScreenShot");
                        writer.flush();
                        int imageSize = Integer.parseInt(reader.readLine());
                        byte[] imageByte = new byte[imageSize];
                        int byteRead = socket.getInputStream().read(imageByte);
                        if (byteRead > 0) {
                            System.out.println("Nhap ten anh: ");
                            String name = sc.nextLine();
                            String path = "C:\\Users\\HI\\Desktop\\HocKy1Nam4\\Laptrinhmang\\Anh";
                            Path imagePath = Paths.get(path + "\\" + name + ".png");
                            Files.write(imagePath, imageByte);
                            System.out.println("Da luu anh!");
                        }
                        break;
                    case 5:
                        writer.println("ScheduleOpenApp");
                        writer.flush();
                        System.out.println("Nhap ten ứng dụng muốn mở: ");
                        String appName = sc.nextLine();
                        System.out.println("Nhap giờ: ");
                        int hour = sc.nextInt();
                        System.out.println("Nhap phút: ");
                        int minute = sc.nextInt();

                        writer.println(appName);
                        writer.println(hour);
                        writer.println(minute);
                        writer.flush();

                        String response = reader.readLine();
                        System.out.println(response);

                        // Nhận ảnh
                        String request = reader.readLine();
                        if (request.equals("RequestScreenShot")) {
                            String base64Image = reader.readLine();
                            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

                            // Tạo tên file
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                            String formattedDate = dateFormat.format(new Date());
                            String filename = appName + "_" + formattedDate + ".png";

                            // Lưu ảnh
                            String savePath = "C:\\Users\\HI\\Desktop\\HocKy1Nam4\\Laptrinhmang\\Anh\\" + filename;
                            Path imagePath = Paths.get(savePath);
                            Files.write(imagePath, imageBytes);
                            System.out.println("Screenshot lưu thành công với tên " + filename);
                        }
                        break;
                    case 6:
                        writer.println("UploadFile");
                        writer.flush();

                        // Read the file path from the user
                        System.out.println("Nhap duong dan file: ");
                        String filePath = sc.nextLine();

                        String zipClient = "C:\\Users\\HI\\Desktop\\HocKy1Nam4\\ZipClient\\test.zip";
//                        zipFile(filePath, zipClient);

                        System.out.println("Nhập mật khẩu:");
                        String password = sc.nextLine();
                        try {
                            zipFile(filePath, zipClient, password);
                            System.out.println("File compressed successfully!");
                        } catch (ZipException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        filePath = zipClient;
                        String fileName = Paths.get(filePath).getFileName().toString();

                        // Send the file name to the server
                        writer.println(fileName);
                        writer.flush();

                        // Send the file size to the server
                        long fileSize = Files.size(Paths.get(filePath));
                        writer.println(fileSize);
                        writer.flush();

                        // Send the file content to the server
                        try (FileInputStream fis = new FileInputStream(filePath); BufferedInputStream bis = new BufferedInputStream(fis)) {
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = bis.read(buffer)) != -1) {
                                socket.getOutputStream().write(buffer, 0, bytesRead);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        System.out.println("File uploaded successfully!");
                        break;

                    case 7:
                        writer.println("BlackList");
                        writer.flush();
                        System.out.println("Nhập số các ứng dụng trong danh sách: ");
                        int numProcesses = sc.nextInt();
                        sc.nextLine();
                        writer.println(numProcesses);
                        writer.flush();
                        for (int i = 0; i < numProcesses; i++) {
                            System.out.println("Nhập tên ứng dụng: ");
                            String processName = sc.nextLine();
                            writer.println(processName);
                            writer.flush();
                        }
//                        writer.flush();
                        System.out.println("Các ứng dụng đang có trong blacklist: " + reader.readLine());
                        System.out.println(reader.readLine());
                        int check = Integer.parseInt(reader.readLine());
                        if (check == 1) {
                            int imageSize1 = Integer.parseInt(reader.readLine());
                            byte[] imageByte1 = new byte[imageSize1];
                            int byteRead1 = socket.getInputStream().read(imageByte1);
                            if (byteRead1 > 0) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                                String formattedDate = dateFormat.format(new Date());
                                String filename = formattedDate + ".png";
                                String path = "C:\\Users\\HI\\Desktop\\HocKy1Nam4\\Laptrinhmang\\Anh";
                                Path imagePath = Paths.get(path + "\\" + filename);
                                Files.write(imagePath, imageByte1);
                                System.out.println("Received and saved image: " + filename);
                            }

                        }
                        break;

                    default:
                        throw new AssertionError();
                }
            }
        } catch (Exception e) {
        }
    }

//    public static void zipFile(String sourceFilePath, String zipFilePath) {
//        try {
//            File sourceFile = new File(sourceFilePath);
//            File zipFile = new File(zipFilePath);
//
//            // Tạo thư mục cha nếu nó chưa tồn tại
//            zipFile.getParentFile().mkdirs();
//
//            FileOutputStream fos = new FileOutputStream(zipFile);
//            ZipOutputStream zipOut = new ZipOutputStream(fos);
//            FileInputStream fis = new FileInputStream(sourceFile);
//
//            ZipEntry zipEntry = new ZipEntry(sourceFile.getName());
//            zipOut.putNextEntry(zipEntry);
//
//            byte[] bytes = new byte[1024];
//            int length;
//            while ((length = fis.read(bytes)) >= 0) {
//                zipOut.write(bytes, 0, length);
//            }
//
//            zipOut.close();
//            fis.close();
//            fos.close();
//
//            System.out.println("File compressed successfully!");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    
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
