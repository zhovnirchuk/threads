import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;

import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;


public class MainWindow {

	private JFrame frmThreads;
	private JTextField textFieldOriginalPath;
	private JTextField textFieldResultPath;
	
	private File originalFile;
	private String resultFile;

	private ThreadsProcessor threadsProcessor;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmThreads.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmThreads = new JFrame();
		frmThreads.setTitle("Threads");
		frmThreads.setResizable(false);
		frmThreads.setBounds(100, 100, 850, 600);
		frmThreads.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmThreads.getContentPane().setLayout(null);
		
		JLabel lblOriginal = new JLabel("Original:");
		lblOriginal.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblOriginal.setBounds(10, 10, 49, 20);
		frmThreads.getContentPane().add(lblOriginal);
		
		JLabel lblResult = new JLabel("Result:");
		lblResult.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblResult.setBounds(435, 10, 40, 20);
		frmThreads.getContentPane().add(lblResult);
		
		final ImagePanel imagePanelOriginal = new ImagePanel();
		imagePanelOriginal.setBackground(Color.WHITE);
		imagePanelOriginal.setBounds(10, 41, 400, 400);
		frmThreads.getContentPane().add(imagePanelOriginal);
		
		final ImagePanel imagePanelResult = new ImagePanel();
		imagePanelResult.setBackground(Color.WHITE);
		imagePanelResult.setBounds(435, 41, 400, 400);
		frmThreads.getContentPane().add(imagePanelResult);
		
		JLabel lblOriginalPath = new JLabel("Original path:");
		lblOriginalPath.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblOriginalPath.setBounds(10, 459, 78, 20);
		frmThreads.getContentPane().add(lblOriginalPath);
		
		JLabel lblResultPath = new JLabel("Result path:");
		lblResultPath.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblResultPath.setBounds(10, 500, 78, 20);
		frmThreads.getContentPane().add(lblResultPath);
		
		JButton btnRun = new JButton("Run");
		btnRun.setBounds(745, 530, 90, 30);
		frmThreads.getContentPane().add(btnRun);
		btnRun.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	Stopwatch timer = new Stopwatch();
                threadsProcessor = new ThreadsProcessor(originalFile, resultFile);
            	System.out.println(timer.elapsedTime());
            	imagePanelOriginal.setImage(threadsProcessor.originalToPanel());
            	try {
					imagePanelResult.setImage(threadsProcessor.resultToPanel());
					new File("resCopy.jpg").delete();
				} catch (IOException e1) {e1.printStackTrace();}
            	
        		frmThreads.repaint();
            }
        });
		
		textFieldOriginalPath = new JTextField();
		textFieldOriginalPath.setBackground(Color.WHITE);
		textFieldOriginalPath.setEditable(false);
		textFieldOriginalPath.setBounds(98, 455, 500, 30);
		frmThreads.getContentPane().add(textFieldOriginalPath);
		textFieldOriginalPath.setColumns(10);
		
		textFieldResultPath = new JTextField();
		textFieldResultPath.setBackground(Color.WHITE);
		textFieldResultPath.setEditable(false);
		textFieldResultPath.setBounds(98, 496, 500, 30);
		frmThreads.getContentPane().add(textFieldResultPath);
		textFieldResultPath.setColumns(10);
		
		JButton btnFindOriginal = new JButton("Find...");
		btnFindOriginal.setBounds(608, 455, 100, 30);
		frmThreads.getContentPane().add(btnFindOriginal);
		btnFindOriginal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileopen = new JFileChooser();             
                int ret = fileopen.showDialog(null, "Open file");                
                if (ret == JFileChooser.APPROVE_OPTION) {
                    originalFile = fileopen.getSelectedFile();
                    textFieldOriginalPath.setText(originalFile.getAbsolutePath());
                }
            }
        });
		
		JButton btnFindResult = new JButton("Find...");
		btnFindResult.setBounds(608, 496, 100, 30);
		frmThreads.getContentPane().add(btnFindResult);
		btnFindResult.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileopen = new JFileChooser();             
                int ret = fileopen.showDialog(null, "Open file");                
                if (ret == JFileChooser.APPROVE_OPTION) {
                	resultFile = fileopen.getSelectedFile().getAbsolutePath();
                    textFieldResultPath.setText(resultFile);
                }
            }
        });
	}
}
