import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Questa classe implementa l'interfaccia InterfaceClient. Ogni istanza di questa classe dovrà avere il riferimento al server, a cui si registrerà.
 * Ha come attributi il numero di crediti rimasti del giocatore, la lista dei calciatori acquistati da lui fino a quel momento, il riferimento al server e altri attributi per gestire al meglio il sistema
 * @author pietro
 *
 */
public class Client extends UnicastRemoteObject implements InterfaceClient
{    
  public static final int GIOCATORI_PER_SQUADRA = 3;

  private boolean partecipate_asta;
  private FrameClient frame_client;
  private InterfaceClient successor_client;
  private String name;
  private Offer last_offer;
  private String client_service_name;
  protected InterfaceServer server_IF;
  private boolean connection_problem=false;
  private boolean bool_squadra_completata;
  private boolean has_portiere;
  private int crediti_rimasti;
  private ArrayList<FantaPlayer> fantaplayer_list;

  public Client(FrameClient frame_client, String name) throws RemoteException {
		super();
		this.frame_client = frame_client;
		this.name = name;
    this.has_portiere=false;
		this.client_service_name = "ClientListenService_" + this.name;
    this.successor_client=null;
    this.crediti_rimasti=100;
    this.fantaplayer_list=new ArrayList<FantaPlayer>();
    this.partecipate_asta=true;
    this.bool_squadra_completata=false;
	}

  /** Funzione che permette al client di registrarsi sul Server.
   *  Viene eseguita da interfaccia grafica, quando l'utente inserisce l'username e fa click sull'apposito bottone
   * 
   * @return valore booleano che rappresenta se l'azione è andata a buon fine (true) o meno (false)
   * @throws RemoteException
   */
  public boolean start_client() throws RemoteException {		
    String hostName = "localhost";
		String[] details = {this.name, hostName, this.client_service_name};

		try {
			Naming.rebind("rmi://" + hostName + "/" + this.client_service_name, this);
			this.server_IF = ( InterfaceServer )Naming.lookup("rmi://" + hostName + "/" + "serverRMI");	
		} 
		catch(NotBoundException | MalformedURLException | ConnectException me){
			this.connection_problem = true;
			me.printStackTrace();
      return false;
		}
		if(!this.connection_problem){
			return register_in_server(details);
		}
    return false; 
	}

  /***
   * Metodo che viene usato in supporto al metodo start_client(), per vedere la riuscita registrazione del client sul server
   * @param details
   * @return valore booleano che rappresenta la riuscita dell'operazione
   */
  public boolean register_in_server(String[] details) {		
      System.out.println("Register in server");	
      try{
        return this.server_IF.add_client(this);
      }
      catch(Exception e){
        e.printStackTrace();
        return false;
      }
  }
/**
 * Getter del client_successor
 * @return 
 */
  public InterfaceClient get_successor_client(){
    return this.successor_client;
  }

  /**
   * Setter del client_successor, viene utilizzato dal server quando verrà riempita la lobby, e esso farà aggiornare tutti i client_successor per creare un overlay network di tipo ring tra i client
   */
  @Override
  public void set_successor_client(InterfaceClient successor){
    this.successor_client=successor;
  }

  public void set_last_offer(Offer o) throws RemoteException{
    this.last_offer=o;
  }


/**
 * Metodo che permette di capire se un client parteciperà o meno a un asta.
 * @return
 */
  public boolean get_partecipate_asta(){
    return this.partecipate_asta && (!this.bool_squadra_completata);
  }
/**
 * Setter del attributo partecipate_asta
 * @param v
 */
  public void set_partecipate_asta(boolean v){
    this.partecipate_asta=v;
  }
 
  /***
   * Metodo che permette di capire quanto è il valore massimo che un client può spendere per un singolo calciatore. 
   * Mi serve per evitare che il client faccia un'offerta superiore ai propri crediti, e quindi ad evitare che qualcuno finisca i crediti.
   * @return numero crediti massimi spendibili per un singolo calciatore
  */
  public int single_player_max_credits(){
    if (this.fantaplayer_list.size()==GIOCATORI_PER_SQUADRA){
      return 0;
    }
    // a scopo didattico ogni user compra 3 giocatori
    int fantaplayer_to_buy = GIOCATORI_PER_SQUADRA - this.fantaplayer_list.size();
    return this.crediti_rimasti - (fantaplayer_to_buy-1); //questi sono i max crediti spendibili per un singolo fantaplayer
    
  }
  /**
   * Getter del name del client
   * @return nome client
   */
  public String get_name(){
    return this.name;
  }
/***
 * Getter dei crediti rimasti del client
 * @return crediti rimasti
 */
  public Integer get_crediti(){
    return this.crediti_rimasti;
  }

  public String get_service_name(){
    return this.client_service_name;
  }
/***
 * Getter del frame client del client (che si occupa della sua interfaccia grafica che vedrà l'utente)
 * @return
 */
  public FrameClient get_frameclient(){
    return this.frame_client;
  }
/***
 * Getter del server associato al client 
 * @return
 */
  public InterfaceServer get_server(){
    return this.server_IF;
  }

  /***
   * Mi fornisce una stringa rappresentante tutti i calciatori che ho nella mia squadra
   * @return
   */
  public String get_current_squad_str(){
    String str="";
    for (FantaPlayer fantaPlayer : fantaplayer_list) {
      str+=fantaPlayer.get_name();
    }
    return str+'\n';
  }

