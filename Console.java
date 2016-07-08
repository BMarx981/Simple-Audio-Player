import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import javax.sound.sampled.*;

public class Console {
  
  public static final int WIDTH = 400;
  public static final int HEIGHT = 200;
  private JFrame frame;
  private JPanel main;
  private JPanel aPanel;
  private JPanel bPanel;
  private JSlider slider;
  private JButton button1;
  private JButton button2;
  private JButton button3;
  private JTextField tField;
  private JLabel label;
  private File f;
  private JFileChooser fc;
  public static String fileName;
  private File soundFile;
  SoundPlay t = new SoundPlay();
  
  public Console() {
    
    frame = new JFrame();
    main = new JPanel();
    button1 = new JButton("Browse...");
    button1.setToolTipText("Select a SoundFile");
    tField = new JTextField(20);
    
    fc = new JFileChooser();
    button1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        int returnValue = fc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
          File selectedFile = fc.getSelectedFile();
          fileName = fc.getSelectedFile().getAbsolutePath();
          tField.setText(fileName);
        }
      }//end actionPerformed
    });//end actionListener
    
    button2 = new JButton("Play");
    
    button2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evnt) {
        
        t.start();
      }//end actionPerformed
    });//end actionlistener
    
    button3 = new JButton("Stop");
    button3.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent e) {
        SoundPlay.pleaseStop();
        t.yield();
      }
    });
    
    //Places the buttons and text field on a panel
    aPanel = new JPanel();
    aPanel.setLayout(new BoxLayout(aPanel, BoxLayout.Y_AXIS));
    aPanel.add(button1);
    aPanel.add(button2);
    aPanel.add(tField);
    aPanel.add(button3);
    
    main.add(aPanel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    //Places the main Panel in the frame
    frame.add(main);
    frame.pack();
    frame.setSize(WIDTH,HEIGHT);
    frame.setVisible(true);
  }//end Console constructor
  
  public static void main(String[] args) {
    Console console = new Console();
  }//end main 
  
  public static String getFileName() {
    return fileName;
  }//end getFileName
}//end class Console

//************SoundPlay Thread Class*******************
class SoundPlay extends Thread {
  
  public static SourceDataLine soundLine = null;
  
  public void run() {
    File soundFile = new File(Console.getFileName());
    int BUFFER_SIZE = 64*1024;
    try {
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
      AudioFormat aFormat = audioInputStream.getFormat();
      DataLine.Info info = new DataLine.Info(SourceDataLine.class, aFormat);
      soundLine = (SourceDataLine) AudioSystem.getLine(info);
      soundLine.open(aFormat);
      soundLine.start();
      
      int nBytesRead = 0;
      byte[] sampledData = new byte[BUFFER_SIZE];
      while(nBytesRead != -1) 
      {
        nBytesRead = audioInputStream.read(sampledData, 0, sampledData.length);
        if(nBytesRead >= 0) {
          soundLine.write(sampledData, 0, nBytesRead);
        }
      }
    }//end try audio input stream
    catch (UnsupportedAudioFileException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (LineUnavailableException ex) {
      ex.printStackTrace();
    } finally {
      soundLine.stop();
      soundLine.drain();
      soundLine.close();
    }
  }//end run
  
  public static void pleaseStop() {
    soundLine.stop();
  }
}//SoundPlay class
