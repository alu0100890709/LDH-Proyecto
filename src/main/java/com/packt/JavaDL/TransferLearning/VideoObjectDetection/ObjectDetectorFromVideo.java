package com.packt.JavaDL.TransferLearning.VideoObjectDetection;

import static org.bytedeco.javacpp.opencv_highgui.destroyAllWindows;
import static org.bytedeco.javacpp.opencv_highgui.imshow;
import static org.bytedeco.javacpp.opencv_highgui.waitKey;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
/**
 * LDH Proyecto final
 * ObjectDetectorFromVideo.java
 * Purpose: Clase que detecta objetos en videos dado un modelo entrenado
 *
 * @author Grupo Practica Yolo
 * @version 1.0.0 22/11/2018
 */
public class ObjectDetectorFromVideo extends JFrame implements ActionListener {
    private volatile Mat[] v = new Mat[1];
    JPanel panelContenido;
	JButton documentSelector;
	String path;
	private final JFileChooser fc = new JFileChooser();
	public static final Logger logger = Logger.getLogger("log");
    private String windowName;

	/** Metodo main de la clase */
    public static void main( String[] args ) throws Exception
	{
		new ObjectDetectorFromVideo().createJFrame();
	}
    
    
    public void createJFrame() throws java.lang.Exception {
        
        
        panelContenido = new JPanel();
		GroupLayout layout = new GroupLayout(panelContenido);
		panelContenido.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		documentSelector = new JButton("Seleccione video a analizar");
			
			
		documentSelector.addActionListener(this);
			
			
			
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(documentSelector));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(documentSelector)));
		this.setContentPane(panelContenido);
		this.setTitle("Detector de objetos en videos");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocation(0, 0);
		pack();
		setMaximumSize(Toolkit.getDefaultToolkit().getScreenSize());
		this.setResizable(true);
		this.setVisible(true);
        
		
        
    }
    
    /**
     * @brief Lanza la aplicación que detecta objetos en videos
     * @param videoFileName dirección del video en el que detectar cosas
     * @param model modelo entrenado
     * @throws java.lang.Exception
     */
    public void startRealTimeVideoDetection(String videoFileName, TinyYoloModel model) throws java.lang.Exception {
        windowName = "Object Detection from Video";
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(videoFileName);
        frameGrabber.start();

        Frame frame;
        double frameRate = frameGrabber.getFrameRate();
        System.out.println("The inputted video clip has " + frameGrabber.getLengthInFrames() + " frames");
        System.out.println("The inputted video clip has frame rate of " + frameRate);

        try {
            for(int i = 1; i < frameGrabber.getLengthInFrames(); i+=(int)frameRate) {
                frameGrabber.setFrameNumber(i);
                frame = frameGrabber.grab();
                v[0] = new OpenCVFrameConverter.ToMat().convert(frame);
                model.markObjectWithBoundingBox(v[0], frame.imageWidth, frame.imageHeight, true, windowName);
                imshow(windowName, v[0]);

                char key = (char) waitKey(20);
                // Exit on escape:
                if (key == 27) {
                    destroyAllWindows();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            frameGrabber.stop();
        }
        frameGrabber.close();
    }
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		TinyYoloModel model = TinyYoloModel.getPretrainedModel();
		int returnVal = fc.showOpenDialog(panelContenido);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			path = file.getAbsolutePath();
			System.out.println(TinyYoloModel.getSummary());
	        try {
				new ObjectDetectorFromVideo().startRealTimeVideoDetection(path, model);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				ObjectDetectorFromVideo.logger.log(Level.WARNING, "No se pudo abrir el documento", e1);
			}
		} else {
			System.out.println("Open command cancelled by user.");
		}
		
	}
}
