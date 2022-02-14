import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * E' la classe che rappresenta il Server. Implementa l'interffacia InterfaceServer. 
 * A un istanza della classe Server verranno registrati varie istanze della classe Client. 
 * Ha degli attributi che rappresentano lo stato di un particolare momento del sistema, come ad esempio numero di giocatori collegati, il listone degli svincolati in un particolare momento etc..
 * 
 * @author pietro
 *
 */
public class Server extends UnicastRemoteObject implements InterfaceServer {

  public static final int NUMERO_CLIENT = 4;

  private int turno_inizio;
  private int giocatori_collegati;
  private List<Offer> lista_offerte_vincenti;
  private static String name="serverRMI";
  // private FrameServer frame;
  private List<InterfaceClient> client_list;
  private List<FantaPlayer> listone_svincolati;
  // private List<FrameClient> client_list;

  public Server(String s) throws RemoteException {
    super();
    this.name = s;
    this.giocatori_collegati = 0;
    this.lista_offerte_vincenti=new ArrayList<Offer>();
    // this.client_list=new ArrayList<FrameClient>();
    this.client_list = new ArrayList<InterfaceClient>();
    // this.frame = new FrameServer();
    this.listone_svincolati = new ArrayList<FantaPlayer>();
  }

  /**
   * Metodo che mi fornisce in formato stringa la lista dei client in lobby
   * @return
   * @throws RemoteException
   */
  public String users_in_lobby() throws RemoteException {
    String str = "";
    for (InterfaceClient client : client_list) {
      str += client.get_name() + " ";
    }
    return str + "\n";
  }
/***
 * Metodo che mi permette di convertire la lista di calciatori presente su file e inserirla nella struttura dati che sarà presente sul server che rappresenterà la lista dei calciatori svincolati in ogni momento.
 * @throws FileNotFoundException
 * @throws IOException
 */
  public void create_listone_from_file() throws FileNotFoundException, IOException {
    try (BufferedReader br = new BufferedReader(new FileReader("listone.txt"))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] parts = line.split(",");
        FantaPlayer player = new FantaPlayer(parts[0], parts[1], parts[2].charAt(1));
        this.listone_svincolati.add(player);
      }
    }
  }

  /***
   * Metodo che mi permette di controllare se è già presente un client che ha username con lo stesso nome. Viene fatta per garantire che i client abbiano nome univoco, e nel caso ci fossero duplicati, il client darà un errore da interfaccia e non riuscirà a iscriversi al server.
   * @param username username del client 
   * @return 
   * @throws RemoteException
   */
  public boolean user_is_duplicate(String username) throws RemoteException {
    for (InterfaceClient client : client_list) {
      if (client.get_name().equals(username)) {
        return true;
      }
    }
    return false;
  }

/***
 * Metodo che permette di aggiornare tutti i successori dei client in modo che formino un overlay network di tipo ring e riescano a gestire in modo distribuito i vari rilanci dell'asta autonomamente.
 * @throws RemoteException
 */
  public void update_successor_client() throws RemoteException{
    for(int i=0; i<this.giocatori_collegati; i++){
      int index_successor = (i+1)%NUMERO_CLIENT;
      InterfaceClient client_scelto = this.client_list.get(i);
      InterfaceClient successor = this.client_list.get(index_successor);
      client_scelto.set_successor_client(successor);
    }
  }
/***
 * Il client viene aggiunto alla lista dei client sul server (se l'username non è gia presente nella lista dei client).
 * In caso di riuscita dell'operazione il server notificherà tutti client iscritti del join del nuovo client al server tramite il metodo notify_users
 * @param client client che si vuole registrare sul server
 * @return true se il client si è riuscito ad iscrivere sul server, false altrimenti
 */
  @Override
  public boolean add_client(InterfaceClient client) throws RemoteException {
    try {
      InterfaceClient client_joined = (InterfaceClient) Naming
          .lookup("rmi://" + "localhost" + "/" + client.get_service_name());
      String username = client.get_name();
      // controllo se username è già presente (e nel caso notifico e dico di cambiare
      // username)
      if (user_is_duplicate(username)) {
        return false;
      } 
      else {
        this.client_list.add(client);
        this.giocatori_collegati++;
        System.out.println("Aggiunto user " + client.get_name());
        client_joined.message_from_server("[Server] : Benvenuto " + username + " nella lobby d'asta \n");
        // this.frame.update_frame(client_list);
        String str_list_user = users_in_lobby();
        String message_to_send_to_all_clients = username + " Si è unito in lobby! Lista giocatori " + str_list_user;
        this.notify_user(message_to_send_to_all_clients);
        if (giocatori_collegati == NUMERO_CLIENT) { // l'asta si fa in 4
          this.notify_user("Lobby completa! Inizia l'asta!");
          this.turno_inizio = 0;
          update_successor_client();
          nuovo_turno();          
        }
        return true;
      }

    } catch (RemoteException | MalformedURLException | NotBoundException e) {
      e.printStackTrace();
      return false;
    }
  }

/***
 * Metodo che permette di inviare un messaggio a tutti i client iscritti sul server
 * @param message
 * @throws RemoteException
 */  public void notify_user(String message) throws RemoteException {
    for (InterfaceClient client : client_list) {
      client.message_from_server(message);
    }
  }

