import java.rmi.*;
import java.util.List;

public interface InterfaceServer extends java.rmi.Remote
{
  void notify_player_buy(Offer o) throws java.rmi.RemoteException;
  boolean add_client(InterfaceClient client) throws java.rmi.RemoteException;
  List<FantaPlayer> get_listone_svincolati() throws java.rmi.RemoteException; 
  //void notify_user(String message) throws java.rmi.RemoteException;
  //void nuovo_turno() throws java.rmi.RemoteException;

}
