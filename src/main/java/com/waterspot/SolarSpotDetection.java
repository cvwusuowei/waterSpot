package com.waterspot;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

public class SolarSpotDetection {
    static {
        // 加载opencv动态库
        //System.load(ClassLoader.getSystemResource("lib/opencv_java470-无用.dll").getPath());
        nu.pattern.OpenCV.loadLocally();
    }
    public static void main(String[] args) {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // 加载 OpenCV 库

        // 1. 图片光斑检测
        String inputImagePath = "spot/4b102b69eb0ef977cc507585ae0c612.png";
        String outputImagePath = "spot/image_with_spots.png";
        detectSpotsInImage(inputImagePath, outputImagePath);

        // 2. 视频光斑检测
        String inputVideoPath = "spot/cc7f8f2c96dad9aac9933e3878b87289.mp4";
        String outputVideoPath = "spot/video_with_spots.mp4";
        detectSpotsInVideo(inputVideoPath, outputVideoPath);
    }

    public static void detectSpotsInImage(String inputPath, String outputPath) {
        // 读取图像
        Mat image = Imgcodecs.imread(inputPath);
        if (image.empty()) {
            System.out.println("无法加载图片: " + inputPath);
            return;
        }

        // 转为灰度图
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // 应用阈值检测高亮区域
        Mat binary = new Mat();
        Imgproc.threshold(gray, binary, 200, 255, Imgproc.THRESH_BINARY);

        // 查找轮廓
        java.util.List<MatOfPoint> contours = new java.util.ArrayList<>();
        Imgproc.findContours(binary, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // 在原图上绘制检测到的光斑区域
        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            Imgproc.rectangle(image, rect, new Scalar(0, 255, 0), 2); // 绘制绿色矩形框
        }

        // 保存结果图像
        Imgcodecs.imwrite(outputPath, image);
        System.out.println("光斑检测完成，结果保存至: " + outputPath);
    }

    public static void detectSpotsInVideo(String inputPath, String outputPath) {
        // 打开视频文件
        VideoCapture capture = new VideoCapture(inputPath);
        if (!capture.isOpened()) {
            System.out.println("无法打开视频: " + inputPath);
            return;
        }

        // 获取视频参数
        int frameWidth = (int) capture.get(Videoio.CAP_PROP_FRAME_WIDTH);
        int frameHeight = (int) capture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
        int fps = (int) capture.get(Videoio.CAP_PROP_FPS);

        // 初始化视频写入
        VideoWriter writer = new VideoWriter(
                outputPath, VideoWriter.fourcc('m', 'p', '4', 'v'), fps,
                new Size(frameWidth, frameHeight)
        );

        Mat frame = new Mat();
        while (capture.read(frame)) {
            // 转为灰度图
            Mat gray = new Mat();
            Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);

            // 应用阈值检测高亮区域
            Mat binary = new Mat();
            Imgproc.threshold(gray, binary, 200, 255, Imgproc.THRESH_BINARY);

            // 查找轮廓
            java.util.List<MatOfPoint> contours = new java.util.ArrayList<>();
            Imgproc.findContours(binary, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            // 在帧上绘制检测到的光斑区域
            for (MatOfPoint contour : contours) {
                Rect rect = Imgproc.boundingRect(contour);
                Imgproc.rectangle(frame, rect, new Scalar(0, 255, 0), 2);
            }

            // 写入处理后的帧
            writer.write(frame);
        }

        // 释放资源
        capture.release();
        writer.release();
        System.out.println("光斑检测完成，视频保存至: " + outputPath);
    }
}