  /**
   * Metodo che permette la comunicazione dal server al client. Può essere usato in modo one-to-many (in una notify_users del server) oppure in modo one-to-one
   * @param message : il parametro rappresenta il messaggio che il server ha mandato al client
   */
  @Override
  public void message_from_server(String message) throws RemoteException {
    if (message.equals("NUOVO TURNO")){
      System.out.println("Nuovo turno");
      this.partecipate_asta=true;
      this.frame_client.update_GUI_nuovo_turno();
    }
    else if (message.equals("ASTA FINITA")){
      this.frame_client.update_GUI_asta_finita();
    }
    else { // messaggio generico
      System.out.println(message);
      this.get_frameclient().update_GUI_new_user(message); 
    }
  }

/**
 * Si controlla la validità di un'offerta fatta dal client. Più in particolare si controlla lato client che l'offerta attuale superi la precedente, che l'offerta attuale non superi il numero massimo di crediti spendibili per un singolo calciatore da quel client, e che l'offerta sia compatibile col fatto che ogni client deve avere esattamente 1 portiere in squadra
 * @param price_offered
 * @param price_precedent
 * @param ruolo
 * @return
 */
  public Boolean offer_is_valid(Integer price_offered, Integer price_precedent, char ruolo){
    // check if price_offered <= max_credits
    if ( price_offered>0 && price_offered<= this.single_player_max_credits() && price_offered > price_precedent){
      // se ho già un portiere o se non lo ho ancora e quello per cui sto rilanciando non è un portiere ed è l'ultimo giocatore
      if ( (this.has_portiere && ruolo=='P') || (ruolo!='P' && this.has_portiere==false && this.fantaplayer_list.size()==(GIOCATORI_PER_SQUADRA-1))){ 
        return false;
      }
      return true;
    }
    else return false;
  }
/**
 * Getter del flag bool_squadra_completata, controlla se il client in questione ha completato o meno la squadra (ovvero che il numero di calciatori acqistati==NUMERO_CALCIATORI)
 */
  @Override
  public boolean is_squadra_completata(){
    return this.bool_squadra_completata;
  }
/***
 * Metodo che permette al client di scegliere un calciatore. In pratica a ogni turno il serve chiama questo metodo sul client. A sua volta il client prende il listone dei calciatori (fantaplayer) dal server e aggiornerà la propria interfaccia di conseguenza per fare scegliere l'utente
 */
  @Override
  public void choose_fantaplayer() throws RemoteException{
    try {
      List<FantaPlayer> lista_svincolati_attuale = this.server_IF.get_listone_svincolati();
      System.out.println("Presa lista svincolati dal server");
      this.frame_client.update_GUI_choose_player(lista_svincolati_attuale); // questo client sceglierà il giocatore 
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  /***
   * Metodo che è alla base della logica del programma. In ingresso il metodo riceve l'offerta vincente in quel momento, e questa offerta ci è passata dal client che precede il client corrente nel ring.
   * Nel caso l'offerta vincente che arriva in ingresso sia stata fatta da questo client, si capisce che essa avrà girato tutto il ring e sia ritornata al mittente, quindi si capisce implicitamente che tutti gli altri client nel ring avranno lasciato oppure avranno già completato la squadra e si capirà che si è vinta l'asta per quel calciatore (e notificherò il server del mio acquisto).
   * Nel caso invece che l'offerta vincente che arriva in ingresso sia fatta da un client diverso da questo, se questo client ha già completato la squadra o ha già lasciato, allora esso si limiterà a forwardare l'offerta al suo client_successor; se invece è ancora in gioco per l'asta per quel calciatore, potrà decidere se rilanciare o lasciare l'asta attraverso l'apposita interfaccia grafica.
   * @param o: offerta vincente fino a quel momento
   */
  @Override
  public void rilancio(Offer o) throws RemoteException{
    System.out.println(this.name+" metodo rilancio");
    if (o.get_username().equals(this.name)){ // ha fatto il giro del Ring, ed è tornato a lui, quindi hanno lasciato tutti e devo notificare il server
      System.out.println("L'offerta fatta era mia");
      this.fantaplayer_list.add(o.get_fantaplayer());
      this.crediti_rimasti-=o.get_price();
      if (o.get_fantaplayer().get_ruolo()=='P'){
        this.has_portiere=true;
      }
      if (this.fantaplayer_list.size()==GIOCATORI_PER_SQUADRA){ // squadra completata
        System.out.println("Il client "+this.name+" ha completato la squadra!");
        this.bool_squadra_completata=true;
      }
      System.out.println("offerta vincente "+o.get_csv_offer());
      this.frame_client.update_GUI_vinta_asta(this.fantaplayer_list);
      // l'asta è finita, notifico il server di tutto e esso farà iniziare un nuovo turno
      try {
        System.out.println("Notifico il server del mio acquisto");
        this.server_IF.notify_player_buy(o);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }
    else { // vuole dire che c'è ancora qualcuno 
      if (this.bool_squadra_completata || !this.partecipate_asta){ // mi limito a forwardare l'offerta al mio successore
        try {
          this.successor_client.rilancio(o);
        } catch (RemoteException e) {
          e.printStackTrace();
        }
      }
      else { // non ho ancora lasciato e non ho ancora lasciato, quindi partecipo a asta
        this.frame_client.update_GUI_asta(o);
      }
    }
  }


  public static void main(String args[])
  {
    System.setSecurityManager(new SecurityManager());
    FrameClient start_frame_client = new FrameClient();
  }




}