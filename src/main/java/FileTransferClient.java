
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JOptionPane;


public class FileTransferClient {
    private static DataInputStream din;
    private static DataOutputStream dout;
    private static Socket clientSocket;
    private static String clientFiles = "/home/ellentuane/NetBeansProjects/tranferirArquivo/clienteArquivo/";
	
	
    public static void main(String[] args) {
        try {
            clientSocket = new Socket("localhost", 6969);
            //host is 'localhost' when Client and Server are on the same machine, input IpAaddress when both are on different machines

            din = new DataInputStream(clientSocket.getInputStream());
            dout = new DataOutputStream(clientSocket.getOutputStream());  
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            String str;

            str = din.readUTF();
            System.out.println("Server1: " + str);
            
            // Name
            dout.writeUTF(br.readLine());
            
            Boolean checkpassWord = false;
            
            while(!checkpassWord){
                //password
                str = din.readUTF();
                System.out.println("Server2: " + str);
                dout.writeUTF(br.readLine());
                checkpassWord = Boolean.valueOf(din.readUTF());
            }

            str = din.readUTF();
            System.out.println("Server3: " + str);
            
            str = din.readUTF();
            System.out.println("\nServer4: " + str);

            str = din.readUTF();
            
            System.out.println("\nServer5: " + str);

            str = br.readLine().trim();				

            dout.writeUTF(str);	//writes the inputed fileName
            
            String songName = str;

            receiveFile();	//file is received over the network
            
            playSong(songName);
      
            
            }
            catch (IOException ex) {
                    ex.printStackTrace();
            }
        
        
	
    }
	
	
	private static void receiveFile() {
            int bytesRead = 0, current = 0;

            try {
                String fileName = din.readUTF();
                int fileLength = din.readInt();
                byte[] byteArray = new byte[fileLength];					//creating byteArray with length same as file length

                BufferedInputStream bis = new BufferedInputStream(din);

                File file = new File(clientFiles + fileName);

                //fileFoundFlag is a Flag which denotes the file is present or absent from the Server directory, is present int 0 is sent, else 1
                int fileFoundFlag = din.readInt();
                if(fileFoundFlag == 1)
                        return;

                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

                bytesRead = bis.read(byteArray, 0, byteArray.length);			//reads bytes of length byteArray from BufferedInputStream & writes into the byteArray, (Offset 0 and length is of byteArray)
                current = bytesRead;

                //Sometimes only a portion of the file is read, hence to read the remaining portion...
                do {
                        //BufferedInputStream is read again into the byteArray, offset is current (which is the amount of bytes read previously) and length is the empty space in the byteArray after current is subtracted from its length
                        bytesRead = bis.read(byteArray, current, (byteArray.length - current));

                        if(bytesRead >= 0)
                                current += bytesRead;					//current is updated after the new bytes are read
                } while(bytesRead > 0);						
                bos.write(byteArray, 0, current);				//writes bytes from the byteArray into the BufferedOutputStream, offset is 0 and length is current (which is the amount of bytes read into byteArray)

                bos.close();

                System.out.println("        File " + fileName + " Successfully Downloaded!" );
                dout.writeInt(0);						//writeInt is used to reset if any bytes are present in the buffer after the file transfer
            }
            catch(IOException ex) {
                    ex.printStackTrace();
            }
        }
        
        public static void playSong(String song) {
            
            File musicPath = new File(clientFiles + song);
            
            try {
                if (musicPath.exists()){
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicPath);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    clip.start();
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                    
                    JOptionPane.showMessageDialog(null, "Press OK to Stop Music");
                                        
                }else{
                    System.out.println("music not found");
                }
                
            } catch(Exception ex) {
                System.out.println("Error with playing sound.");
                ex.printStackTrace();
            }
    }

}

