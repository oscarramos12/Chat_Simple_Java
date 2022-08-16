/*
 * Oscar Ramos 19184
 * Proyecto Cliente usando XMPP y Smack
 * Referencias:
 *  http://download.igniterealtime.org/smack/docs/3.0.1.beta1/javadoc/index.html?org/jivesoftware/smackx/filetransfer/package-summary.html
 *  http://download.igniterealtime.org/smack/docs/3.0.1/javadoc/org/jivesoftware/smack/Roster.html 
 *  https://www.tabnine.com/code/java/methods/org.jivesoftware.smackx.muc.MultiUserChat/create 
 * 
 */

import java.io.File;
import java.text.Normalizer.Form;
import java.util.Collection;
import java.util.Scanner;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.commands.AdHocCommand.Status;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.search.UserSearchManager;


public class App {
    public static void main(String[] args) throws Exception {
        while(true){
            Scanner reader = new Scanner(System.in);
            System.out.println("--------------------Chat--------------------");
            System.out.println("Elija su accion: \n1) Iniciar Sesion\n2) Crear Cuenta\n3) Borrar cuenta");
            String opcion = reader.nextLine();

            if(Integer.valueOf(opcion) == 1){
                //El try valida si la conexion se puede realizar ya que si los datos ingresados son erroneos tira error
                try{
                    //Se indica a que servirdor se conecta
                    XMPPConnection con = new XMPPConnection("alumchat.fun");
                    con.connect();
                    //Solicita datos al usuario
                    System.out.println("Ingrese su nombre de usuario:");
                    String usr = reader.nextLine();
                    System.out.println("Ingrese su contrasena");
                    String pass = reader.nextLine();
                    //Inicia sesion si los datos son correctos
                    con.login(usr,pass);
                    System.out.println("Conexion Realizada");
                    //Entra en un ciclo infinito de menu
                    while(true){
                        System.out.println("--------------------Chat--------------------");
                        System.out.println("Elija su accion: \n1) Mostrar Usuarios\n2) Agregar Contacto\n3) Mostrar detalles contacto\n4) Chat Privado\n5) Chat Grupal\n6) Notificaciones\n7) Archivos\n8) Cambiar estado\n9) Cerrar Sesion");
                        String opcion1 = reader.nextLine();

                        //Despliega contactos
                        if(Integer.valueOf(opcion1) == 1){
                            System.out.println("--------------------Conctactos--------------------");
                            Roster roster = con.getRoster();
                            Collection<RosterEntry> entries = roster.getEntries();
                            for (RosterEntry entry : entries) {
                                System.out.println(entry);
                                }

                        }

                        //Agregar contacto
                        else if(Integer.valueOf(opcion1) == 2){
                            //Solicita datos
                            System.out.println("Ingrese el usuario a agreagar:");
                            String fren = reader.nextLine();
                            //Obtiene el roster y agrega el contacto
                            Roster roster = con.getRoster();
                            roster.reload();
                            roster.createEntry(fren+"@alumchat.fun",fren,null);
                            System.out.println("Usuario Agregado");
                        }
                        //Obtener Estado
                        else if(Integer.valueOf(opcion1) == 3){
                            //Solicita datos
                            System.out.println("Ingrese el usuario:");
                            String estado = reader.nextLine();
                            estado = estado + "@alumchat.fun";
                            //Obtiene el roster y busca el estado del usuario ingresado
                            Roster roster = con.getRoster();
                            roster.reload();
                            System.out.println(estado + " se encuentra: " +roster.getPresence(estado));
                        }
                        //Chat Privado
                        else if(Integer.valueOf(opcion1) == 4){
                            //Solicita datos
                            System.out.println("Ingrese el usuario:");
                            String chatUSR = reader.nextLine();
                            //Crea el manejador de chat
                            Chat chat = con.getChatManager().createChat(chatUSR+"@alumchat.fun", new MessageListener() {
                                //Funcion que procesa y despliega el mensaje
                                @Override
                                public void processMessage(Chat chat, Message message) {
                                    if(message.getBody() != null){
                                    System.out.println("Mensaje de " + chatUSR + ": " + message.getBody());
                                }

                                }
                            });
                            //Ciclo que espera recibir un input para enviar mensaje
                            while(true){
                                String sendMSG = reader.nextLine();
                                if("exit".equals(sendMSG)){
                                    break;
                                }
                                else{
                                    chat.sendMessage(sendMSG);
                                }
                                
                            }
                        }
                        //Chat Grupal
                        else if(Integer.valueOf(opcion1) == 5){
                            //Solicita datos
                            System.out.println("Ingrese el nombre del room:");
                            String room = reader.nextLine();
                            //Se conecta al cuarto
                            MultiUserChat muc = new MultiUserChat(con,room+"@conference.alumchat.fun" );
                            muc.join(con.getUser());

                            //Crea un listener para mostrar los mensajes del grupo
                            muc.addMessageListener(new PacketListener() {

                            @Override
                            public void processPacket(Packet packet) {
                                // TODO Auto-generated method stub
                                Message msg = (Message) packet;
                                System.out.println("Mensaje: " + msg.getBody());
                            }   
                            });

                            //Ciclo que espera un input para enviarlo como mensaje
                            while(con.isConnected()){
                                String sendGMSG = reader.nextLine();
                                if("exit".equals(sendGMSG)){
                                    break;
                                }
                                else{
                                    muc.sendMessage(sendGMSG);
                                }
                            }

                        }
                        //Mandar Archivos
                        else if(Integer.valueOf(opcion1) == 7){
                            System.out.println("Ingrese el nombre del usuario:");
                            String ftusr = reader.nextLine();
                            FileTransferManager ftman = new FileTransferManager(con);
                            OutgoingFileTransfer oft = ftman.createOutgoingFileTransfer(ftusr+"@alumchat.fun");
                            File dir = new File("C:\\Users\\Oscar\\Desktop\\send.txt");

                            ftman.addFileTransferListener(new FileTransferListener() {
                             public void fileTransferRequest(FileTransferRequest req){
                                IncomingFileTransfer transfer = req.accept();
                                try{

                                    transfer.recieveFile(new File ("recvFile.txt"));
                                    System.out.println("Llego archivo");
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                                
                             }   
                            });

                            while(con.isConnected()){
                                String sendGMSG = reader.nextLine();
                                if("send".equals(sendGMSG)){
                                    oft.sendFile(dir, "file");
                                    while(!oft.isDone()) {

                                        System.out.println(oft.getStatus());
                                        System.out.println(oft.getProgress());
                                        //Thread.sleep(50);
                                    }
                                    System.out.println("Se envio el archivo");
                                }
                                else if("exit".equals(sendGMSG)){
                                    break;
                                }
                            }
                            //System.out.println("Archivo enviado");


                        }
                        //Cambiar Estado
                        else if(Integer.valueOf(opcion1) == 8){
                            //Solicita datos
                            System.out.println("Su nuevo estado es:\n1) Disponible\n2) No disponible");
                            String estado = reader.nextLine();

                            //Manda un paquete con el nuevo estado
                            if(Integer.valueOf(estado) == 1){
                                Presence pres = new Presence(Presence.Type.available);
                                con.sendPacket(pres);
                            }

                            //Manda un paquete con el nuevo estado
                            else if(Integer.valueOf(estado) == 2){
                                Presence pres = new Presence(Presence.Type.unavailable);
                                con.sendPacket(pres);
                            }
                            else{
                                System.out.println("Opcion invalida");
                            }
                            
                        }
                        //Cerrar Sesion
                        else if(Integer.valueOf(opcion1) == 9){
                            con.disconnect();
                            System.out.println("Sesion Terminada");
                            break;
                        }

                    }

                }catch(Exception e){
                    System.out.println("Datos Invalidos");
                }
            }

            //Crear Cuenta
            else if(Integer.valueOf(opcion) == 2){
                //Se conecta al servidor
                XMPPConnection con = new XMPPConnection("alumchat.fun");
                con.connect();
                AccountManager manager = new AccountManager(con);
                //Solicita datos
                System.out.println("Ingrese su nombre de usuario:");
                String usr = reader.nextLine();
                System.out.println("Ingrese su contrasena");
                String pass = reader.nextLine();
                //Envia los datos para crear la cuenta
                manager.createAccount(usr, pass);
                System.out.println("Cuenta Creada");
                con.disconnect();

            }

            //Borrar Cuenta
            else if(Integer.valueOf(opcion) == 3){
                try{
                //Crea conexion con el servidor
                XMPPConnection con = new XMPPConnection("alumchat.fun");
                con.connect();
                AccountManager manager = new AccountManager(con);
                //Solicita datos
                System.out.println("Ingrese su nombre de usuario:");
                String usr = reader.nextLine();
                System.out.println("Ingrese su contrasena");
                String pass = reader.nextLine();
                //Una vez se inicia la sesion se borra la cuenta desde la sesion
                con.login(usr,pass);
                manager.deleteAccount();
                con.disconnect();
            }catch(Exception e){
                System.out.println("Datos Invalidos");
            }
                
            }
            else{
                System.out.println("Opcion invalida");
            }
        }

    }
}
