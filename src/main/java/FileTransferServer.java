
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Formatter;
import java.util.Locale;


public class FileTransferServer {
    private static Socket socket;
    private static DataInputStream din;
    private static DataOutputStream dout;
    private static StringBuilder sb = new StringBuilder();
    private static String fileName;
    private static String ServerFile = "/home/ellentuane/NetBeansProjects/tranferirArquivo/serverArquivos/";
    private static String password = "@Otario123";

    public static void main(String[] args) {
        File file = new File(ServerFile);
		File[] files = new File[0];
		try {
                    // start socket server
                    ServerSocket serverSocket = new ServerSocket(6969);
                    //waits for the Client to connect//Server Port: 69696
                    socket = serverSocket.accept();
                    
                    // din are msg coming from Client
                    //dout are msg sending to Client

                    din = new DataInputStream(socket.getInputStream());  
                    dout = new DataOutputStream(socket.getOutputStream());

                    String clientName;
                    String passwordIn = " ";

                    dout.writeUTF("Enter your name ");
                    clientName = din.readUTF();


                    while (!passwordIn.equals(password)){
                        // send msg and print directly on command line
                        dout.writeUTF("Enter your password");
                        
                        // Waits for the password
                        passwordIn = din.readUTF();
                        
                        // check if password is correct
                        if (passwordIn.equals(password)){
                            dout.writeUTF("true");
                        }  else{
                            dout.writeUTF("false");
                        } 
                    }
                    
                    // password was corret
                    dout.writeUTF("Welcome, " + clientName);

                    file = new File(ServerFile);
                    files = file.listFiles();
                    int j = 0;
                    sb.append("Total Files in folder - " + files.length + "\n");

                    FileTransferServer.listFiles(files);

                    dout.writeUTF(sb.toString());

                    dout.writeUTF("Enter the FileName which you want to Download\n");

                    fileName = din.readUTF();	//asks client to input fileName to download

                    file = new File(ServerFile + fileName);

                    FileTransferServer.sendFile(file, fileName);//the file if present, is sent over the network

                    file = new File(ServerFile);

                    serverSocket.close();
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	

	private static void sendFile(File file, String fileName) {
		try {
                    dout.writeUTF(fileName);

                    byte[] byteArray = new byte[(int) file.length()];					//creating byteArray with length same as file length
                    dout.writeInt(byteArray.length);

                    BufferedInputStream bis = new BufferedInputStream (new FileInputStream(file));
                    //Writing int 0 as a Flag which denotes the file is present in the Server directory, if file was absent, FileNotFound exception will be thrown and int 1 will be written
                    dout.writeInt(0);								

                    BufferedOutputStream bos = new BufferedOutputStream(dout);

                    int count;
                    while((count = bis.read(byteArray)) != -1) {			//reads bytes of byteArray length from the BufferedInputStream into byteArray
                            bos.write(byteArray, 0, count);					//writes bytes from byteArray into the BufferedOutputStream (0 is the offset and count is the length)
                    }

                    bos.flush();
                    bis.close();

                    din.readInt();					//readInt is used to reset if any bytes are present in the buffer after the file transfer
		}
		catch(FileNotFoundException ex) {
			sb.append("File " + fileName + " Not Found! \n        Please Check the input and try again.\n\n        ");
			
			try {
				//Writing int 1 as a Flag which denotes the file is absent from the Server directory, if file was present int 0 would be written
				dout.writeInt(1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
		
	}
	
	
	private static void listFiles(File[] files) {
		int k = 0;
		
		sb.append("\n        +---------+----------------------+\n");			
		Formatter formatter = new Formatter(sb, Locale.US);
		//formats the fields to create table like structure while displaying on console
		formatter.format("        | %-7s | %-20s |\n", "Sr No", "Filename");			
		sb.append("        +---------+----------------------+\n");
		
		for(File f: files) {
			if(! f.isDirectory()) 
				formatter.format("        | %-7s | %-20s |\n", ++k, f.getName());
		}
		
		sb.append("        +---------+----------------------+\n\n        ");
		formatter.close();					
	}

}