/***
 * Metodo che permette di fare una ricerca del fantaplayer avendo come parametro il suo nome
 * @param player
 * @return
 */
  public FantaPlayer search_fantaplayer(String player) {
    for (FantaPlayer p : listone_svincolati) {
      if (player.equals(p.get_name())) {
        System.out.println("trovato fantaplayer nella search"+p.get_name());
        return p;
      }
    }
    System.out.println("Errore, non trovato FantaPlayer");
    return null;
  }

  /***
 * Metodo che permette di fare una ricerca del client avendo come parametro il suo nome
 * @param username
 * @return
 */
  public InterfaceClient search_client(String username) throws RemoteException { // username è univoco (infatti nella
                                                                                 // registrazione si controlla)
    for (InterfaceClient client : client_list) {
      if (client.get_name().equals(username)) {
        return client;
      }
    }
    System.out.println("Errore, client non trovato!");
    return null; //
  }
/***
 * Metodo che permette di salvare tutte le offerte in un file. E' eseguito solamente a fine asta, quando tutti i client hanno completato la squadra.
 * Il salvataggio dei dati avviene sul server per avere un file centralizzato-
 */
  void salva_dati(){
    System.out.println("Salvo le offerte");
    FileWriter writer;
    try {
      writer = new FileWriter("output.txt");
      for (Offer offer : lista_offerte_vincenti) {
        writer.write(offer.get_csv_offer());
        System.out.println(offer.get_csv_offer());
      }
      writer.close();
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }
  /***
   * Controlla se è verificata la condizione in cui tutti i client hanno completato la squadra
   * @return
   * @throws RemoteException
   */
  public boolean tutti_hanno_finito_squadra()throws RemoteException{
    boolean condition=true;
    for (InterfaceClient client : client_list) {
      condition=condition & client.is_squadra_completata(); 
    }
    return condition; // è true solo se hanno finito tutte
  }
/**
 * Metodo che permette l'inizio di un nuovo turno. Il metodo continua fino a quando tutti non avranno finito la squadra.
 * Il server, basandosi su un giro, scegliere un client da chiamare che dovrà scegliere un giocatore.
 * @throws RemoteException
 */
  public void nuovo_turno() throws RemoteException {
    if (tutti_hanno_finito_squadra()){
      notify_user("ASTA FINITA");
      System.out.println("Asta finita, ora dovresti solo salvare");
      salva_dati();
    }
    else {
      notify_user("NUOVO TURNO");
      // player che deve scegliere giocatore
      InterfaceClient client_scelto = this.client_list.get(this.turno_inizio);
      while (client_scelto.is_squadra_completata()){
        this.turno_inizio=(this.turno_inizio+1)%NUMERO_CLIENT;
        client_scelto = this.client_list.get(this.turno_inizio);
      }
      System.out.println("Nuovo turno, sta a "+client_scelto.get_name());
      client_scelto.choose_fantaplayer();
    }
    
  }

  public void stampa_listone() {
    for (FantaPlayer p : listone_svincolati) {
      System.out.println(p.get_fantaplayer_str());
    }
  }
/***
 * Mi fornisce in formato stringa il listone degli svincolati attuale
 * @return
 */
  public String get_listone() {
    String str = "";
    for (FantaPlayer p : listone_svincolati) {
      str += p.get_fantaplayer_str() + '\n';
    }
    return str;
  }



/**
 * Gettere del listone degli svincolati 
 */
  @Override
  public List<FantaPlayer> get_listone_svincolati() throws RemoteException {
    return this.listone_svincolati;
  }

/***
 * Metodo che viene chiamato dal client quando capirà di avere vinto l'asta per un singolo calciatore. Esso infatti notificherà il server, che aggiungerà questa offerta nella lista delle offerte vincenti (che salverà alla fine su file), rimuoverà il giocatore appena comprato dalla lista degli svincolati e farà iniziare un nuovo turno per fare scegliere un nuovo calciatore a un altro client
 * @param offer
 */
@Override
  public void notify_player_buy(Offer offer) throws RemoteException{
    System.out.println("Asta vinta da "+offer.get_username()+offer.get_fantaplayer()+offer.get_price());
    // molto simile alla funzione response asta
    lista_offerte_vincenti.add(offer);
    FantaPlayer p = search_fantaplayer(offer.get_fantaplayer().get_name());
    //FantaPlayer p = offer.get_fantaplayer();
    System.out.println("fantaplayer comprato: "+p.get_name());
    this.listone_svincolati.remove(p);
      // finito il giocatore, se ne inizia uno nuovo
    this.turno_inizio = (this.turno_inizio + 1) % NUMERO_CLIENT;
    nuovo_turno();    
  }

  public static void main(String args[]) {
    // security manager needed to load remote classes
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new SecurityManager());
    }
    try {
      Server obj = new Server("serverRMI");
      obj.create_listone_from_file();
      // obj.stampa_listone();
      if (args.length > 0)
        name = args[0] + name;
      Naming.rebind(name, obj);
      System.out.println("Avviato il Server: " + name);
    } catch (Exception e) {
      System.out.println("ServerImplementation err: " + e.getMessage());
      e.printStackTrace();
    }
  }

}
