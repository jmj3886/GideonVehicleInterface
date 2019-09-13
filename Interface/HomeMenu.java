/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;


/**
 *
 * @author Joshua
 */
public class HomeMenu extends javax.swing.JFrame {

    /**
     * Creates new form HomeMenu
     */
    public HomeMenu() { 
       this.setUndecorated(true);
       initComponents();
       topFrame = this;
       Exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                topFrame.dispose();
                System.exit(0);
            }
       });
       SetupLights();
       comPort = SerialPort.getCommPort("/dev/rfcomm0");
       comPort.openPort();
       comPort.addDataListener(new SerialPortPacketListener() {
               @Override
               public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }

               @Override
               public int getPacketSize() { return 4; }

               @Override
               public void serialEvent(SerialPortEvent event)
               {
                  byte[] newData = event.getReceivedData();
                  System.out.println("Received data of size: " + newData.length);
                  char[] command = new char[newData.length];
                  for (int i = 0; i < newData.length; ++i)
                     command[i] = (char)newData[i];
                  PerformCommand(command);
               }
       });
       NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "/usr/lib/arm-linux-gnueabihf");
       Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
       MediaPlayerFactory mpf = new MediaPlayerFactory();
       emp = mpf.newEmbeddedMediaPlayer();
       emp.setVideoSurface(mpf.newVideoSurface(canvas1));
       emp.toggleFullScreen();
       emp.setEnableMouseInputHandling(false);
       emp.setEnableKeyInputHandling(false);
       movieUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RotateMovies(true);
            }
       });
       movieDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RotateMovies(false);
            }
       });
       movieButtons = new JButton[4];
       movieButtons[0] = movie1;
       movieButtons[1] = movie2;
       movieButtons[2] = movie3;
       movieButtons[3] = movie4;       
       movies = GetMovies("/media/pi/Movies");  
       InitMovies();
       movieButtons[0].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(movies[0].isFile()){
                    emp.prepareMedia(movies[0].getAbsolutePath());
                    emp.play();
                    startMovie();
                }else{
                    File currentDir = movies[0].getParentFile();
                    movies = GetMovies(movies[0].getAbsolutePath());
                    File[] tempMovies = new File[movies.length+1];
                    for(int i = 0; i < movies.length; i++){
                        tempMovies[i] = movies[i];
                    }
                    tempMovies[movies.length] = currentDir;
                    movies = tempMovies;                     
                    InitMovies();
                }
            }
       });
       movieButtons[1].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(movies[1].isFile()){
                    emp.prepareMedia(movies[1].getAbsolutePath());
                    startMovie();
                }else{
                    File currentDir = movies[1].getParentFile();
                    movies = GetMovies(movies[1].getAbsolutePath());
                    File[] tempMovies = new File[movies.length+1];
                    for(int i = 0; i < movies.length; i++){
                        tempMovies[i] = movies[i];
                    }
                    tempMovies[movies.length] = currentDir;
                    movies = tempMovies;                    
                    InitMovies();
                }
            }
       });
       movieButtons[2].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(movies[2].isFile()){
                    emp.prepareMedia(movies[2].getAbsolutePath());
                    startMovie();
                }else{
                    File currentDir = movies[2].getParentFile();
                    movies = GetMovies(movies[2].getAbsolutePath());
                    File[] tempMovies = new File[movies.length+1];
                    for(int i = 0; i < movies.length; i++){
                        tempMovies[i] = movies[i];
                    }
                    tempMovies[movies.length] = currentDir;
                    movies = tempMovies;                    
                    InitMovies();
                }
            }
       });
       movieButtons[3].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(movies[3].isFile()){
                    emp.prepareMedia(movies[3].getAbsolutePath());
                    startMovie();
                }else{
                    File currentDir = movies[3].getParentFile();
                    movies = GetMovies(movies[3].getAbsolutePath());
                    File[] tempMovies = new File[movies.length+1];
                    for(int i = 0; i < movies.length; i++){
                        tempMovies[i] = movies[i];
                    }
                    tempMovies[movies.length] = currentDir;
                    movies = tempMovies;                    
                    InitMovies();
                }
            }
       });
       playPauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(emp.isPlaying()){
                    emp.pause();
                    playPauseButton.setIcon(play);
                }else{
                    playPauseButton.setIcon(pause);
                    emp.play();
                }                
            }
       });
       rewind.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                long rewindTime = (emp.getTime()-(long)(emp.getLength()*0.01));
                if(rewindTime >= 0){
                    emp.setTime(rewindTime);
                }
            }
       });
       fastForward.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                long forward = (emp.getTime()+(long)(emp.getLength()*0.01));
                if(forward < emp.getLength()){
                    emp.setTime(forward);
                }
            }
       });
       jSlider1.addChangeListener(new ChangeListener() {
           public void stateChanged(ChangeEvent e) {
               if(jSlider1.getValueIsAdjusting()){
                    write = true;                    
               }else if(write){
                   System.out.println(jSlider1.getValue()/100.0);
                   emp.setPosition((float)(jSlider1.getValue()/100.0));
                   write = false;
               }
           }
       });
    }

    private void SetupLights(){
        blue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                comPort.writeBytes(blueString, blueString.length);
                UpdateColor(blueString);
            }
        });
        green.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                comPort.writeBytes(greenString, greenString.length);
                UpdateColor(greenString);
            }
        });
        red.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                comPort.writeBytes(redString, redString.length);
                UpdateColor(redString);
            }
        });
        pink.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                comPort.writeBytes(pinkString, pinkString.length);
                UpdateColor(pinkString);
            }
        });
        orange.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                comPort.writeBytes(orangeString, orangeString.length);
                UpdateColor(orangeString);
            }
        });
        yellow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                comPort.writeBytes(yellowString, yellowString.length);
                UpdateColor(yellowString);
            }
        });
        lightsOff.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                comPort.writeBytes(offString, offString.length);
                UpdateColor(offString);
            }
        });
    }
    
    private void startMovie(){        
        emp.play();   
        new javax.swing.Timer(600, new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                int sec = (int)(emp.getLength()*0.001);
                int min = (int)(sec/60);
                sec = sec - (min*60);
                int hour = (min/60);                
                min = min - (hour*60);
                endTime.setText(hour+":"+min+":"+sec);
            }
        }).start();
        new javax.swing.Timer(600, new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                int sec = (int)(emp.getTime()*0.001);
                int min = (int)(sec/60);
                sec = sec - (min*60);
                int hour = (min/60);               
                min = min - (hour*60);
                currentTime.setText(hour+":"+min+":"+sec);
                if(!write){
                    jSlider1.setValue((int)(emp.getPosition()*100));
                    jSlider1.setToolTipText(Integer.toString(jSlider1.getValue()));
                }
            }
        }).start();
    }
    
    private void UpdateColor(byte[] color){
        String colorChoice = new String(color);
        if(!colorChoice.trim().equals("OFF")){
            carImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/Jeep" + colorChoice.charAt(0) + ".png")));
        }else{
            carImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/Jeep.png")));
        }
    }
    
    private File[] GetMovies(String folder){
        File dir = new File(folder);

        File[] moviesList = dir.listFiles(new FilenameFilter()
        {
          public boolean accept(File dir, String name)
          {
             return name.endsWith(".mpg") || name.endsWith(".mkv") || name.endsWith(".m4v") || name.endsWith(".mp4") || name.endsWith(".avi") || dir.isDirectory();
          }
        });
        return moviesList;
    }
    
    private void PerformCommand(char[] command){
        String value = "";
        boolean stop = false;
        for(int i = 1; i < command.length && !stop; i++){
            if(command[i] != '.'){
                value = value.concat(Character.toString(command[i]));
            }else{
                stop = true;
            }
        }
        switch(command[0]){
            case 'S':                
                speed.setValue(Double.parseDouble(value));
                break;
            case 'F':      
                double gasLevel = Double.parseDouble(value);
                gasLevel = (gasLevel/100)*19;
                fuel.setValue(gasLevel);
                if(gasLevel < 4){
                    fuel.setLedBlinking(true);
                    gasAlarm.setOn(true);
                }else{
                    fuel.setLedBlinking(false);
                    gasAlarm.setOn(false);
                }
                break;
            case 'M':      
                double mals = Double.parseDouble(value);
                fuel.setValue(mals);
                if(mals > 0){
                    malLight.setOn(true);
                }else{
                    malLight.setOn(true);
                }
                break;
            case 'A':                
                airTemp.setValue(Double.parseDouble(value));
                break;
            case 'C':                
                carTemp.setValue(Double.parseDouble(value));
                break;
        }
    }
    
    private void InitMovies(){
        if(movies.length <= 4){
           movieUp.setVisible(false);
           movieDown.setVisible(false);
       }else{
           movieUp.setVisible(true);
           movieDown.setVisible(true);
       }
       for(int i = 0; i < 4; i++){
           if(i < movies.length){
                String text = movies[i].getName();
                if(movies[i].isFile()){
                    text = movies[i].getName().substring(0, movies[i].getName().indexOf("."));
                    char someChar = '-';
                    int count = 0;

                    for (int k = 0; k < text.length(); k++) {
                        if (text.charAt(k) == someChar) {
                            count++;
                        }
                    }      
                    if(count == 2){
                        text = text.substring(text.indexOf('-', text.indexOf('-')+1)+1,text.length());
                    }
                }
                movieButtons[i].setText(text);
                movieButtons[i].setVisible(true);
           }else{
               movieButtons[i].setVisible(false);
           }
       }  
    }
    
    private void RotateMovies(boolean direction){
        File[] tempMovies = new File[4];
        if(direction){            
            for(int i = 0; i < 4; i++){
                tempMovies[i] = movies[i];
            }  
            for(int i = 4; i < movies.length; i++){
                movies[i-4] = movies[i];
            }
            for(int i = 0; i < 4; i++){
                movies[(movies.length - 4) + i] = tempMovies[i];
            }
        }else{
            int j = 0;
            for(int i = (movies.length - 4); i < movies.length; i++){
                tempMovies[j] = movies[i];
                j++;
            }
            for(int i = (movies.length - 5); i >= 0; i--){
                movies[i+4] = movies[i];
            }
            for(int i = 0; i < 4; i++){
                movies[i] = tempMovies[i];
            }
        }
        for(int i = 0; i < 4; i++){
            String text = movies[i].getName();
            if(movies[i].isFile()){
                text = movies[i].getName().substring(0, movies[i].getName().indexOf("."));
                char someChar = '-';
                int count = 0;

                for (int k = 0; k < text.length(); k++) {
                    if (text.charAt(k) == someChar) {
                        count++;
                    }
                }      
                if(count == 2){
                    text = text.substring(text.indexOf('-', text.indexOf('-')+1)+1,text.length());
                }
            }
            movieButtons[i].setText(text);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        movie1 = new javax.swing.JButton();
        movie2 = new javax.swing.JButton();
        movie3 = new javax.swing.JButton();
        movie4 = new javax.swing.JButton();
        movieUp = new javax.swing.JButton();
        movieDown = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        green = new javax.swing.JButton();
        carImage = new javax.swing.JLabel();
        pink = new javax.swing.JButton();
        red = new javax.swing.JButton();
        yellow = new javax.swing.JButton();
        lightsOff = new javax.swing.JButton();
        orange = new javax.swing.JButton();
        blue = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        canvas1 = new java.awt.Canvas();
        jPanel3 = new javax.swing.JPanel();
        fuel = new eu.hansolo.steelseries.gauges.Radial1Vertical();
        speed = new eu.hansolo.steelseries.gauges.Radial();
        Exit = new javax.swing.JButton();
        gasAlarm = new eu.hansolo.steelseries.extras.Indicator();
        displaySingle1 = new eu.hansolo.steelseries.gauges.DisplaySingle();
        malLight = new eu.hansolo.steelseries.extras.Indicator();
        carTemp = new eu.hansolo.steelseries.gauges.RadialBargraph();
        airTemp = new eu.hansolo.steelseries.gauges.RadialBargraph();
        playPauseButton = new javax.swing.JButton();
        currentTime = new javax.swing.JLabel();
        endTime = new javax.swing.JLabel();
        rewind = new javax.swing.JButton();
        fastForward = new javax.swing.JButton();
        jSlider1 = new javax.swing.JSlider();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 102, 204));
        setResizable(false);

        jPanel2.setBackground(new java.awt.Color(102, 102, 102));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Garamond", 1, 48)); // NOI18N
        jLabel2.setText("Gideon Vehicle");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 0, -1, -1));

        jPanel1.setBackground(new java.awt.Color(153, 153, 153));
        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(0, 51, 153));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 470, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 670, -1, -1));

        jLabel3.setFont(new java.awt.Font("Perpetua", 1, 36)); // NOI18N
        jLabel3.setText("Avaiable Videos");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        movie1.setFont(new java.awt.Font("Perpetua", 0, 24)); // NOI18N
        movie1.setText("Happy Gilmore");
        jPanel1.add(movie1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 268, 44));

        movie2.setFont(new java.awt.Font("Perpetua", 0, 24)); // NOI18N
        movie2.setText("Frankenstien");
        jPanel1.add(movie2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 268, 44));

        movie3.setFont(new java.awt.Font("Perpetua", 0, 24)); // NOI18N
        movie3.setText("Supernatural - S01E12 - BLAH BLAH");
        movie3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                movie3ActionPerformed(evt);
            }
        });
        jPanel1.add(movie3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 268, 44));

        movie4.setFont(new java.awt.Font("Perpetua", 0, 24)); // NOI18N
        movie4.setText("House - S02E02 - BLAH BLAH");
        jPanel1.add(movie4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 268, 44));

        movieUp.setFont(new java.awt.Font("Garamond", 0, 11)); // NOI18N
        movieUp.setText("Move Down");
        jPanel1.add(movieUp, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 260, 131, 33));

        movieDown.setFont(new java.awt.Font("Garamond", 0, 11)); // NOI18N
        movieDown.setText("Move Up");
        jPanel1.add(movieDown, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 260, 119, 33));

        jTabbedPane1.setBackground(new java.awt.Color(204, 204, 204));
        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jPanel6.setBackground(new java.awt.Color(204, 204, 204));
        jPanel6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        green.setFont(new java.awt.Font("Garamond", 0, 11)); // NOI18N
        green.setText("Green");

        carImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/JeepB.png"))); // NOI18N

        pink.setFont(new java.awt.Font("Garamond", 0, 11)); // NOI18N
        pink.setText("Pink");

        red.setFont(new java.awt.Font("Garamond", 0, 11)); // NOI18N
        red.setText("Red");

        yellow.setFont(new java.awt.Font("Garamond", 0, 11)); // NOI18N
        yellow.setText("Yellow");

        lightsOff.setFont(new java.awt.Font("Garamond", 0, 11)); // NOI18N
        lightsOff.setText("Off");

        orange.setFont(new java.awt.Font("Garamond", 0, 11)); // NOI18N
        orange.setText("Orange");

        blue.setFont(new java.awt.Font("Garamond", 0, 11)); // NOI18N
        blue.setText("Blue");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(orange, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(blue, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(lightsOff, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(green, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pink, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(red, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(yellow, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                        .addComponent(carImage)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(green, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(red, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pink, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(yellow, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(orange, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(blue, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lightsOff, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(carImage))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Lights", jPanel6);

        jPanel4.setBackground(new java.awt.Color(204, 204, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        canvas1.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(canvas1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(canvas1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Movie", jPanel4);

        jPanel3.setBackground(new java.awt.Color(204, 204, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 471, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 248, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Control", jPanel3);

        jPanel1.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, 480, 280));
        jTabbedPane1.getAccessibleContext().setAccessibleName("Control");

        jPanel2.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 780, 300));

        fuel.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.CHROME);
        fuel.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        fuel.setMaxNoOfMajorTicks(8);
        fuel.setMaxValue(20.0);
        fuel.setTickmarkDirection(eu.hansolo.steelseries.tools.Direction.COUNTER_CLOCKWISE);
        fuel.setTitle("Fuel Level");
        fuel.setTitleAndUnitFont(new java.awt.Font("Garamond", 0, 10)); // NOI18N
        fuel.setTrackVisible(true);
        fuel.setUnitString("gallons");
        fuel.setValue(19.0);

        javax.swing.GroupLayout fuelLayout = new javax.swing.GroupLayout(fuel);
        fuel.setLayout(fuelLayout);
        fuelLayout.setHorizontalGroup(
            fuelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 162, Short.MAX_VALUE)
        );
        fuelLayout.setVerticalGroup(
            fuelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 162, Short.MAX_VALUE)
        );

        jPanel2.add(fuel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        speed.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.CHROME);
        speed.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        speed.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.GRAY_LCD);
        speed.setLcdUnitString("mph");
        speed.setLcdUnitStringVisible(true);
        speed.setLedVisible(false);
        speed.setMaxNoOfMajorTicks(20);
        speed.setMaxValue(120.0);
        speed.setTitle("Speed");
        speed.setUnitString("mph");

        Exit.setBorderPainted(false);
        Exit.setContentAreaFilled(false);

        javax.swing.GroupLayout speedLayout = new javax.swing.GroupLayout(speed);
        speed.setLayout(speedLayout);
        speedLayout.setHorizontalGroup(
            speedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, speedLayout.createSequentialGroup()
                .addGap(0, 137, Short.MAX_VALUE)
                .addComponent(Exit, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        speedLayout.setVerticalGroup(
            speedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(speedLayout.createSequentialGroup()
                .addComponent(Exit, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 139, Short.MAX_VALUE))
        );

        jPanel2.add(speed, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 0, -1, -1));

        gasAlarm.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.CHROME);
        gasAlarm.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        gasAlarm.setSymbolType(eu.hansolo.steelseries.tools.SymbolType.FUEL);
        jPanel2.add(gasAlarm, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, 50, 50));

        displaySingle1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        displaySingle1.setCustomLcdUnitFont(new java.awt.Font("Garamond", 0, 24)); // NOI18N
        displaySingle1.setFont(new java.awt.Font("Garamond", 0, 11)); // NOI18N
        displaySingle1.setLcdDecimals(0);
        displaySingle1.setLcdInfoFont(new java.awt.Font("Garamond", 1, 4)); // NOI18N
        displaySingle1.setLcdUnitString("mpg");
        displaySingle1.setLcdValue(15.0);

        javax.swing.GroupLayout displaySingle1Layout = new javax.swing.GroupLayout(displaySingle1);
        displaySingle1.setLayout(displaySingle1Layout);
        displaySingle1Layout.setHorizontalGroup(
            displaySingle1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 112, Short.MAX_VALUE)
        );
        displaySingle1Layout.setVerticalGroup(
            displaySingle1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        jPanel2.add(displaySingle1, new org.netbeans.lib.awtextra.AbsoluteConstraints(347, 50, -1, -1));

        malLight.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.CHROME);
        malLight.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        malLight.setSymbolType(eu.hansolo.steelseries.tools.SymbolType.ATTENTION);
        jPanel2.add(malLight, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 10, 50, 50));

        carTemp.setCustomLcdUnitFont(new java.awt.Font("Garamond", 1, 24)); // NOI18N
        carTemp.setFont(new java.awt.Font("Garamond", 0, 11)); // NOI18N
        carTemp.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.CHROME);
        carTemp.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        carTemp.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.GRAY_LCD);
        carTemp.setLcdUnitString("°F");
        carTemp.setLcdUnitStringVisible(true);
        carTemp.setLedVisible(false);
        carTemp.setMaxNoOfMajorTicks(20);
        carTemp.setMinValue(30.0);
        carTemp.setTitle("Car Temperature");
        carTemp.setUnitString("°F");
        carTemp.setValue(72.0);

        javax.swing.GroupLayout carTempLayout = new javax.swing.GroupLayout(carTemp);
        carTemp.setLayout(carTempLayout);
        carTempLayout.setHorizontalGroup(
            carTempLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        carTempLayout.setVerticalGroup(
            carTempLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPanel2.add(carTemp, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 60, -1, -1));

        airTemp.setCustomLcdUnitFont(new java.awt.Font("Garamond", 1, 24)); // NOI18N
        airTemp.setFont(new java.awt.Font("Garamond", 0, 11)); // NOI18N
        airTemp.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.CHROME);
        airTemp.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        airTemp.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.GRAY_LCD);
        airTemp.setLcdUnitString("°F");
        airTemp.setLcdUnitStringVisible(true);
        airTemp.setLedVisible(false);
        airTemp.setMaxNoOfMajorTicks(20);
        airTemp.setMinValue(-30.0);
        airTemp.setTitle("Air Temperature");
        airTemp.setTitleAndUnitFont(new java.awt.Font("Garamond", 0, 10)); // NOI18N
        airTemp.setUnitString("°F");
        airTemp.setValue(72.0);
        jPanel2.add(airTemp, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 60, 100, 100));

        playPauseButton.setBackground(new java.awt.Color(102, 102, 102));
        playPauseButton.setIcon(pause);
        playPauseButton.setContentAreaFilled(false);
        jPanel2.add(playPauseButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 80, -1, -1));

        currentTime.setForeground(new java.awt.Color(255, 255, 255));
        currentTime.setText("0:0:0");
        jPanel2.add(currentTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 150, -1, -1));

        endTime.setForeground(new java.awt.Color(255, 255, 255));
        endTime.setText("2:0:0");
        jPanel2.add(endTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 150, -1, -1));

        rewind.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/rewind.png"))); // NOI18N
        rewind.setBorderPainted(false);
        rewind.setContentAreaFilled(false);
        jPanel2.add(rewind, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 90, -1, -1));

        fastForward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/fastForward.png"))); // NOI18N
        fastForward.setBorderPainted(false);
        fastForward.setContentAreaFilled(false);
        jPanel2.add(fastForward, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 90, -1, -1));

        jSlider1.setBackground(new java.awt.Color(102, 102, 102));
        jSlider1.setToolTipText(Integer.toString(jSlider1.getValue()));
        jSlider1.setValue(0);
        jPanel2.add(jSlider1, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 126, 250, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void movie3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_movie3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_movie3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HomeMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomeMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomeMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomeMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HomeMenu().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Exit;
    private eu.hansolo.steelseries.gauges.RadialBargraph airTemp;
    private javax.swing.JButton blue;
    private java.awt.Canvas canvas1;
    private javax.swing.JLabel carImage;
    private eu.hansolo.steelseries.gauges.RadialBargraph carTemp;
    private javax.swing.JLabel currentTime;
    private eu.hansolo.steelseries.gauges.DisplaySingle displaySingle1;
    private javax.swing.JLabel endTime;
    private javax.swing.JButton fastForward;
    private eu.hansolo.steelseries.gauges.Radial1Vertical fuel;
    private eu.hansolo.steelseries.extras.Indicator gasAlarm;
    private javax.swing.JButton green;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton lightsOff;
    private eu.hansolo.steelseries.extras.Indicator malLight;
    private javax.swing.JButton movie1;
    private javax.swing.JButton movie2;
    private javax.swing.JButton movie3;
    private javax.swing.JButton movie4;
    private javax.swing.JButton movieDown;
    private javax.swing.JButton movieUp;
    private javax.swing.JButton orange;
    private javax.swing.JButton pink;
    private javax.swing.JButton playPauseButton;
    private javax.swing.JButton red;
    private javax.swing.JButton rewind;
    private eu.hansolo.steelseries.gauges.Radial speed;
    private javax.swing.JButton yellow;
    // End of variables declaration//GEN-END:variables
    private javax.swing.JFrame topFrame;
    private EmbeddedMediaPlayer emp;
    private File[] movies;
    private JButton[] movieButtons;
    private SerialPort comPort;
    final private byte[] blueString = {'B','L','U','E','\n'};
    final private byte[] greenString = {'G','R','E','E','N','\n'};
    final private byte[] redString = {'R','E','D','\n'};
    final private byte[] pinkString = {'P','I','N','K','\n'};
    final private byte[] orangeString = {'O','R','A','N','G','E','\n'};
    final private byte[] yellowString = {'Y','E','L','L','O','W','\n'};
    final private byte[] offString = {'O','F','F','\n'};
    final private javax.swing.ImageIcon pause = new javax.swing.ImageIcon(getClass().getResource("/Resources/pause.png"));
    final private javax.swing.ImageIcon play = new javax.swing.ImageIcon(getClass().getResource("/Resources/play.png"));
    private boolean write = false;
}
