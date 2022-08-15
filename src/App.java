import java.text.Normalizer.Form;
import java.util.Collection;
import java.util.Scanner;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
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
                try{
                    XMPPConnection con = new XMPPConnection("alumchat.fun");
                    con.connect();

                    System.out.println("Ingrese su nombre de usuario:");
                    String usr = reader.nextLine();
                    System.out.println("Ingrese su contrasena");
                    String pass = reader.nextLine();

                    con.login(usr,pass);
                    System.out.println("Conexion Realizada");

                    while(true){
                        System.out.println("--------------------Chat--------------------");
                        System.out.println("Elija su accion: \n1) Mostrar Usuarios\n2) Agregar Contacto\n3) Mostrar detalles contacto\n4) Chat Privado\n5) Chat Grupal\n6) Notificaciones\n7) Archivos\n8) Cambiar estado\n9) Cerrar Sesion");
                        String opcion1 = reader.nextLine();
                        if(Integer.valueOf(opcion1) == 1){
                            System.out.println("--------------------Conctactos--------------------");
                            Roster roster = con.getRoster();
                            Collection<RosterEntry> entries = roster.getEntries();
                            for (RosterEntry entry : entries) {
                                System.out.println(entry);
                                }
                            /*System.out.println("--------------------Otros Usuarios--------------------");

                            UserSearchManager umanager = new UserSearchManager(con);
                            String searchFormString = "search." + con.getConnection().getServiceName();*/
                        }
                        else if(Integer.valueOf(opcion1) == 2){
                            System.out.println("Ingrese el usuario a agreagar:");
                            String fren = reader.nextLine();

                            Roster roster = con.getRoster();
                            roster.reload();
                            roster.createEntry(fren+"@alumchat.fun",fren,null);
                            System.out.println("Usuario Agregado");
                        }
                        else if(Integer.valueOf(opcion1) == 3){
                            System.out.println("Ingrese el usuario:");
                            String estado = reader.nextLine();
                            estado = estado + "@alumchat.fun";

                            Roster roster = con.getRoster();
                            roster.reload();
                            System.out.println(estado + " se encuentra: " +roster.getPresence(estado));
                        }
                        else if(Integer.valueOf(opcion1) == 4){
                            System.out.println("Ingrese el usuario:");
                            String chatUSR = reader.nextLine();
                            Chat chat = con.getChatManager().createChat(chatUSR+"@alumchat.fun", new MessageListener() {
 

                                @Override
                                public void processMessage(Chat chat, Message message) {
                                    if(message.getBody() != null){
                                    System.out.println("Mensaje de " + chatUSR + ": " + message.getBody());
                                }

                                }
                            });
                            while(true){
                                String sendMSG = reader.nextLine();
                                if("exit\n".equals(sendMSG)){
                                    chat.sendMessage(sendMSG);
                                }
                                else{
                                    break;
                                }
                                
                            }
                        }
                        else if(Integer.valueOf(opcion1) == 5){
                            
                            //MultiUserChat muc = new MultiUserChat(con, room)
                            

                        }
                        else if(Integer.valueOf(opcion1) == 7){

                            

                        }
                        else if(Integer.valueOf(opcion1) == 8){
                            System.out.println("Su nuevo estado es:\n1) Disponible\n2) No disponible");
                            String estado = reader.nextLine();
                            if(Integer.valueOf(estado) == 1){
                                Presence pres = new Presence(Presence.Type.available);
                                con.sendPacket(pres);
                            }
                            else if(Integer.valueOf(estado) == 2){
                                Presence pres = new Presence(Presence.Type.unavailable);
                                con.sendPacket(pres);
                            }
                            else{
                                System.out.println("Opcion invalida");
                            }
                            
                        }
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

            else if(Integer.valueOf(opcion) == 2){
                XMPPConnection con = new XMPPConnection("alumchat.fun");
                con.connect();
                AccountManager manager = new AccountManager(con);

                System.out.println("Ingrese su nombre de usuario:");
                String usr = reader.nextLine();
                System.out.println("Ingrese su contrasena");
                String pass = reader.nextLine();

                manager.createAccount(usr, pass);
                System.out.println("Cuenta Creada");
                con.disconnect();

            }

            else if(Integer.valueOf(opcion) == 3){
                try{
                XMPPConnection con = new XMPPConnection("alumchat.fun");
                con.connect();
                AccountManager manager = new AccountManager(con);

                System.out.println("Ingrese su nombre de usuario:");
                String usr = reader.nextLine();
                System.out.println("Ingrese su contrasena");
                String pass = reader.nextLine();

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
