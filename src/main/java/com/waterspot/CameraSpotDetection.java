package com.waterspot;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class CameraSpotDetection {
    static {
        // 加载opencv动态库
        //System.load(ClassLoader.getSystemResource("lib/opencv_java470-无用.dll").getPath());
        nu.pattern.OpenCV.loadLocally();
    }

    public static void main(String[] args) {
        // 加载 OpenCV 本地库
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // 打开摄像头（设备编号 0）
        VideoCapture capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            System.out.println("无法打开摄像头，请检查设备！");
            return;
        }

        // 设置摄像头参数
        capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
        capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);

        Mat frame = new Mat(); // 保存摄像头帧数据
        while (capture.read(frame)) {
            if (frame.empty()) {
                System.out.println("无法从摄像头读取数据！");
                break;
            }

            // 检测光斑
            Mat processedFrame = detectSpots(frame);

            // 显示处理后的画面
            HighGui.imshow("Spot Detection", processedFrame);

            // 按键退出（等待 1 毫秒检测按键）
            if (HighGui.waitKey(1) != -1) {
                break;
            }
        }

        // 释放资源
        capture.release();
        HighGui.destroyAllWindows();
    }

    /**
     * 检测光斑区域并在图像上标注
     * @param frame 原始帧
     * @return 带标注的帧
     */
    public static Mat detectSpots(Mat frame) {
        // 转为灰度图
        Mat gray = new Mat();
        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);

        // 应用阈值检测高亮区域
        Mat binary = new Mat();
        Imgproc.threshold(gray, binary, 200, 255, Imgproc.THRESH_BINARY);

        // 查找轮廓
        java.util.List<MatOfPoint> contours = new java.util.ArrayList<>();
        Imgproc.findContours(binary, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // 在原图上绘制检测到的光斑区域
        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            Imgproc.rectangle(frame, rect, new Scalar(0, 255, 0), 2); // 绘制绿色矩形框
        }

        return frame;
    }
}
