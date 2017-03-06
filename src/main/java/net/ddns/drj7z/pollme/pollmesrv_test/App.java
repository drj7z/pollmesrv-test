package net.ddns.drj7z.pollme.pollmesrv_test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import net.ddns.drj7z.pollme.pollmesrv.action.ActionNames;
import net.ddns.drj7z.pollme.pollmesrv.action.error.ActionError;
import net.ddns.drj7z.pollme.pollmesrv.action.error.messageinterpolation.ContextMessageInterpolatorImpl;
import net.ddns.drj7z.pollme.pollmesrv.action.error.messageinterpolation.MessageInterpolator;
import net.ddns.drj7z.pollme.pollmesrv.action.register.RegisterData;
import net.ddns.drj7z.pollme.pollmesrv.action.register.RegisterDataImpl;
import net.ddns.drj7z.pollme.pollmesrv.communication.json.JsonEncoder;
import net.ddns.drj7z.pollme.pollmesrv.communication.json.gson.JsonEncoderGson;
import net.ddns.drj7z.pollme.pollmesrv.communication.packet.ActionPacket;
import net.ddns.drj7z.pollme.pollmesrv.communication.packet.NakPacket;
import net.ddns.drj7z.pollme.pollmesrv.communication.packet.Packet;
import net.ddns.drj7z.pollme.pollmesrv.communication.packet.PacketBase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App
{
  public static void main (String[] args)
  {
    ApplicationContext context =
        new ClassPathXmlApplicationContext("application.xml");
    ((AbstractApplicationContext)context).registerShutdownHook();

    MessageSource ms= (MessageSource)context.getBean("messageSource");

    ObjectOutputStream out;
    ObjectInputStream in;

    String username= args[0];
    Socket socket= null;

    String serverHost= args[1];

    try {

      System.out.println("client: running for " + username);

      JsonEncoder jsonEncoder= new JsonEncoderGson();

      RegisterData registrationData= new RegisterDataImpl();
      registrationData.setEmail("drjxzoidberg@gmail.com");
      registrationData.setName("John Zoidberg");
      registrationData.setPassword(String.valueOf(Math.random()));
      //      registrationData.setUsername("drj7z");
      registrationData.setUsername(username);
      //      registrationData.setTelephoneNumber("+390987654321");

      Packet actionPacket= new ActionPacket(ActionNames.register,
          registrationData);

      String packet= jsonEncoder.encode(actionPacket);

      socket= new Socket(serverHost,7001);
      System.out.println(socket + " :: timeout: " + socket.getSoTimeout());
      try {
        socket.setSoTimeout(10000);
      } catch ( SocketException e) {
        // TODO logger
        System.out.println(socket + " :: cannot set socket timeout.");
      }


      System.out.println("client: running for " + username + ":" + socket.getLocalPort());

      out= new ObjectOutputStream(socket.getOutputStream());
      in= new ObjectInputStream(socket.getInputStream());

      out.writeObject(packet);
      out.flush();
      //      out.close();
      // wait for an ACK or NAK
      String answer= (String)in.readObject();
      //      in.close();
      System.out.println(socket + ":: answer is: " + answer);

      // TODO come ottengo Ack o Nak???
      PacketBase pkt= (PacketBase)jsonEncoder.decode(answer,PacketBase.class);
      PacketBase.Header header= (PacketBase.Header)pkt.getHeader();

      if ( header.getType() == Packet.Types.NAK ) {
        NakPacket nak= (NakPacket)jsonEncoder.decode(answer,NakPacket.class);
        for ( Object error: ((NakPacket.Payload)nak.getPayload()).getErrors() ) {
          ActionError aex= ((ActionError)error);
          //          System.err.println(aex.interpolateMessage(ms));
          MessageInterpolator mi= new ContextMessageInterpolatorImpl(ms);
          System.err.println(mi.interpolate(aex.getMessageTemplate(),aex.getContext()));
        }
      }

      System.out.println(socket + ":: client: I'm done." + username);
    } catch ( Exception e ) {
      System.out.println(socket + " :: error: client(" + username + ") - '" + e.getMessage() + "'.");
    }
    finally {
      if ( socket != null ) {
        try {
          System.out.println(socket + " :: closing.");
          socket.close();
        } catch ( IOException e ) {
          System.out.println("error while closing socket");
          // ignored.
        }
      }
    }
  }
}
