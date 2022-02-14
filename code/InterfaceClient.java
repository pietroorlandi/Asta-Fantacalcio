import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceClient extends Remote{
	public void message_from_server(String message) throws RemoteException;
    public String get_name() throws RemoteException;
	public boolean is_squadra_completata() throws RemoteException;
	public void choose_fantaplayer() throws RemoteException;
	public void set_successor_client(InterfaceClient successore) throws RemoteException;
	public void rilancio(Offer o) throws RemoteException;
	public String get_service_name() throws RemoteException;

	
}
